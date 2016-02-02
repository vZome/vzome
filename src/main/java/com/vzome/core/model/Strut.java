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
	public boolean equals( Object obj )
	{
        if ( obj == null )
            return false;
        if ( obj == this )
            return true;
        if ( ! ( obj instanceof Strut ) )
            return false;
        Strut other = (Strut) obj;
        AlgebraicVector otherStart = other .m_end1;
        AlgebraicVector otherEnd = other .m_end2;
		// A strut from J to K should be considered equal to a strut from K to J.
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
        if ( this == other ) {
            return 0;
        }
        if (other.equals(this)) { // intentionally throws a NullPointerException if other is null
            return 0;
        }
        AlgebraicVector thisFirst = this.getCanonicalLesserEnd();
        AlgebraicVector thisLast = this.getCanonicalGreaterEnd();
        AlgebraicVector otherFirst = other.getCanonicalLesserEnd();
        AlgebraicVector otherLast = other.getCanonicalGreaterEnd();
        int comparison = thisFirst.compareTo( otherFirst );
		// A strut from J to K should be considered equal to a strut from K to J.
        return ( comparison  == 0 ) 
            ? thisLast.compareTo( otherLast )
			: comparison;
	}
	
	public AlgebraicVector getCanonicalLesserEnd() {
		return (m_end1.compareTo(m_end2) < 0) ? m_end1 : m_end2;
	}

	public AlgebraicVector getCanonicalGreaterEnd() {
		return (m_end1.compareTo(m_end2) > 0) ? m_end1 : m_end2;
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
