package com.vzome.jsweet;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Strut;

import def.js.Object;

public class JsStrut extends JsManifestation implements Strut
{
    public JsStrut( AlgebraicField field, Object adapter, int[][][] coords )
    {
        super( field, adapter, coords );
    }

    @Override
    public AlgebraicVector getLocation()
    {
        return ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ 0 ] );
    }

    @Override
    public Construction toConstruction()
    {
        return new SegmentJoiningPoints( new FreePoint( getLocation() ), new FreePoint( getEnd() ) );
    }

    @Override
    public AlgebraicVector getEnd()
    {
        return ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ 1 ] );
    }

    @Override
    public AlgebraicVector getOffset()
    {
        AlgebraicVector start = this .getLocation();
        AlgebraicVector end = this .getEnd();
        return end .minus( start );
    }

    @Override
    public void setZoneVector( AlgebraicVector vector ) {}

    @Override
    public AlgebraicVector getZoneVector()
    {
        return this .getOffset();
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
    
    public AlgebraicVector getCanonicalLesserEnd()
    {
        AlgebraicVector m_end1 = this .getLocation();
        AlgebraicVector m_end2 = this .getEnd();
        return (m_end1.compareTo(m_end2) < 0) ? m_end1 : m_end2;
    }

    public AlgebraicVector getCanonicalGreaterEnd()
    {
        AlgebraicVector m_end1 = this .getLocation();
        AlgebraicVector m_end2 = this .getEnd();
        return (m_end1.compareTo(m_end2) > 0) ? m_end1 : m_end2;
    }
}
