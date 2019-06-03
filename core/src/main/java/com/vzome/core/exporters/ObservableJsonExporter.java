package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


public class ObservableJsonExporter extends Exporter3d
{
    private final JsonMapper mapper = new JsonMapper();
    
    public ObservableJsonExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        ArrayList<JsonNode> shapes = new ArrayList<>();
        ArrayList<JsonNode> balls = new ArrayList<>();
        ArrayList<JsonNode> struts = new ArrayList<>();

        for (RenderedManifestation rm : mModel) {
            ObjectNode node = mapper .getObjectNode( rm );
            if ( node != null ) {
                ObjectNode shapeNode = mapper .getShapeNode( rm .getShape() );
                if ( shapeNode != null )
                {
                    shapes .add( shapeNode );
                }

                Manifestation man = rm .getManifestation();
                if ( man instanceof Strut )
                {
                    ObjectNode strutJson = mapper .getObjectNode( rm );
                    struts .add( strutJson );
                }
                else if ( man instanceof Connector )
                {
                    ObjectNode ballJson = mapper .getObjectNode( rm );
                    balls .add( ballJson );
                }
            }
        }

        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper .getObjectMapper() );

        generator .writeStartObject();
        generator .writeObjectField( "shapes", shapes );
        generator .writeObjectField( "balls", balls );
        generator .writeObjectField( "segments", struts );
        generator .writeEndObject();
        generator.close();
    }

    @Override
    public String getFileExtension()
    {
        return "json";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
}


