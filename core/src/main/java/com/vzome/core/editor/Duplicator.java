
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class Duplicator
{
    private Map<AlgebraicVector, Point> vertexData = new HashMap<>();
    private final ChangeManifestations edit;
    private final AlgebraicVector offset;

    public Duplicator( ChangeManifestations edit, AlgebraicVector offset )
    {
        this .edit = edit;
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
            List<Point> vs = new ArrayList<>();
            for (AlgebraicVector v : ((Panel) man)) {
                vs .add( getVertex( v ));
            }
            return new PolygonFromVertices( vs .toArray( new Point[0] ) );
        }
        return null;
    }
        
    protected Point getVertex( AlgebraicVector vertexVector )
    {
        Point result = vertexData .get( vertexVector);
        if ( result == null )
        {
            AlgebraicVector key = vertexVector;
            if ( this .offset != null )
                vertexVector = vertexVector .plus( this.offset );
            result = new FreePoint( vertexVector );
            vertexData .put( key, result );
        }
        return result;
    }
}
