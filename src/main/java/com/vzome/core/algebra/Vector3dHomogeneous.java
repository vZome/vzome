
//(c) Copyright 2013, Scott Vorthmann.

package com.vzome.core.algebra;

public class Vector3dHomogeneous
{
    final BigRational[] e1, e2, e3, e0;
	private final AlgebraicField field;

	public Vector3dHomogeneous( BigRational[] e1, BigRational[] e2, BigRational[] e3, BigRational[] e0, AlgebraicField field )
	{
		this .e1 = e1;
		this .e2 = e2;
		this .e3 = e3;
		this .e0 = e0;
		this .field = field;
	}

	public Vector3dHomogeneous( int[] e1, int[] e2, int[] e3, AlgebraicField field )
	{
		this( field .makeBigElement( e1 ), field .makeBigElement( e1 ), field .makeBigElement( e1 ),
				field .makeBigElement( field .createPower( 0 ) ), field );
	}

	public Vector3dHomogeneous( int[] v, AlgebraicField field )
	{
		this( field .getVectorComponent( v, 0 ), field .getVectorComponent( v, 1 ), field .getVectorComponent( v, 2 ), field );
	}

	public Bivector3dHomogeneous outer( Vector3dHomogeneous that )
	{
		BigRational[] e12 = field .subtract( field .multiply( this.e1, that.e2 ), field .multiply( this.e2, that.e1 ) );
		BigRational[] e23 = field .subtract( field .multiply( this.e2, that.e3 ), field .multiply( this.e3, that.e2 ) );
		BigRational[] e31 = field .subtract( field .multiply( this.e3, that.e1 ), field .multiply( this.e1, that.e3 ) );
		BigRational[] e10 = field .subtract( field .multiply( this.e1, that.e0 ), field .multiply( this.e0, that.e1 ) );
		BigRational[] e20 = field .subtract( field .multiply( this.e2, that.e0 ), field .multiply( this.e0, that.e2 ) );
		BigRational[] e30 = field .subtract( field .multiply( this.e3, that.e0 ), field .multiply( this.e0, that.e3 ) );
		return new Bivector3dHomogeneous( e12, e23, e31, e10, e20, e30, field );
	}
	
	public int[] getVector()
	{
		int[] result = field .origin( 3 );
		field .setVectorComponent( result, 0, field .makeIntElement( field .divide( this.e1, this.e0 ) ) );
		field .setVectorComponent( result, 1, field .makeIntElement( field .divide( this.e2, this.e0 ) ) );
		field .setVectorComponent( result, 2, field .makeIntElement( field .divide( this.e3, this.e0 ) ) );
		return result;
	}

	public Vector3dHomogeneous dot( Bivector3dHomogeneous v )
	{
		// we keep only the grade-1 results
		BigRational[] e1 = field .subtract( field .subtract( field .multiply( this.e3, v.e31 ), field .multiply( this.e2, v.e12 ) ), field .multiply( this.e0, v.e10 ) );
		BigRational[] e2 = field .subtract( field .subtract( field .multiply( this.e1, v.e12 ), field .multiply( this.e3, v.e23 ) ), field .multiply( this.e0, v.e20 ) );
		BigRational[] e3 = field .subtract( field .subtract( field .multiply( this.e2, v.e23 ), field .multiply( this.e1, v.e31 ) ), field .multiply( this.e0, v.e30 ) );
		BigRational[] e0 = field .add( field .add( field .multiply( this.e1, v.e10 ), field .multiply( this.e2, v.e20 ) ), field .multiply( this.e3, v.e30 ) );
		return new Vector3dHomogeneous( e1, e2, e3, e0, field );
	}

	public boolean exists()
	{
		return ! field .isZero( e0 );
	}
}
