package com.vzome.fields.heptagon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

public class HeptagonalAntiprismSymmetryTest {

	@Test
	public void testPermutations()
	{
		HeptagonField field = new HeptagonField();
		Symmetry symm = new HeptagonalAntiprismSymmetry( field, "blue" );

		Permutation perm7 = symm .getPermutation( 3 );
		assertEquals( 7, perm7 .getOrder() );
		assertEquals( 0, perm7 .mapIndex( 4 ) );
		assertEquals( 8, perm7 .mapIndex( 12 ) );
		assertEquals( 3, symm .getMapping( 11, 7 ) );

		Permutation perm2 = symm .getPermutation( 9 );
		assertEquals( 2, perm2 .getOrder() );
		assertEquals( 4, perm2 .mapIndex( 12 ) );
		assertEquals( 10, perm2 .mapIndex( 6 ) );
		assertEquals( 9, symm .getMapping( 3, 13 ) );
		
		Permutation prod = perm2 .compose( perm7 );
		assertEquals( 2, prod .getOrder() );
		assertEquals( 10, prod .mapIndex( 3 ) );
	}

	@Test
	public void testOrientations()
	{
		HeptagonField field = new HeptagonField();
		HeptagonalAntiprismSymmetry symm = new HeptagonalAntiprismSymmetry( field, "blue" );

		AlgebraicMatrix m2 = symm .getMatrix( 2 );
		AlgebraicMatrix m4 = symm .getMatrix( 4 );
		AlgebraicMatrix m6 = symm .getMatrix( 6 );
		
		assertEquals( m2, m6 .times( m6 .times( m4 ) ) );
	}

	@Test
    public void testGetAxisUncorrected()
    {
        HeptagonField field = new HeptagonField();
        HeptagonalAntiprismSymmetry symm = new HeptagonalAntiprismSymmetry( field, "blue" );
		symm .createStandardOrbits( "blue" );

        RealVector v1 = new RealVector( 0.1, 0.1, 3.0 );
        RealVector v2 = new RealVector( 0.1, 0.1, -3.0 );
        RealVector v3 = new RealVector( 0.1, -0.1, 3.0 );
        RealVector v4 = new RealVector( 0.1, -0.1, -3.0 );
        RealVector v5 = new RealVector( -0.1, 0.1, 3.0 );
        RealVector v6 = new RealVector( -0.1, 0.1, -3.0 );
        RealVector v7 = new RealVector( -0.1, -0.1, 3.0 );
        RealVector v8 = new RealVector( -0.1, -0.1, -3.0 );

        Direction orbit = symm .getDirection( "red" );

        Axis axis = orbit .getAxis( v1 );
        Axis expected = orbit .getAxis( Axis.PLUS, 1 ); // these numbers are pretty arbitrary, for red...
        assertEquals( expected, axis );

        axis = orbit .getAxis( v2 );
        expected = orbit .getAxis( Axis.PLUS, 7 ); // since there are really only "up" and "down"
        assertEquals( expected, axis );

        axis = orbit .getAxis( v3 );
        expected = orbit .getAxis( Axis.MINUS, 11 );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v4 );
        expected = orbit .getAxis( Axis.PLUS, 13 );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v5 );
        expected = orbit .getAxis( Axis.PLUS, 3 );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v6 );
        expected = orbit .getAxis( Axis.PLUS, 8 );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v7 );
        expected = orbit .getAxis( Axis.MINUS, 8 );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v8 );
        expected = orbit .getAxis( Axis.PLUS, 9 );
        assertEquals( expected, axis );
    }

	@Test
    public void testGetAxisCorrected()
    {
        HeptagonField field = new HeptagonField();
        HeptagonalAntiprismSymmetry symm = new HeptagonalAntiprismSymmetry( field, "blue", true );
		symm .createStandardOrbits( "blue" );

        RealVector v1 = new RealVector( 0.5, 0.1, 0.1 );
        RealVector v2 = new RealVector( 0.5, 0.1, -0.1 );
        RealVector v3 = new RealVector( 0.5, -0.1, 0.1 );
        RealVector v4 = new RealVector( 0.5, -0.1, -0.1 );
        RealVector v5 = new RealVector( -0.5, 0.1, 0.1 );
        RealVector v6 = new RealVector( -0.5, 0.1, -0.1 );
        RealVector v7 = new RealVector( -0.5, -0.1, 0.1 );
        RealVector v8 = new RealVector( -0.5, -0.1, -0.1 );

        Direction orbit = symm .getDirection( "blue" );

        Axis axis = orbit .getAxis( v1 );
        Axis expected = orbit .getAxis( Axis.PLUS, 0, true );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v2 );
        expected = orbit .getAxis( Axis.MINUS, 0, true );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v3 );
        expected = orbit .getAxis( Axis.MINUS, 7, true );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v4 );
        expected = orbit .getAxis( Axis.PLUS, 7, true );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v5 );
        expected = orbit .getAxis( Axis.PLUS, 7, false );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v6 );
        expected = orbit .getAxis( Axis.MINUS, 7, false );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v7 );
        expected = orbit .getAxis( Axis.MINUS, 0, false );
        assertEquals( expected, axis );

        axis = orbit .getAxis( v8 );
        expected = orbit .getAxis( Axis.PLUS, 0, false );
        assertEquals( expected, axis );
    }
}
