
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.platform.mac;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vorthmann.j3d.Platform;
import org.vorthmann.zome.ui.ApplicationUI;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.QuitEvent;

/**
 * @author Scott Vorthmann
 *
 */
public class Adapter
{
    public static void main( String[] args )
    {
        try {
            // These four lines duplicate ApplicationUI.main()
            String prop = System .getProperty( "user.dir" );
            File workingDir = new File( prop );
            URL codebase = workingDir .toURI() .toURL();
            final ApplicationUI ui = ApplicationUI .initialize( args, codebase );
            
            // Now hook it up to the Finder events
            Application appl = Application .getApplication();
            
            appl .setOpenFileHandler( new OpenFilesHandler()
            {
                public void openFiles( OpenFilesEvent ofe )
                {
                    for (Iterator iterator = ofe .getFiles() .iterator(); iterator.hasNext(); ) {
                        File file = (File) iterator.next();
                        ui .openFile( file, new Properties() );
                    }
                }
            } );
            
            appl .setAboutHandler( new AboutHandler()
            {
                public void handleAbout( AboutEvent about )
                {
                    ui .about();
                }
            } );
            
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

        } catch ( Throwable e ) {
            Logger .getLogger( "com.vzome.platform.mac.Adapter" )
                .log( Level.SEVERE, "problem in main()", e );
        }
    }
}
