package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class Connector extends Manifestation
{

	public Connector( AlgebraicVector loc )
	{
		super();
		
		m_center = loc;
	}

	protected AlgebraicVector m_center;

	public AlgebraicVector getLocation()
    {
		return m_center;
	}

	public int hashCode()
	{
	    return m_center .hashCode();
	}

	public  boolean equals( Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		if ( ! ( other instanceof Connector ) )
			return false;
		Connector conn = (Connector) other;
		return this .getLocation() .equals( conn .getLocation() );
	}

    public String toString()
    {
        return "connector at " + m_center .toString();
    }
}


