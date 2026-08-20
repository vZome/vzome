
package com.vzome.desktop.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import com.vzome.api.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.desktop.api.Controller;

public class ToolsController extends DefaultController implements PropertyChangeListener
{
    private final ToolsModel tools;

    public ToolsController( ToolsModel tools )
    {
        super();

        this .tools = tools;
        tools .addPropertyListener( this );
    }

    @Override
    protected void doParamAction( String action, Properties params ) throws Exception
    {
        switch ( action ) {

        case "reorderTools":
        case "reorderBookmarks":
            // The client sends the full desired sequence of ids as a comma-joined "order" param
            //  (custom-tool ids are "tool-N", so comma is a safe delimiter).
            String order = params .getProperty( "order" );
            String[] ids = ( order == null || order .isEmpty() )? new String[0] : order .split( "," );
            this .tools .reorderTools( action .equals( "reorderBookmarks" ), ids );
            break;

        default:
            super .doParamAction( action, params );
        }
    }

    @Override
    public Controller getSubController( String name )
    {
        // we don't store any subcontrollers in a map, since the UI never asks twice

        Tool tool = tools .get( name );
        if ( tool != null ) {
            Controller controller = new ToolController( tool );
            this .addSubController( name, controller );
            return controller;
        }
        return null;
    }

    public void addTool( Tool tool )
    {
        Controller controller = new ToolController( tool );
        this .addSubController( tool .getId(), controller );
        this .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch ( evt .getPropertyName() ) {
        
        case "customTools":
            // forward for the web client
            this .firePropertyChange( new PropertyChangeEvent( this, evt .getPropertyName(), null, evt .getNewValue() ) );
            // the full roster (incl. hidden) also changed, so refresh the "more tools" overflow
            this .firePropertyChange( new PropertyChangeEvent( this, "allCustomTools", null, this .tools .getAllCustomToolIDs( false ) ) );
            break;

        case "customBookmarks":
            this .firePropertyChange( new PropertyChangeEvent( this, evt .getPropertyName(), null, evt .getNewValue() ) );
            this .firePropertyChange( new PropertyChangeEvent( this, "allCustomBookmarks", null, this .tools .getAllCustomToolIDs( true ) ) );
            break;

        case "tool.instances":
            if ( evt .getOldValue() == null ) // adding a tool
            {
                Tool tool = (Tool) evt .getNewValue();
                if ( tool .isPredefined() || tool .isHidden() )
                    // ignore the forced events at startup, and tools that have been hidden
                    return;
                Controller controller = new ToolController( tool );
                this .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
            }
            else { // hiding a tool
                Tool tool = (Tool) evt .getOldValue();
                this .firePropertyChange( new PropertyChangeEvent( this, "tool.hidden", tool .getId(), null ) );
            }
            break;

        default:
            break;
        }
    }

    @Override
    public String[] getCommandList( String listName )
    {
        switch (listName) {

        case "customTools":
            return this .tools .getToolIDs( false );

        case "customBookmarks":
            return this .tools .getToolIDs( true );

        case "allCustomTools":
            return this .tools .getAllCustomToolIDs( false );

        case "allCustomBookmarks":
            return this .tools .getAllCustomToolIDs( true );

        default:
            break;
        }
        return super.getCommandList( listName );
    }
}
