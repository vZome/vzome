package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
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
        Map[] shapes = new Map[]{ new HashMap(), new HashMap() };
        Map transforms = new HashMap();

        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            RenderedManifestation rm = (RenderedManifestation) rms .next();
            Polyhedron shape = rm .getShape();
            boolean flip = rm .reverseOrder(); // need to reverse face vertex order
            Integer shapeNum = (Integer) shapes[ flip?1:0 ] .get( shape );
            if ( shapeNum == null ) {
                if ( numShapes != 0 )
                    output .print( ",\n\n" );
                shapeNum = new Integer( numShapes++ );
                shapes[ flip?1:0 ] .put( shape, shapeNum );
                exportShape( shapeNum, shape, flip );
            }
            int[][] transform = rm .getOrientation();
            Integer transformNum = (transform==null)? new Integer(0) : (Integer) transforms .get( transform );
            if ( transformNum == null ){
                if ( numTransforms > 0 )
                    orientations .append( ",\n" );
                transformNum = new Integer( numTransforms++ );
                transforms .put( transform, transformNum );
                exportTransform( transformNum, transform, orientations );
            }
            
            RealVector loc = rm .getLocation();
            Color color = mColors .getColor( rm .getColorName() );
            float[] rgb = new float[3];
            color .getRGBColorComponents( rgb );

            if ( instanceCount > 0 )
                instances .append( ",\n" );
            instances .append( "{ \"location\" : [" );
            instances .append( FORMAT .format( loc.x ) + "," );
            instances .append( FORMAT .format( loc.y ) + "," );
            instances .append( FORMAT .format( loc.z ) );
            instances .append( "], \"orientation\" : " + transformNum .intValue() );
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
        output .print( FORMAT .format( rgb[1] ) + ",1.0" );
        output .print( " ],\n\n\"instances\" :\n[\n" );
        output .print( instances );
        output .print( "\n],\n\n\"orientations\" :\n[1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1" );
        output .print( orientations );
        output .print( "\n]\n}\n" );
	}


    private void exportTransform( Integer num, int[][] transform, StringBuffer buf )
    {
        AlgebraicField field = mModel .getField();
        // Now we generate the transpose of the transform matrix... I don't recall why.
        //  Perhaps something to do with POV-Ray's left-handed coordinate system.
        for ( int i = 0; i < 3; i++ )
        {
            int[] columnSelect = field .basisVector( 3, i );
            int[] columnI = field .transform( transform, columnSelect );
            RealVector colRV = field .getRealVector( columnI );
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
    

    private void exportShape( Integer shapeNum, Polyhedron shape, boolean reverseFaces )
    {
        int vertexCount = 0;
        int normalCount = 0;
        int triangleCount = 0;
        StringBuffer vertices = new StringBuffer();
        StringBuffer normals = new StringBuffer();
        StringBuffer triangles = new StringBuffer();

        List faceVertices = shape .getVertexList();
        for ( Iterator faces = shape .getFaceSet() .iterator(); faces.hasNext(); ) {

            Polyhedron.Face face = (Polyhedron.Face) faces.next();

            int arity = face .size();

            Integer index = (Integer) face .get( reverseFaces? arity-1 : 0 );
            int[] /*AlgebraicVector*/ gv = (int[]) faceVertices .get( index .intValue() );
            RealVector vert0 = field .getRealVector( gv );
            index = (Integer) face .get( reverseFaces? arity-2 : 1 );
            gv = (int[]) faceVertices .get( index .intValue() );
            RealVector vert1 = field .getRealVector( gv );
            index = (Integer) face .get( reverseFaces? arity-3 : 2 );
            gv = (int[]) faceVertices .get( index .intValue() );
            RealVector vert2 = field .getRealVector( gv );
            RealVector edge1 = vert1 .minus( vert0 );
            RealVector edge2 = vert2 .minus( vert1 );
            RealVector norm = edge1 .cross( edge2 ) .normalize();
            
            int v0 = -1, v1 = -1;
            for ( int j = 0; j < arity; j++ ){
                index = (Integer) face .get( reverseFaces? arity-j-1 : j );
                gv = (int[]) faceVertices .get( index .intValue() );
                RealVector vertex = field .getRealVector( gv );

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


