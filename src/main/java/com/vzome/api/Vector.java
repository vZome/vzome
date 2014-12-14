
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;

public class Vector {

	private final AlgebraicField field;
	private final int[] vector;
	
	public Vector( Number x, Number y, Number z )
	{
		this .field = x .getField();
		int[] loc = field .origin( 3 );
		field .setVectorComponent( loc, 0, x .getIntArray() );
		field .setVectorComponent( loc, 1, y .getIntArray() );
		field .setVectorComponent( loc, 2, z .getIntArray() );
		this .vector = loc;
	}

	Vector( AlgebraicField field, int[] location )
	{
		this .field = field;
		this .vector = location;
	}
	
	public Number getX()
	{
		int[] val = this .field .getVectorComponent( this .vector, 0 );
		return new Number( this .field, val, false );
	}

	public Number getY()
	{
		int[] val = this .field .getVectorComponent( this .vector, 1 );
		return new Number( this .field, val, false );
	}

	public Number getZ()
	{
		int[] val = this .field .getVectorComponent( this .vector, 2 );
		return new Number( this .field, val, false );
	}

	int[] getIntArray()
	{
		return this .vector;
	}
	
	public Vector plus( Vector rhs )
	{
		return new Vector( this .field, this .field .add( this.vector, rhs.vector ) );
	}

	public Vector minus( Vector rhs )
	{
		return new Vector( this .field, this .field .subtract( this.vector, rhs.vector ) );
	}

	public Vector times( Number n )
	{
		return new Vector( this .field, this .field .scaleVector( this.vector, n .getIntArray() ) );
	}
	
	public boolean isParallel( Vector other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		return this .field .isOrigin( this .field .cross( this .vector, other .vector ) );
	}

	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			Vector that = (Vector) other;
			return Arrays.equals( this .vector, that .vector );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

	public int hashCode()
	{
	    return Arrays .hashCode( this .vector );
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		this.field .getVectorExpression( buf, this .vector, AlgebraicField.DEFAULT_FORMAT );
		return buf .toString();
	}
}
