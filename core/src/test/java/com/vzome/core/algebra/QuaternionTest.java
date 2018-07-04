package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuaternionTest {

	@Test
    public void testQuaternions3D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector left3 = field .createVector( new int[][]
            {
                    {1,1, 0,1}, {2,1, 0,1},{ 3,1, 0,1}
            } ); // same as below, but just imaginary parts
        AlgebraicVector right3 = field .createVector( new int[][]
            {
                    {0,1, 0,1}, {5,1, 0,1}, {1,1, 0,1}
            } );
        AlgebraicVector left = field .createVector( new int[][]
            {
                    {0,1, 0,1}, {1,1, 0,1}, {2,1, 0,1}, {3,1, 0,1}
            } ); // i + 2j + 3k
        AlgebraicVector right = field .createVector( new int[][]
            {
                    {0,1, 0,1}, {0,1, 0,1},{ 5,1, 0,1}, {1,1, 0,1}
            } ); // 5j + k
        AlgebraicVector expected = field .createVector( new int[][]
            {
                    {-13,1, 0,1}, {-13,1, 0,1}, {-1,1, 0,1}, {5,1, 0,1}
            } ); // -13 - 13i -j + 5k

        // = (i + 2j + 3k) * 5j + (i + 2j + 3k) * k
        // = 5ij + 10jj + 15kj + ik + 2jk + 3kk
        // = 5k - 10 - 15i - j + 2i - 3
        // = -13 - 13i -j + 5k

        Quaternion q = new Quaternion( field, left3 );
        AlgebraicVector result = q.rightMultiply( right );
        assertEquals( expected, result );

        q = new Quaternion( field, right3 );
        result = q.leftMultiply( left );
        assertEquals( expected, result );
    }

	@Test
    public void testQuaternions()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector left = field .createVector( new int[][]
            {
                    {1,1, 0,1}, {2,1, 0,1}, {3,1, 0,1}, {1,1, 0,1}
            } ); // 1 + 2i + 3j + k
        AlgebraicVector right = field .createVector( new int[][]
            {
                    {2,1, 0,1}, {0,1,0,1}, {1,1, 0,1}, {1,1, 0,1}
            } ); // 2 + j + k
        AlgebraicVector expected = field .createVector( new int[][]
            {
                    {-2,1, 0,1}, {6,1, 0,1}, {5,1, 0,1}, {5,1, 0,1}
            } ); // -2 + 6i + 5j + 5k

        // = (2 + j + k) + (4i + 2ij + 2ik) + (6j + 3jj + 3jk) + (2k + kj + kk)
        // = 2 + j + k + 4i + 2k - 2j + 6j - 3 + 3i + 2k - i - 1
        // = (2-3-1) + (4+3-1)i + (1-2+6)j + (1+2+2)k
        // = -2 + 6i + 5j + 5k

        Quaternion q = new Quaternion( field, left );
        AlgebraicVector result = q.rightMultiply( right );
        assertEquals( expected, result );

        q = new Quaternion( field, right );
        result = q .leftMultiply( left );
        assertEquals( expected, result );
    }

	@Test
	public void testReflection()
	{
        AlgebraicField field = new PentagonField();

        AlgebraicVector input = field .createVector( new int[][]
            {
                    {1,1, 0,1}, {2,1, 0,1}, {3,1, 0,1}, {1,1, 0,1}
            } ); // 1 + 2i + 3j + k
        AlgebraicVector mirror = field .createVector( new int[][]
            {
                    {0,1, 0,1}, {0,1, 0,1}, {0,1, 0,1}, {-1,1, 0,1}
            } ); // -k
        AlgebraicVector expected = field .createVector( new int[][]
            {
                {1,1, 0,1}, {2,1, 0,1}, {3,1, 0,1}, {-1,1, 0,1}
            } ); // 1 + 2i + 3j - k

        Quaternion q = new Quaternion( field, mirror );
        AlgebraicVector result = q .reflect( input );
        assertEquals( expected, result );

        result = q .reflect( result );
        assertEquals( input, result );
}
}
