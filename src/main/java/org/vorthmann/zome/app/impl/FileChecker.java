
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.LoggingErrorChannel;
import org.vorthmann.ui.Controller.ErrorChannel;

class FileChecker implements ErrorChannel, Runnable
{
    static Logger logger = Logger .getLogger( "org.vorthmann.zome.filechecker" );

    /**
     * 
     */
    private final DefaultApplication controller;
    private final Controller .ErrorChannel errors;
    private final File root;
    
    public FileChecker( DefaultApplication defaultApplication, File file )
    {
        controller = defaultApplication;
        errors = new LoggingErrorChannel( logger );
        this.root = file;
    }
    
    private boolean errorLogged = false;
    
    public void reset()
    {
        errorLogged = false;
    }
            
    public void reportError( String errorCode, Object[] arguments )
    {
        errors .reportError( errorCode, arguments );
        errorLogged = true;
    }
    
    public boolean errorWasLogged()
    {
        return errorLogged;
    }
    
    private int tests = 0, failures = 0;

    public void run()
    {
        checkAllFiles( root );
        logger .severe( "RESULTS:    " + failures + " failures in " + tests + " tests." );
    }

    private void checkAllFiles( final File folderOrFile )
    {
        if ( folderOrFile .isDirectory() )
        {
            if ( ".svn" .equals( folderOrFile .getName() ) )
                return;
            logger .info( "--------  folder     " + folderOrFile .getAbsolutePath() );
            File[] files = folderOrFile .listFiles();
            for ( int i = 0; i < files.length; i++ )
                checkAllFiles( files[ i ] );
        }
        else if ( folderOrFile .getName() .endsWith( ".vZome-files" ) )
        {
            try {
                logger .info( "++++++++ READING FILE LIST " + folderOrFile .getCanonicalPath() );
                BufferedReader lines = new BufferedReader( new InputStreamReader( new FileInputStream( folderOrFile ) ) );
                do {
                    String line = lines .readLine();
                    if ( line == null )
                    {
                        lines .close();
                        return;
                    }
                    line = line .trim();
                    if ( line .length() == 0 || line .startsWith( "#" ) || line .startsWith( "//" ) )
                        continue;
                    File lineFile = new File( folderOrFile .getParentFile(), line );
                    checkAllFiles( lineFile );
                } while ( true );
            } catch ( Throwable e ) {
                reportError( Controller.UNKNOWN_ERROR_CODE, new Object[]{ e } );
            }
        }
        else if ( folderOrFile .getName() .endsWith( ".vZome" ) )
        {
            try {
                ++ tests;
                InputStream bytes = new FileInputStream( folderOrFile );
                logger .fine( "[[[[[[[ OPENING " + folderOrFile .getCanonicalPath() );
                EditorController model = (EditorController) controller.loadController( bytes, DefaultApplication.noRenderProps );
                model .setErrorChannel( this );
                model .doAction( "finish.load", null );
            } catch ( Throwable e ) {
                reportError( Controller.UNKNOWN_ERROR_CODE, new Object[]{ e } );
            }
            if ( errorWasLogged() )
            {
                ++ failures;
                logger .severe( "!@#$%^&*!@#$%^&* FAILED TO OPEN   " + folderOrFile .getAbsolutePath() );
            }
            else
                logger .fine(   "]]]]]]] SUCCESSFULLY OPENED " + folderOrFile .getAbsolutePath() );
            reset();
        }
    }

    public void clearError()
    {}
}