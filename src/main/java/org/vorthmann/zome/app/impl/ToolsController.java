
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Tool;

public class ToolsController extends DefaultController implements PropertyChangeListener
{
	private final DocumentModel tools;  // TODO this should be a ToolsModel
	private final Map<String,Controller> subcontrollers = new TreeMap<>();

    public ToolsController( DocumentModel tools )
    {
		super();
		
		this.tools = tools;
		tools .addPropertyChangeListener( this );
	}

	@Override
	public Controller getSubController( String name )
	{
		Controller subc = this .subcontrollers .get( name );
		if ( subc != null )
			return subc;
        Tool tool = tools .getTool( name );
        if ( tool == null )
        	return null;
        Controller controller = new ToolController( tool, tools );
        controller .setNextController( this );
		subcontrollers .put( name, controller );
		return controller;
	}

    @Override
    public String[] getCommandList( String listName )
    {
        if ( "tool.instances" .equals( listName ) )
            return (String[]) subcontrollers .keySet() .toArray();

        return super .getCommandList( listName );
    }

	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		switch ( evt .getPropertyName() ) {

		case "tool.instances":
            String toolName = (String) evt .getNewValue(); // will be "group.N/label"
            Controller controller = this .getSubController( toolName );
            if ( controller .propertyIsTrue( "predefined" ) )
            	// ignore the forced events at startup
            	return;
    		this .properties() .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
			break;
			
		case "tools.enabled":
    		this .properties() .firePropertyChange( evt ); // propagate to the UI
			break;

		default:
			break;
		}
	}

	@Override
	public String getProperty( String name )
	{
		switch (name) {

		case "next.tool.number":
			return "" + subcontrollers .size();

		default:
			return super .getProperty( name );
		}
	}
}
