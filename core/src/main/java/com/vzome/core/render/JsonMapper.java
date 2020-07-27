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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Color;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
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
    private static class RealTrianglesView implements AlgebraicNumber.Views.Real, Polyhedron.Views.Triangles {}

    // Keep things simple for the client code: all real numbers, all faces triangulated
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    private final Set<String> shapeIds = new HashSet<>();
    private Map<AlgebraicMatrix,ObjectNode> rotations = new HashMap<>();
    
    public JsonMapper()
    {
        this( RealTrianglesView.class );
    }
    
    public JsonMapper( Class<?> view )
    {
        this .objectMapper = new ObjectMapper();
        this .objectWriter = objectMapper .writerWithView( view );
    }
    
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
            node .put( "id", shape .getGuid() .toString() );

            ArrayNode arrayNode = this .objectMapper .createArrayNode();
            for ( AlgebraicVector vector : shape .getVertexList() ) {
                arrayNode .add( this .getVectorNode( vector .toRealVector() ) );
            }
            node .set( "vertices", arrayNode );

            arrayNode = this .objectMapper .createArrayNode();
            for ( Polyhedron.Face.Triangle triangle : shape .getTriangleFaces() ) {
                ObjectNode tNode = this .objectMapper .createObjectNode();
                tNode .set( "vertices", this .objectMapper .valueToTree( triangle .vertices ) );
                // Sending normals bloats the JSON to the point where it is untenable for CheerpJ cjStringJavaToJs
                // tNode .set( "normal", this .getVectorNode( triangle .normal ) );
                arrayNode .add( tNode );
            }
            node .set( "faces", arrayNode );
            
            return node;
        }
        else
            return null; // a known shape, returned earlier
    }
    
    public ObjectNode getObjectNode( RenderedManifestation rm )
    {
        try {
            Manifestation man = rm .getManifestation();
            Polyhedron shape = rm .getShape();
            String shapeId = shape .getGuid() .toString();
            if ( man instanceof Strut )
            {

                ObjectNode node = this .objectMapper .createObjectNode();
                node .put( "shape", shapeId );

                node .put( "color", rm .getColor() .toWebString() );

                node .set( "position", this .getLocation( rm ) );

                ObjectNode quaternion = getQuaternionNode( rm .getOrientation() );
                node .set( "rotation", quaternion );

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

                node .set( "position", this .getLocation( rm ) );

                return node;
            }
            else if ( man instanceof Panel )
            {
                ObjectNode node = this .objectMapper .createObjectNode();
                node .put( "shape", shapeId );

                Color color = rm .getColor();
                if ( color == null )
                    color = Color.WHITE;
                node .put( "color", color .toWebString() );

                node .set( "position", this .getLocation( rm ) );

                return node;
            }
            else
                return null;
        } catch (RuntimeException e) {
            System.err.println( e .getMessage() );
            e .printStackTrace();
            throw e;
        }
    }
    
    private ObjectNode getVectorNode( RealVector vector )
    {
        ObjectNode node = this .objectMapper .createObjectNode();
        node .put( "x", (float) vector .x );
        node .put( "y", (float) vector .y );
        node .put( "z", (float) vector .z );
        return node;
    }
    
    private ObjectNode getLocation( RenderedManifestation rm )
    {
        return getVectorNode( rm .getLocation() );
    }

    @Deprecated
    /**
     * This doesn't work with CheerpJ for some reason.
     * @param object
     * @return
     */
    private JsonNode asTreeWithView( Object object )
    {
        try {
            String str = objectWriter .writeValueAsString( object );
            return objectMapper .readTree( str );
        } catch (IOException e) {
            e .printStackTrace();
            return objectMapper .createObjectNode();
        }
    }

    private ObjectNode getQuaternionNode( AlgebraicMatrix orientation )
    {
        ObjectNode result = this .rotations .get( orientation );
        if ( result == null ) {
            Matrix4d matrix = new Matrix4d();
            for ( int i = 0; i < 3; i++) {
                for ( int j = 0; j < 3; j++) {
                    double value = orientation .getElement( i, j ) .evaluate();
                    matrix .setElement( i, j, value );
                }
            }
            Quat4d quat = new Quat4d();
            matrix .get( quat );
            result = this .objectMapper .createObjectNode();
            result .put( "x", (float) quat .x );
            result .put( "y", (float) quat .y );
            result .put( "z", (float) quat .z );
            result .put( "w", (float) quat .w );
            this .rotations .put( orientation, result );
        }
        return result;
    }
}
