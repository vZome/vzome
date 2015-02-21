
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Vector3d
{
    final AlgebraicNumber a, b, c;

	public Vector3d( AlgebraicNumber a, AlgebraicNumber b, AlgebraicNumber c )
	{
		this .a = a;
		this .b = b;
		this .c = c;
	}

	public Vector3d( AlgebraicVector v )
	{
		this .a = v .getComponent( 0 );
		this .b = v .getComponent( 1 );
		this .c = v .getComponent( 2 );
	}

	public Bivector3d outer( Vector3d that )
	{
	    AlgebraicNumber a = this.a .times( that.b ) .minus( this.b .times( that.a ) );
	    AlgebraicNumber b = this.b .times( that.c ) .minus( this.c .times( that.b ) );
	    AlgebraicNumber c = this.c .times( that.a ) .minus( this.a .times( that.c ) );
		return new Bivector3d( a, b, c );
	}
}
