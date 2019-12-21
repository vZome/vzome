package com.vzome.core.tools;

import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.Symmetry;


public class TetrahedralToolFactory extends OctahedralToolFactory
{
	private static final String ID = "tetrahedral";
	private static final String LABEL = "Create a tetrahedral symmetry tool";
	private static final String TOOLTIP1 = "<p>" +
    		"Each tool produces up to 11 copies of the input<br>" +
    		"selection, using the rotation symmetries of a<br>" +
    		"tetrahedron.  To create a tool, select a ball<br>" +
    		"that defines the center of symmetry, and a single<br>" +
    		"blue or green strut, defining one of five<br>" +
    		"possible orientations for the symmetry.<br>" +
    		"<br>" +
    		"Combine with a point reflection tool to achieve<br>" +
    		"all 24 symmetries of the tetrahedron, including<br>" +
    		"reflections.<br>" +
		"</p>";
	
	private static final String TOOLTIP2 = "<p>" +
    		"Each tool produces up to 11 copies of the input<br>" +
    		"selection, using the rotation symmetries of a<br>" +
    		"tetrahedron.  To create a tool, select a ball<br>" +
    		"that defines the center of symmetry.<br>" +
    		"<br>" +
    		"Combine with a point reflection tool to achieve<br>" +
    		"all 24 symmetries of the tetrahedron, including<br>" +
    		"reflections.<br>" +
		"</p>";
	
	public TetrahedralToolFactory( ToolsModel tools, Symmetry symmetry )
	{
		super( tools, symmetry, ID, LABEL, "icosahedral" == symmetry.getName() ? TOOLTIP1 : TOOLTIP2 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new SymmetryTool( id, getSymmetry(), getToolsModel() );
	}
}