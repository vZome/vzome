
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.j3d;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
import com.apple.eawt.AboutHandler;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
*/

/**
 * @author Scott Vorthmann
 * 
 * DJH - Modified original code to use reflection so we can build on Windows where com.apple.eawt is unavailable, even though this code will only ever run on a Mac.
 *     - Added try/catch blocks inside of all of the anonymous "handler" objects since they may be run in another thread.
 *
 */
public class MacAdapter
{
    static void setupListener( final Platform.UI ui )
    {
        try {
            /* 
            // Original Code
            com.apple.eawt.Application appl = com.apple.eawt.Application .getApplication();
            */
            // Via Reflection
            Class applicationClass = Class.forName( "com.apple.eawt.Application" );
            Object appl = applicationClass.getDeclaredMethod( "getApplication" ).invoke( (Object) null );

                        
            /* 
            // Original Code
            appl .setOpenFileHandler( new OpenFilesHandler()
            {
                public void openFiles( OpenFilesEvent ofe )
                {
                    for (Iterator iterator = ofe .getFiles() .iterator(); iterator.hasNext(); ) {
                        File file = (File) iterator.next();
                        ui .openFile( file );
                    }
                }
            } );
            */
            // Via Reflection
            applicationClass.getDeclaredMethod( "setOpenFileHandler", Class.forName( "com.apple.eawt.OpenFilesHandler" ) )
                .invoke(appl, new /* OpenFilesHandler */ Object()
                    {
                        public void openFiles( /* OpenFilesEvent */ Object ofe )
                        {
                            try {
                                List fileList = (List) Class.forName( "com.apple.eawt.AppEvent.OpenFilesEvent" ).getDeclaredMethod( "getFiles" )
                                        .invoke(ofe, (Object) null);
                                for (Iterator iterator = fileList.iterator(); iterator.hasNext(); ) 
                                {
                                    File file = (File) iterator.next();
                                    ui .openFile( file );
                                }
                            } catch(ClassNotFoundException ex) {
                                Log(ex);
                            } catch (NoSuchMethodException ex) {
                                Log(ex);
                            } catch (SecurityException ex) {
                                Log(ex);
                            } catch (IllegalAccessException ex) {
                                Log(ex);
                            } catch (IllegalArgumentException ex) {
                                Log(ex);
                            } catch (InvocationTargetException ex) {
                                Log(ex);
                            } catch (Exception ex) {
                                Log(ex);
                            }
                        }
                    } );

            
            /*
            // Original Code
            appl .setAboutHandler( new AboutHandler()
            {
                public void handleAbout( AboutEvent about )
                {
                    ui .about();
                }
            } );
            */
            // Via Reflection
            applicationClass.getDeclaredMethod( "setAboutHandler", Class.forName( "com.apple.eawt.AboutHandler" ) )
                .invoke(appl, new /* AboutHandler */ Object()
                    {
                        public void handleAbout( /* AboutEvent */ Object aboutUnused )
                        {
                            try {
                                ui.about();
                            } catch ( Exception ex ) {
                                Log(ex);
                            }
                        }
                    } );

            
            /*
            // Original Code
            appl .setQuitHandler( new QuitHandler()
            {
                public void handleQuitRequestWith( QuitEvent qe, QuitResponse qr )
                {
                    if ( ui .quit() )
                        qr .performQuit();
                    else
                        qr .cancelQuit();

                }
            } );
            */
            // Via Reflection
            applicationClass.getDeclaredMethod( "setQuitHandler", Class.forName( "com.apple.eawt.QuitHandler" ) )
                .invoke(appl, new /* QuitHandler */ Object()
                    {
                        public void handleQuitRequestWith( /* QuitEvent */ Object qeUnused, /* QuitResponse */ Object qr )
                        {
                            try {
                                String methodName = ui .quit() ? "performQuit" : "cancelQuit";
                                Class.forName( "com.apple.eawt.QuitResponse" ).getDeclaredMethod( methodName ).invoke(qr, (Object) null);
                            } catch(ClassNotFoundException ex) {
                                Log(ex);
                            } catch (NoSuchMethodException ex) {
                                Log(ex);
                            } catch (SecurityException ex) {
                                Log(ex);
                            } catch (IllegalAccessException ex) {
                                Log(ex);
                            } catch (IllegalArgumentException ex) {
                                Log(ex);
                            } catch (InvocationTargetException ex) {
                                Log(ex);
                            } catch (Exception ex) {
                                Log(ex);
                            }
                        }
                    } );
            
        } catch ( ClassNotFoundException ex ) {
            Log(ex);
        } catch (NoSuchMethodException ex) {
            Log(ex);
        } catch (SecurityException ex) {
            Log(ex);
        } catch (IllegalAccessException ex) {
            Log(ex);
        } catch (IllegalArgumentException ex) {
            Log(ex);
        } catch (InvocationTargetException ex) {
            Log(ex);
        } catch ( Exception ex ) {
            Log(ex);
        }
    }
    
    private static void Log(Exception ex)
    {
        ex.printStackTrace();
        Logger.getLogger( "org.vorthmann.vzome" ).log(Level.WARNING, "{0}\nStack Trace sent to stderr.\n", ex.toString());
    }
}
