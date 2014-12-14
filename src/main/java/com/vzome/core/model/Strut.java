package com.vzome.core.model;

import java.util.Arrays;

import com.vzome.core.algebra.RationalVectors;


/**
 * @author Scott Vorthmann
 */
public class Strut extends Manifestation
{
	private int[] /*AlgebraicVector*/ m_end1, m_end2;

    public Strut( /*AlgebraicVector*/ int[] end1, /*AlgebraicVector*/ int[] end2 )
    {
        super();

        m_end1 = end1;
        m_end2 = end2;
    }

	public int hashCode()
	{
        return RationalVectors .hashCode( m_end1 ) ^ RationalVectors .hashCode( m_end2 );
	}

	public boolean equals( Object other )
	{
        if ( other == null )
            return false;
        if ( other == this )
            return true;
        if ( ! ( other instanceof Strut ) )
            return false;
        Strut strut = (Strut) other;
        /*AlgebraicVector*/ int[] otherStart = strut .m_end1;
        /*AlgebraicVector*/ int[] otherEnd = strut .m_end2;
        if ( Arrays .equals( otherStart, m_end1 ) ) 
            return Arrays .equals( otherEnd, m_end2 );
        else
            if ( Arrays .equals( otherEnd, m_end1 ) )
                return Arrays .equals( otherStart, m_end2 );
            else
                return false;
	}

    public int[] /*AlgebraicVector*/ getLocation()
    {
        return m_end1;
    }

    public int[] /*AlgebraicVector*/ getEnd()
    {
        return m_end2;
    }
    
    public int[] /*AlgebraicVector*/ getOffset()
    {
        return RationalVectors .subtract( m_end2, m_end1 );
    }

    public String toString()
    {
        return "strut from " + RationalVectors .toString( m_end1 ) + " to " + RationalVectors .toString( m_end2 );
    }
}


