
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;

public class Number
{
	private final AlgebraicNumber number;
	
	public Number( AlgebraicField field, int... ints )
	{
        if ( ints .length > field .getOrder() )
            throw new IllegalArgumentException( "too many integer arguments for this field: " + field .getName() );
        number = field .createAlgebraicNumber( ints );
	}
	
	Number( AlgebraicNumber number, boolean dummy )
	{
		this.number = number;
	}
	
	public double value()
	{
		return this .number .evaluate();
	}
	
	public Number plus( Number that )
	{
		return new Number( this.number .plus( that.number ), false );
	}
	
	public Number minus( Number that )
	{
		return new Number( this.number .minus( that.number ), false );
	}
	
	public Number times( Number that )
	{
		return new Number( this.number .times( that.number ), false );
	}
	
	public Number dividedBy( Number that )
	{
		return new Number( this.number .dividedBy( that.number ), false );
	}
	
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			Number that = (Number) other;
			return this .number .equals( that .number );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

	public int hashCode()
	{
	    return this .number .hashCode();
	}

	public AlgebraicField getField()
	{
		return this .number .getField();
	}
	
	AlgebraicNumber getAlgebraicNumber()
	{
		return this .number;
	}
	
	public String toString()
	{
		return this .number .toString();
	}
}
