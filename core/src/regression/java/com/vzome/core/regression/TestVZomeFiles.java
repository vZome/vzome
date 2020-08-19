

package com.vzome.core.regression;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.api.Application;
import com.vzome.api.Exporter;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.xml.DomUtils;


/**
 * 
 * @author Scott Vorthmann
 */
public class TestVZomeFiles extends FileSystemVisitor2 .Actor
{
    private static final Logger logger = Logger.getLogger("");

    private final Document doc;

    public static void main( String[] args )
    {
        
        FileOutputStream out;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
            DocumentBuilder builder = factory .newDocumentBuilder();
            Document doc = builder .newDocument();
            TestVZomeFiles tester = new TestVZomeFiles( doc, args[0], args[1] );
            doc .appendChild( tester .collectResults() );

            out = new FileOutputStream( args[2] );
            DomUtils .serialize( doc, out );
            out .close();
            System.exit( tester .exitCode );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        System.exit( -1 );
    }

    public TestVZomeFiles( Document doc, String baseFolder, String testPath )
    {
        super();
        this .doc = doc;
        this .baseFolder = new File( baseFolder ) .toPath();
        this .testPath = testPath;
    }

    private Application app;

    private Exporter historyExporter;

    private Path baseFolder;

    private String testPath;

    private Element testSuites;

    private Element testCase;

    private int exitCode = 0;

    private final static String BROWSER = "https://github.com/vorth/vzome-core/blob/master/src/regression/files";

    private final static String EXPORTS = "https://raw.githubusercontent.com/vorth/vzome-core/master/src/regression/files";

    private Element collectResults()
    {
        testSuites = doc .createElement( "testsuites" );

        app = new Application( new Command.FailureChannel()
        {
            @Override
            public void reportFailure( Failure f )
            {
                Element failure = doc .createElement( "failure" );
                failure .setAttribute( "type", "user.command.error" );
                failure .setAttribute( "message", f .getMessage() );
                //                if ( Controller.USER_ERROR_CODE .equals( errorCode ) )
                //                {
                //                    // don't want a stack trace for a user error
                //                    failure .addAttribute( new Attribute( "type", Controller.USER_ERROR_CODE ) );
                //                    failure .addAttribute( new Attribute( "message", ((Exception) arguments[0]) .getMessage() ) );
                //                }
                //                else if ( Controller.UNKNOWN_ERROR_CODE .equals( errorCode ) )
                //                {
                //                    Throwable t = (Throwable) arguments[0];
                //                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                //                    t .printStackTrace( new PrintStream( out ) );
                //                    failure .appendChild( new Text( new String( out .toByteArray() ) ) );
                //                    failure .addAttribute( new Attribute( "type", Controller.USER_ERROR_CODE ) );
                //                    failure .addAttribute( new Attribute( "message", "internal error" ) );
                //                }
                //                else
                //                {
                //                    failure .addAttribute( new Attribute( "type", "other" ) );
                //                    failure .addAttribute( new Attribute( "message", errorCode ) );
                //                }
                testCase .appendChild( failure );
            }
        });
        historyExporter = app .getExporter( "history" );

        Path root = baseFolder .resolve( testPath );
        if ( ! Files.exists( root, new LinkOption[]{} ) )
            System .err. println( "directory does not exist: " + root .toString() );
        else {
            try {
                actOnFileOrFolder( root .toFile(), new Collector() );
            } catch ( IOException ioe ) {
                System .err. println( "scan failed: " + ioe .getLocalizedMessage() );
            }
        }
        app = null;
        return testSuites;
    }

    protected static class Collector extends FileSystemVisitor2 {

        @Override
        public void visitFolder( File directory, Actor actor ) throws IOException
        {
            String[] files = directory .list();
            if (files != null)
                for (String file : files) {
                    if (file.toLowerCase().endsWith(".testsuite")) {
                        File linksFile = new File(directory, file);
                        if ( linksFile .isDirectory() )
                            // don't act on it, that would make another layer of suite
                            super .visitFolder( linksFile, actor );
                        else
                            actor .actOnFile( linksFile, "testsuite", this );
                        return;
                    }
                }
            super .visitFolder( directory, actor );
        }
    }	

    @Override
    public void actOnFolder( File folder, FileSystemVisitor2 visitor ) throws IOException
    {
        if ( ".svn" .equals( folder .getName() ) )
            return;
        super .actOnFolder( folder, visitor );
    }

