
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.vorthmann.ui.Controller;

public class ControllerFileAction implements ActionListener
{
    private final Controller mController;
    private final String mCommand;
    private final FileDialog mFileChooser;
    private final boolean mOpening;
    private final String mExtension;
    private final String mTitle;
    
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );

    public ControllerFileAction( FileDialog chooser, boolean open, String command, String extension, Controller controller )
    {
        mFileChooser = chooser;
        mOpening = open;
        mExtension = extension;
        mController = controller;
        mCommand = command;
        mTitle = controller .getProperty( "file-dialog-title." + command );
    }
    
    public static String readTextFromFile( File file )
    {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = input .read( buf, 0, 1024 )) > 0 )
                    out .write( buf, 0, num );
            return new String( out .toByteArray() );
        }
        catch (IOException exc)
        {
            logger .log( Level.WARNING, "unable to read file " + file .getAbsolutePath(), exc );
            return null;
        }
        finally
        {
            if ( input != null )
                try {
                    input .close();
                } catch ( IOException e ) {
                    logger .log( Level.SEVERE, "unable to close input file " + file .getAbsolutePath(), e );
                }
        }
    }
    
    private String getDirectoryProperty()
    {
        return ( this .mOpening? "last-open-directory." : "last-save-directory." ) + this .mExtension;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed( ActionEvent arg0 )
    {
        String title = ( mTitle != null )? mTitle : ( mOpening? "Choose a file" : "Save a file" );
        mFileChooser .setTitle( title );
        mFileChooser .setMode( mOpening? FileDialog .LOAD : FileDialog .SAVE );

        String directory = this .mController .getProperty( this .getDirectoryProperty() );
        if ( directory == null )
            directory = this .mController .getProperty( this .mOpening? "last-open-directory.vZome" : "last-save-directory.vZome" );
        mFileChooser .setDirectory( directory );

        String fileName = this .mController .getProperty( "window.file" );
        if ( fileName != null && ! this .mOpening ) {
            Path filePath = new File( fileName ) .toPath();
            fileName = filePath .getFileName() .toString();
            int index = fileName .lastIndexOf( "." );
            if ( index > 0 )
            {
                fileName = fileName .substring( 0, index );
            }
            fileName = fileName + "." + mExtension;
            mFileChooser .setFile( fileName );
        }

        mFileChooser .setVisible( true );
        fileName = mFileChooser .getFile();
        if ( fileName == null )
        {
            logger .info( "file action cancelled" );
            return;
        }
        if ( ! mOpening && ! fileName .endsWith( "." + mExtension ) ) {
            fileName = fileName + "." + mExtension;
        }
        directory = mFileChooser .getDirectory();  // cache this for convenience, for the next use
        this .mController .setProperty( this .getDirectoryProperty(), directory ); // set it for other windows
        File file = new File( directory, fileName );
        try {
            mController .doFileAction( mCommand, file );
        }
        catch ( Exception exc ) {
            logger .log( Level.SEVERE, exc .getMessage(), exc );
            exc.printStackTrace();
            JOptionPane .showMessageDialog( mFileChooser .getOwner(),
                    exc .getMessage(),
                    "command failure", JOptionPane.ERROR_MESSAGE );
        }
    }
}
