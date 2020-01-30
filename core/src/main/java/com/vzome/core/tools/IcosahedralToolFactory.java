package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.model.Connector;

public class IcosahedralToolFactory extends AbstractToolFactory
{
	private static final String ID = "icosahedral";
	private static final String LABEL = "Create an icosahedral symmetry tool";
	private static final String TOOLTIP = "<p>" +
    		"Each tool produces up to 59 copies of the input<br>" +
    		"selection, using the rotation symmetries of an<br>" +
    		"icosahedron.  To create a tool, select a single<br>" +
    		"ball that defines the center of symmetry.<br>" +
    		"<br>" +
    		"Combine with a point reflection tool to achieve<br>" +
    		"all 120 symmetries of the icosahedron, including<br>" +
    		"reflections.<br>" +
		"</p>";
	
	public IcosahedralToolFactory( ToolsModel tools, IcosahedralSymmetry symmetry )
	{
		super( tools, symmetry, ID, LABEL, TOOLTIP );
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
