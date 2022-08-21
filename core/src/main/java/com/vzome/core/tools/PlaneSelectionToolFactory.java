package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class PlaneSelectionToolFactory extends AbstractToolFactory
{
    public PlaneSelectionToolFactory( ToolsModel tools )
    {
        super( tools, null, "plane", "", "" );
    }

    @Override
    protected boolean countsAreValid( int total, int balls, int struts, int panels )
    {
        return ( total == 3 && balls == 3 );
    }

    @Override
    public Tool createToolInternal( String id )
    {
        return new PlaneSelectionTool( id, getToolsModel() );
    }

    @Override
    protected boolean bindParameters( Selection selection )
    {
        return true;
    }
}