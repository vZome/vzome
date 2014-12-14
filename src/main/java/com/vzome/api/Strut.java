
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.model.Manifestation;

public class Strut {
	
	private final AlgebraicField field;
	private final com.vzome.core.model.Strut manifestation;
	private int zone = 0;
	
	public Strut( AlgebraicField field, com.vzome.core.model.Strut strut )
	{
		this .field = field;
		this .manifestation = strut;
	}
	
	public Strut( AlgebraicField field, com.vzome.core.model.Strut strut, int zone )
	{
		this( field, strut );
		this .zone = zone;
	}
	
	Manifestation getManifestation()
	{
		return this .manifestation;
	}

	public Vector location()
	{
		return new Vector( this .field, this .manifestation .getLocation() );
	}

	public Vector offset()
	{
		return new Vector( this .field, this .manifestation .getOffset() );
	}
	
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			Strut that = (Strut) other;
			return this .manifestation .equals( that .manifestation );
		} catch( ClassCastException cce ) {
			return false;
		}
	}

	public int hashCode()
	{
	    return this .manifestation .hashCode();
	}
	
	public String toString()
	{
		return this .manifestation .toString();
	}
	
	public int getZone()
	{
		return this .zone;
	}
}
