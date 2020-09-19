package com.vzome.jsweet;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Group;

public class JsBall extends JsManifestation implements Connector
{
    public JsBall( AlgebraicField field, int[][][] coords )
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
        return new FreePoint( getLocation() );
    }

    @Override
    public Group getContainer()
    {
        return null;
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
    public int compareTo( Connector other )
    {
        throw new RuntimeException( "unimplemented" );
    }

}