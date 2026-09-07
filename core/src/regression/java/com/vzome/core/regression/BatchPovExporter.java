
package com.vzome.core.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.api.Exporter;
import com.vzome.core.commands.Command;

/**
 * Exports every ".vZome" file under a folder to POV-Ray format, writing the results into a
 * mirrored folder hierarchy.
 *
 * Two modes:
 *
 *   record  (no golden folder)  -- write the ".pov" files.  This produces the golden baseline.
 *   compare (golden folder set) -- write the ".pov" files, then compare each one against the
 *                                  corresponding file in the golden tree, and exit non-zero if
 *                                  anything differs or fails.  This is the regression test.
 *
 * Comparison is plain equality, which only works because POVRayExporter now emits its
 * manifestations in a deterministic, content-derived order.
 *
 * PARALLELISM IS BY PROCESS, NOT BY THREAD.  Loading a design cannot be done concurrently
 * within one JVM: XmlSaveFormat instances are cached in a static map (XmlSaveFormat.FORMATS)
 * but carry per-document state -- initialize() sets the field, scale and multiplier that are
 * then read throughout history replay -- so two simultaneous loads corrupt each other, and
 * designs fail to open at random.  Loading is also ~96% of the runtime, so serializing just
 * the load would leave almost nothing to parallelize anyway (measured: 35.2s with 6 threads
 * and a load lock, versus 36.6s single-threaded).
 *
 * Instead, --split/--shard let several independent JVMs each take a slice of the files.
 * Statics are not shared between processes, so this is both correct and genuinely parallel.
 * Use the "batchPov" Gradle task, which forks the workers for you.
 *
 * @author Scott Vorthmann
 */
public class BatchPovExporter
{
    private static final String VZOME_SUFFIX = ".vzome";  // compared lowercased

    /**
     * A folder containing this file lists exactly what should be tested there; anything omitted
     * is a known failure being skipped.  The name and semantics come from the existing collection.
     */
    private static final String SKIP_FILE = "skipKnownFailures.testsuite";

    private static final int DEFAULT_TIMEOUT_SECONDS = 60;

    /** One Application for the whole process; it is reused for every design. */
    private final Application application = new Application( new Command.FailureChannel()
    {
        @Override
        public void reportFailure( Command.Failure f )
        {
            // Recorded per-file by the caller; a failure here is not fatal to the run.
            System .err .println( "  failure: " + f .getMessage() );
        }
    } );

    /**
     * What happened to one design in ONE run.  This is what a manifest records; it says
     * nothing about the baseline.
     */
    enum Outcome
    {
        OK,       // exported; the manifest carries a hash of the geometry
        FAILED,   // threw; the manifest carries the exception text
        TIMEOUT;  // took too long -- deliberately NOT the same as FAILED, see Verdict
    }

    /**
     * How this run compares to the baseline, for one design.  The distinction that matters
     * is between "worse than the baseline" (which fails the build) and merely "different"
     * or "unknown" (which do not).  A baseline full of known failures is still a useful
     * baseline: the question is never "is everything OK", it is "did anything get worse".
     */
    enum Verdict
    {
        UNCHANGED,      // same as the baseline, whatever the baseline said
        REGRESSED,      // was OK and now differs or fails -- the only thing that fails a run
        IMPROVED,       // the baseline recorded a failure and this run succeeded
        CHANGED,        // failed before and fails now, but differently
        UNKNOWN,        // a timeout on either side: we cannot tell, and must not call it a pass
        NEW,            // not in the baseline at all
        RECORDED;       // no baseline in play; we are recording one
    }

    static class Result
    {
        final Path relative;
        final Outcome outcome;
        final Verdict verdict;
        /** Hash of the geometry when OK; the exception text when FAILED; the limit when TIMEOUT. */
        final String detail;
        /** How the difference reads, for the report.  Null unless something differs. */
        final String difference;

