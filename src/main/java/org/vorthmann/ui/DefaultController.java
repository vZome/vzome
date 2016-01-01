

package org.vorthmann.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import org.vorthmann.j3d.MouseTool;

public class DefaultController implements Controller
{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
    
    protected ErrorChannel mErrors;
    
    protected Controller mNextController;
    
    protected PropertyChangeSupport properties()
    {
        return this.pcs;
    }

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

    public void addPropertyListener( PropertyChangeListener listener )
    {
        pcs .addPropertyChangeListener( listener );
    }

    public void removePropertyListener( PropertyChangeListener listener )
    {
        pcs .removePropertyChangeListener( listener );
    }

    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( mNextController != null )
            mNextController .doAction( action, e );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ action } );
    }

    public void doFileAction( String command, File file )
    {
        if ( mNextController != null )
            mNextController .doFileAction( command, file );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ command } );
    }

    public void doScriptAction( String command, String script )
    {
        if ( mNextController != null )
            mNextController .doScriptAction( command, script );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ command } );
    }

    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        if ( mNextController != null )
            return mNextController .enableContextualCommands( menu, e );
        else
            return new boolean[0];
    }

    public String[] getCommandList( String listName )
    {
        if ( mNextController != null )
            return mNextController .getCommandList( listName );
        else
            return new String[0];
    }

    public boolean[] getCommandListDefaultStates( String string )
    {
        return null;
    }

    public String getProperty( String string )
    {
        if ( mNextController != null )
            return mNextController .getProperty( string );
        return null;
    }
    
    public boolean propertyIsTrue( String propName )
    {
        return "true" .equals( getProperty( propName ) );
    }

    public Controller getSubController( String string )
    {
        if ( mNextController != null )
            return mNextController .getSubController( string );
        return null;
    }

    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( mNextController != null )
            mNextController .repaintGraphics( panelName, graphics, size );
    }

    public void setErrorChannel( ErrorChannel errors )
    {
        mErrors = errors;
    }

    public void setProperty( String cmd, Object value )
    {
        if ( mNextController != null )
            mNextController .setProperty( cmd, value );
    }

    public void setNextController( Controller controller )
    {
        mNextController = controller;
        mNextController .addPropertyListener( new PropertyChangeListener()
        {
            // multicast prop changes down the tree of controllers... watch out for loops!
            public void propertyChange( PropertyChangeEvent event )
            {
                properties() .firePropertyChange( event );
            }
        } );
        if ( mNextController instanceof DefaultController )
            mErrors = ((DefaultController) mNextController) .mErrors;
    }

    public MouseTool getMouseTool()
    {
        return null;
    }

    public boolean userHasEntitlement( String propName )
    {
        if ( mNextController != null )
            return mNextController .userHasEntitlement( propName );
        return false;
    }

}