    @Override
    public void actOnFile( File file, String extension, FileSystemVisitor2 visitor ) throws IOException
    {
        if ( "vZome" .equals( extension ) )
        {
            String pkgName = "noFolder";
            String className = "noFolder";
            File classFolder = file .getParentFile();
            if ( ! classFolder .equals( baseFolder ) )
            {
                className = classFolder .getName();
                File packageFolder = classFolder .getParentFile();
                if ( ! packageFolder .equals( baseFolder ) )
                {
                    String basePath = baseFolder .toAbsolutePath() .toString();
                    pkgName = packageFolder .getAbsolutePath() .substring( basePath .length() );
                }
            }
            testCase = doc .createElement( "testcase" );
            testCase .setAttribute( "classname", pkgName + "." + className );
            String testName = file .getName();
            testName = testName .substring( 0, testName .length() - 6 ); // strip ".vZome"
            String suiteName = pkgName + "/" + className + "/" + testName;
            System .out .println( suiteName );
            testCase .setAttribute( "name", testName );

            final Element stderr = doc .createElement( "system-err" );
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Handler handler = new StreamHandler( bos, new SimpleFormatter() );
            handler .setLevel( Level .INFO );
            logger .addHandler( handler );

            try {
                InputStream bytes = new FileInputStream( file );
                com.vzome.api.Document doc = app .loadDocument( bytes );

                File goldenHistory = new File( classFolder, testName + ".history" );
                if ( goldenHistory .getAbsoluteFile() .exists() ) {
                    File testHistory = new File( System .getProperty( "user.dir" ), testName + ".history.test" );
                    System .out .println( "History replay output: " + testHistory );
                    PrintWriter histOut = new PrintWriter( testHistory );
                    try {
                        historyExporter .doExport( doc, histOut, 1080, 1920 );
                    } finally {
                        histOut .close();
                    }
                    Process process = Runtime .getRuntime() .exec( "diff " + goldenHistory .getAbsolutePath() + " " + testHistory .getAbsolutePath() );
                    // any error message?
                    StreamGobbler errorGobbler = new 
                            StreamGobbler( process.getErrorStream(), "ERROR" );            

                    // any output?
                    StreamGobbler outputGobbler = new 
                            StreamGobbler( process.getInputStream(), "OUTPUT" );

                    // kick them off
                    errorGobbler.start();
                    outputGobbler.start();

                    // any error???
                    int exitVal = process.waitFor();
                    if ( exitVal != 0 ) {
                        throw new Exception( "ExitValue: " + exitVal );
                    }
                    else
                        testHistory .delete();
                }
            } catch ( Exception e ) {
                Element error = doc .createElement( "error" );
                error .setAttribute( "type", "finish.load.exception" );
                String msg = e .getMessage();
                if(msg == null) {
                    msg = "(null)";
                }
                error .setAttribute( "message", msg );
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                e .printStackTrace( new PrintStream( out ) );
                error .setTextContent( new String( out .toByteArray() ) );
                testCase .appendChild( error );
                exitCode = 1;
            } finally
            {
                handler .close();
                String logText = new String( bos .toByteArray() );
                stderr .setTextContent( logText );
                logger .removeHandler( handler );
            }

            testCase .setAttribute( "time", "0.1" );
            Element testSuite = doc .createElement( "testsuite" );
            testSuite .setAttribute( "tests", "1" );
            testSuite .setAttribute( "errors", "0" );
            testSuite .setAttribute( "failures", "0" );
            testSuite .setAttribute( "name", suiteName );
            String hostName = System.getenv("HOSTNAME");
            if ( hostName == null )
                hostName = "localhost";
            testSuite .setAttribute( "hostname", hostName );
            testSuite .setAttribute( "time", "4.363" );
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = new Date();
            testSuite .setAttribute( "timestamp", dateFormat.format( date ) );
            testSuite .appendChild( doc .createElement( "properties" ) );
            testSuite .appendChild( testCase );
            Element stdOut = doc .createElement( "system-out" );
            stdOut .setTextContent( BROWSER + suiteName + ".vZome\n" );
            stdOut .setTextContent( EXPORTS + suiteName + ".vZome\n" );
            testSuite .appendChild( stdOut );
            testSuite .appendChild( stderr );
            testSuites .appendChild( testSuite );
        }
        else
            super .actOnFile( file, extension, visitor );
    }

    @Override
    public boolean fileContainsLinks( String ext )
    {
        // This appears to make a file like "foo.testsuite" act like a collection
        //  of other files.  That is a side effect.  The main purpose is to make
        //  "testsuite" (the name is the extension) act like links.
        return ext .equals( "testsuite" ) || ext .equals( "vZome-files" );
    }

    class StreamGobbler extends Thread
    {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type)
        {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null)
                    System.out.println(type + ">" + line);    
            } catch (IOException ioe)
            {
                ioe.printStackTrace();  
            }
        }
    }
}
