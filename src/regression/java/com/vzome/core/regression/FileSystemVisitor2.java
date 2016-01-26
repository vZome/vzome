
package com.vzome.core.regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;


/**
 * A file system visitor, with support for line-oriented files and
 * files containing relative paths.
 * 
 * This differs from the original FileSystemVisitor by explicitly factoring
 * out the "action" from the traversal, as normally occurs in a visitor pattern
 * by virtue of the "accept" methods appearing on the structure classes and the
 * "visit" methods appearing on the visitor class.
 * 
 * I think this also reverses the implication relationship between fileContainsLinks
 * and fileIsLineOriented.  There may be other differences in detail.
 * 
 */
public class FileSystemVisitor2 {

	
	public static final File INITIAL_WORKING_DIR = new File( System .getProperty( "user.dir" ) );

	
	public static class Actor {
		
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
		
		public void actOnFileOrFolder( File dirOrFile, FileSystemVisitor2 visitor ) throws IOException
		{
			visitor .visitFileOrFolder( dirOrFile, this );
		}
		
		public void actOnFile( File file, String extension, FileSystemVisitor2 visitor ) throws IOException
		{
			if ( fileIsLineOriented( extension ) )
				actOnFileLines( file, extension, visitor );
			else if ( fileHasProperties( extension ) ){
				Properties props = new Properties();
				props .load( new FileInputStream( file ) );
                //trim those values
                for (Map.Entry<Object, Object> entry : props .entrySet()) {
                    String value = (String) entry .getValue();
                    if (value != value .trim())
                        entry .setValue( value .trim() );
                }
				actOnPropertiesFile( file, props, visitor );
			}
		}

		public void actOnPropertiesFile( File file, Properties props, FileSystemVisitor2 visitor ) throws IOException {
		}


		public boolean fileHasProperties( String ext )
		{
			return "properties" .equals( ext );
		}


		public boolean fileIsLineOriented( String ext )
		{
			return fileContainsLinks( ext );
		}


		public void actOnFileLines( File file, String extension, FileSystemVisitor2 visitor ) throws IOException {
			try  {
				boolean links = fileContainsLinks( extension );
				BufferedReader in = new BufferedReader( new FileReader( file ) );
				String line;
				while ( (line = in .readLine()) != null ) {
					line = line .trim();
					if ( line .startsWith( "#" ) )
						continue;
					if ( line .length() == 0 )
						continue;
					if ( links )
					{
						File link = new File( file .getParentFile(), line );
						// this is not a visit because we are simulating visitFolder
						actOnFileOrFolder( link, visitor );
					}
					else
						actOnFileLine( file, line, visitor );
				}
			} catch( IOException ioe ) {
				ioe .printStackTrace();
			}
		}
		
		
		public boolean fileContainsLinks( String ext )
		{
			return false;
		}


		public void actOnFileLine( File file, String line, FileSystemVisitor2 visitor ) throws IOException
		{
		}

		
		public void actOnFolder( File folder, FileSystemVisitor2 visitor ) throws IOException
		{
			visitor .visitFolder( folder, this );
		}
	}

	public void visitFileOrFolder( File dirOrFile, Actor actor ) throws IOException
	{
		if ( dirOrFile .isDirectory() )
			actor .actOnFolder( dirOrFile, this );
		else 
			actor .actOnFile( dirOrFile, getFileExtension( dirOrFile ), this );
	}


	public void visitFolder( File directory, Actor actor ) throws IOException
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


}

