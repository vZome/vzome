package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class ModuleToolFactory extends AbstractToolFactory
{
	public ModuleToolFactory( ToolsModel tools )
	{
		super( tools, null, ModuleTool.ID, ModuleTool.LABEL, ModuleTool.TOOLTIP );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total > 0);
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new ModuleTool( id, getToolsModel() );
	}

	@Override
	protected boolean bindParameters( Selection selection )
	{
		return true;
	}
}