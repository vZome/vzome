
package com.vzome.api;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.model.Manifestation;

public class Panel {
	
	private final com.vzome.core.model.Panel manifestation;
	
	public Panel( com.vzome.core.model.Panel panel )
	{
		this .manifestation = panel;
	}
	
	Manifestation getManifestation()
	{
		return this .manifestation;
	}

	public Vector location()
	{
		return new Vector( this .manifestation .getLocation() );
	}

	public List<Vector> vertices()
	{
		ArrayList<Vector> list = new ArrayList<>();
		for ( AlgebraicVector vertex : this .manifestation ) {
			list .add( new Vector( vertex ) );
		}
		return list;
	}
	
    @Override
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
}
