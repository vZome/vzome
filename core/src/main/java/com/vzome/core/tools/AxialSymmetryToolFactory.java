package com.vzome.core.tools;

import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.Symmetry;


public class AxialSymmetryToolFactory extends RotationToolFactory
{
	private static final String ID = "axial symmetry";
	private static final String LABEL = "Create a rotational symmetry tool";
	private static final String TOOLTIP = "<p>" +
    		"Each tool creates enough copies of the selected objects to<br>" +
    		"create rotational symmetry around an axis.  To create a tool,<br>" +
    		"select a strut that defines that axis,  You can also define<br>" +
    		"the direction and center independently, by selecting a ball<br>" +
    		"for the center and a strut for the axis.  Note: not all struts<br>" +
    		"correspond to rotational symmetries!<br>" +
    		"<br>" +
    		"Combine with a point reflection or mirror reflection tool to<br>" +
    		"achieve more symmetries.<br>" +
		"</p>";
	
	/**
	 * Used for the throwaway factories used to create predefined tools.
	 * Using the symmetry name in the factory ID prevents collisions across
	 * symmetries for common tools.
	 * @param tools
	 * @param symmetry
	 * @param useSymmetryName
	 */
    public AxialSymmetryToolFactory( ToolsModel tools, Symmetry symmetry, boolean useSymmetryName )
    {
        super( tools, symmetry, (useSymmetryName ? symmetry.getName() + ' ' : "") + ID, LABEL, TOOLTIP );
    }
    
	public AxialSymmetryToolFactory( ToolsModel tools, Symmetry symmetry )
	{
	    this( tools, symmetry, false );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		// Just like the superclass, but with "true" for full rotation.
		return new RotationTool( id, getSymmetry(), getToolsModel(), true );
	}
}