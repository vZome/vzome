package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class MirrorToolFactory extends AbstractToolFactory
{
    public MirrorToolFactory( ToolsModel tools )
    {
        super( tools, null, MirrorTool.ID, MirrorTool.LABEL, MirrorTool.TOOLTIP );
    }

    @Override
    protected boolean countsAreValid( int total, int balls, int struts, int panels )
    {
        return ( total == 2 && balls == 1 && struts == 1 )
                || ( total == 1 && panels == 1 );
    }

    @Override
    public Tool createToolInternal( String id )
    {
        return new MirrorTool( id, getToolsModel() );
    }

    @Override
    public Tool createTool()
    {
        Tool result = super.createTool();
        result .setCopyColors( false ); // Overriding true default, only for newly created tools
        return result;
    }

    @Override
    protected boolean bindParameters( Selection selection )
    {
        return true;
    }
}