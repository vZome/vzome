
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.commands.Command;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.TransformationTool;

public class ToolsController extends DefaultController implements Tool.Registry
{
    private final Map<String, Tool> tools = new HashMap<>();
    
    private final Map<String, Tool> categories = new HashMap<>();
        
    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
    	// TODO first-class support for modes in ToolEvent
    	
        int modifiers = e .getModifiers();
        if ( action .startsWith( "menu-last-tool/" ) )
        {
            action = action .substring( "menu-" .length() );
            modifiers = ActionEvent .SHIFT_MASK;
        }
        
        if ( action .startsWith( "last-tool/" ) )
        {
            String category = action .substring( "last-tool/" .length() );
            Tool tool = categories .get( category );
            if ( tool == null )
            {
                super .doAction( "newTool/default." + category + ".auto/" + TransformationTool.DEFAULT_NAME_TRIGGER, e );
                tool = categories .get( category );
            }
            if ( tool == null )
                throw new Command.Failure( "No tool in the category \"" + category + "\" has been applied yet, or the last-used was removed." );
            super .doAction( "applyTool", new ToolEvent( tool, modifiers, this, e ) );
        }
        else if ( tools.keySet() .contains( action ) )
        {
            Tool tool = getTool( action );
            super .doAction( "applyTool", new ToolEvent( tool, modifiers, this, e ) );
        }
        else
            super .doAction( action, e );
    }

    @Override
    public String[] getCommandList( String listName )
    {
        if ( "tool.instances" .equals( listName ) )
            return tools.keySet().toArray( new String[tools.keySet().size()] );

        return super .getCommandList( listName );
    }

    @Override
    public void addTool( Tool tool )
    {
        String name = tool .getName();
        tools .put( name, tool );
        properties() .firePropertyChange( "tool.instances", null, name );
    }

    @Override
    public Tool getTool( String toolName )
    {
        return tools .get( toolName );
    }

    public Tool getLastInCategory( String category )
    {
        return categories .get( category );
    }

	@Override
	public Tool findEquivalent(Tool arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
