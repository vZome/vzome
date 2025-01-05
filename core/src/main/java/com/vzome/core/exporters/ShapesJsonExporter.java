package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.render.JsonMapper;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;


public class ShapesJsonExporter extends DocumentExporter
{
    private transient JsonMapper mapper;
    private final boolean justTriangles;
    
    public ShapesJsonExporter( boolean justTriangles )
    {
        this.justTriangles = justTriangles;
    }

    public void exportDocument( DocumentIntf doc, File file, Writer writer, int height, int width ) throws Exception
    {
        mapper = new JsonMapper( JsonMapper.RealTrianglesView.class, false, this.justTriangles );
        mScene = ((DocumentModel) doc) .getCamera();
        mModel = doc .getRenderedModel();
        mLights = doc .getSceneLighting();

        ArrayList<JsonNode> shapes = new ArrayList<>();
        
        ArrayList<JsonNode> instances = exportRenderedModel( mModel, shapes );
        OrbitSource orbitSource = mModel .getOrbitSource();
        AlgebraicField field = mModel .getField();
        
        // First, turn the embedding into a set of real column vectors.
        Embedding embedding = this .mModel .getEmbedding();
        float[] embeddingRows = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        for ( int i = 0; i < 3; i++ ) {
            RealVector column = embedding .embedInR3( this .mModel .getField() .basisVector( 3, i ) );
            embeddingRows[ 0 + i ] = column.x;
            embeddingRows[ 4 + i ] = column.y;
            embeddingRows[ 8 + i ] = column.z;
        }
        
        ArrayList<ArrayList<JsonNode>> snapshots = new ArrayList<>();
        for ( RenderedModel snapshot : ((DocumentModel) doc) .getSnapshots() ) {
            if ( snapshot != null )
                snapshots .add( exportRenderedModel( snapshot, shapes ) );
        }

        JsonFactory factory = new JsonFactory() .disable( JsonGenerator.Feature.AUTO_CLOSE_TARGET );
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper .getObjectMapper() );

        generator .writeStartObject();
        if ( ! justTriangles )
            generator .writeStringField( "polygons", "true" );
        generator .writeStringField( "field", field.getName() );
        generator .writeStringField( "symmetry", orbitSource .getName() );
        generator .writeObjectField( "orientations", orbitSource .getOrientations( true ) );
        generator .writeObjectField( "lights", this .mLights );
        generator .writeObjectField( "camera", this .mScene );
        generator .writeObjectField( "embedding", embeddingRows );
        generator .writeObjectField( "shapes", shapes );
        generator .writeObjectField( "instances", instances );
        generator .writeObjectField( "scenes", ((DocumentModel) doc) .getLesson() .iterator() );
        generator .writeObjectField( "snapshots", snapshots );
        generator .writeEndObject();
        generator.close();
        mScene = null;
        mModel = null;
        mLights = null;
    }

    private ArrayList<JsonNode> exportRenderedModel( RenderedModel model, ArrayList<JsonNode> shapes )
    {
        ArrayList<JsonNode> instances = new ArrayList<>();
        for ( RenderedManifestation rm : model ) {
            ObjectNode instanceNode = mapper .getObjectNode( rm, true );
            if ( instanceNode != null ) {
                ObjectNode shapeNode = mapper .getShapeNode( rm .getShape() );
                if ( shapeNode != null )
                {
                    shapes .add( shapeNode ); // a shape not reported yet
                }
                instances .add( instanceNode );
            }
        }
        return instances;
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException {}

    @Override
    public String getFileExtension()
    {
        return "shapes.json";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
}


