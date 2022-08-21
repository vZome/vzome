package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class ProjectionToolFactory extends AbstractToolFactory
{
	public ProjectionToolFactory( ToolsModel tools )
	{
		super( tools, null, ProjectionTool.ID, ProjectionTool.LABEL, ProjectionTool.TOOLTIP );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 2 && panels == 1 && struts == 1 )
                || ( total == 1 && panels == 1 )
                || ( total == 2 && balls == 1 && struts == 1 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new ProjectionTool( id, getToolsModel() );
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