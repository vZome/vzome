
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import com.vzome.core.model.Manifestation;

public class Strut {
	
	private final com.vzome.core.model.Strut manifestation;
	private int zone = 0;
	
	public Strut( com.vzome.core.model.Strut strut )
	{
		this .manifestation = strut;
	}
	
	public Strut( com.vzome.core.model.Strut strut, int zone )
	{
		this( strut );
		this .zone = zone;
	}
	
	Manifestation getManifestation()
	{
		return this .manifestation;
	}

	public Vector location()
	{
		return new Vector( this .manifestation .getLocation() );
	}

	public Vector offset()
	{
		return new Vector( this .manifestation .getOffset() );
	}
	
    @Override
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

    @Override
	public int hashCode()
	{
	    return this .manifestation .hashCode();
	}
	
    @Override
	public String toString()
	{
		return this .manifestation .toString();
	}
	
	public int getZone()
	{
		return this .zone;
	}
}
