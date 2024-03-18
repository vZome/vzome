package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.SegmentJoiningPoints;

/**
 * @author Scott Vorthmann
 */
public class StrutImpl extends ManifestationImpl implements Strut
{
	private final AlgebraicVector m_end1, m_end2;
	private AlgebraicVector zoneVector;
    private String label;

    public StrutImpl( AlgebraicVector end1, AlgebraicVector end2 )
    {
        super();

        m_end1 = end1;
        m_end2 = end2;
    }
    
    public AlgebraicVector getZoneVector()
    {
    	if ( this .zoneVector != null )
    		return this .zoneVector;
    	else
    		return this .getOffset();
    }
    
    public void setZoneVector( AlgebraicVector vector )
    {
    	this .zoneVector = vector;
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
        if ( ! ( obj instanceof StrutImpl ) )
            return false;
        StrutImpl other = (StrutImpl) obj;
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
	public int compareTo( Strut other ) {
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
    public AlgebraicVector getLocation()
    {
        return m_end1;
    }

	@Override
    public AlgebraicVector getCentroid()
    {
        return AlgebraicVectors.getCentroid(new AlgebraicVector[] { m_end1, m_end2 });
    }

    @Override
    public Construction toConstruction()
    {
        Construction first = this .getFirstConstruction();
        if ( first != null && first .is3d() )
            return first;
        
        AlgebraicField field = m_end1 .getField();
        Point pt1 = new FreePoint( field .projectTo3d( m_end1, true ) );
        Point pt2 = new FreePoint( field .projectTo3d( m_end2, true ) );
        return new SegmentJoiningPoints( pt1, pt2 );
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

    @Override
    public void setLabel( String label )
    {
        this.label = label;
    }

    @Override
    public String getLabel()
    {
        return this .label;
    }
}
