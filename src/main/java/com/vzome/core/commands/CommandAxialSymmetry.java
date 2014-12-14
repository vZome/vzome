

package com.vzome.core.commands;

import java.util.Iterator;
import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public class CommandAxialSymmetry extends CommandSymmetry
{           
    public CommandAxialSymmetry()
    {
        this( null );
    }
    
    public CommandAxialSymmetry( Symmetry symmetry )
    {
        super( symmetry );
    }

    public ConstructionList apply( ConstructionList parameters, Map attributes, final ConstructionChanges effects ) throws Failure
    {
        setSymmetry( attributes );
        
        final Segment norm = (Segment) attributes .get( SYMMETRY_AXIS_ATTR_NAME );
        if ( norm == null ) {
            throw new Command.Failure( "no symmetry axis provided" );
        }
        ConstructionList output = new ConstructionList();
        
        int[] vector = norm .getOffset();
        vector = norm .getField() .projectTo3d( vector, true );

        Axis axis = mSymmetry .getAxis( vector );
        Permutation rotation = axis .getRotationPermutation(); // should get this from Direction
        if ( rotation == null ) {
            throw new Command.Failure( "symmetry axis does not support axial symmetry" );
        }
        int order = rotation .getOrder(); // TODO should get this from Direction
        CommandRotate rotate = new CommandRotate();
        for ( int i = 1; i < order; i++ ) {
            for ( Iterator params = parameters .iterator(); params .hasNext(); )
                output .addConstruction( (Construction) params .next() );
            parameters = rotate .apply( parameters, attributes, effects );
        }
        for ( Iterator params = parameters .iterator(); params .hasNext(); )
            output .addConstruction( (Construction) params .next() );
        return output;
    }
}
