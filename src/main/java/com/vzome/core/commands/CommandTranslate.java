

package com.vzome.core.commands;


import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.Translation;

/**
 * @author Scott Vorthmann
 */
public class CommandTranslate extends CommandTransform
{
    
    @Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        final Segment norm = (Segment) attributes .get( SYMMETRY_AXIS_ATTR_NAME );
        if ( norm == null ) {
            throw new Command.Failure( "no symmetry axis provided" );
        }
        final Construction[] params = parameters .getConstructions();
        AlgebraicField field = norm .getField();
        AlgebraicVector offset = field .projectTo3d( norm .getOffset(), true );
        Transformation transform = new Translation( offset );
        return transform( params, transform, effects );
    }
}
