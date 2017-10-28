package com.vzome.core.algebra;

import static org.junit.Assert.*;

import org.junit.Test;

public class AlgebraicVectorTest {

	@Test
	public void testCrossProduct()
	{
		AlgebraicField field = new PentagonField();
		AlgebraicNumber two = field .createAlgebraicNumber( 2 );
		AlgebraicNumber three = field .createAlgebraicNumber( 3 );
		AlgebraicVector x = new AlgebraicVector( two, field .one(), two );
		AlgebraicVector y = new AlgebraicVector( three, field .one() .negate(), three .negate() );
		AlgebraicVector result = x .cross( y );
		AlgebraicVector target = new AlgebraicVector( field .one() .negate(), field .createAlgebraicNumber( 12 ), field .createAlgebraicNumber( -5 ) );
		assertEquals( target, result );
	}

}
