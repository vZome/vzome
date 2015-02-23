
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.j3d;

import java.io.File;
import java.util.Iterator;

import com.apple.eawt.AboutHandler;
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
public class MacAdapter
{
    static void setupListener( final org.vorthmann.interfaces.IHookMacFinderUI ui )
    {
        com.apple.eawt.Application appl = com.apple.eawt.Application .getApplication();
        
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
    }
}
