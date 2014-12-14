
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Vector3d
{
    final int[] a, b, c;
	private final AlgebraicField field;

	public Vector3d( int[] a, int[] b, int[] c, AlgebraicField field )
	{
		this .a = a;
		this .b = b;
		this .c = c;
		this .field = field;
	}

	public Vector3d( int[] v, AlgebraicField field )
	{
		this .a = field .getVectorComponent( v, 0 );
		this .b = field .getVectorComponent( v, 1 );
		this .c = field .getVectorComponent( v, 2 );
		this .field = field;
	}

	public Bivector3d outer( Vector3d that )
	{
		int[] a = field .subtract( field .multiply( this.a, that.b ), field .multiply( this.b, that.a ) );
		int[] b = field .subtract( field .multiply( this.b, that.c ), field .multiply( this.c, that.b ) );
		int[] c = field .subtract( field .multiply( this.c, that.a ), field .multiply( this.a, that.c ) );
		return new Bivector3d( a, b, c, field );
	}
}
