package com.vzome.fields.heptagon;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

public class HeptagonalAntiprismSymmetryTest {

	@Test
	public void testPermutations()
	{
		HeptagonField field = new HeptagonField();
		Symmetry symm = new HeptagonalAntiprismSymmetry( field, "blue", null );

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
	public void testBlueOrbit()
	{
		HeptagonField field = new HeptagonField();
		Symmetry symm = new HeptagonalAntiprismSymmetry( field, "blue", null );

		Direction orbit = symm .getDirection( "blue" );
	}

	@Test
	public void testOrientations()
	{
		HeptagonField field = new HeptagonField();
		Symmetry symm = new HeptagonalAntiprismSymmetry( field, "blue", null );

		AlgebraicMatrix m2 = symm .getMatrix( 2 );
		AlgebraicMatrix m4 = symm .getMatrix( 4 );
		AlgebraicMatrix m6 = symm .getMatrix( 6 );
		
		assertEquals( m2, m6 .times( m6 .times( m4 ) ) );
	}
}
