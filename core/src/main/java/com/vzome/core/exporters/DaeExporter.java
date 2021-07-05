package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


public class DaeExporter extends Exporter3d
{
    private static final String DAE_TEMPLATE = "com/vzome/core/exporters/template-dae.xml";

    private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );

    public DaeExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }


    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.0000" );
        }

        ColladaDocument doc = new ColladaDocument( DAE_TEMPLATE, this .mModel .getEmbedding(), this .mModel .getField() );

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut )
            {
                String orientedShapeId = doc .addOrientedShape( rm );
                doc .addShapeInstance( rm, orientedShapeId );
            }
            else
            {
                String nodeId = doc .addColoredShape( rm );
                doc .addShapeInstance( rm, nodeId );
            }
        }
        doc .write( writer );
    }

    @Override
    public String getFileExtension()
    {
        return "dae";
    }

    @Override
    public String getContentType()
    {
        return "model/vnd.collada+xml";
    }


    private class ColladaDocument
    {
        private Document doc;
        private XPath xpath;
        private Element visual_scene_node_template, oriented_shape_template, geometry_template, shape_template,
        visual_scene, library_nodes, library_geometries,
        material_template, library_materials, effect_template, library_effects;
        private int instanceNum = 0, shapeNum = 0, colorNum = 0, nodeNum = 0;
        private String embeddingMatrixText = null;
        private Set<String> orientedShapeIds = new HashSet<>();
        private Map<Polyhedron,String> shapeIds = new HashMap<>();
        private Map<String,Map<String,String>> coloredShapeIds = new HashMap<>();
        private Map<Color,String> colorIds = new HashMap<>();

        ColladaDocument( String templatePath, Embedding embedding, AlgebraicField field )
        {
            super();

            InputStream bytes = getClass() .getClassLoader() .getResourceAsStream( templatePath );

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //            factory .setNamespaceAware(true);
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
                doc = builder .parse( bytes );

                // Create XPathFactory object
                XPathFactory xpathFactory = XPathFactory.newInstance();

                // Create XPath object
                xpath = xpathFactory.newXPath();

                visual_scene_node_template = (Element) xpath .evaluate( "//node[@id='instance']", doc .getDocumentElement(), XPathConstants.NODE );
                visual_scene = (Element) visual_scene_node_template .getParentNode();
                visual_scene .removeChild( visual_scene_node_template );

                oriented_shape_template = (Element) xpath .evaluate( "//node[@id='oriented-shape']", doc .getDocumentElement(), XPathConstants.NODE );
                library_nodes = (Element) oriented_shape_template .getParentNode();
                library_nodes .removeChild( oriented_shape_template );

                shape_template = (Element) xpath .evaluate( "//node[@id='shape-node']", doc .getDocumentElement(), XPathConstants.NODE );
                library_nodes .removeChild( shape_template );

                geometry_template = (Element) xpath .evaluate( "//geometry[@id='shape-geom']", doc .getDocumentElement(), XPathConstants.NODE );
                library_geometries = (Element) geometry_template .getParentNode();
                library_geometries .removeChild( geometry_template );

                material_template = (Element) xpath .evaluate( "//material[@id='color']", doc .getDocumentElement(), XPathConstants.NODE );
                library_materials = (Element) material_template .getParentNode();
                library_materials .removeChild( material_template );

                effect_template = (Element) xpath .evaluate( "//effect[@id='color-fx']", doc .getDocumentElement(), XPathConstants.NODE );
                library_effects = (Element) effect_template .getParentNode();
                library_effects .removeChild( effect_template );

                if ( embedding .isTrivial() )
                    embeddingMatrixText = null;
                else {
                    // First, turn the embedding into a set of real column vectors.
                    RealVector[] columns = new RealVector[3];
                    for ( int i = 0; i < columns.length; i++ ) {
                        columns[i] = embedding .embedInR3( field .basisVector( 3, i ) );
                    }

                    StringBuffer sb = new StringBuffer();
                    for (int j = 0; j < 3; j++) {
                        sb .append( FORMAT .format( columns[ j ] .x ) + " " );
                    }
                    sb .append( "0.0 " );
                    for (int j = 0; j < 3; j++) {
                        sb .append( FORMAT .format( columns[ j ] .y ) + " " );
                    }
                    sb .append( "0.0 " );
                    for (int j = 0; j < 3; j++) {
                        sb .append( FORMAT .format( columns[ j ] .z ) + " " );
                    }
                    sb .append( "0.0 " );
                    sb .append( "0.0 0.0 0.0 1.0" );
                    embeddingMatrixText = sb .toString();
                }

            } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
                e .printStackTrace();
            } finally {
                try {
                    bytes .close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String addColor( RenderedManifestation rm )
        {
            Color color = rm .getColor();
            if ( color == null )
                color = Color .WHITE;
            String colorId = colorIds .get( color );
            if ( colorId == null)
            {
                float[] rgba = new float[4];
                color .getRGBColorComponents( rgba );
                Float alphaComponent = rgba[3];
                StringBuffer sb = new StringBuffer();
                String delim = "";
                for ( float f : rgba ) {
                    sb .append( delim );
                    sb .append( f );
                    delim = " ";
                }

                colorId = "color" + Integer .toString( colorNum++ );
                String effectId = colorId + "-fx";

                Element material = (Element) material_template .cloneNode( true );
                material .setAttribute( "id", colorId );
                library_materials .appendChild( material );
                Element effect = (Element) effect_template .cloneNode( true );
                effect .setAttribute( "id", effectId );
                library_effects .appendChild( effect );
                try {
                    Element instance_effect = (Element) xpath .evaluate( "instance_effect", material, XPathConstants.NODE );
                    instance_effect .setAttribute( "url", "#" + effectId );
                    Element diffuse_color = (Element) xpath .evaluate( "profile_COMMON//diffuse/color", effect, XPathConstants.NODE );
                    diffuse_color .setTextContent( sb .toString() );
                    if ( alphaComponent < 1.0f ) {
                        Element transparency_float = (Element) xpath.evaluate("profile_COMMON//transparency/float", effect, XPathConstants.NODE);
                        transparency_float.setTextContent(alphaComponent.toString());
                    }
                } catch ( XPathExpressionException e ) {
                    e.printStackTrace();
                }
                colorIds .put( color, colorId );
            }
            return colorId;
        }

        String addShape( RenderedManifestation rm )
        {
            Polyhedron shape = rm .getShape();
            String shapeId = shapeIds .get( shape );
            if ( shapeId == null)
            {
                int vertexCount = 0;
                int normalCount = 0;
                int triangleCount = 0;
                StringBuffer vertices = new StringBuffer();
                StringBuffer normals = new StringBuffer();
                StringBuffer triangles = new StringBuffer();

                for ( AlgebraicVector av : shape .getVertexList() )
                {
                    RealVector vertex = av .toRealVector(); // cannot use embedding yet
                    vertices .append( FORMAT .format( vertex.x ) + " " );
                    vertices .append( FORMAT .format( vertex.y ) + " " );
                    vertices .append( FORMAT .format( vertex.z ) + " " );
                    ++ vertexCount;
                }
                for (Polyhedron.Face face : shape .getFaceSet())
                {
                    int arity = face .size();
                    int v0 = -1, v1 = -1;
                    for ( int j = 0; j < arity; j++ ){
                        Integer index = face .get( j );
                        if ( v0 == -1 )
                        {
                            v0 = index;
                        }
                        else if ( v1 == -1 )
                        {
                            v1 = index;
                        }
                        else
                        {
                            //                            if ( triangleCount % 40 == 0 )
                            //                                triangles .append( "\n" );
                            triangles .append( v0 + " " );
                            triangles .append( normalCount + " " );
                            triangles .append( v1 + " " );
                            triangles .append( normalCount + " " );
                            triangles .append( index + " " );
                            triangles .append( normalCount + " " );
                            v1 = index;
                            ++ triangleCount;
                        }
                        //                        if ( vertexCount % 20 == 0 )
                        //                            vertices .append( "\n" );
                        //                        if ( normalCount % 20 == 0 )
                        //                            normals .append( "\n" );
                    }
                    RealVector norm = face .getNormal( shape .getVertexList() ) .toRealVector() .normalize(); // cannot use embedding yet
                    normals .append( FORMAT .format( norm.x ) + " " );
                    normals .append( FORMAT .format( norm.y ) + " " );
                    normals .append( FORMAT .format( norm.z ) + " " );
                    ++ normalCount;
                }

                shapeId = "shape" + Integer .toString( shapeNum++ );
                String geomId = shapeId + "-geom";
                String positionsId = shapeId + "-positions";
                String positionsArrayId = positionsId + "-array";
                String normalsId = shapeId + "-normals";
                String normalsArrayId = normalsId + "-array";
                String verticesId = shapeId + "-vertices";
                String materialId = shapeId + "-material";

                Element geometry = (Element) geometry_template .cloneNode( true );
                geometry .setAttribute( "id", geomId );
                library_geometries .appendChild( geometry );
                try {
                    Element source = (Element) xpath .evaluate( "mesh/source[@name='position']", geometry, XPathConstants.NODE );
                    source .setAttribute( "id", positionsId );
                    Element float_array = (Element) xpath .evaluate( "float_array", source, XPathConstants.NODE );
                    float_array .setAttribute( "id", positionsArrayId );
                    float_array .setAttribute( "count", "" + ( 3 * vertexCount ) );
                    float_array .setTextContent( vertices .toString() );
                    Element accessor = (Element) xpath .evaluate( "technique_common/accessor", source, XPathConstants.NODE );
                    accessor .setAttribute( "source", "#" +  positionsArrayId );
                    accessor .setAttribute( "count", "" + vertexCount );

                    source = (Element) xpath .evaluate( "mesh/source[@name='normal']", geometry, XPathConstants.NODE );
                    source .setAttribute( "id", normalsId );
                    float_array = (Element) xpath .evaluate( "float_array", source, XPathConstants.NODE );
                    float_array .setAttribute( "id", normalsArrayId );
                    float_array .setAttribute( "count", "" + ( 3 * normalCount ) );
                    float_array .setTextContent( normals .toString() );
                    accessor = (Element) xpath .evaluate( "technique_common/accessor", source, XPathConstants.NODE );
                    accessor .setAttribute( "source", "#" +  normalsArrayId );
                    accessor .setAttribute( "count", "" + normalCount );

                    Element vertices_node = (Element) xpath .evaluate( "mesh/vertices", geometry, XPathConstants.NODE );
                    vertices_node .setAttribute( "id", verticesId );
                    Element input = (Element) xpath .evaluate( "input[@semantic='POSITION']", vertices_node, XPathConstants.NODE );
                    input .setAttribute( "source", "#" + positionsId );

                    Element triangles_node = (Element) xpath .evaluate( "mesh/triangles", geometry, XPathConstants.NODE );
                    triangles_node .setAttribute( "count", triangleCount + "" );
                    triangles_node .setAttribute( "material", materialId );
                    input = (Element) xpath .evaluate( "input[@semantic='VERTEX']", triangles_node, XPathConstants.NODE );
                    input .setAttribute( "source", "#" + verticesId );
                    input = (Element) xpath .evaluate( "input[@semantic='NORMAL']", triangles_node, XPathConstants.NODE );
                    input .setAttribute( "source", "#" + normalsId );
                    Element p = (Element) xpath .evaluate( "p", triangles_node, XPathConstants.NODE );
                    p .setTextContent( triangles .toString() );

                } catch ( XPathExpressionException e ) {
                    e.printStackTrace();
                }
                shapeIds .put( shape, shapeId );
            }
            return shapeId;
        }

        String addColoredShape( RenderedManifestation rm )
        {
            String colorId = addColor( rm );
            String shapeId = addShape( rm );
            Map<String,String> nodeIds = coloredShapeIds .get( shapeId );
            if ( nodeIds == null ) {
                nodeIds = new HashMap<>();
                coloredShapeIds .put( shapeId, nodeIds );
            }
            String nodeId = nodeIds .get( colorId );
            if ( nodeId == null)
            {
                nodeId = "node" + Integer .toString( nodeNum++ );
                Element shape_node = (Element) shape_template .cloneNode( true );
                shape_node .setAttribute( "id", nodeId );
                library_nodes .appendChild( shape_node );
                try {
                    Element instance_geometry = (Element) xpath .evaluate( "instance_geometry", shape_node, XPathConstants.NODE );
                    instance_geometry .setAttribute( "url", "#" + shapeId + "-geom" );
                    Element instance_material = (Element) xpath .evaluate( "bind_material//instance_material", instance_geometry, XPathConstants.NODE );
                    instance_material .setAttribute( "symbol", shapeId + "-material" );
                    instance_material .setAttribute( "target", "#" + colorId );

                } catch ( XPathExpressionException e ) {
                    e.printStackTrace();
                }
                nodeIds .put( colorId, nodeId );
            }
            return nodeId;
        }

        String addOrientedShape( RenderedManifestation rm )
        {
            String nodeId = addColoredShape( rm );
            String orientedShapeId = nodeId + "-" + rm .getStrutZone();
            if ( ! orientedShapeIds .contains( orientedShapeId ) )
            {
                Element oriented_shape = (Element) oriented_shape_template .cloneNode( true );
                try {
                    StringBuffer sb = new StringBuffer();
                    Element matrix = (Element) xpath .evaluate( "matrix", oriented_shape, XPathConstants.NODE );
                    AlgebraicMatrix transform = rm .getOrientation();
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            AlgebraicNumber num = transform .getElement( i, j );
                            sb .append( FORMAT .format( num .evaluate() ) + " " );
                        }
                        sb .append( "0.0 " );
                    }
                    sb .append( "0.0 0.0 0.0 1.0" );
                    matrix .setTextContent( sb .toString() );
                    Element instance_node = (Element) xpath .evaluate( "instance_node", oriented_shape, XPathConstants.NODE );
                    instance_node .setAttribute( "url", "#" + nodeId );
                    oriented_shape .setAttribute( "id", orientedShapeId );
                    library_nodes .appendChild( oriented_shape );
                } catch ( XPathExpressionException e ) {
                    e.printStackTrace();
                }
                orientedShapeIds .add( orientedShapeId );
            }
            return orientedShapeId;
        }

        void addShapeInstance( RenderedManifestation rm, String shapeId )
        {
            Element visual_scene_node = (Element) visual_scene_node_template .cloneNode( true );
            try {
                Element translate = (Element) xpath .evaluate( "translate", visual_scene_node, XPathConstants.NODE );
                AlgebraicVector location = rm .getLocationAV(); // don't want embedding yet
                if ( location == null )
                    visual_scene_node .removeChild( translate );
                else
                    translate .setTextContent( location .toRealVector() .spacedString() );

                // Apply the global embedding, if non-trivial.
                Element matrix = (Element) xpath .evaluate( "matrix", visual_scene_node, XPathConstants.NODE );
                if ( this .embeddingMatrixText == null )
                    visual_scene_node .removeChild( matrix );
                else {
                    matrix .setTextContent( embeddingMatrixText );
                }

                Element instance_node = (Element) xpath .evaluate( "instance_node", visual_scene_node, XPathConstants.NODE );
                instance_node .setAttribute( "url", "#" + shapeId );
                visual_scene_node .setAttribute( "id", "instance" + Integer .toString( instanceNum++ ) );
                visual_scene .appendChild( visual_scene_node );
            } catch ( XPathExpressionException e ) {
                e.printStackTrace();
            }
        }

        void write( Writer writer )
        {
            // Pretty-prints a DOM document to XML using DOM Load and Save's LSSerializer.
            // Note that the "format-pretty-print" DOM configuration parameter can only be set in JDK 1.6+.
            DOMImplementation domImplementation = doc .getImplementation();
            if (domImplementation .hasFeature("LS", "3.0") && domImplementation .hasFeature( "Core", "2.0" ) ) {
                DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation .getFeature( "LS", "3.0" );
                LSSerializer lsSerializer = domImplementationLS .createLSSerializer();
                DOMConfiguration domConfiguration = lsSerializer .getDomConfig();
                if (domConfiguration .canSetParameter( "format-pretty-print", Boolean.TRUE )) {
                    lsSerializer .getDomConfig() .setParameter( "format-pretty-print", Boolean.TRUE );
                    LSOutput lsOutput = domImplementationLS .createLSOutput();
                    lsOutput .setEncoding( "UTF-8" );
                    lsOutput .setCharacterStream( writer );
                    lsSerializer.write( doc, lsOutput );
                } else {
                    throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
                }
            } else {
                throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
            }
        }
    }
}


