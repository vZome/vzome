package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AlgebraicVectorTest {

	@Test
	public void testCrossProduct()
	{
		AlgebraicField field = new PentagonField();
		AlgebraicNumber two = field .createRational( 2 );
		AlgebraicNumber three = field .createRational( 3 );
		AlgebraicVector x = new AlgebraicVector( two, field .one(), two );
		AlgebraicVector y = new AlgebraicVector( three, field .one() .negate(), three .negate() );
		AlgebraicVector result = x .cross( y );
		AlgebraicVector target = new AlgebraicVector( field .one() .negate(), field .createRational( 12 ), field .createRational( -5 ) );
		assertEquals( target, result );
	}

}
