
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Bivector3dHomogeneous
{
    final BigRational[] e12, e23, e31, e10, e20, e30;
	private final AlgebraicField field;

	public Bivector3dHomogeneous( BigRational[] e12, BigRational[] e23, BigRational[] e31, BigRational[] e10, BigRational[] e20, BigRational[] e30, AlgebraicField field )
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
		BigRational[] e123 = field .add     ( field .add     ( field .multiply( this.e12, that.e3 ), field .multiply( this.e23, that.e1 ) ), field .multiply( this.e31, that.e2 ) );
		BigRational[] e310 = field .subtract( field .add     ( field .multiply( this.e10, that.e3 ), field .multiply( this.e31, that.e0 ) ), field .multiply( this.e30, that.e1 ) );
		BigRational[] e320 = field .subtract( field .subtract( field .multiply( this.e20, that.e3 ), field .multiply( this.e30, that.e2 ) ), field .multiply( this.e23, that.e0 ) );
		BigRational[] e120 = field .subtract( field .add     ( field .multiply( this.e12, that.e0 ), field .multiply( this.e20, that.e1 ) ), field .multiply( this.e10, that.e2 ) );
		return new Trivector3dHomogeneous( e123, e310, e320, e120, field );
	}
}
