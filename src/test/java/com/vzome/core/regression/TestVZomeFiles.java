

package com.vzome.core.regression;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.Text;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;

import com.vzome.api.Application;

//import org.vorthmann.ui.Controller;
//import org.vorthmann.zome.app.impl.DefaultApplication;
//import org.vorthmann.zome.app.impl.EditorController;



/**
 * 
 * @author Scott Vorthmann
 */
public class TestVZomeFiles extends FileSystemVisitor2 .Actor
{
    public static void main( String[] args )
    {
        Element results = new TestVZomeFiles( args[0], args[1] ) .collectResults();

        FileOutputStream out;
        try {
            out = new FileOutputStream( args[2] );
            Serializer serializer = new Serializer( out );
            serializer .setLineSeparator( System .getProperty( "line.separator" ) );
// don't spoil the whitespace!
//            serializer .setIndent( 2 );
//            serializer .setMaxLength( 120 );
            serializer .write( new Document( results ) );
            out .close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        System.exit( 0 );
    }

    public TestVZomeFiles( String baseFolder, String testPath )
    {
        super();
        this .baseFolder = new File( baseFolder ) .toPath();
        this .testPath = testPath;
    }
	
	private Application app;
	
	private Path baseFolder;

	private String testPath;
	
	private Element testSuites;
	
	private Element testCase;
	
    private final static String BROWSER = "http://imac:8000/trac/browser/vZome2/attachments";
    
    private final static String EXPORTS = "http://imac:8000/trac/export/HEAD/vZome2/attachments";
    
	private Element collectResults()
	{
        testSuites = new Element( "testsuites" );
		
		app = new Application( new Command.FailureChannel()
        {
            @Override
            public void reportFailure( Failure f )
            {
                Element failure = new Element( "failure" );
                failure .addAttribute( new Attribute( "type", "user.command.error" ) );
                failure .addAttribute( new Attribute( "message", f .getMessage() ) );
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

		public void visitFolder( File directory, Actor actor ) throws IOException
		{
			String[] files = directory .list();
			if (files != null)
				for ( int i = 0; i < files .length; i++ )
					if ( files[i] .toLowerCase() .endsWith( ".testsuite" ) ) {
						File linksFile = new File( directory, files[i] );
						if ( linksFile .isDirectory() )
							// don't act on it, that would make another layer of suite
							super .visitFolder( linksFile, actor );
						else 
							actor .actOnFile( linksFile, "testsuite", this );
						return;
					}
			super .visitFolder( directory, actor );
		}
	}	
	
	public void actOnFolder( File folder, FileSystemVisitor2 visitor ) throws IOException
	{
        if ( ".svn" .equals( folder .getName() ) )
            return;
        super .actOnFolder( folder, visitor );
	}
	
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
            testCase = new Element( "testcase" );
            testCase .addAttribute( new Attribute( "classname", pkgName + "." + className ) );
            String testName = file .getName();
            testName = testName .substring( 0, testName .length() - 6 ); // strip ".vZome"
            String suiteName = pkgName + "/" + className + "/" + testName;
            System .out .println( suiteName );
            testCase .addAttribute( new Attribute( "name", testName ) );
            
            Logger logger = Logger .getLogger( "" );
            final Element stderr = new Element( "system-err" );
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Handler handler = new StreamHandler( bos, new SimpleFormatter() );
            handler .setLevel( Level .INFO );
            logger .addHandler( handler );
            
            try {
                InputStream bytes = new FileInputStream( file );
                com.vzome.api.Document doc = app .loadDocument( bytes );
            } catch ( Exception e ) {
                Element error = new Element( "error" );
                error .addAttribute( new Attribute( "type", "finish.load.exception" ) );
                error .addAttribute( new Attribute( "message", e .getMessage() ) );
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                e .printStackTrace( new PrintStream( out ) );
                error .appendChild( new Text( new String( out .toByteArray() ) ) );
                testCase .appendChild( error );
            } finally
            {
                handler .close();
                String logText = new String( bos .toByteArray() );
                stderr .appendChild( logText );
                logger .removeHandler( handler );
            }
            
            testCase .addAttribute( new Attribute( "time", "0.1" ) );
            Element testSuite = new Element( "testsuite" );
            testSuite .addAttribute( new Attribute( "tests", "1" ) );
            testSuite .addAttribute( new Attribute( "errors", "0" ) );
            testSuite .addAttribute( new Attribute( "failures", "0" ) );
            testSuite .addAttribute( new Attribute( "name", suiteName ) );
            testSuite .addAttribute( new Attribute( "hostname", "Flori-iMac.local" ) );
            testSuite .addAttribute( new Attribute( "time", "4.363" ) );
            testSuite .addAttribute( new Attribute( "timestamp", "2009-10-13T06:58:29" ) );
            testSuite .appendChild( new Element( "properties" ) );
            testSuite .appendChild( testCase );
            Element stdOut = new Element( "system-out" );
            stdOut .appendChild( new Text( BROWSER + suiteName + ".vZome\n" ) );
            stdOut .appendChild( new Text( EXPORTS + suiteName + ".vZome\n" ) );
            testSuite .appendChild( stdOut );
            testSuite .appendChild( stderr );
            testSuites .appendChild( testSuite );
        }
        else
            super .actOnFile( file, extension, visitor );
    }

	public boolean fileContainsLinks( String ext )
	{
	    // This appears to make a file like "foo.testsuite" act like a collection
	    //  of other files.  That is a side effect.  The main purpose is to make
	    //  "testsuite" (the name is the extension) act like links.
		return ext .equals( "testsuite" ) || ext .equals( "vZome-files" );
	}
}