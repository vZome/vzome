

package com.vzome.core.commands;


import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;

/**
 * @author Scott Vorthmann
 */
public class CommandRotate extends CommandSymmetry
{
    @Override
    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        final Point center = setSymmetry( attributes );
        
        final Segment norm = (Segment) attributes .get( SYMMETRY_AXIS_ATTR_NAME );
        if ( norm == null ) {
            throw new Command.Failure( "no symmetry axis provided" );
        }

        final Construction[] params = parameters .getConstructions();
        final ConstructionList output = new ConstructionList();

        AlgebraicVector vector = norm .getOffset();
        vector = norm .getField() .projectTo3d( vector, true );
        Axis axis = mSymmetry .getAxis( vector );
        int rotation = axis .getRotation();
//        int order = axis .getRotationPermutation() .getOrder(); // should get this from Direction
        
//        for ( int i = 1; i < order; i++ ) {
            
            Transformation transform = new SymmetryTransformation( mSymmetry, rotation, center );
            effects .constructionAdded( transform );
            output .addAll( transform( params, transform, effects ) );
//            rotation = axis .getRotationPermutation() .mapIndex( rotation );
//        }
        return output;
    }
}
