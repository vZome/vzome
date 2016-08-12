
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
            Tool tool = tools .getTool( toolName );
            Controller controller = new ToolController( tool, tools );
            controller .setNextController( this );
    		this .properties() .firePropertyChange( new PropertyChangeEvent( this, "tool.added", null, controller ) );
    		subcontrollers .put( toolName, controller );
			break;
			
    	case "tool.separator":
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
