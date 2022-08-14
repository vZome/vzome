package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;

public class SymmetryToolFactory extends AbstractToolFactory
{
	public SymmetryToolFactory( ToolsModel tools, Symmetry symmetry )
	{
		super( tools, symmetry, SymmetryTool.ID, SymmetryTool.LABEL, SymmetryTool.TOOLTIP );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 1 && balls == 1 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new SymmetryTool( id, getSymmetry(), getToolsModel() );
	}

	@Override
	protected boolean bindParameters( Selection selection )
	{
        return selection.size() == 1 && selection.iterator().next() instanceof Connector;
	}
}