        Result( Path relative, Outcome outcome, Verdict verdict, String detail, String difference )
        {
            this .relative = relative;
            this .outcome = outcome;
            this .verdict = verdict;
            this .detail = detail;
            this .difference = difference;
        }
    }

    /** One design's line in a manifest: what happened, and enough to compare against. */
    static class Entry
    {
        final Outcome outcome;
        final String detail;

        Entry( Outcome outcome, String detail )
        {
            this .outcome = outcome;
            this .detail = detail;
        }
    }

    private final Path inputRoot, outputRoot, goldenRoot;

    /** This process handles the files whose position satisfies (i % split) == shard. */
    private final int shard, split, timeoutSeconds;

    /**
     * When set, comparison ignores the camera and lighting preamble, so a difference is only
     * reported for the geometry -- which is what the edit history actually produces.  Camera
     * and lighting are viewing preferences, and the Javascript exporter cannot reproduce the
     * light directions headlessly, so comparing them is noise.
     */
    private boolean geometryOnly;

    /** The baseline manifest, keyed by relative ".pov" path.  Null when recording. */
    private Map<String, Entry> baseline;

    BatchPovExporter( Path inputRoot, Path outputRoot, Path goldenRoot, int shard, int split, int timeoutSeconds )
    {
        this .inputRoot = inputRoot;
        this .outputRoot = outputRoot;
        this .goldenRoot = goldenRoot;
        this .shard = shard;
        this .split = split;
        this .timeoutSeconds = timeoutSeconds;
    }

    /** The manifest file name, written into the output root and read from the golden root. */
    static final String MANIFEST = "manifest.txt";

    /**
     * Written when a run starts and rewritten when it finishes.  Without it there is no way
     * to tell a finished output tree from one whose run was killed half way: the reports of
     * an interrupted run stay on disk looking authoritative, which has already caused a
     * partial run to be read as a complete one.
     */
    static final String RUN_STATE = "run.txt";

    /** Reports a run produces; cleared at the start so no stale file can be mistaken for fresh. */
    private static final String[] REPORTS = {
        MANIFEST, "failures.txt", "regressions.txt", "improved.txt", "changed.txt",
        "unknown.txt", "new.txt", "missing-golden.txt",
    };

