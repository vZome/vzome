

package org.vorthmann.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import org.vorthmann.j3d.MouseTool;

public class DefaultController implements Controller
{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
    
    protected ErrorChannel mErrors;
    
    protected Controller mNextController;

	private ActionListener uiActions;
    
    protected PropertyChangeSupport properties()
    {
        return this.pcs;
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        try {
            doAction( e .getActionCommand(), e );
        } catch ( Exception ex )
        {
            ex .printStackTrace();
            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ ex } );
        }
    }

    @Override
    public final void addPropertyListener( PropertyChangeListener listener )
    {
        pcs .addPropertyChangeListener( listener );
    }

    @Override
    public final void removePropertyListener( PropertyChangeListener listener )
    {
        pcs .removePropertyChangeListener( listener );
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( mNextController != null )
            mNextController .doAction( action, e );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ action } );
    }

    @Override
    public void doFileAction( String command, File file )
    {
        if ( mNextController != null )
            mNextController .doFileAction( command, file );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ command } );
    }

    @Override
    public void doScriptAction( String command, String script )
    {
        if ( mNextController != null )
            mNextController .doScriptAction( command, script );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ command } );
    }

    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        if ( mNextController != null )
            return mNextController .enableContextualCommands( menu, e );
        else
            return new boolean[0];
    }

    @Override
    public String[] getCommandList( String listName )
    {
        if ( mNextController != null )
            return mNextController .getCommandList( listName );
        else
            return new String[0];
    }

    @Override
    public boolean[] getCommandListDefaultStates( String string )
    {
        return null;
    }

    @Override
    public String getProperty( String string )
    {
        if ( mNextController != null )
            return mNextController .getProperty( string );
        return null;
    }
    
    @Override
    public final boolean propertyIsTrue( String propName )
    {
        return "true" .equals( getProperty( propName ) );
    }

	@Override
	public Configuration getConfiguration( String name )
	{
        if ( mNextController != null )
            return mNextController .getConfiguration( name );
        return null;
	}

    @Override
    public Controller getSubController( String string )
    {
        if ( mNextController != null )
            return mNextController .getSubController( string );
        return null;
    }

    @Override
    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( mNextController != null )
            mNextController .repaintGraphics( panelName, graphics, size );
    }

    @Override
    public void setErrorChannel( ErrorChannel errors )
    {
        mErrors = errors;
    }

    @Override
    public void setProperty( String cmd, Object value )
    {
        if ( mNextController != null )
            mNextController .setProperty( cmd, value );
    }

    @Override
    final public void setNextController( Controller controller )
    {
        mNextController = controller;
        mNextController .addPropertyListener(new PropertyChangeListener()
        {
            // multicast prop changes down the tree of controllers... watch out for loops!
            @Override
            public void propertyChange( PropertyChangeEvent event )
            {
                properties() .firePropertyChange( event );
            }
        } );
        if ( mNextController instanceof DefaultController )
            mErrors = ((DefaultController) mNextController) .mErrors;
    }

    @Override
    public MouseTool getMouseTool()
    {
        return null;
    }

    @Override
    public boolean userHasEntitlement( String propName )
    {
        if ( mNextController != null )
            return mNextController .userHasEntitlement( propName );
        return false;
    }

    protected void openApplication( File file )
    {
    	String script = this .getProperty( "export.script" );
    	if ( script != null )
    	{
    		try {
    			Runtime .getRuntime() .exec( script + " " + file .getAbsolutePath(),
    					null, file .getParentFile() );
    		} catch ( IOException e ) {
    			System .err .println( "Runtime.exec() failed on " + file .getAbsolutePath() );
    			e .printStackTrace();
    		}        }
    	else
    		try {
    			if ( Desktop .isDesktopSupported() ) {
    				// DH - The test for file.exists() shouldn't be needed if this method is invoked in the proper sequence
    				// so I think it should be omitted eventually so the exceptions will be thrown
    				// but I'm leaving it here for now as a debugging aid.
    				if( ! file .exists() ) {
    					System .err .println( file .getAbsolutePath() + " does not exist." );
    					//                    return;
    				}
    				Desktop desktop = Desktop .getDesktop();
    				System .err .println( "Opening app for  " + file .getAbsolutePath() + " in thread: " + Thread.currentThread() );
    				desktop .open( file );
    			}
    		} catch ( IOException | IllegalArgumentException e ) {
    			System .err .println( "Desktop.open() failed on " + file .getAbsolutePath() );
    			if ( ! file .exists() ) {
    				System .err .println( "File does not exist." );
    			}
    			e.printStackTrace();
    		}
    }

	@Override
	public Controller asController()
	{
		return this;
	}

	@Override
	public void setUiActionListener( ActionListener listener )
	{
		this .uiActions = listener;
	}

	public ActionListener getUiActionListener()
	{
		return uiActions;
	}
}
