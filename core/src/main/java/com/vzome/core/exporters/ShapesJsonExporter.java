package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;


public class ShapesJsonExporter extends Exporter3d
{
    private final JsonMapper mapper = new JsonMapper();
    
    public ShapesJsonExporter()
    {
        super( null, null, null, null );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        ArrayList<JsonNode> shapes = new ArrayList<>();
        ArrayList<JsonNode> instances = new ArrayList<>();

        for ( RenderedManifestation rm : mModel ) {
            ObjectNode node = mapper .getObjectNode( rm );
            if ( node != null ) {
                ObjectNode shapeNode = mapper .getShapeNode( rm .getShape() );
                if ( shapeNode != null )
                {
                    shapes .add( shapeNode ); // a shape not reported yet
                }

                ObjectNode instanceNode = mapper .getObjectNode( rm );
                instances .add( instanceNode );
            }
        }
        
        // First, turn the embedding into a set of real column vectors.
        Embedding embedding = this .mModel .getEmbedding();
        float[] embeddingRows = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        for ( int i = 0; i < 3; i++ ) {
            RealVector column = embedding .embedInR3( this .mModel .getField() .basisVector( 3, i ) );
            embeddingRows[ 0 + i ] = column.x;
            embeddingRows[ 4 + i ] = column.y;
            embeddingRows[ 8 + i ] = column.z;
        }

        JsonFactory factory = new JsonFactory() .disable( JsonGenerator.Feature.AUTO_CLOSE_TARGET );
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper .getObjectMapper() );

        generator .writeStartObject();
        generator .writeObjectField( "lights", this .mLights );
        generator .writeObjectField( "camera", this .mScene );
        generator .writeObjectField( "embedding", embeddingRows );
        generator .writeObjectField( "shapes", shapes );
        generator .writeObjectField( "instances", instances );
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


