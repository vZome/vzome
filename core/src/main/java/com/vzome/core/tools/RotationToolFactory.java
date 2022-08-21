package com.vzome.core.tools;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class RotationToolFactory extends AbstractToolFactory
{
    static final String ID = "rotation";
    private static final String LABEL = "Create a rotation tool";
    private static final String TOOLTIP = "<p>" +
            "Each tool rotates the selected objects around an axis<br>" +
            "of symmetry.  To create a tool, select a strut that<br>" +
            "defines that axis.  You can also define the direction<br>" +
            "and center independently, by selecting a ball for the<br>" +
            "center and a strut for the axis.  Note: not all struts<br>" +
            "correspond to rotational symmetries!<br>" +
            "<br>" +
            "The direction of rotation depends on the strut<br>" +
            "orientation, which is hard to discover, but easy to<br>" +
            "control, by dragging out a new strut.<br>" +
            "<br>" +
            "By default, the input selection will be moved to the new,<br>" +
            "rotated orientation.  After creating a tool, you can<br>" +
            "right-click to configure the tool to create a copy, instead.<br>" +
            "</p>";

    private transient boolean noStrut;

    public RotationToolFactory( ToolsModel tools, Symmetry symmetry )
    {
        this( tools, symmetry, false );
    }

    public RotationToolFactory( ToolsModel tools, Symmetry symmetry, boolean useSymmetryName )
    {
        this( tools, symmetry, (useSymmetryName ? symmetry.getName() + ' ' : "") + ID, LABEL, TOOLTIP );
    }

    public RotationToolFactory( ToolsModel tools, Symmetry symmetry, String id, String label, String tooltip )
    {
        super( tools, symmetry, id, label, tooltip );
    }

    @Override
    protected boolean countsAreValid( int total, int balls, int struts, int panels )
    {
        if ( total == 1 && balls == 1 ) 
        {
            this .noStrut = true;
            return true;
        }
        else if ( ( total == 1 && struts == 1 ) || ( total == 2 && balls == 1 && struts == 1 ) )
        {
            this .noStrut = false;
            return true;
        }
        return false;
    }

    @Override
    public Tool createToolInternal( String id )
    {
        return new RotationTool( id, getSymmetry(), getToolsModel(), false );
    }

    @Override
    protected boolean bindParameters( Selection selection )
    {
        Symmetry symmetry = getSymmetry();
        for ( Manifestation man : selection )
            if ( man instanceof Strut )
            {
                Strut axisStrut = (Strut) man;
                AlgebraicVector vector = axisStrut .getOffset();
                vector = symmetry .getField() .projectTo3d( vector, true ); // TODO: still necessary?
                Axis axis = symmetry .getAxis( vector );
                if ( axis == null )
                    return false;
                Permutation perm = axis .getRotationPermutation();
                if ( perm == null )
                    return false;
            }
            else if ( this .noStrut )
            {
                Axis axis = symmetry .getPreferredAxis();
                if ( axis == null )
                    return false;
            }
        return true;
    }
}
