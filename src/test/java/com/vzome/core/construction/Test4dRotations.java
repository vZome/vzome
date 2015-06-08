
//(c) Copyright 2015, Scott Vorthmann.

package com.vzome.core.construction;

import junit.framework.TestCase;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;

public class Test4dRotations extends TestCase
{

    public void testRotatedPoint()
    {
        AlgebraicField field = new PentagonField();
        QuaternionicSymmetry h4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", field );
        Quaternion[] roots = h4 .getRoots();
        AlgebraicVector yAxis = field .basisVector( 3, AlgebraicVector.Y );

        ModelRoot root = new ModelRoot( field );
        Point prototype = new FreePoint( yAxis, root );
        prototype .setImpossible( false );
        PointRotated4D rotated = new PointRotated4D( roots[ 28 ], roots[ 105 ], prototype );
        AlgebraicVector expected = field .createVector( new int[]{ 0, 1, 0, 1, 0, 1, -1, 2, 1, 2, -1, 2, 1, 2, 0, 1 } );
        assertEquals( expected, rotated .getLocation() );
    }
}
