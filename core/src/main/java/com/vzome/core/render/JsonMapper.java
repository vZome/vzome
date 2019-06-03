package com.vzome.core.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.construction.Polygon;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

/**
 * This will accumulate shapes for the lifetime of the object.
 * 
 * TODO: support panels
 * 
 * @author vorth
 *
 */
public class JsonMapper
{
    private static class RealTrianglesView implements AlgebraicNumber.Views.Real, Polygon.Views.Triangles {}

    // Keep things simple for the client code: all real numbers, all faces triangulated
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper .writerWithView( RealTrianglesView.class );

    private final Set<String> shapeIds = new HashSet<>();
    private Map<AlgebraicMatrix,Quat4d> rotations = new HashMap<>();
    
    public ObjectMapper getObjectMapper()
    {
        return this .objectMapper;
    }
    
    public ObjectNode getShapeNode( Polyhedron shape )
    {
        String shapeId = shape .getGuid() .toString();
        if ( ! this .shapeIds .contains( shapeId ) )
        {
            this .shapeIds .add( shapeId );
            ObjectNode node = this .objectMapper .createObjectNode();
            node .set( "shape", this .asTreeWithView( shape ) );
            return node;
        }
        else
            return null; // a known shape, returned earlier
    }
    
    public ObjectNode getObjectNode( RenderedManifestation rm )
    {
        Manifestation man = rm .getManifestation();
        Polyhedron shape = rm .getShape();
        String shapeId = shape .getGuid() .toString();
        if ( man instanceof Strut )
        {

            ObjectNode node = this .objectMapper .createObjectNode();
            node .put( "shape", shapeId );

            node .put( "color", rm .getColor() .toWebString() );

            node .set( "start", this .asTreeWithView( rm .getLocation() ) );

            Quat4d quaternion = getQuaternion( rm .getOrientation() );
            node .set( "rotation", this .asTreeWithView( quaternion ) );

            return node;
        }
        else if ( man instanceof Connector )
        {
            ObjectNode node = this .objectMapper .createObjectNode();
            node .put( "shape", shapeId );

            Color color = rm .getColor();
            if ( color == null )
                color = Color.WHITE;
            node .put( "color", color .toWebString() );

            RealVector center = ((Connector) man) .getLocation() .toRealVector();
            node .set( "center", this .asTreeWithView( center ) );

            return node;
        }
        else
            return null;
    }

    private JsonNode asTreeWithView( Object object )
    {
        try {
            return objectMapper .readTree( objectWriter .writeValueAsString( object ) );
        } catch (IOException e) {
            e .printStackTrace();
            return objectMapper .createObjectNode();
        }
    }

    private Quat4d getQuaternion( AlgebraicMatrix orientation )
    {
        Quat4d result = this .rotations .get( orientation );
        if ( result == null ) {
            Matrix4d matrix = new Matrix4d();
            for ( int i = 0; i < 3; i++) {
                for ( int j = 0; j < 3; j++) {
                    double value = orientation .getElement( i, j ) .evaluate();
                    matrix .setElement( i, j, value );
                }
            }
            result = new Quat4d();
            matrix .get( result );
            this .rotations .put( orientation, result );
        }
        return result;
    }

}
