
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class Ball {

	private final Connector manifestation;

	public Ball( Connector connector )
	{
		this .manifestation = connector;
	}
	
	Manifestation getManifestation()
	{
		return this .manifestation;
	}
	
	public Vector location()
	{
		return new Vector( this .manifestation .getLocation() );
	}
	
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		try  {
			Ball that = (Ball) other;
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
}
