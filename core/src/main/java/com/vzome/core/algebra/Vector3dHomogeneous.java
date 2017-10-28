
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Vector3dHomogeneous
{
    final AlgebraicNumber e1, e2, e3, e0;
	private final AlgebraicField field;

	public Vector3dHomogeneous( AlgebraicNumber e1, AlgebraicNumber e2, AlgebraicNumber e3, AlgebraicNumber e0, AlgebraicField field )
	{
		this .e1 = e1;
		this .e2 = e2;
		this .e3 = e3;
		this .e0 = e0;
		this .field = field;
	}

	public Vector3dHomogeneous( AlgebraicNumber e1, AlgebraicNumber e2, AlgebraicNumber e3, AlgebraicField field )
	{
		this( e1, e1, e1, field .createPower( 0 ), field );
	}

	public Vector3dHomogeneous( AlgebraicVector v, AlgebraicField field )
	{
		this( v .getComponent( 0 ), v .getComponent( 1 ), v .getComponent( 2 ), field );
	}

	public Bivector3dHomogeneous outer( Vector3dHomogeneous that )
	{
		AlgebraicNumber e12 = this.e1 .times( that.e2 ) .minus( this.e2 .times( that.e1 ) );
		AlgebraicNumber e23 = this.e2 .times( that.e3 ) .minus( this.e3 .times( that.e2 ) );
		AlgebraicNumber e31 = this.e3 .times( that.e1 ) .minus( this.e1 .times( that.e3 ) );
		AlgebraicNumber e10 = this.e1 .times( that.e0 ) .minus( this.e0 .times( that.e1 ) );
		AlgebraicNumber e20 = this.e2 .times( that.e0 ) .minus( this.e0 .times( that.e2 ) );
		AlgebraicNumber e30 = this.e3 .times( that.e0 ) .minus( this.e0 .times( that.e3 ) );
		return new Bivector3dHomogeneous( e12, e23, e31, e10, e20, e30, field );
	}
	
	public AlgebraicVector getVector()
	{
	    return new AlgebraicVector( this.e1 .dividedBy( this.e0 ), this.e2 .dividedBy( this.e0 ), this.e3 .dividedBy( this.e0 ) );
	}

	public Vector3dHomogeneous dot( Bivector3dHomogeneous v )
	{
		// we keep only the grade-1 results
		AlgebraicNumber e1 = this.e3 .times( v.e31 ) .minus( this.e2 .times( v.e12 ) ) .minus( this.e0 .times( v.e10 ) );
		AlgebraicNumber e2 = this.e1 .times( v.e12 ) .minus( this.e3 .times( v.e23 ) ) .minus( this.e0 .times( v.e20 ) );
		AlgebraicNumber e3 = this.e2 .times( v.e23 ) .minus( this.e1 .times( v.e31 ) ) .minus( this.e0 .times( v.e30 ) );
		AlgebraicNumber e0 = this.e1 .times( v.e10 ) .plus( this.e2 .times( v.e20 ) ) .plus( this.e3 .times( v.e30 ) );
		return new Vector3dHomogeneous( e1, e2, e3, e0, field );
	}

	public boolean exists()
	{
		return ! e0 .isZero();
	}
}
