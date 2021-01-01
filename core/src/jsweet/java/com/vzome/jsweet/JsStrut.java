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
    public int compareTo(Strut other)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector getCanonicalLesserEnd()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector getCanonicalGreaterEnd()
    {
        throw new RuntimeException( "unimplemented" );
    }
}
