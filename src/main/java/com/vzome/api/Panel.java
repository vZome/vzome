
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.model.Manifestation;

public class Panel {
	
	private final AlgebraicField field;
	private final com.vzome.core.model.Panel manifestation;
	
	public Panel( AlgebraicField field, com.vzome.core.model.Panel panel )
	{
		this .field = field;
		this .manifestation = panel;
	}
	
	Manifestation getManifestation()
	{
		return this .manifestation;
	}

	public Vector location()
	{
		return new Vector( this .field, this .manifestation .getLocation() );
	}

	public List<Vector> vertices()
	{
		ArrayList<Vector> list = new ArrayList<Vector>();
		for ( int[] intArray : this .manifestation ) {
			list .add( new Vector( this .field, intArray ) );
		}
		return list;
	}
	
	public boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		if ( ! ( other instanceof Panel ) )
			return false;
		try  {
			Panel that = (Panel) other;
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
