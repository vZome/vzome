
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.algebra;

public class Trivector3dHomogeneous
{
    final BigRational[] e123, e310, e320, e120;
	private final AlgebraicField field;

	public Trivector3dHomogeneous( BigRational[] e123, BigRational[] e310, BigRational[] e320, BigRational[] e120, AlgebraicField field )
	{
		this.e123 = e123;
		this.e310 = e310;
		this.e320 = e320;
		this.e120 = e120;
		this.field = field;
	}
	
	public Vector3dHomogeneous dual()
	{
		return new Vector3dHomogeneous( field .negate( e320 ), e310, e120, field .negate( e123 ), field );
	}

}
