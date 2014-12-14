

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public class CommandTetrahedralSymmetry extends CommandSymmetry
{    
    public static final String SUBGROUP_ATTR_NAME = "symmetry.permutations";

    protected static final Object[][] ATTR_SIGNATURE = new Object[][]{ { SYMMETRY_CENTER_ATTR_NAME, Point.class },
        { SYMMETRY_GROUP_ATTR_NAME, Symmetry.class },
        { SUBGROUP_ATTR_NAME, new int[0] .getClass() } };
    
    public CommandTetrahedralSymmetry( Symmetry symmetry )
    {
        super( symmetry );
    }
    
    public CommandTetrahedralSymmetry()
    {
        this( null );
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    public ConstructionList apply( final ConstructionList parameters, Map attributes, final ConstructionChanges effects ) throws Failure
    {
        final Point center = setSymmetry( attributes );
                
        int[] closure = mSymmetry .subgroup( Symmetry.TETRAHEDRAL );
        
        final Construction[] params = parameters .getConstructions();
        ConstructionList output = new ConstructionList();
        for ( int j = 0; j < params .length; j++ )
            output .addConstruction( params[j] );
        
        for ( int i = 1; i < closure .length; i++ ) {
            
            Transformation transform = new SymmetryTransformation( mSymmetry, closure[i], center );
            output .addAll( transform( params, transform, effects ) );
        }
        return output;
    }
}
