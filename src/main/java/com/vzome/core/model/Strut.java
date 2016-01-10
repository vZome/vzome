package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicVector;
import java.util.Comparator;


/**
 * @author Scott Vorthmann
 */
public class Strut extends Manifestation implements Comparable<Strut>, Comparator<Strut>
{
	private final AlgebraicVector m_end1, m_end2;

    public Strut( AlgebraicVector end1, AlgebraicVector end2 )
    {
        super();

        m_end1 = end1;
        m_end2 = end2;
    }

	@Override
	public int hashCode()
	{
        int result = m_end1 .hashCode() ^ m_end2 .hashCode();
        return result;
	}

	@Override
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
	
	@Override
	public int compareTo(Strut other) {
		if ( other == null )
			return -1;
		if ( this == other )
			return 0;
		if ( this.equals(other) )
			return 0;
        AlgebraicVector otherLoc = other.getLocation();
        return ( this. getLocation().equals( otherLoc ) ) 
            ? this. getEnd() .compareTo( other.getEnd() )
			: this. getLocation() .compareTo( otherLoc );
	}

	@Override
	public int compare(Strut o1, Strut o2) {
		return (o1 == null)
				? ((o2 == null) ? 0 : -1)
				: o1.compareTo(o2);
	}

	@Override
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

	@Override
    public String toString()
    {
        return "strut from " + m_end1 .toString() + " to " + m_end2 .toString();
    }
}
