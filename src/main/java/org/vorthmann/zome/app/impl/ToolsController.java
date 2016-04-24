
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Tool;

public class ToolsController extends DefaultController
{
	private final DocumentModel tools;
	private final List<String> toolNames = new ArrayList<>();

    public ToolsController( DocumentModel tools )
    {
		super();
		
		this.tools = tools;
	}
            
    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( action.startsWith( "newTool/" ) )
        {
            String name = action .substring( "newTool/" .length() );
    		super .doAction( action, e );
    		addTool( name );
        }
        else {
        	Tool tool = tools .getTool( action );
        	if ( tool != null )
            	tools .applyTool( tool, tools, e .getModifiers() );
        	else
        		super .doAction( action, e );
        }
    }

    @Override
    public String[] getCommandList( String listName )
    {
        if ( "tool.instances" .equals( listName ) )
            return toolNames .toArray( new String[ toolNames.size() ] );

        return super .getCommandList( listName );
    }

	public void addTool( String toolName )
	{
		toolNames .add( toolName);
        properties() .firePropertyChange( "tool.instances", null, toolName );
	}
}
