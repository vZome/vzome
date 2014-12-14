
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;

public class Number
{
	private final AlgebraicField field;
	private final int[] number;
	
	public Number( AlgebraicField field, int... i )
	{
		this.field = field;

		if ( i .length == 1 )
			number = field .createAlgebraicNumber( i[ 0 ], 0, 1, 0 );
		else if ( i .length == 2 )
			number = field .createAlgebraicNumber( i[ 0 ], i[ 1 ], 1, 0 );
		else
		{
			if ( i .length > field .getOrder() )
				throw new IllegalArgumentException( "too many integer arguments for this field: " + field .getName() );
			number = new int[ field .getOrder() * 2 ];
			for (int j = 0; j < number.length/2; j++) {
				number[ 2*j + 0 ] = 0;
				number[ 2*j + 1 ] = 1;
			}
			for (int j = 0; j < i.length; j++) {
				number[ 2*j + 0 ] = i[ j ];
			}
		}
	}
	
	Number( AlgebraicField field, int[] number, boolean dummy )
	{
		this.field = field;
		this.number = number;
	}
	
	public double value()
	{
		return field .evaluateNumber( number );
	}
	
	public Number plus( Number that )
	{
		return new Number( this.field, field .add( this.number, that.number ), false );
	}
	
	public Number minus( Number that )
	{
		return new Number( this.field, field .subtract( this.number, that.number ), false );
	}
	
	public Number times( Number that )
	{
		return new Number( this.field, field .multiply( this.number, that.number ), false );
	}
	
	public Number dividedBy( Number that )
	{
		return new Number( this.field, field .divide( this.number, that.number ), false );
	}
	
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			Number that = (Number) other;
			return Arrays.equals( this .number, that .number );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

	public int hashCode()
	{
	    return Arrays .hashCode( this .number );
	}

	public AlgebraicField getField()
	{
		return this .field;
	}
	
	int[] getIntArray()
	{
		return this .number;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		this.field .getNumberExpression( buf, this .number, 0, 0 );
		return buf .toString();
	}
}
