
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.api.Tool;
import com.vzome.core.editor.ToolsModel;

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
            controller .setNextController( this );
            return controller;
        }
        return null;
    }

    void addTool( Tool tool )
    {
        Controller controller = new ToolController( tool );
        controller .setNextController( this );
        this .properties() .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
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
                this .properties() .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
            }
            else { // hiding a tool
                Tool tool = (Tool) evt .getOldValue();
                this .properties() .firePropertyChange( new PropertyChangeEvent( this, "tool.hidden", tool .getId(), null ) );
            }
            break;

        default:
            break;
        }
    }
}
