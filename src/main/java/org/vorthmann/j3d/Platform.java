/*
 * Created on Mar 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.vorthmann.j3d;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessControlException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author scott
 */
public class Platform
{
	static boolean isJFreeD = false;
	
	static boolean isMac = false;
	
	static boolean isWindows = false;
    
    static boolean is14 = true;
    
    static boolean hasSingleInstanceService = false;
    
    static boolean isWebStart = false;

	static
    {
        Logger logger = Logger .getLogger( "org.vorthmann.vzome" );
        try {
            String os = System .getProperty( "org.vorthmann.zome.Platform" );
            if ( os != null && os .startsWith( "JFreeD" ) )
                isJFreeD = true;
            os = System .getProperty( "os.name" );
            logger .fine( "os.name: " + os );
            if ( os != null && os .startsWith( "Mac" ) )
                isMac = true;
            else if ( os != null && os .startsWith( "Win" ) )
                isWindows = true;
            os = System .getProperty( "java.specification.version" );
            logger .fine( "java.specification.version: " + os );
            if ( os != null && os .startsWith( "1.3" ) )
                is14 = false;
        } catch ( AccessControlException e ) {
            // must be running in JNLP without signing
            logger .fine( "running in JNLP without signing" );
            isWebStart = true;
        }
	}
    
    public static void setWebStart()
    {
        isWebStart = true;
        try {
            Class .forName( "javax.jnlp.SingleInstanceService" );
            System .out .println( "found Java Web Start SingleInstanceService" );
            hasSingleInstanceService = true;
        } catch ( ClassNotFoundException e ) {
            Logger .getLogger( "org.vorthmann.vzome" ) .warning( "no Java Web Start SingleInstanceService" );
        }
    }

    public static void setupEventListener( final org.vorthmann.interfaces.IHookMacFinderUI ui )
    {
        if ( isMac )
            try {
                // org.vorthmann.extensions.MacAdapter.setupListener( ui );
                Class adapterClass = Class .forName( "org.vorthmann.extensions.MacAdapter" );
                Class iHookMacFinderUIClass = org.vorthmann.interfaces.IHookMacFinderUI.class;
                System .out .println( "found MacAdapter for Finder integration" );
                Method setupMethod = adapterClass .getDeclaredMethod( "setupListener", iHookMacFinderUIClass );
                System .out .println( "found MacAdapter.setupListener method" );
                setupMethod .invoke( adapterClass, ui );
                System .out .println( "invoked MacAdapter.setupListener() via reflection" );
            } catch ( ClassNotFoundException e ) {
                Log( e, "found no MacAdapter class" );
            } catch ( NoSuchMethodException e ) {
                Log( e, "found no MacAdapter.setupListener() method" );
            } catch ( IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e ) {
                Log( e, "exception invoking MacAdapter.setupListener() method" );
            } catch ( Exception e ) {
                Log( e, "unexpected exception invoking MacAdapter.setupListener() method" );
            }
    }
    
     private static void Log(Exception ex, String msg)
    {
        ex.printStackTrace();
        Logger.getLogger( "org.vorthmann.vzome" ).log(Level.WARNING, "{0}\nStack Trace sent to stderr.\n{1}]n", ex.toString());
        if(msg != null) {
            Logger.getLogger( "org.vorthmann.vzome" ).log(Level.WARNING, msg);
        }
    }
    
    public static boolean isMac()
    {
        return isMac;
    }
    
    public static boolean isWindows()
    {
        return isWindows;
    }
    
    public static String logsPath()
    {
        return isWindows()? "vZomeLogs" : isMac()? "Library/Logs/vZome" : "vZomeLogs";
    }
    
    public static File logsFolder()
    {
        return new File( System.getProperty( "user.home" ), logsPath() );
    }
    	
	public static Object newInstance( Class clazz )
	{
	    try
        {
            if ( is14 ) {
                String className = clazz .getName() + "14";
                try {
					clazz = Class .forName( className );
                }
                catch ( Exception e )
                {
                	// just use the original tryClass
                }
            }
            Object result = clazz .newInstance();
            return result;
        } catch (Exception e)
        {
            e .printStackTrace();
            return null;
        }
	}
		
	public static boolean mustRepaint(){
		return isJFreeD;
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
				else if ( extension .equals( "zomod" ) )
					creator = 0x5A4D4F44 /*ZMOD*/;
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
                Class fmclass = Class.forName( "com.apple.eio.FileManager" );
                
				if ( creator != 0 ) {
                    Method method = fmclass .getMethod( "setFileCreator", new Class[] { String.class, int.class } );
                    method .invoke( fmclass, new Object[]{ fname, new Integer(creator) } );
				}
				if ( type != 0 ) {
                    Method method = fmclass .getMethod( "setFileType", new Class[] { String.class, int.class } );
                    method .invoke( fmclass, new Object[]{ fname, new Integer(type) } );
                }
			}
			catch ( Exception e) {
				// no big deal
				e .printStackTrace();
			}            
		}
	}
    	
//	public static GraphicsConfiguration getGraphicsConfig(){
//		if ( isJFreeD ){
//			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
//			GraphicsDevice gd = genv.getScreenDevices()[0];
//			return gd.getDefaultConfiguration();
//		}
//		else
//			return null;//SimpleUniverse.getPreferredConfiguration();
//	}
    
    
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
    
    public static float getDirectionalBrightness(){
        if ( isJFreeD )
            return 0.87f;
        else
            return 0.67f;
    } 
    public static float getAmbientBrightness(){
        if ( isJFreeD )
            return 0.07f;
        else
            return 0.54f;
    }
}
