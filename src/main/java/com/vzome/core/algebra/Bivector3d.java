
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Bivector3d
{
	private final int[] a, b, c;
	private final AlgebraicField field;

	public Bivector3d( int[] a, int[] b, int[] c, AlgebraicField field )
	{
		this .a = a;
		this .b = b;
		this .c = c;
		this .field = field;
	}
	
	/**
	 * The pseudoscalar is implied in the result.
	 * @param v
	 * @return
	 */
	public int[] outer( Vector3d v )
	{
		int[] a = field .multiply( this.a, v .c );
		int[] b = field .multiply( this.b, v .a );
		int[] c = field .multiply( this.c, v .b );
		return field .add( a, field .add( b, c ) );
	}

}
