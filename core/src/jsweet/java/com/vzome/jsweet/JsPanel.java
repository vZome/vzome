package com.vzome.jsweet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.model.Group;
import com.vzome.core.model.Panel;

import def.js.Object;

public class JsPanel extends JsManifestation implements Panel
{
    public JsPanel( AlgebraicField field, Object adapter, int[][][] coords )
    {
        super( field, adapter, coords );
    }

    @Override
    public Group getContainer()
    {
        return null;
    }

    @Override
    public Construction toConstruction()
    {
        List<Point> projected = new ArrayList<Point>();
        for (int i = 0; i < this.vectors.length; i++) {
            AlgebraicVector pt = ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ i ] );
            projected .add( new FreePoint( field .projectTo3d( pt, true ) ) );
        }
        return new PolygonFromVertices( projected );
    }

    @Override
    public AlgebraicVector getLocation()
    {
        return null;
    }

    @Override
    public AlgebraicVector getNormal()
    {
        return this .getZoneVector();
    }

    @Override
    public AlgebraicVector getZoneVector()
    {
        AlgebraicVector v0 = ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ 0 ] );
        AlgebraicVector v1 = ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ 1 ] );
        AlgebraicVector v2 = ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ 2 ] );
        return AlgebraicVectors.getNormal(v0, v1, v2);
    }

    @Override
    public Iterator<AlgebraicVector> iterator()
    {
        // There is probably a Java built-in for this 
        return new Iterator<AlgebraicVector>()
        {
            int i = 0;
            
            @Override
            public boolean hasNext()
            {
                return i < vectors.length;
            }

            @Override
            public AlgebraicVector next()
            {
                return ((JsAlgebraicField) field) .createVectorFromTDs( vectors[ i++ ] );
            }
        };
    }

    @Override
    public void setZoneVector( AlgebraicVector vector ) {}

    @Override
    public AlgebraicVector getFirstVertex()
    {
        return ((JsAlgebraicField) this .field) .createVectorFromTDs( this .vectors[ 0 ] );
    }

    
    
    
    
    
    

    @Override
    public Iterator<Construction> getConstructions()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Element getXml(Document doc)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector getCentroid()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void setContainer( Group container )
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Iterator<AlgebraicVector> getVertices()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int getVertexCount()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public RealVector getNormal( Embedding embedding )
    {
        throw new RuntimeException( "unimplemented" );
    }
}
