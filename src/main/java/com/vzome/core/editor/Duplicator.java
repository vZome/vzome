
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class Duplicator
{
    private Map vertexData = new HashMap();
    private final ModelRoot root;
    private final ChangeManifestations edit;
    private final AlgebraicVector offset;

    public Duplicator( ChangeManifestations edit, ModelRoot root, AlgebraicVector offset )
    {
        this .edit = edit;
        this .root = root;
        this .offset = offset;
    }


    public void duplicateManifestation( Manifestation man )
    {
    	Construction constr = duplicateConstruction( man );
    	edit .manifestConstruction( constr );
    }

    public Construction duplicateConstruction( Manifestation man )
    {
        if ( man instanceof Connector )
        {
            AlgebraicVector vector = ((Connector) man) .getLocation();
            return getVertex( vector );
        }
        else if ( man instanceof Strut )
        {
            Strut strut = (Strut) man;
            Point p1 = getVertex( strut .getLocation() );
            Point p2 = getVertex( strut .getEnd() );
            return new SegmentJoiningPoints( p1, p2 );
        }
        else if ( man instanceof Panel )
        {
            List vs = new ArrayList();
            for ( Iterator verts = ((Panel) man) .getVertices(); verts .hasNext(); )
                vs .add( getVertex( (AlgebraicVector) verts .next() ));
            return new PolygonFromVertices( (Point[]) vs .toArray( new Point[0] ) );
        }
        return null;
    }
        
    protected Point getVertex( AlgebraicVector vertexVector )
    {
        Point result = (Point) vertexData .get( vertexVector);
        if ( result == null )
        {
            AlgebraicVector key = vertexVector;
            if ( this .offset != null )
                vertexVector = vertexVector .plus( this.offset );
            result = new FreePoint( vertexVector, root );
            vertexData .put( key, result );
        }
        return result;
    }
}
