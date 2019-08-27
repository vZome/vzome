package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class OctahedralToolFactory extends AbstractToolFactory
{
	private static final String ID = "octahedral";
	private static final String LABEL = "Create an octahedral symmetry tool";
	private static final String TOOLTIP1 = "<p>" +
    		"Each tool produces up to 23 copies of the input<br>" +
    		"selection, using the rotation symmetries of a<br>" +
    		"cube or octahedron.  To create a tool, select a<br>" +
    		"ball that defines the center of symmetry, and<br>" +
    		"a single blue or green strut, defining one of<br>" +
    		"five possible orientations for the symmetry.<br>" +
    		"<br>" +
    		"Combine with a point reflection tool to achieve<br>" +
    		"all 48 symmetries of the octahedron, including<br>" +
    		"reflections.<br>" +
		"</p>";
	
	private static final String TOOLTIP2 = "<p>" +
    		"Each tool produces up to 23 copies of the input<br>" +
    		"selection, using the rotation symmetries of a<br>" +
    		"cube or octahedron.  To create a tool, select a<br>" +
    		"ball that defines the center of symmetry.<br>" +
    		"<br>" +
    		"Combine with a point reflection tool to achieve<br>" +
    		"all 48 symmetries of the octahedron, including<br>" +
    		"reflections.<br>" +
		"</p>";
	
	public OctahedralToolFactory( ToolsModel tools, Symmetry symmetry )
	{
		this( tools, symmetry, ID, LABEL, TOOLTIP2 );
	}

	public OctahedralToolFactory( ToolsModel tools, Symmetry symmetry, String id, String label, String tooltip )
	{
		super( tools, symmetry, id, label, tooltip );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 1 && balls == 1 ) || ( total == 2 && balls == 1 && struts == 1 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new SymmetryTool( id, getSymmetry(), getToolsModel() );
	}

	@Override
	protected boolean bindParameters( Selection selection )
	{
		Symmetry symmetry = getSymmetry();
		int total = getSelection() .size();
		if ( symmetry instanceof IcosahedralSymmetry ) { // TODO make this OO
			if ( total != 2 )
				return false;
			for ( Manifestation man : selection )
        		if ( man instanceof Strut )
        		{
        			Axis zone = symmetry .getAxis( ((Strut) man) .getOffset() );
        			if ( zone == null )
        				return false;
        			switch ( zone .getDirection() .getName() ) {
					case "blue":
					case "green":
						return true;

					default:
						return false;
					}
        		}
			return false; // should never get here
		}
		else {
			return total == 1;
		}
	}
}