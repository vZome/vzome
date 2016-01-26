package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;


public class JsonExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
		
	protected transient AlgebraicField field;

	private transient Color background;
	
	
	public JsonExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


	public void doExport( File directoryUnused, Writer writer, int height, int width ) throws IOException
	{
		this .doExport(writer);
	}

    private class ShapeMap extends HashMap<Polyhedron, Integer> {};

	public void doExport( Writer writer ) throws IOException
	{
        this .field = this .mModel .getField();
        this .background = this .mLights .getBackgroundColor();

        if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.0000" );
        }

        output = new PrintWriter( writer );
        output .print( "{\n\"shapes\" :\n[\n" );

        int instanceCount = 0;
        StringBuffer instances = new StringBuffer();
        StringBuffer orientations = new StringBuffer();
        int numShapes = 0, numTransforms = 1;
        ShapeMap[] shapes = new ShapeMap[] { new ShapeMap(), new ShapeMap() };
        Map<AlgebraicMatrix, Integer> transforms = new HashMap<>();
        AlgebraicMatrix identity = this .field .identityMatrix( 3 );
        Integer identityNum = 0;
        transforms .put( identity, identityNum );
        exportTransform( identityNum, identity, orientations );

        for (RenderedManifestation rm : mModel) {
            Polyhedron shape = rm .getShape();
            boolean flip = rm .reverseOrder(); // need to reverse face vertex order
            Integer shapeNum = shapes[ flip?1:0 ] .get( shape );
            if ( shapeNum == null ) {
                if ( numShapes != 0 )
                    output .print( ",\n\n" );
                shapeNum = numShapes++;
                shapes[ flip?1:0 ] .put( shape, shapeNum );
                exportShape( shapeNum, shape, flip );
            }
            AlgebraicMatrix transform = rm .getOrientation();
            if ( transform == null )
                transform = identity;
            Integer transformNum = transforms .get( transform );
            if ( transformNum == null ){
                if ( numTransforms > 0 )
                    orientations .append( ",\n" );
                transformNum = numTransforms++;
                transforms .put( transform, transformNum );
                exportTransform( transformNum, transform, orientations );
            }
            
            RealVector loc = rm .getLocation();
            Color color = rm .getColor();
            float[] rgb = new float[3];
            color .getRGBColorComponents( rgb );

            if ( instanceCount > 0 )
                instances .append( ",\n" );
            instances .append( "{ \"location\" : [" );
            instances .append( FORMAT .format( loc.x ) + "," );
            instances .append( FORMAT .format( loc.y ) + "," );
            instances .append( FORMAT .format( loc.z ) );
            instances .append( "], \"orientation\" : " + transformNum );
            instances .append( ", \"shape\" : " + shapeNum );
            instances .append( ", \"color\" : [" );
            instances .append( FORMAT .format( rgb[0] ) + "," );
            instances .append( FORMAT .format( rgb[1] ) + "," );
            instances .append( FORMAT .format( rgb[2] ) + ",1.0" );
            // TODO do we need flip?
            instances .append( "] }" );
            ++instanceCount;
        }
        output .print( "\n],\n\n\"background\" : [" );
        float[] rgb = new float[3];
        this .background .getRGBColorComponents( rgb );
        output .print( FORMAT .format( rgb[0] ) + "," );
        output .print( FORMAT .format( rgb[1] ) + "," );
        output .print( FORMAT .format( rgb[2] ) + ",1.0" );
        output .print( " ],\n\n\"instances\" :\n[\n" );
        output .print( instances );
        output .print( "\n],\n\n\"orientations\" :\n[\n" );
        output .print( orientations );
        output .print( "\n]\n}\n" );
	}


    // TODO: Get rid of the unused parameter
    private void exportTransform( Integer num, AlgebraicMatrix transform, StringBuffer buf )
    {
        // TODO: Any reason we can't just use this.field instead of mModel.getField()?
        AlgebraicField modelField = mModel .getField();
        
        // Now we generate the transpose of the transform matrix... I don't recall why.
        //  Perhaps something to do with POV-Ray's left-handed coordinate system.
        for ( int i = 0; i < 3; i++ )
        {
            AlgebraicVector columnSelect = modelField .basisVector( 3, i );
            AlgebraicVector columnI = transform .timesColumn( columnSelect );
            RealVector colRV = columnI .toRealVector();
            if ( i > 0 )
                buf .append( ", " );
            buf .append( FORMAT .format( colRV.x ) );
            buf .append( ", " );
            buf .append( FORMAT .format( colRV.y ) );
            buf .append( ", " );
            buf .append( FORMAT .format( colRV.z ) );
            buf .append( ", 0" );
        }
        buf .append( ", 0, 0, 0, 1" );
    }
    

    // TODO: Get rid of the unused parameter
    private void exportShape( Integer shapeNum, Polyhedron shape, boolean reverseFaces )
    {
        int vertexCount = 0;
        int normalCount = 0;
        int triangleCount = 0;
        StringBuffer vertices = new StringBuffer();
        StringBuffer normals = new StringBuffer();
        StringBuffer triangles = new StringBuffer();

        List<AlgebraicVector> faceVertices = shape .getVertexList();
        for (Polyhedron.Face face : shape .getFaceSet()) {
            int arity = face .size();
            int index = face .get( reverseFaces? arity-1 : 0 );
            AlgebraicVector gv = faceVertices .get(index);
            RealVector vert0 = gv .toRealVector();
            index = face .get( reverseFaces? arity-2 : 1 );
            gv = faceVertices .get(index);
            RealVector vert1 = gv .toRealVector( );
            index = face .get( reverseFaces? arity-3 : 2 );
            gv = faceVertices .get(index);
            RealVector vert2 = gv .toRealVector( );
            RealVector edge1 = vert1 .minus( vert0 );
            RealVector edge2 = vert2 .minus( vert1 );
            RealVector norm = edge1 .cross( edge2 ) .normalize();
            int v0 = -1, v1 = -1;
            for ( int j = 0; j < arity; j++ ){
                index = face .get( reverseFaces? arity-j-1 : j );
                gv = faceVertices .get(index);
                RealVector vertex = gv .toRealVector( );

                if ( v0 == -1 )
                    v0 = vertexCount;
                else if ( v1 == -1 )
                    v1 = vertexCount;
                else
                {
                    if ( triangleCount > 0 )
                        triangles .append( "," );
                    if ( triangleCount % 20 == 0 )
                        triangles .append( "\n" );
                    triangles .append( "[" + v0 + "," );
                    triangles .append( v1 + "," );
                    triangles .append( vertexCount + "]" );
                    v1 = vertexCount;
                    ++ triangleCount;
                }
                if ( vertexCount > 0 )
                    vertices .append( "," );
                if ( vertexCount % 10 == 0 )
                    vertices .append( "\n" );
                vertices .append( "[" + FORMAT .format( vertex.x ) + "," );
                vertices .append( FORMAT .format( vertex.y ) + "," );
                vertices .append( FORMAT .format( vertex.z ) + "]" );
                ++ vertexCount;

                if ( normalCount > 0 )
                    normals .append( "," );
                if ( normalCount % 10 == 0 )
                    normals .append( "\n" );
                normals .append( "[" + FORMAT .format( norm.x ) + "," );
                normals .append( FORMAT .format( norm.y ) + "," );
                normals .append( FORMAT .format( norm.z ) + "]" );
                ++ normalCount;
            }
        }
        output .print( "{\n\"position\" :\n[\n" );
        output .print( vertices );
        output .print( "],\n\"normal\" :\n[\n" );
        output .print( normals );
        output .print( "],\n\"indices\" :\n[\n" );
        output .print( triangles );
        output .print( "]\n}" );
        
        output .flush();
    }
	
	public String getFileExtension()
    {
        return "json";
    }

    public String getContentType()
    {
        return "application/json";
    }
}


