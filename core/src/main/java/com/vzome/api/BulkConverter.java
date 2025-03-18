package com.vzome.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;

/**
 * Modified from {@link com.vzome.core.regression.FileSystemVisitor2}
 */
public class BulkConverter
{
    public static final File INITIAL_WORKING_DIR = new File( System .getProperty( "user.dir" ) );
    
    public static class Actor
    {
        private Application app;

        private Exporter exporter;

        private Path baseFolder;

        private String format, suffix;

        public Actor( String baseFolder, String format, String suffix )
        {
            super();
            this .baseFolder = new File( baseFolder ) .toPath();
            this .format = format;
            this .suffix = suffix;
        }

        private void convertAll()
        {
            app = new Application( new Command.FailureChannel()
            {
                @Override
                public void reportFailure( Failure f )
                {
                    System.err .println( f .toString() );
                }
            });
            exporter = app .getExporter( this.format );

            if ( ! Files.exists( baseFolder, new LinkOption[]{} ) )
                System .err. println( "directory does not exist: " + baseFolder .toString() );
            else {
                try {
                    actOnFileOrFolder( baseFolder .toFile(), new BulkConverter() );
                } catch ( Exception ioe ) {
                    System .err. println( "scan failed: " + ioe .getLocalizedMessage() );
                }
            }
            app = null;
        }

        public static File getFileFromSystemProperty( String propName )
        {
            String prop = System .getProperty( propName );
            if ( prop == null )
                return null;
            if ( prop .startsWith( "/" ) )
                return new File( prop );
            else
                return new File( INITIAL_WORKING_DIR, prop );
        }
        
        public void actOnFileOrFolder( File dirOrFile, BulkConverter visitor ) throws Exception
        {
            visitor .visitFileOrFolder( dirOrFile, this );
        }
        
        public void actOnFile( File file, String extension, BulkConverter visitor ) throws Exception
        {
            if ( ! "vZome" .equals( extension ) )
                return;

            File classFolder = file .getParentFile();
            String testName = file .getName();
            testName = testName .substring( 0, testName .length() - 6 ); // strip ".vZome"

            InputStream bytes = new FileInputStream( file );
            com.vzome.api.Document doc = app .loadDocument( bytes );

            File outFile = new File( classFolder, testName + '.' + this.suffix );

            PrintWriter output = new PrintWriter( outFile );
            try {
                exporter .doExport( doc, output, 1080, 1920 );
            } finally {
                output .close();
            }
        }
        
        public void actOnFolder( File folder, BulkConverter visitor ) throws Exception
        {
            visitor .visitFolder( folder, this );
        }
    }

    public void visitFileOrFolder( File dirOrFile, Actor actor ) throws Exception
    {
        if ( dirOrFile .isDirectory() )
            actor .actOnFolder( dirOrFile, this );
        else 
            actor .actOnFile( dirOrFile, getFileExtension( dirOrFile ), this );
    }

    public void visitFolder( File directory, Actor actor ) throws Exception
    {
      String[] files = directory .list();
      if (files != null) {
          for (String fileName : files) {
              actor .actOnFileOrFolder( new File( directory, fileName ), this );
          }
      }
    }
    
    public static String getFileExtension( File file )
    {
        String ext = file .getAbsolutePath();
        return ext .substring( ext .lastIndexOf( '.' ) + 1 );
    }

    public static String readFile( File dirOrFile ) throws IOException
    {
        BufferedReader in = new BufferedReader( new FileReader( dirOrFile ) );
        StringWriter out = new StringWriter();
        char[] buffer = new char [ 2048 ];
        int chars;
        while ( (chars = in .read( buffer, 0, buffer .length )) > 0 ) {
            out .write( buffer, 0, chars );
        }
        out .close();
        in .close();
        return out.toString();
    }

    public static void main( String[] args )
    {
        try {
            BulkConverter.Actor actor = new BulkConverter.Actor( args[0], args[1], args[2] );
            actor .convertAll();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}

