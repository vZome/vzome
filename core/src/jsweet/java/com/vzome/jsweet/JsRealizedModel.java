package com.vzome.jsweet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class JsRealizedModel implements RealizedModel {

    private final AlgebraicField field;

    public JsRealizedModel( AlgebraicField field )
    {
        this.field = field;
    }

    @Override
    public Iterator<Manifestation> iterator()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicField getField()
    {
        return this.field;
    }

    @Override
    public Manifestation findConstruction(Construction c)
    {
        return manifest( c );
    }

    private Manifestation manifest( Construction c )
    {
        Manifestation m = null;
        if ( c instanceof Point )
        {
            Point p = (Point) c;
            m = new JsBall( p .getLocation() );
        }
        else if ( c instanceof Segment )
        {
            Segment s = (Segment) c;
            AlgebraicVector start = s .getStart();
            AlgebraicVector end = s .getEnd();
            if ( ! start .equals( end ) )
            {
                m = null; //new StrutImpl( start, end );
            }
        }
        else if ( c instanceof Polygon )
        {
            Polygon p = (Polygon) c;
            List<AlgebraicVector> vertices = new ArrayList<>();
            for (int i = 0; i < p.getVertexCount(); i++) {
                vertices .add( p.getVertex( i ) );
            }
            m = null; // new PanelImpl( vertices );
        }
        return m;
    }

    @Override
    public void show( Manifestation mManifestation )
    {
        System.out.println( "show ball at: " + mManifestation .getLocation() .toRealVector() );
    }

    @Override
    public Manifestation removeConstruction(Construction c)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Manifestation getManifestation(Construction c)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int size()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void hide(Manifestation mManifestation)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void add(Manifestation m)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void remove(Manifestation mManifestation)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void setColor(Manifestation manifestation, Color color)
    {
        throw new RuntimeException( "unimplemented" );
    }
}
