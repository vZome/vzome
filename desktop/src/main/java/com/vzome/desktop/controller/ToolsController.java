
package com.vzome.desktop.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
        this .addSubController( "tools", controller );
        this .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch ( evt .getPropertyName() ) {

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
}