    /**
     * A stable, short summary of what the edit history produced.  Comparing hashes rather
     * than whole files keeps a manifest small enough to read, diff and commit, while still
     * detecting any change: the ".pov" files stay on disk for investigating one that moved.
     */
    static String hashOf( String text )
    {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest .getInstance( "SHA-256" );
            byte[] bytes = digest .digest( text .getBytes( StandardCharsets .UTF_8 ) );
            StringBuilder hex = new StringBuilder();
            for ( int i = 0; i < 8; i++ )   // 8 bytes is ample to distinguish designs
                hex .append( String .format( "%02x", bytes[ i ] ) );
            return hex .toString();
        } catch ( java.security.NoSuchAlgorithmException e ) {
            throw new IllegalStateException( "SHA-256 is required by the Java platform", e );
        }
    }

    /**
     * Reads a manifest.  Lines are "path<TAB>OUTCOME<TAB>detail"; anything unparseable is
     * skipped rather than failing the run, so a hand-edited manifest stays usable.
     */
    static Map<String, Entry> readManifest( Path file ) throws IOException
    {
        Map<String, Entry> entries = new LinkedHashMap<>();
        if ( ! Files .isRegularFile( file ) )
            return entries;
        for ( String line : Files .readAllLines( file, StandardCharsets .UTF_8 ) ) {
            if ( line .isEmpty() || line .startsWith( "#" ) )
                continue;
            String[] fields = line .split( "\t", 3 );
            if ( fields .length < 2 )
                continue;
            try {
                entries .put( fields[ 0 ],
                        new Entry( Outcome .valueOf( fields[ 1 ] ), fields .length > 2 ? fields[ 2 ] : "" ) );
            } catch ( IllegalArgumentException unknownOutcome ) {
                // A manifest from a newer version; skip the line rather than abort.
            }
        }
        return entries;
    }

    private void writeManifest( List<Result> results ) throws IOException
    {
        String name = this .split > 1 ? MANIFEST .replace( ".txt", "-" + this .shard + ".txt" ) : MANIFEST;
        StringBuilder text = new StringBuilder();
        for ( Result result : results )
            text .append( result .relative ) .append( '\t' )
                 .append( result .outcome ) .append( '\t' )
                 .append( result .detail == null ? "" : result .detail .replace( '\t', ' ' ) )
                 .append( '\n' );
        Files .write( this .outputRoot .resolve( name ), text .toString() .getBytes( StandardCharsets .UTF_8 ) );
    }

    /**
     * Compares one design's outcome against the baseline.  A timeout on either side yields
     * UNKNOWN: if a change makes a design hang, it would otherwise time out on both runs and
     * be reported as a match -- a severe regression reading as a pass.
     */
    static Verdict judge( Entry before, Outcome now, String detail )
    {
        if ( before == null )
            return Verdict .NEW;
        if ( before .outcome == Outcome .TIMEOUT || now == Outcome .TIMEOUT )
            return Verdict .UNKNOWN;

        if ( before .outcome == Outcome .OK ) {
            if ( now != Outcome .OK )
                return Verdict .REGRESSED;              // used to export, now fails
            return before .detail .equals( detail ) ? Verdict .UNCHANGED : Verdict .REGRESSED;
        }

        // The baseline recorded a failure.
        if ( now == Outcome .OK )
            return Verdict .IMPROVED;
        return before .detail .equals( detail ) ? Verdict .UNCHANGED : Verdict .CHANGED;
    }

    /**
     * The base folder that relative paths are computed against.  When a single file is given,
     * that is its parent, so the output is a single file rather than a deep tree.
     */
    private Path baseFolder()
    {
        return Files .isDirectory( this .inputRoot ) ? this .inputRoot : this .inputRoot .getParent();
    }

    private List<Path> collectDesigns() throws IOException
    {
        if ( ! Files .isDirectory( this .inputRoot ) )
            return Collections .singletonList( this .inputRoot );

        List<Path> designs = new ArrayList<>();
        collectFolder( this .inputRoot, designs );
        // Sort, so progress output and reports read in a predictable order.
        Collections .sort( designs, Comparator .comparing( Path::toString ) );
        return designs;
    }

    /**
     * Walks one folder, honoring the "skipKnownFailures.testsuite" convention that the historical
     * collection already uses (and that TestVZomeFiles.Collector implements for the JUnit-XML
     * harness).  When such a file is present, it acts as an ALLOW-list: only the files and
     * subfolders it names are visited, so any sibling it omits is skipped as a known failure.
     * Blank lines and "#" comments are ignored, which is how a skip gets annotated with a reason.
     */
    private void collectFolder( Path folder, List<Path> designs ) throws IOException
    {
        Path suite = folder .resolve( SKIP_FILE );
        if ( Files .isRegularFile( suite ) ) {
            for ( String line : Files .readAllLines( suite, StandardCharsets .UTF_8 ) ) {
                line = line .trim();
                if ( line .isEmpty() || line .startsWith( "#" ) )
                    continue;
                Path entry = folder .resolve( line );
                if ( Files .isDirectory( entry ) )
                    collectFolder( entry, designs );
                else if ( Files .isRegularFile( entry ) && isDesign( entry ) )
                    designs .add( entry );
                else
                    System .err .println( "  listed in " + suite + " but not found: " + line );
            }
            return;
        }
        try ( Stream<Path> children = Files .list( folder ) ) {
            List<Path> entries = new ArrayList<>();
            children .forEach( entries::add );
            for ( Path entry : entries ) {
                if ( Files .isDirectory( entry ) )
                    collectFolder( entry, designs );
                else if ( isDesign( entry ) )
                    designs .add( entry );
            }
        }
    }

    private static boolean isDesign( Path path )
    {
        return path .getFileName() .toString() .toLowerCase() .endsWith( VZOME_SUFFIX );
    }

    private Path relativize( Path design )
    {
        return this .baseFolder() .relativize( design );
    }

    private static Path withPovSuffix( Path relative )
    {
        String name = relative .getFileName() .toString();
        name = name .substring( 0, name .length() - VZOME_SUFFIX .length() ) + ".pov";
        Path parent = relative .getParent();
        return parent == null ? Paths .get( name ) : parent .resolve( name );
    }

    private Result exportOne( Path design ) throws Exception
    {
        Path relative = relativize( design );
        Path relativePov = withPovSuffix( relative );

        Exporter exporter = this .application .getExporter( "pov" );

        StringWriter buffer = new StringWriter();
        try ( InputStream bytes = new FileInputStream( design .toFile() ) ) {
            Document doc = this .application .loadDocument( bytes );  // this overload replays the edit history
            try ( PrintWriter out = new PrintWriter( buffer ) ) {
                // A null File (inside the api.Exporter facade) suppresses the ".ini" sidecar.
                exporter .doExport( doc, out, 1080, 1920 );
            }
        }
        String exported = buffer .toString();

        Path target = this .outputRoot .resolve( relativePov );
        Files .createDirectories( target .getParent() );
        // Write to a temp file and move into place, so an interrupted run never leaves a
        // half-written ".pov" that a later comparison would silently misread.
        Path temp = Files .createTempFile( target .getParent(), target .getFileName() .toString(), ".tmp" );
        Files .write( temp, exported .getBytes( StandardCharsets .UTF_8 ) );
        Files .move( temp, target, StandardCopyOption .REPLACE_EXISTING );

        // The manifest records a hash of whatever we would compare, so --geometry-only
        // changes what the hash covers as well as what a diff reports.
        String compared = this .geometryOnly ? geometryOf( exported ) : exported;
        String hash = hashOf( compared );

        if ( this .baseline == null )
            return new Result( relativePov, Outcome .OK, Verdict .RECORDED, hash, null );

        Entry before = this .baseline .get( relativePov .toString() );
        Verdict verdict = judge( before, Outcome .OK, hash );

        String difference = null;
        if ( verdict == Verdict .REGRESSED && before != null && before .outcome == Outcome .OK ) {
            // Both runs exported, but the geometry moved.  Read the golden ".pov" itself to
            // say WHERE, which the hash alone cannot.
            Path golden = this .goldenRoot .resolve( relativePov );
            String prefix = "hash " + before .detail + " -> " + hash;
            if ( Files .isRegularFile( golden ) ) {
                String expected = new String( Files .readAllBytes( golden ), StandardCharsets .UTF_8 );
                String want = this .geometryOnly ? geometryOf( expected ) : expected;
                // The manifest hash and the stored ".pov" can disagree if the manifest was
                // hand-edited, or was recorded with a different --geometry-only setting.  Say
                // so rather than printing a confusing "these are the same" diff.
                difference = want .equals( compared )
                        ? prefix + " (but the golden .pov matches; is the manifest stale?)"
                        : prefix + ", " + describeDifference( want, compared );
            }
            else
                difference = prefix + " (no golden .pov to diff)";
        }
        return new Result( relativePov, Outcome .OK, verdict, hash, difference );
    }

    /** Builds the Result for a design that failed or timed out, judged against the baseline. */
    private Result failure( Path relative, Outcome outcome, String detail )
    {
        Path relativePov = withPovSuffix( relative );
        if ( this .baseline == null )
            return new Result( relativePov, outcome, Verdict .RECORDED, detail, null );
        Entry before = this .baseline .get( relativePov .toString() );
        Verdict verdict = judge( before, outcome, detail );
        String difference = null;
        if ( verdict == Verdict .REGRESSED || verdict == Verdict .CHANGED )
            difference = ( before == null ? "(not in baseline)" : before .outcome + " " + before .detail )
                    + "  ->  " + outcome + " " + detail;
        return new Result( relativePov, outcome, verdict, detail, difference );
    }


    /**
     * Everything the edit history produced, with the camera and lighting preamble removed.
     * That preamble is the "#declare" view settings, the "light_source" lines, the ambient and
     * background colors, and the boilerplate preamble between them -- none of which say
     * anything about the model.  The geometry proper is the shape, transform and color
     * declarations plus the "triangle" and "object" lines, which is what is kept here.
     */
    static String geometryOf( String pov )
    {
        StringBuilder kept = new StringBuilder();
        for ( String line : pov .split( "\n", -1 ) ) {
            String trimmed = line .trim();
            if ( trimmed .isEmpty() )
                continue;
            boolean isGeometry = trimmed .startsWith( "object " ) || trimmed .startsWith( "object{" )
                    || trimmed .startsWith( "triangle" )
                    || trimmed .startsWith( "#declare S" )
                    || trimmed .startsWith( "#declare trans" )
                    || trimmed .startsWith( "#declare color" )
                    || trimmed .startsWith( "#declare embedding" )
                    || trimmed .startsWith( "mesh {" ) || trimmed .equals( "}" );
            if ( isGeometry )
                kept .append( line ) .append( '\n' );
        }
        return kept .toString();
    }

    /**
     * Reports the first differing line, rather than a whole-file diff.  With thousands of files
     * and gigabytes of output, full diffs in a report are unreadable; the two ".pov" files are
     * left on disk for a real diff when a case needs investigating.
     */
    static String describeDifference( String expected, String actual )
    {
        String[] expectedLines = expected .split( "\n", -1 );
        String[] actualLines = actual .split( "\n", -1 );
        int limit = Math .min( expectedLines .length, actualLines .length );
        for ( int i = 0; i < limit; i++ ) {
            String expectedLine = expectedLines[ i ];
            String actualLine = actualLines[ i ];
            if ( ! expectedLine .equals( actualLine ) ) {
                // POV-Ray instance lines run long, so show a window around the first differing
                // character rather than the start of the line -- otherwise two lines that differ
                // near their end look identical in the report.
                int column = 0;
                int shared = Math .min( expectedLine .length(), actualLine .length() );
                while ( column < shared && expectedLine .charAt( column ) == actualLine .charAt( column ) )
                    column++;
                return "line " + ( i + 1 ) + ", column " + ( column + 1 ) + ":"
                        + "\n      golden: " + excerpt( expectedLine, column )
                        + "\n      actual: " + excerpt( actualLine, column );
            }
        }
        return "golden has " + expectedLines .length + " lines, actual has " + actualLines .length;
    }

    private static final int EXCERPT_CONTEXT = 60;

    /**
     * A window of the line centered on the given column, with ellipses marking any truncation,
     * so the reader can see what actually changed.
     */
    private static String excerpt( String line, int column )
    {
        int start = Math .max( 0, column - EXCERPT_CONTEXT );
        int end = Math .min( line .length(), column + EXCERPT_CONTEXT );
        String text = line .substring( start, end )
                .replace( "\t", "\\t" ) .replace( "\r", "\\r" );
        return ( start > 0 ? "..." : "" ) + text + ( end < line .length() ? "..." : "" )
                + ( line .length() == end && line .endsWith( " " ) ? "<trailing space>" : "" );
    }

    private int run() throws Exception
    {
        List<Path> all = collectDesigns();
        if ( all .isEmpty() ) {
            System .err .println( "No .vZome files found under " + this .inputRoot );
            return 1;
        }
        // Take this process's slice.  Round-robin rather than contiguous blocks, so that a
        // folder of unusually large designs does not all land on one worker.
        List<Path> designs = new ArrayList<>();
        for ( int i = 0; i < all .size(); i++ )
            if ( i % this .split == this .shard )
                designs .add( all .get( i ) );

        boolean comparing = this .goldenRoot != null;
        if ( comparing ) {
            this .baseline = readManifest( this .goldenRoot .resolve( MANIFEST ) );
            if ( this .baseline .isEmpty() )
                System .err .println( "  no manifest in " + this .goldenRoot
                        + " -- every design will be reported as NEW."
                        + "  Record a baseline with this version first." );
        }
        // Machine-readable progress, for the parent process to aggregate across shards; see
        // the batchPov task.  Printed even when running standalone -- it is easy to ignore.
        System .out .println( "@total " + designs .size() );
        System .out .flush();
        String label = this .split > 1 ? " (shard " + this .shard + " of " + this .split + ")" : "";
        System .out .println( ( comparing ? "Comparing " : "Exporting " ) + designs .size()
                + " of " + all .size() + " designs" + label );
        System .out .println( "  input:  " + this .inputRoot );
        System .out .println( "  output: " + this .outputRoot );
        if ( comparing ) {
            System .out .println( "  golden: " + this .goldenRoot );
            if ( this .geometryOnly )
                System .out .println( "  comparing geometry only; camera and lighting ignored" );
        }

        Files .createDirectories( this .outputRoot );
        clearStaleReports();
        writeRunState( "RUNNING", designs .size(), 0, comparing );

        List<Result> results = new ArrayList<>();
        long started = System .currentTimeMillis();

        // One design at a time.  Loading cannot be parallelized inside a JVM (see the class
        // comment); a watchdog thread is used only to bound how long a single pathological
        // design may take.
        ExecutorService watchdog = Executors .newSingleThreadExecutor( runnable -> {
            Thread thread = new Thread( runnable, "batch-pov-export" );
            thread .setDaemon( true );
            return thread;
        } );
        try {
            for ( Path design : designs ) {
                Path relative = relativize( design );
                Future<Result> future = watchdog .submit( () -> exportOne( design ) );
                try {
                    results .add( future .get( this .timeoutSeconds, TimeUnit .SECONDS ) );
                } catch ( TimeoutException te ) {
                    future .cancel( true );
                    results .add( failure( relative, Outcome .TIMEOUT,
                            this .timeoutSeconds + "s" ) );
                } catch ( Throwable t ) {
                    Throwable cause = t .getCause() == null ? t : t .getCause();
                    String message = cause .getMessage();
                    results .add( failure( relative, Outcome .FAILED,
                            cause .getClass() .getSimpleName()
                            + ( message == null ? "" : ": " + message ) ) );
                }
                Result latest = results .get( results .size() - 1 );
                System .out .println( "@progress " + latest .verdict + " " + latest .relative );
                System .out .flush();
            }
        } finally {
            watchdog .shutdownNow();
        }

        int exitCode = report( results, comparing, System .currentTimeMillis() - started );
        writeRunState( "FINISHED", designs .size(), results .size(), comparing );
        return exitCode;
    }

    private int report( List<Result> results, boolean comparing, long elapsedMillis ) throws IOException
    {
        writeManifest( results );

        Map<Verdict, List<Result>> byVerdict = new LinkedHashMap<>();
        for ( Verdict v : Verdict .values() )
            byVerdict .put( v, new ArrayList<>() );
        for ( Result result : results )
            byVerdict .get( result .verdict ) .add( result );

        Comparator<Result> byPath = Comparator .comparing( r -> r .relative .toString() );
        for ( List<Result> list : byVerdict .values() )
            Collections .sort( list, byPath );

        int failedNow = 0, timedOut = 0;
        for ( Result result : results ) {
            if ( result .outcome == Outcome .FAILED ) failedNow++;
            if ( result .outcome == Outcome .TIMEOUT ) timedOut++;
        }

        System .out .println();
        System .out .println( "Finished in " + ( elapsedMillis / 1000 ) + "s" );
        if ( comparing ) {
            System .out .println( "  unchanged:      " + byVerdict .get( Verdict .UNCHANGED ) .size() );
            System .out .println( "  REGRESSED:      " + byVerdict .get( Verdict .REGRESSED ) .size() );
            System .out .println( "  improved:       " + byVerdict .get( Verdict .IMPROVED ) .size() );
            System .out .println( "  changed:        " + byVerdict .get( Verdict .CHANGED ) .size() );
            System .out .println( "  unknown:        " + byVerdict .get( Verdict .UNKNOWN ) .size()
                    + "  (a timeout on one side or the other)" );
            System .out .println( "  new:            " + byVerdict .get( Verdict .NEW ) .size() );
        }
        else
            System .out .println( "  recorded:       " + results .size() );
        System .out .println( "  (failed: " + failedNow + ", timed out: " + timedOut + ")" );

        // Only regressions are printed in full: they are what someone has to act on.
        for ( Result result : byVerdict .get( Verdict .REGRESSED ) )
            System .out .println( "  REGRESSED  " + result .relative
                    + ( result .difference == null ? "" : "  " + result .difference ) );

        writeVerdictReport( "regressions.txt", byVerdict .get( Verdict .REGRESSED ) );
        writeVerdictReport( "improved.txt",    byVerdict .get( Verdict .IMPROVED ) );
        writeVerdictReport( "changed.txt",     byVerdict .get( Verdict .CHANGED ) );
        writeVerdictReport( "unknown.txt",     byVerdict .get( Verdict .UNKNOWN ) );
        writeVerdictReport( "new.txt",         byVerdict .get( Verdict .NEW ) );

        List<Result> troubled = new ArrayList<>();
        for ( Result result : results )
            if ( result .outcome != Outcome .OK )
                troubled .add( result );
        Collections .sort( troubled, byPath );
        writeVerdictReport( "failures.txt", troubled );

        // ONLY regressions fail the run.  A baseline that records known failures is still a
        // useful baseline; improvements, timeouts and new designs are reported, not punished.
        return byVerdict .get( Verdict .REGRESSED ) .isEmpty() ? 0 : 1;
    }

    private void writeVerdictReport( String name, List<Result> results ) throws IOException
    {
        StringBuilder text = new StringBuilder();
        for ( Result result : results )
            text .append( result .relative ) .append( "  " ) .append( result .outcome )
                 .append( "  " ) .append( result .detail == null ? "" : result .detail )
                 .append( result .difference == null ? "" : "\n      " + result .difference )
                 .append( '\n' );
        writeText( name, text .toString(), results .isEmpty() );
    }

    /**
     * Removes this shard's reports from a previous run.  A run that is killed leaves its
     * reports behind, and they are indistinguishable from a completed run's; deleting them
     * up front means anything present afterwards belongs to the run in progress.
     */
    private void clearStaleReports() throws IOException
    {
        for ( String name : REPORTS ) {
            Files .deleteIfExists( this .outputRoot .resolve( name ) );
            if ( this .split > 1 )
                Files .deleteIfExists( this .outputRoot
                        .resolve( name .replaceFirst( "\\.txt$", "-" + this .shard + ".txt" ) ) );
        }
    }

    /**
     * Records what this run is doing and whether it finished.  Shards each write their own,
     * so a tree is complete only when every shard says FINISHED.
     */
    private void writeRunState( String state, int total, int done, boolean comparing ) throws IOException
    {
        String name = this .split > 1 ? RUN_STATE .replace( ".txt", "-" + this .shard + ".txt" ) : RUN_STATE;
        StringBuilder text = new StringBuilder();
        text .append( "state\t" ) .append( state ) .append( '\n' );
        text .append( "started\t" ) .append( java.time.Instant .now() ) .append( '\n' );
        text .append( "input\t" ) .append( this .inputRoot ) .append( '\n' );
        text .append( "shard\t" ) .append( this .shard ) .append( " of " ) .append( this .split ) .append( '\n' );
        text .append( "designs\t" ) .append( done ) .append( " of " ) .append( total ) .append( '\n' );
        text .append( "timeout\t" ) .append( this .timeoutSeconds ) .append( "s\n" );
        text .append( "geometryOnly\t" ) .append( this .geometryOnly ) .append( '\n' );
        text .append( "mode\t" ) .append( comparing ? "compare" : "record" ) .append( '\n' );
        if ( comparing )
            text .append( "golden\t" ) .append( this .goldenRoot ) .append( '\n' );
        Files .write( this .outputRoot .resolve( name ), text .toString() .getBytes( StandardCharsets .UTF_8 ) );
    }

    private void writeText( String name, String text, boolean empty ) throws IOException
    {
        // Shards run as separate processes writing into one output tree, so each needs its own
        // report file; the Gradle task merges them when the run finishes.
        if ( this .split > 1 )
            name = name .replaceFirst( "\\.txt$", "-" + this .shard + ".txt" );
        Path report = this .outputRoot .resolve( name );
        if ( empty ) {
            Files .deleteIfExists( report );
            return;
        }
        Files .write( report, text .getBytes( StandardCharsets .UTF_8 ) );
        System .out .println( "  wrote " + report );
    }

    public static void main( String[] args ) throws Exception
    {
        List<String> positional = new ArrayList<>();
        Path golden = null;
        int shard = 0, split = 1;
        int timeout = DEFAULT_TIMEOUT_SECONDS;
        boolean geometryOnly = false;
        for ( int i = 0; i < args .length; i++ ) {
            switch ( args[ i ] ) {
            case "--compare":
                golden = Paths .get( requireValue( args, ++i, "--compare" ) ) .toAbsolutePath() .normalize();
                break;
            case "--shard":
                shard = Integer .parseInt( requireValue( args, ++i, "--shard" ) );
                break;
            case "--split":
                split = Integer .parseInt( requireValue( args, ++i, "--split" ) );
                break;
            case "--geometry-only":
                geometryOnly = true;
                break;
            case "--timeout":
                timeout = Integer .parseInt( requireValue( args, ++i, "--timeout" ) );
                break;
            default:
                positional .add( args[ i ] );
            }
        }
        if ( positional .size() < 2 ) {
            System .err .println( "usage: BatchPovExporter <inputRoot> <outputRoot>"
                    + " [--compare <goldenRoot>] [--geometry-only]"
                    + " [--shard N --split M] [--timeout seconds]" );
            System .exit( 2 );
        }
        if ( split < 1 || shard < 0 || shard >= split ) {
            System .err .println( "invalid --shard/--split: need 0 <= shard < split" );
            System .exit( 2 );
        }
        Path input = Paths .get( positional .get( 0 ) ) .toAbsolutePath() .normalize();
        Path output = Paths .get( positional .get( 1 ) ) .toAbsolutePath() .normalize();

        if ( ! Files .exists( input ) ) {
            System .err .println( "input does not exist: " + input );
            System .exit( 2 );
        }
        if ( golden != null && ! Files .isDirectory( golden ) ) {
            System .err .println( "golden folder does not exist: " + golden );
            System .exit( 2 );
        }

        BatchPovExporter exporter = new BatchPovExporter( input, output, golden, shard, split, timeout );
        exporter .geometryOnly = geometryOnly;
        int exitCode = exporter .run();
        System .exit( exitCode );
    }

    private static String requireValue( String[] args, int index, String option )
    {
        if ( index >= args .length ) {
            System .err .println( option + " requires a value" );
            System .exit( 2 );
        }
        return args[ index ];
    }
}
