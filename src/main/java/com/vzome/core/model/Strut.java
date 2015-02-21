package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class Strut extends Manifestation
{
	private AlgebraicVector m_end1, m_end2;

    public Strut( AlgebraicVector end1, AlgebraicVector end2 )
    {
        super();

        m_end1 = end1;
        m_end2 = end2;
    }

	public int hashCode()
	{
        int result = m_end1 .hashCode() ^ m_end2 .hashCode();
        return result;
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
        AlgebraicVector otherStart = strut .m_end1;
        AlgebraicVector otherEnd = strut .m_end2;
        if ( otherStart .equals( m_end1 ) ) 
            return otherEnd .equals( m_end2 );
        else
            if ( otherEnd .equals( m_end1 ) )
                return otherStart .equals( m_end2 );
            else
                return false;
	}

    public AlgebraicVector getLocation()
    {
        return m_end1;
    }

    public AlgebraicVector getEnd()
    {
        return m_end2;
    }
    
    public AlgebraicVector getOffset()
    {
        return m_end2 .minus( m_end1 );
    }

    public String toString()
    {
        return "strut from " + m_end1 .toString() + " to " + m_end2 .toString();
    }
}


