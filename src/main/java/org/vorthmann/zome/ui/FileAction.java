
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.vorthmann.j3d.Platform;

/**
 * @author Scott Vorthmann
 *
 */
public abstract class FileAction implements ActionListener
{
    protected final FileDialog mFileChooser;
    protected final boolean mOpening;
    protected final String mExtension;
    protected final String mTitle;
    
    private static Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );
    
    public FileAction( FileDialog chooser, boolean open, String extension, String title )
    {
        mFileChooser = chooser;
        mOpening = open;
        mExtension = extension;
        mTitle = title;
    }
    
    protected abstract void actOnFile( File file ) throws Exception;
    
    protected boolean actOrError( File file )
    {
        try {
            actOnFile( file );
            return true;
        }
        catch ( Exception exc ) {
            exc.printStackTrace();
            JOptionPane .showMessageDialog( mFileChooser .getOwner(),
                    exc .getMessage(),
                    "command failure", JOptionPane.ERROR_MESSAGE );
            return false;
        }}

    protected void openApplication( File file )
    {
		Platform .openApplication( file );
    }
    
    protected static String readTextFromFile( File file )
    {
        try {
            InputStream input = new FileInputStream(file);
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
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent arg0 )
    {
		mFileChooser .setTitle( mTitle );
		mFileChooser .setMode( mOpening? FileDialog .LOAD : FileDialog .SAVE );
		mFileChooser .setVisible( true );
		mFileChooser .setFilenameFilter( null );
		String fileName = mFileChooser .getFile();
		if ( fileName == null )
		{
		    logger .info( "file action cancelled" );
		    return;
		}
        if ( ! mOpening && ! fileName .endsWith( "." + mExtension ) )
            fileName = fileName + "." + mExtension;
		File file = new File( mFileChooser .getDirectory(), fileName );
        if ( ! actOrError( file ) )
            return;
        if ( ! mOpening ) {
            Platform .setFileType( file, mExtension );
            openApplication( file );
        }
    }
	
}
