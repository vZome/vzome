
package com.vzome.api;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;

public class Vector {

	private final AlgebraicVector vector;
	
	public Vector( Number x, Number y, Number z )
	{
		AlgebraicField field = x .getField();
		AlgebraicVector loc = field .origin( 3 );
		loc .setComponent( 0, x .getAlgebraicNumber() );
		loc .setComponent( 1, y .getAlgebraicNumber() );
		loc .setComponent( 2, z .getAlgebraicNumber() );
		this .vector = loc;
	}

	Vector( AlgebraicVector location )
	{
		this .vector = location;
	}
	
	public Number getX()
	{
		AlgebraicNumber val = this .vector .getComponent( 0 );
		return new Number( val, false );
	}

	public Number getY()
	{
	    AlgebraicNumber val = this .vector .getComponent( 1 );
		return new Number( val, false );
	}

	public Number getZ()
	{
        AlgebraicNumber val = this .vector .getComponent( 2 );
        return new Number( val, false );
	}

	AlgebraicVector getAlgebraicVector()
	{
		return this .vector;
	}
	
	public Vector plus( Vector rhs )
	{
		return new Vector( this.vector .plus( rhs.vector ) );
	}

	public Vector minus( Vector rhs )
	{
		return new Vector( this.vector .minus( rhs.vector ) );
	}

	public Vector times( Number n )
	{
		return new Vector( this.vector .scale( n .getAlgebraicNumber() ) );
	}
	
	public boolean isParallel( Vector other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		return AlgebraicVectors.areParallel(this .vector, other .vector );
	}

    @Override
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			Vector that = (Vector) other;
			return this .vector .equals( that .vector );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

    @Override
	public int hashCode()
	{
	    return this .vector .hashCode();
	}
	
    @Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		this .vector .getVectorExpression( buf, AlgebraicField.DEFAULT_FORMAT );
		return buf .toString();
	}
}
