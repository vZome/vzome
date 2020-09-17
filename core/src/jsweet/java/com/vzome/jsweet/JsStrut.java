package com.vzome.jsweet;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Group;
import com.vzome.core.model.Strut;

public class JsStrut extends JsManifestation implements Strut
{
    public JsStrut( AlgebraicField field, int[][][] coords )
    {
        super( field, coords );
    }

    @Override
    public boolean isUnnecessary()
    {
        return false;
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
    public Group getContainer()
    {
        return null;
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
    public void setZoneVector(AlgebraicVector vector)
    {
    }

    
    
    
    
    @Override
    public Iterator<Construction> getConstructions()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Construction getFirstConstruction()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Element getXml(Document doc)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean isHidden()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean isRendered()
    {
        return false;
    }

    @Override
    public AlgebraicVector getCentroid()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void addConstruction(Construction mConstruction)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void removeConstruction(Construction mConstruction)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void setHidden(boolean b)
    {}

    @Override
    public void setContainer(Group container)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector getZoneVector()
    {
        throw new RuntimeException( "unimplemented" );
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
