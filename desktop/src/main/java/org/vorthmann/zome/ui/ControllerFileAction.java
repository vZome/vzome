
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

import com.vzome.desktop.api.Controller;

public class ControllerFileAction implements ActionListener
{
    private final Controller mController;
    private final String mCommand;
    private final FileDialog mFileChooser;
    private final boolean mOpening;
    private final String mExtension;
    private final String mTitle;
    private boolean headless;
    
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );

    public void setHeadless( boolean headless )
    {
        this.headless = headless;
    }

    public ControllerFileAction( FileDialog chooser, boolean open, String command, String extension, Controller controller )
    {
        mFileChooser = chooser;
        mOpening = open;
        mExtension = extension;
        mController = controller;
        mCommand = command;
        mTitle = controller .getProperty( "file-dialog-title." + command );
        this.headless = false;
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
    public void actionPerformed( ActionEvent actionEvent )
    {
        String title = ( mTitle != null )? mTitle : ( mOpening? "Choose a file" : "Save a file" );
        mFileChooser .setTitle( title );
        mFileChooser .setMode( mOpening? FileDialog .LOAD : FileDialog .SAVE );

        String directory = this .mController .getProperty( this .getDirectoryProperty() );
        if ( directory == null )
            directory = this .mController .getProperty( this .mOpening? "last-open-directory.vZome" : "last-save-directory.vZome" );

        String fileName = this .mController .getProperty( "window.file" );
        if ( fileName != null && ! this .mOpening ) {
            Path filePath = new File( fileName ) .toPath();
            if ( this.headless )
                directory = filePath .getParent() .toString();
            fileName = filePath .getFileName() .toString();
            int index = fileName .lastIndexOf( "." );
            if ( index > 0 )
            {
                fileName = fileName .substring( 0, index );
            }
            fileName = fileName + "." + mExtension;
            mFileChooser .setFile( fileName );
        }

        mFileChooser .setDirectory( directory );

        if ( ! this.headless ) {
            mFileChooser .setVisible( true );
            fileName = mFileChooser .getFile();
        }
        if ( fileName == null )
        {
            logger .info( "file action cancelled" );
            return;
        }
        if ( ! mOpening ) {
	        // If fileName already has the correct extension, be sure it's the same case as mExtension.
	        // This doesn't matter on an OS that's not case sensitive like Mac or Windows, but it would on Linux.
        	// Mainly, this avoids the situation where a fileName like "foo.vzome" 
        	// is incorrectly given an additional extension that differs only in case, such as "foo.vzome.vZome".
        	//
        	// Use "while" instead of just an "if", so that "foo.vzome.vzome.vzome.vzome" 
        	// will always be reduced to "foo.vZome".
        	// This only affects saving files, not opening existing files with repeated extensions 
        	// because mOpening is false at this point.
	        while (fileName.toLowerCase() .endsWith( "." + mExtension.toLowerCase() ) ) {
	        	// strip off the "." and any potentially wrong cased extension
	        	fileName = fileName.substring(0, fileName.length() - 1 - mExtension.length());
	        }
	        // Append the correct extension whether it was missing or we stripped it off to ensure proper case 
	        fileName = fileName + "." + mExtension;
        }
        directory = mFileChooser .getDirectory();  // cache this for convenience, for the next use
        if ( directory != null )
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
