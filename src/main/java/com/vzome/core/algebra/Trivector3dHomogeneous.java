
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.algebra;

public class Trivector3dHomogeneous
{
    final AlgebraicNumber e123, e310, e320, e120;
	private final AlgebraicField field;

	public Trivector3dHomogeneous( AlgebraicNumber e123, AlgebraicNumber e310, AlgebraicNumber e320, AlgebraicNumber e120, AlgebraicField field )
	{
		this.e123 = e123;
		this.e310 = e310;
		this.e320 = e320;
		this.e120 = e120;
		this.field = field;
	}
	
	public Vector3dHomogeneous dual()
	{
		return new Vector3dHomogeneous( e320 .negate(), e310, e120, e123 .negate(), field );
	}

}
