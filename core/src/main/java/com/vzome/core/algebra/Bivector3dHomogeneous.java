
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Bivector3dHomogeneous
{
    final AlgebraicNumber e12, e23, e31, e10, e20, e30;
	private final AlgebraicField field;

	public Bivector3dHomogeneous( AlgebraicNumber e12, AlgebraicNumber e23, AlgebraicNumber e31, AlgebraicNumber e10, AlgebraicNumber e20, AlgebraicNumber e30, AlgebraicField field )
	{
		this .e12 = e12;
		this .e23 = e23;
		this .e31 = e31;
		this .e10 = e10;
		this .e20 = e20;
		this .e30 = e30;
		this .field = field;
	}

	public Trivector3dHomogeneous outer( Vector3dHomogeneous that )
	{
		AlgebraicNumber e123 = this.e12 .times( that.e3 ) .plus( this.e23 .times( that.e1 ) ) .plus( this.e31 .times( that.e2 ) );
		
		AlgebraicNumber e310 = this.e10 .times( that.e3 ) .plus( this.e31 .times( that.e0 ) ) .minus( this.e30 .times( that.e1 ) );
		AlgebraicNumber e320 = this.e20 .times( that.e3 ) .minus( this.e30 .times( that.e2 ) ) .minus( this.e23 .times( that.e0 ) );
		AlgebraicNumber e120 = this.e12 .times( that.e0 ) .plus( this.e20 .times( that.e1 ) ) .minus( this.e10 .times( that.e2 ) );
		return new Trivector3dHomogeneous( e123, e310, e320, e120, field );
	}
}
