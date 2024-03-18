package com.vzome.core.tools;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class ScalingToolFactory extends AbstractToolFactory
{
    public ScalingToolFactory( ToolsModel tools, Symmetry symmetry )
    {
        super( tools, symmetry, ScalingTool.ID, ScalingTool.LABEL, ScalingTool.TOOLTIP );
    }

    @Override
    protected boolean countsAreValid( int total, int balls, int struts, int panels )
    {
        return ( total == 3 && balls == 1 && struts == 2 );
    }

    @Override
    public Tool createToolInternal( String id )
    {
        ScalingTool tool = new ScalingTool( id, getSymmetry(), this .getToolsModel() );

        int scalePower = 0;;
        switch ( id ) {

        case "scaling.builtin/scale up":
            scalePower = 1;
            break;

        case "scaling.builtin/scale down":
            scalePower = -1;
            break;

        default:
            return tool;  // non-predefined, no scale factor set
        }
        AlgebraicField field = this .getToolsModel() .getEditorModel() .getRealizedModel() .getField();
        tool .setScaleFactor( field .createPower( scalePower ) );
        return tool;
    }

    // This is never called for the predefined tool, so symmetry can be null.
    @Override
    protected boolean bindParameters( Selection selection )
    {
        Symmetry symmetry = getSymmetry();
        AlgebraicVector offset1 = null;
        AlgebraicVector offset2 = null;
        for ( Manifestation man : selection )
            if ( man instanceof Strut ) {
                Strut strut = (Strut) man;
                if ( offset1 == null )
                    offset1 = strut .getOffset();
                else
                    offset2 = strut .getOffset();
            }
        Axis zone1 = symmetry .getAxis( offset1 );
        Axis zone2 = symmetry .getAxis( offset2 );
        if ( zone1 == null || zone2 == null )
            return false;
        Direction orbit1 = zone1 .getDirection();
        Direction orbit2 = zone2 .getDirection();
        if ( orbit1 != orbit2 )
            return false;
        if ( orbit1 .isAutomatic() )
            return false;
        AlgebraicNumber l1 = zone1 .getLength( offset1 );
        AlgebraicNumber l2 = zone2 .getLength( offset2 );
        if ( l1 .equals( l2 ) )
            return false;
        return true;
    }
}