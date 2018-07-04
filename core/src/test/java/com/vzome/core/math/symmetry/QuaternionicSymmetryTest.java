
//(c) Copyright 2015, Scott Vorthmann.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.Quaternion;

import junit.framework.TestCase;

public class QuaternionicSymmetryTest extends TestCase
{

    public void testH4Symmetry()
    {
        AlgebraicField field = new PentagonField();
        QuaternionicSymmetry h4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", field );
        Quaternion[] roots = h4 .getRoots();
        AlgebraicVector x = field .basisVector( 4, AlgebraicVector.Y4 );
        
        AlgebraicVector result = roots[ 101 ] .leftMultiply( x );
        AlgebraicVector expected = field .createVector( new int[][]{ {-1,2, 0,1}, {1,2, 0,1}, {-1,2, 0,1}, {1,2, 0,1} } );
        assertEquals( expected, result );
        
        result = roots[ 65 ] .leftMultiply( x );
        expected = field .createVector( new int[][]{ {-1,2, 1,2}, {0,1, 0,1}, {0,1, -1,2}, {-1,2, 0,1} } );
        assertEquals( expected, result );
        
        result = roots[ 23 ] .leftMultiply( x );
        expected = field .createVector( new int[][]{ {-1,2, 0,1}, {1,2, -1,2}, {0,1, 1,2}, {0,1, 0,1} } );
        assertEquals( expected, result );
        
        result = roots[ 117 ] .rightMultiply( x );
        expected = field .createVector( new int[][]{ {-1,2, 0,1}, {1,2, 0,1}, {-1,2, 0,1}, {-1,2, 0,1} } );
        assertEquals( expected, result );
        
        result = roots[ 81 ] .rightMultiply( x );
        expected = field .createVector( new int[][]{ {1,2, -1,2}, {1,2, 0,1}, {0,1, 0,1}, {0,1, -1,2} } );
        assertEquals( expected, result );
    }
}
