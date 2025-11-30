

package com.vzome.desktop.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.desktop.api.Controller;

public class DefaultController implements Controller
{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
    
    protected ErrorChannel mErrors;
    
    protected DefaultController mNextController;
    
    private final Map<String,Controller> subcontrollers = new HashMap<>();

    private String name = "";

    static final Logger logger = Logger.getLogger( "org.vorthmann.zome.controller" );
    
    protected PropertyChangeSupport properggties()
    {
        return this.pcs;
    }

    @Override
    public void actionPerformed( Object source, String action )
    {
        try {
            if ( logger .isLoggable( Level .FINE ) )
                logger.fine( "ACTION: " + getPath() + "||" + action );
            doAction( action );
        } catch ( Exception ex )
        {
            ex .printStackTrace();
            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ ex, action } );
        }
    }

    /**
     * This is only overridden or called in Javascript code, in vZome Online.
     * @param source
     * @param action
     * @param params
     */
    public void paramActionPerformed( Object source, String action, Properties params )
    {
        try {
            if ( logger .isLoggable( Level .FINE ) )
                logger.fine( "PARAM ACTION: " + getPath() + "||" + action );
            doParamAction( action, params );
        } catch ( Exception ex )
        {
            ex .printStackTrace();
            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[]{ ex } );
        }
    }

    private String getPath()
    {
        if ( mNextController == null )
            return this .name;
        else
            return this .mNextController .getPath() + "::" + this .name;
    }

    @Override
    public void addPropertyListener( PropertyChangeListener listener )
    {
        pcs .addPropertyChangeListener( listener );
    }

    @Override
    public void removePropertyListener( PropertyChangeListener listener )
    {
        pcs .removePropertyChangeListener( listener );
    }

    protected void doAction( String action ) throws Exception
    {
        if ( mNextController != null )
            mNextController .doAction( action );
        else
            mErrors .reportError( UNKNOWN_ACTION, new Object[]{ action } );
    }

    /**
     * This is only overridden or called in Javascript code, in vZome Online.
     * @param source
     * @param action
     * @param params
     */
    protected void doParamAction( String action, Properties params ) throws Exception
    {
        if ( mNextController != null )
            mNextController .doParamAction( action, params );
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
    public void addSubController( String name, Controller sub )
    {
        ((DefaultController) sub) .name = name;
        this .subcontrollers .put( name, sub );
        ((DefaultController) sub) .setNextController( this, name );
    }

    @Override
    public Controller getSubController( String name )
    {
        Controller subc = this .subcontrollers .get( name );
        if ( subc != null )
            return subc;
        if ( mNextController != null )
            return mNextController .getSubController( name );
        return null;
    }

    @Override
    public void setErrorChannel( ErrorChannel errors )
    {
        mErrors = errors;
    }

    @Override
    public final void setProperty( String name, Object value )
    {
        if ( logger .isLoggable( Level .FINE ) )
            logger.fine( "SETPROP: " + getPath() + "||" + name + "=" + value );
        this .setModelProperty( name, value );
    }

    public void setModelProperty( String name, Object value )
    {
        if ( mNextController != null )
            mNextController .setProperty( name, value );
    }

    final private void setNextController( Controller controller, String name )
    {
        this .name = name;
        mNextController = (DefaultController) controller;
        mNextController .addPropertyListener(new PropertyChangeListener()
        {
            // multicast prop changes down the tree of controllers... watch out for loops!
            @Override
            public void propertyChange( PropertyChangeEvent event )
            {
                pcs .firePropertyChange( event );
            }
        } );
        if ( mNextController instanceof DefaultController )
            mErrors = mNextController .mErrors;
    }
    
    protected void firePropertyChange( PropertyChangeEvent event )
    {
        if ( logger .isLoggable( Level .FINE ) )
            logger.fine( "CHGEVENT: " + getPath() + "||" + event.getPropertyName() + "=" + event.getNewValue() );
        this .pcs .firePropertyChange( event );
    }
    
    protected void firePropertyChange( String propName, Object oldValue, Object newValue )
    {
        if ( logger .isLoggable( Level .FINE ) )
            logger.fine( "CHGEVENT: " + getPath() + "||" + propName + "=" + newValue );
        this .pcs .firePropertyChange( propName, oldValue, newValue );
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
        if ( this .mNextController != null )
            this .mNextController .openApplication( file );
    }
    
    protected void runScript( String script, File file )
    {
        if ( this .mNextController != null )
            this .mNextController .runScript( script, file );
    }
}
