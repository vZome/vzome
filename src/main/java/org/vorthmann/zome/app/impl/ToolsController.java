
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.commands.Command;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.TransformationTool;

public class ToolsController extends DefaultController implements Tool.Registry
{
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
            Tool tool = (Tool) categories .get( category );
            if ( tool == null )
            {
                super .doAction( "newTool/default." + category + ".auto/" + TransformationTool.DEFAULT_NAME_TRIGGER, e );
                tool = (Tool) categories .get( category );
            }
            if ( tool == null )
                throw new Command.Failure( "No tool in the category \"" + category + "\" has been applied yet, or the last-used was removed." );
            super .doAction( "applyTool", new ToolEvent( tool, modifiers, this, e ) );
        }
        else if ( toolNames .contains( action ) )
        {
            Tool tool = getTool( action );
            super .doAction( "applyTool", new ToolEvent( tool, modifiers, this, e ) );
        }
        else
            super .doAction( action, e );
    }

    public String[] getCommandList( String listName )
    {
        if ( "tool.instances" .equals( listName ) )
            return (String[]) toolNames .toArray( new String[]{} );

        return super .getCommandList( listName );
    }

    private final ArrayList toolNames = new ArrayList();
    
    private final Map tools = new HashMap();
    
    private final Map categories = new HashMap();
        
    public void addTool( Tool tool )
    {
        String name = tool .getName();
        tools .put( name, tool );
        toolNames .add( name );
        useTool( tool );
        properties() .firePropertyChange( "tool.instances", null, name );
    }

    public void removeTool( Tool tool )
    {
        String name = tool .getName();
        tools .remove( name );
        toolNames .remove( name );
        categories .remove( tool .getCategory() );
        properties() .firePropertyChange( "tool.instances", name, null );
    }

    public Tool getTool( String toolName )
    {
        return (Tool) tools .get( toolName );
    }

    public void useTool( Tool tool )
    {
        String category = tool .getCategory();
        categories .put( category, tool );
    }

    public Tool getLastInCategory( String category )
    {
        return (Tool) categories .get( category );
    }
}
