
package org.vorthmann.j3d;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author scott
 */
public class Platform
{	
	static boolean isMac = false;
	
	static boolean isWindows = false;
    
    private static final Logger logger = Logger .getLogger( "org.vorthmann.vzome" );
   
	static
    {
        try {
            String os = System .getProperty( "os.name" );
            logger .log(Level.FINE, "os.name: {0}", os);
			if (os != null && os.startsWith("Mac")) {
				isMac = true;
			} else {
				// "vzome.glcanvas.rescaling" is used 
				// in JoglFactory.createRenderingViewer()
                // and JoglRenderingViewer.pickRay()
                // and DocumentFrame constructor
				System.setProperty("vzome.glcanvas.rescaling", "true");
				if (os != null && os.startsWith("Win")) {
					isWindows = true;
				}
			}
            os = System .getProperty( "java.specification.version" );
            logger .log(Level.FINE, "java.specification.version: {0}", os);
        } catch ( SecurityException e ) {
            // must be running in JNLP without signing
            logger .fine( "running in JNLP without signing" );
        }
	}
    
    public static String logsPath()
    {
        return isWindows? "vZomeLogs" : isMac? "Library/Logs/vZome" : "vZomeLogs";
    }
    
    public static Path logsFolder()
    {
        return Paths .get( System.getProperty( "user.home" ), logsPath() );
    }
	
	public static void openApplication( File file )
	{
		if ( isMac )
			try {
				String path = file .getAbsolutePath();
				Runtime .getRuntime() .exec( "open " + path );
			} catch ( IOException e ) {
				System .err .println( "Runtime.exec() failed on " + file .getAbsolutePath() );
				e .printStackTrace();
			}
	}

	public static void setFileType( File file, String extension )
	{
		if ( isMac ){
			try {
				String fname = file .getCanonicalPath();
				int type = 0x54455854 /*TEXT*/;
				int creator = 0x74747874 /* ttxt, TextEdit */;
				if ( extension .equals( "pov" ) )
					creator = 0x504F5633 /*POV3*/;
//				else if ( extension .equals( "zomod" ) )
//					creator = 0x5A4D4F44 /*ZMOD*/;
				else if ( extension .equals( "jpg" ) ){
					type = 0x4A504547 /*JPEG*/;
					creator = 0; // should be "ogle"
				}
				else if ( extension .equals( "png" ) ){
					type = 0x504E4766 /*PNGf*/;
					creator = 0;
				}
				else if ( extension .equals( "bmp" ) ){
					type = 0x424D5066 /*BMPf*/;
					creator = 0;
				}
                else if ( extension .equals( "pdf" ) ){
                    type = 0x50444620 /*PDF */;
                    creator = 0;
                }
                else if ( extension .equals( "tiff" ) ){
                    type = 0x54494646 /*TIFF*/;
                    creator = 0;
                }
				else if ( extension .equals( "vZome" ) ){
					creator = 0x765A6F6D /*vZom*/;
					type = 0;
				}
                Class<?> fmclass = Class.forName( "com.apple.eio.FileManager" );
                
				if ( creator != 0 ) {
                    Method method = fmclass .getMethod( "setFileCreator", String.class, int.class );
                    method .invoke( fmclass, fname, creator );
				}
				if ( type != 0 ) {
                    Method method = fmclass .getMethod( "setFileType", String.class, int.class );
                    method .invoke( fmclass, fname, type );
                }
			}
			catch ( Exception e) {
				// no big deal
				e .printStackTrace();
			}            
		}
	}    
    
    public static File getPreferencesFolder()
    {
        if ( isMac )
            return new File( System.getProperty( "user.home" ), "Library/Preferences/vZome" );
        else
            return new File( System.getProperty( "user.home" ), "vZome-Preferences" );
    }
	
	public static int getKeyModifierMask()
	{
		if ( isMac )
			return ActionEvent.META_MASK;
		else
			return ActionEvent.CTRL_MASK;
	}
}
