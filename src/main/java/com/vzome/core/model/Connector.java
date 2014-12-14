package com.vzome.core.model;

import java.util.Arrays;

import com.vzome.core.algebra.RationalVectors;


/**
 * @author Scott Vorthmann
 */
public class Connector extends Manifestation
{

	public Connector( int[] /*AlgebraicVector*/ loc )
	{
		super();
		
		m_center = loc;
	}

	protected  int[] /*AlgebraicVector*/ m_center;

	public  int[] /*AlgebraicVector*/ getLocation()
    {
		return m_center;
	}

	public int hashCode()
	{
        int result = 0;
        for ( int i = 0, j = 0; i < m_center .length; i++, j++ )
            result ^= m_center[ i ] << j;
		return result;
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
		return Arrays .equals( getLocation(), conn .getLocation() );
	}

    public String toString()
    {
        return "connector at " + RationalVectors .toString( m_center );
    }
}


