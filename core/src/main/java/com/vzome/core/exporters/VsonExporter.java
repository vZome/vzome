package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


public class VsonExporter extends Exporter3d
{			
    public VsonExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        SortedSet<AlgebraicVector> vertices = new TreeSet<>();
        ArrayList<Integer> ballIndices = new ArrayList<>();
        ArrayList<JsonNode> struts = new ArrayList<>();
        ArrayList<JsonNode> panels = new ArrayList<>();

        // phase one: find and index all vertices
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Connector )
            {
                vertices .add( man .getLocation() );
            }
            else if ( man instanceof Strut )
            {
                vertices .add( man .getLocation() );
                vertices .add( ((Strut) man) .getEnd() );
            }
            else if ( man instanceof Panel )
            {
                for ( AlgebraicVector vertex : (Panel) man ) {
                    vertices .add( vertex );
                }
            }
        }

        // Up to this point, the vertices TreeSet has collected and sorted every unique vertex of every manifestation.
        // From now on we'll need their index, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        ArrayList<AlgebraicVector> sortedVertexList = new ArrayList<>(vertices);
        // we no longer need the vertices collection, 
        // so set it to null to free the memory and to ensure we don't use it later by mistake.
        vertices = null;

        // phase three: generate the JSON
        ObjectMapper mapper = new ObjectMapper();
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Connector )
            {
                ballIndices .add( sortedVertexList .indexOf( man .getLocation() ) );
            }
            else if ( man instanceof Strut )
            {
                ObjectNode strutJson = mapper .createObjectNode();
                JsonNode start = mapper .valueToTree( sortedVertexList .indexOf( man .getLocation() ) );
                JsonNode end = mapper .valueToTree( sortedVertexList .indexOf( ((Strut) man) .getEnd() ) );
                boolean reverse = rm .getStrutSense() == Axis.MINUS;
                strutJson .set( "start", reverse? end : start );
                strutJson .set( "end", reverse? start : end );
                JsonNode length = mapper .valueToTree( rm .getStrutLength() );
                strutJson .set( "length", length );
                JsonNode orbit = mapper .valueToTree( rm .getStrutOrbit() .getName() );
                strutJson .set( "orbit", orbit );
                JsonNode orientation = mapper .valueToTree( rm .getStrutZone() );
                strutJson .set( "orientation", orientation );
                struts .add( strutJson );
            }
            else if ( man instanceof Panel )
            {
                ObjectNode panelJson = mapper .createObjectNode();
                @SuppressWarnings("unchecked")
                Stream<AlgebraicVector> vertexStream = StreamSupport.stream( ( (Iterable<AlgebraicVector>) man ).spliterator(), false);
                JsonNode node = mapper .valueToTree( vertexStream.map( v -> sortedVertexList .indexOf( v ) ). collect( Collectors.toList() ) );
                panelJson .set( "vertices", node );
                panelJson .set( "color", mapper .valueToTree( rm .getColor() .toWebString() ) );
                panels .add( panelJson );
            }
        }

        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper );

        generator .writeStartObject();
        generator .writeStringField( "field", mModel .getField() .getName() );
        generator .writeStringField( "symmetry", mModel .getOrbitSource() .getSymmetry() .getName() );
        generator .writeObjectField( "vertices", sortedVertexList );
        generator .writeObjectField( "balls", ballIndices );
        generator .writeObjectField( "struts", struts );
        generator .writeObjectField( "panels", panels );
        generator .writeEndObject();
        generator.close();
    }

    @Override
    public String getFileExtension()
    {
        return "vson";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
}


