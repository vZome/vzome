
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Bivector3d
{
	private final AlgebraicNumber a, b, c;

	public Bivector3d( AlgebraicNumber a, AlgebraicNumber b, AlgebraicNumber c )
	{
		this .a = a;
		this .b = b;
		this .c = c;
	}
	
	/**
	 * The pseudoscalar is implied in the result.
	 * @param v
	 * @return
	 */
	public AlgebraicNumber outer( Vector3d v )
	{
	    AlgebraicNumber a = this.a .times( v .c );
	    AlgebraicNumber b = this.b .times( v .a );
	    AlgebraicNumber c = this.c .times( v .b );
		return a .plus( b ) .plus( c );
	}

}
