package com.vzome.core.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.Projection;

public class ColoredMeshJson
{
    public static void generate( Iterable<Manifestation> model, AlgebraicField field, Writer writer ) throws IOException
    {
        SortedSet<AlgebraicVector> vertices = new TreeSet<>();
        ArrayList<JsonNode> balls = new ArrayList<>();
        ArrayList<JsonNode> struts = new ArrayList<>();
        ArrayList<JsonNode> panels = new ArrayList<>();
        AlgebraicVector lastBall = null;
        AlgebraicVector lastVertex = null;

        // phase one: find and index all vertices
        for ( Manifestation man : model ) {
            if ( man instanceof Connector )
            {
                lastBall = man .getLocation();
                vertices .add( lastBall );
            }
            else if ( man instanceof Strut )
            {
                lastVertex = man .getLocation();
                vertices .add( lastVertex );
                vertices .add( ((Strut) man) .getEnd() );
            }
            else if ( man instanceof Panel )
            {
                for ( AlgebraicVector vertex : (Panel) man ) {
                    lastVertex = vertex;
                    vertices .add( vertex );
                }
            }
        }
        final AlgebraicVector origin = ( lastBall != null )? lastBall : lastVertex;
        if ( lastBall != null )
            lastVertex = lastBall;

        // Up to this point, the vertices TreeSet has collected and sorted every unique vertex of every manifestation.
        // From now on we'll need their index, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        ArrayList<AlgebraicVector> sortedVertexList = new ArrayList<>(vertices);
        // we no longer need the vertices collection, 
        // so set it to null to free the memory and to ensure we don't use it later by mistake.
        vertices = null;

        // phase three: generate the JSON
        ObjectMapper mapper = new ObjectMapper();
        for ( Manifestation man : model ) {
            if ( man instanceof Connector )
            {
                ObjectNode ballJson = mapper .createObjectNode();
                ballJson .set( "vertex", mapper .valueToTree( sortedVertexList .indexOf( man .getLocation() ) ) );
                ballJson .set( "color", mapper .valueToTree( man .getColor() .toWebString() ) );
                balls .add( ballJson );
            }
            else if ( man instanceof Strut )
            {
                ObjectNode strutJson = mapper .createObjectNode();
                int start = sortedVertexList .indexOf( man .getLocation() );
                int end = sortedVertexList .indexOf( ((Strut) man) .getEnd() );
                JsonNode ends = mapper .valueToTree( new int[]{ start, end } );
                strutJson .set( "vertices", ends );
                strutJson .set( "color", mapper .valueToTree( man .getColor() .toWebString() ) );
                struts .add( strutJson );
            }
            else if ( man instanceof Panel )
            {
                ObjectNode panelJson = mapper .createObjectNode();
                @SuppressWarnings("unchecked")
                Stream<AlgebraicVector> vertexStream = StreamSupport.stream( ( (Iterable<AlgebraicVector>) man ).spliterator(), false);
                JsonNode node = mapper .valueToTree( vertexStream.map( v -> sortedVertexList .indexOf( v ) ). collect( Collectors.toList() ) );
                panelJson .set( "vertices", node );
                panelJson .set( "color", mapper .valueToTree( man .getColor() .toWebString() ) );
                panels .add( panelJson );
            }
        }

        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper );

        generator .writeStartObject();
        generator .writeStringField( "field", field .getName() );
        generator .writeObjectField( "vertices", sortedVertexList .stream() .map( v -> v .minus( origin )) .collect( Collectors .toList() ) );
        generator .writeObjectField( "balls", balls );
        generator .writeObjectField( "struts", struts );
        generator .writeObjectField( "panels", panels );
        generator .writeEndObject();
        generator.close();
    }
    
    public interface Events
    {
        void constructionAdded( Construction c, Color color );
    }

    public static void parse( String json, AlgebraicVector offset, Events events, AlgebraicField.Registry registry ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper .readTree( json );
        
        String fieldName = ( node .has( "field") )? node .get( "field" ) .asText() : "golden";
        AlgebraicField field = registry .getField( fieldName );
        // TODO: fail if field is null
        Projection projection = new Projection.Default( field );
        
        ArrayList<AlgebraicVector> vertices = new ArrayList<>();
        {
            JsonNode verticesNode = node .get( "vertices" );
            for ( JsonNode vectorNode : verticesNode ) {
                int dimension = vectorNode .size();
                int[][] nums = new int[dimension][];
                int i = 0;
                for ( JsonNode numberNode : vectorNode ) {
                    nums[ i++ ] = mapper .treeToValue( numberNode, int[].class );
                }
                AlgebraicVector vertex = field .createIntegerVector( nums );
                vertex = projection .projectImage( vertex, false );
                if ( offset != null )
                    vertex = offset .plus( vertex );
                vertices .add( vertex );
            }
        }

        JsonNode collection = node .get( "balls" );
        if ( collection != null ) {
            try {
                // Legacy format
                int[] indices = mapper .treeToValue( collection, int[].class );
                Arrays .stream( indices )
                    .forEach( i -> events .constructionAdded( new FreePoint( vertices .get( i ) ), null ) );
            }
            catch ( JsonProcessingException e ) {
                for ( JsonNode ballNode : collection ) {
                    JsonNode vertexNode = ballNode .get( "vertex" );
                    int i = vertexNode .asInt();
                    JsonNode colorNode = ballNode .get( "color" );
                    Color color = ( colorNode == null )? null : Color .parseWebColor( colorNode .asText() );
                    events .constructionAdded( new FreePoint( vertices .get( i ) ), color );
                }
            }
        }

        collection = node .get( "struts" );
        if ( collection != null ) {
            for ( JsonNode strutNode : collection ) {
                if ( strutNode .has( "start" ) ) {
                    // Legacy format
                    int start = mapper .treeToValue( strutNode .get( "start" ), int.class );
                    int end = mapper .treeToValue( strutNode .get( "end" ), int.class );
                    Point p1 = new FreePoint( vertices .get( start ) );
                    Point p2 = new FreePoint( vertices .get( end ) );
                    events .constructionAdded( new SegmentJoiningPoints( p1, p2 ), null );
                }
                else {
                    JsonNode verticesNode = strutNode .get( "vertices" );
                    int[] ends = mapper .treeToValue( verticesNode, int[].class );
                    Point p1 = new FreePoint( vertices .get( ends[ 0 ] ) );
                    Point p2 = new FreePoint( vertices .get( ends[ 1 ] ) );
                    JsonNode colorNode = strutNode .get( "color" );
                    Color color = ( colorNode == null )? null : Color .parseWebColor( colorNode .asText() );
                    events .constructionAdded( new SegmentJoiningPoints( p1, p2 ), color );
                }
            }
        }
                
        collection = node .get( "panels" );
        if ( collection != null ) {
            for ( JsonNode panelNode : collection ) {
                JsonNode verticesNode = panelNode .get( "vertices" );
                int[] indices = mapper .treeToValue( verticesNode, int[].class );
                List<Point> points = Arrays .stream( indices )
                        .mapToObj( i -> new FreePoint( vertices .get( i ) ) )
                        .collect( Collectors .toList() );
                Polygon panel = new PolygonFromVertices( points );
                JsonNode colorNode = panelNode .get( "color" );
                Color color = ( colorNode == null )? null : Color .parseWebColor( colorNode .asText() );
                events .constructionAdded( panel, color );
            }
        }
    }
}