
package com.vzome.core.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.RootTwoField;

public class QuaternionProjectionTest
{
    @Test
    public void testProjection()
    {
        AlgebraicField field = new RootTwoField();
        AlgebraicVector input     = field .createVector( new int[][]{ { -8,1,   4,1}, {0,1, 4,1}, {0,1, 4,1}, {0,1, 4,1} } );
        AlgebraicVector rightQuat = field .createVector( new int[][]{ {  2,1,   1,1}, {2,1, 1,1}, {2,1, 1,1}, {2,1, 1,1} } );
        AlgebraicVector expected  = field .createVector( new int[][]{ {-32,1, -24,1}, {0,1, 8,1}, {0,1, 8,1} } );
        QuaternionProjection proj = new QuaternionProjection( field, null, rightQuat );
        AlgebraicVector actual = proj .projectImage( input, false );
        assertEquals( expected, actual );
    }
}
