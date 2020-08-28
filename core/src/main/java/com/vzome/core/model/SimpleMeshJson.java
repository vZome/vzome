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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.Projection;

public class SimpleMeshJson
{
    public static void generate( Iterable<Manifestation> model, AlgebraicField field, Writer writer ) throws IOException
    {
        SortedSet<AlgebraicVector> vertices = new TreeSet<>();
        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<JsonNode> edgeNodes = new ArrayList<>();
        ArrayList<JsonNode> faceNodes = new ArrayList<>();
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
                vertexIndices .add( sortedVertexList .indexOf( man .getLocation() ) );
            }
            else if ( man instanceof Strut )
            {
                int start = sortedVertexList .indexOf( man .getLocation() );
                int end = sortedVertexList .indexOf( ((Strut) man) .getEnd() );
                JsonNode ends = mapper .valueToTree( new int[]{ start, end } );
                edgeNodes .add( ends );
            }
            else if ( man instanceof Panel )
            {
                @SuppressWarnings("unchecked")
                Stream<AlgebraicVector> vertexStream = StreamSupport.stream( ( (Iterable<AlgebraicVector>) man ).spliterator(), false);
                JsonNode node = mapper .valueToTree( vertexStream.map( v -> sortedVertexList .indexOf( v ) ). collect( Collectors.toList() ) );
                faceNodes .add( node );
            }
        }

        JsonFactory factory = new JsonFactory() .disable( JsonGenerator.Feature.AUTO_CLOSE_TARGET );
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper );

        generator .writeStartObject();
        generator .writeStringField( "field", field .getName() );

        generator .writeFieldName( "vertices" );
        generator .writeStartArray();
        ObjectWriter objectWriter = mapper .writerWithView( AlgebraicNumber.Views.TrailingDivisor.class );
        for ( AlgebraicVector algebraicVector : sortedVertexList ) {
            algebraicVector = algebraicVector .minus( origin );
            // This awkward serialize+deserialize seems to be the only way to use views with streaming JSON
            generator .writeObject( mapper .readTree( objectWriter .writeValueAsString( algebraicVector ) ) );            
        }
        generator .writeEndArray();

        generator .writeObjectField( "edges", edgeNodes );
        generator .writeObjectField( "faces", faceNodes );
        generator .writeEndObject();
        generator.close();
    }
    
    public interface Events
    {
        void constructionAdded( Construction c );
    }

    public static void parse( String json, AlgebraicVector offset, Projection projection, Events events, AlgebraicField.Registry registry ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper .readTree( json );
        
        String fieldName = ( node .has( "field") )? node .get( "field" ) .asText() : "golden";
        AlgebraicField field = registry .getField( fieldName );
        // TODO: fail if field is null
        
        if ( ! node .has( "vertices" ) ) {
            throw new IOException( "There is no 'vertices' list in the JSON" );
        }
        if ( ! node .has( "edges" ) ) {
            throw new IOException( "There is no 'edges' list in the JSON" );
        }
        if ( ! node .has( "faces" ) ) {
            throw new IOException( "There is no 'faces' list in the JSON" );
        }

        ArrayList<AlgebraicVector> vertices = new ArrayList<>();
        {
            JsonNode verticesNode = node .get( "vertices" );
            for ( JsonNode vectorNode : verticesNode ) {
                int dimension = vectorNode .size();
                int[][] nums = new int[dimension][];
                int i = 0;
                for ( JsonNode numberNode : vectorNode ) {
                    nums[ i++ ] = mapper .treeToValue( numberNode, new int[]{}.getClass() ); // JSweet compiler confused by int[].class
                }
                AlgebraicVector vertex = field .createIntegerVectorFromTDs( nums );
                if ( vertex .dimension() > 3 )
                    vertex = projection .projectImage( vertex, false );
                if ( offset != null )
                    vertex = offset .plus( vertex );
                vertices .add( vertex );
            }
        }
        
        JsonNode collection = node .get( "edges" );
        for ( JsonNode strutNode : collection ) {
            int[] ends = mapper .treeToValue( strutNode, new int[]{}.getClass() ); // JSweet compiler confused by int[].class
            Point p1 = new FreePoint( vertices .get( ends[ 0 ] ) );
            Point p2 = new FreePoint( vertices .get( ends[ 1 ] ) );
            events .constructionAdded( p1 );
            events .constructionAdded( p2 );
            events .constructionAdded( new SegmentJoiningPoints( p1, p2 ) );
        }
        
        collection = node .get( "faces" );
        for ( JsonNode panelNode : collection ) {
            int[] indices = mapper .treeToValue( panelNode, new int[]{}.getClass() ); // JSweet compiler confused by int[].class
            List<Point> points = Arrays .stream( indices )
                    .mapToObj( i -> new FreePoint( vertices .get( i ) ) )
                    .collect( Collectors .toList() );
            Polygon panel = new PolygonFromVertices( points );
            events .constructionAdded( panel );
        }
    }
}