
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Tool;

public class ToolsController extends DefaultController implements PropertyChangeListener
{
	private final DocumentModel tools;
	private final List<String> toolNames = new ArrayList<>();

    public ToolsController( DocumentModel tools )
    {
		super();
		
		this.tools = tools;
		tools .addPropertyChangeListener( this );
	}
            
    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
    	Tool tool = tools .getTool( action );
    	if ( tool != null )
    		tools .applyTool( tool, tools, e .getModifiers() );
    	else
    		super .doAction( action, e );
    }

    @Override
    public String[] getCommandList( String listName )
    {
        if ( "tool.instances" .equals( listName ) )
            return toolNames .toArray( new String[ toolNames.size() ] );

        return super .getCommandList( listName );
    }

	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
        if ( evt .getPropertyName() .equals( "tool.instances" ) )
        {
            String toolName = (String) evt .getNewValue(); // will be "group.N/label"
    		toolNames .add( toolName );
    		this .properties() .firePropertyChange( evt ); // propagate to the UI
        }
	}

	@Override
	public String getProperty( String name )
	{
		switch (name) {

		case "next.tool.number":
			return "" + toolNames .size();

		default:
			return super .getProperty( name );
		}
	}
}
