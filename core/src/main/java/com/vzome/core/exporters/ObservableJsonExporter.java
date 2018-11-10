package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


public class ObservableJsonExporter extends Exporter3d
{			
    public ObservableJsonExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }

    private Quat4d getQuaternion( AlgebraicMatrix orientation, Map<AlgebraicMatrix,Quat4d> rotations )
    {
        Quat4d result = rotations .get( orientation );
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
            rotations .put( orientation, result );
        }
        return result;
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        ArrayList<JsonNode> shapes = new ArrayList<>();
        ArrayList<JsonNode> balls = new ArrayList<>();
        ArrayList<JsonNode> struts = new ArrayList<>();
        Set<String> shapeNames = new HashSet<>();
        Map<AlgebraicMatrix,Quat4d> rotations = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            Polyhedron shape = rm .getShape();
            Quat4d quaternion = getQuaternion( rm .getOrientation(), rotations );
            Direction orbit = rm .getStrutOrbit();
            String shapeName = (orbit==null)? "ball" : orbit .getName() + ":" + rm .getStrutLength() .toString();
            if ( ! shapeNames .contains( shapeName ) )
            {
                ObjectNode shapeNode = mapper .valueToTree( shape );
                shapeNode .remove( "id" );
                shapeNode .set( "name", mapper .valueToTree( shapeName ) );
                //shapes .add( shapeNode );
                shapeNames .add( shapeName );
            }
            if ( man instanceof Strut )
            {
                ObjectNode strutJson = mapper .createObjectNode();
                strutJson .set( "start", mapper .valueToTree( rm .getLocation() ) );
                strutJson .set( "rotation", mapper .valueToTree( quaternion ) );
                strutJson .set( "shape", mapper .valueToTree( shapeName ) );
                strutJson .set( "color", mapper .valueToTree( rm .getColor() .toWebString() ) );
                struts .add( strutJson );
            }
            else if ( man instanceof Connector )
            {
                ObjectNode ballJson = mapper .createObjectNode();
                ballJson .set( "center", mapper .valueToTree( rm .getLocation() ) );
                ballJson .set( "shape", mapper .valueToTree( shapeName ) );
                ballJson .set( "color", mapper .valueToTree( "0xfcfcfc" ) );
                balls .add( ballJson );
            }
        }

        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator( writer );
        generator .useDefaultPrettyPrinter();
        generator .setCodec( mapper );

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


