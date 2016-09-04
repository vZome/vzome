
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
import com.vzome.core.editor.ToolFactory;

public class ToolsController extends DefaultController implements PropertyChangeListener
{
	private final DocumentModel tools;  // TODO this should be a ToolsModel
	private final Map<String,Controller> toolControllers = new TreeMap<>();
	private final Map<String,Controller> factoryControllers = new TreeMap<>();

    public ToolsController( DocumentModel tools )
    {
		super();
		
		this .tools = tools;
		tools .addPropertyChangeListener( this );
	}

	@Override
	public Controller getSubController( String name )
	{
		Controller subc = this .toolControllers .get( name );
		if ( subc != null )
			return subc;
		subc = this .factoryControllers .get( name );
		if ( subc != null )
			return subc;

		Tool tool = tools .getTool( name );
        if ( tool != null ) {
            Controller controller = new ToolController( tool, tools );
            controller .setNextController( this );
    		toolControllers .put( name, controller );
    		return controller;
        }
        ToolFactory factory = tools .getToolFactory( name );
        if ( factory != null ) {
        	Controller controller = new ToolFactoryController( factory, this );
            controller .setNextController( this );
            factoryControllers .put( name, controller );
    		return controller;
        }
        return null;
	}
	
	void addTool( Tool tool )
	{
        Controller controller = new ToolController( tool, tools );
        controller .setNextController( this );
		toolControllers .put( tool .getName(), controller );
		this .properties() .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
	}

    @Override
    public String[] getCommandList( String listName )
    {
        if ( "tool.instances" .equals( listName ) )
            return (String[]) toolControllers .keySet() .toArray();

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
			
		default:
			break;
		}
	}

	@Override
	public String getProperty( String name )
	{
		switch (name) {

		case "next.tool.number":
			return "" + toolControllers .size();

		default:
			return super .getProperty( name );
		}
	}
}
