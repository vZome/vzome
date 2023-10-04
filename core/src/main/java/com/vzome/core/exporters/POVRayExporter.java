package com.vzome.core.exporters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.viewing.Camera;

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 */
public class POVRayExporter extends DocumentExporter
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	private static final String PREAMBLE_FILE = "com/vzome/core/exporters/povray/preamble.pov";

    public void mapViewToWorld( Camera view, Vector3f vector )
    {
        Matrix4f viewTrans = new Matrix4f();
        view .getViewTransform( viewTrans );
        viewTrans .invert();
        viewTrans .transform( vector );
    }

    @Override
    public boolean needsManifestations() {
        return false;
    }

    @Override
	public void doExport( File povFile, Writer writer, int height, int width ) throws IOException
	{
	    output = new PrintWriter( writer );
	    
		Vector3f lookDir = new Vector3f(), upDir = new Vector3f(), rightDir = new Vector3f();
		mScene .getViewOrientation( lookDir, upDir );
		rightDir .cross( lookDir, upDir );
		FORMAT .setMaximumFractionDigits( 8 );

        output .println();
        output .println();
		output .println( "#declare           look_dir = " + printTuple3d( lookDir ) + ";" );
        output .println();
        output .println( "#declare             up_dir = " + printTuple3d( upDir ) + ";" );
        output .println();
        output .println( "#declare          right_dir = " + printTuple3d( rightDir ) + ";" );
        output .println();
        output .println( "#declare viewpoint_distance = " + mScene .getViewDistance() + ";" );
        output .println();
        output .println( "#declare near_clip_distance = " + mScene .getNearClipDistance() + ";" );
        output .println();
        output .println( "#declare far_clip_distance = " + mScene .getFarClipDistance() + ";" );
        output .println();
		output .println( "#declare      look_at_point = " + printTuple3d( mScene .getLookAtPoint() ) + ";" );
        output .println();
		output .println( "#declare      field_of_view = " + mScene .getFieldOfView() + ";" );
        output .println();
		output .println( "#declare       canvas_width = " + width + ";" );
        output .println();
		output .println( "#declare      canvas_height = " + height + ";" );
        output .println();
        output .println( "#declare      parallel_proj = " + (mScene.isPerspective()?0:1) + ";" );
        output .println();

		InputStream input = getClass() .getClassLoader()
									.getResourceAsStream( PREAMBLE_FILE );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int num;
		try {
			while ( ( num = input .read( buf, 0, 1024 )) > 0 )
					out .write( buf, 0, num );
		} catch (IOException e) {
			e.printStackTrace();
		}
		output .println( new String( out .toByteArray() ) );
		output .println();

		Vector3f dir = new Vector3f();
		for ( int i = 0; i<3; i++ ) {
			Color color = mLights .getDirectionalLightColor( i );
			mapViewToWorld( mScene, dir );
			output .print( "light_source { -light_distance * " + printTuple3d( new Vector3f( dir ) ) );
			output .print( " " );
			printColor( color );
			output .println( " * multiplier_light_" + (i+1) + " }" );
	        output .println();
		}

        output .print( "#declare ambient_color = " );
        printColor( mLights .getAmbientColor() );
        output .println( ";" );
        output .println();
        output .println( "#default { texture { finish { phong 0.3 ambient multiplier_ambient * ambient_color diffuse 0.6 } } }" );
        output .println();

        output .print( "background { " );
        printColor( mLights .getBackgroundColor() );
        output .println( " }" );
        output .println();
		
//		FORMAT .setMaximumFractionDigits( 3 );

		StringBuffer instances = new StringBuffer();

        AlgebraicField field = mModel .getField();
        
        Embedding embedding = mModel .getEmbedding();
        String embeddingTransform = " ";
        if ( ! embedding .isTrivial() )
        {
        	embeddingTransform = " transform embedding ";
            output .print( "#declare embedding = transform { matrix < " );
            for ( int i = 0; i < 3; i++ )
            {
                AlgebraicVector columnSelect = field .basisVector( 3, i );
                RealVector columnI = embedding .embedInR3( columnSelect );
                output .print( columnI .x );
                output .print( ", " );
                output .print( columnI .y );
                output .print( ", " );
                output .print( columnI .z );
                output .print( ", " );
            }
            output .println( " 0, 0, 0 > }" );
            output .flush();
        }
        
		int numTransforms = 0;
		HashSet<String> shapes = new HashSet<>();
		Map<AlgebraicMatrix, String> transforms = new HashMap<>();
		Map<Color, String> colors = new HashMap<>();
        for (RenderedManifestation rm : mModel) {
            String shapeName = "S" + rm .getShapeId() .toString() .replaceAll( "-", "" );
            if ( ! shapes .contains( shapeName ) ) {
                shapes .add( shapeName );
                exportShape( shapeName, rm .getShape() );
            }
            AlgebraicMatrix transform = rm .getOrientation();
            String transformName = transforms .get( transform );
            if ( transformName == null ){
                transformName = "trans" + numTransforms++;
                transforms .put( transform, transformName );
                exportTransform( transformName, transform );
            }
            Color color = rm .getColor();
            if ( color == null )
            	color = Color.WHITE;
            String colorName = colors .get( color );
            if ( colorName == null ){
                colorName = nameColor( color );
                colors .put( color, colorName );
                exportColor( colorName, color );
            }
            instances .append( "object { " + shapeName + " transform " + transformName + " translate " );
            instances .append( "(<" );
            AlgebraicVector loc = rm .getLocationAV();
            if ( loc == null )
                loc = rm .getShape() .getField() .origin( 3 );
            appendVector( loc, instances );
            instances .append( ">)" );
            instances .append( embeddingTransform + "transform anim texture { " + colorName + " } }" );
            instances .append( System .getProperty( "line.separator" ) );
        }

		output .println( instances .toString() );
		output .flush();
		
		if ( povFile == null )
		    return;
		
		String filename = povFile .getName();
		int index = filename .lastIndexOf( ".pov" );
		if ( index > 0 )
		{
		    filename = filename .substring( 0, index );
		}
		File file = new File( povFile .getParentFile(), filename + ".ini" );
		output = new PrintWriter( new FileWriter( file ) );
		output .println( "+W" + 600 );  // TODO fix these values!
        output .println( "+H" + 600 );
        output .println( "+A" );
        output .println( "Input_File_Name=" + filename + ".pov" );
        output .println( "Output_File_Name=" + filename + ".png" );
		output .close();
	}


    String nameColor( Color color )
    {
        return "color_" + color .toString() .replace( ',', '_' );
    }
   
    private String printTuple3d( Tuple3f t )
    {
    	StringBuilder buf = new StringBuilder( "<" );
    	buf .append( FORMAT.format(t.x) );
    	buf .append( "," );
    	buf .append( FORMAT.format(t.y) );
    	buf .append( "," );
    	buf .append( FORMAT.format(t.z) );
    	buf .append( ">" );
    	return buf .toString();
    }
    
    protected void exportColor( String name, Color color )
    {
		output .print( "#declare " + name .replace( '.', '_' ) + " = texture { pigment { " );
		printColor( color );
		output .println( " } };" );
    }
    
    private void printColor( Color color )
    {
    	boolean doAlpha = color .getAlpha() < 0xFF;
    	if ( doAlpha )
    		output .print( "color rgbf <" );
    	else
    		output .print( "color rgb <" );
		float[] rgb = color .getRGBColorComponents( new float[4] );
		output .print( FORMAT.format(rgb[0]) + "," );
		output .print( FORMAT.format(rgb[1]) + "," );
		if ( doAlpha )
		{
			output .print( FORMAT.format(rgb[2]) + "," );
			output .print( FORMAT.format(rgb[3]) );
		}
		else {
			output .print( FORMAT.format(rgb[2]) );
		}
		output .print( ">" );
    }
    
    
    /*
     * writes out an AlgebraicVector as (<1/2,1/2,0>*phi+<-3/2,0/2,0>)
     */
	protected void appendVector( AlgebraicVector loc, StringBuffer buf )
	{
	    RealVector vector = loc .toRealVector();
        buf .append( FORMAT.format(vector.x) );
        buf .append( ", " );
        buf .append( FORMAT.format(vector.y) );
        buf .append( ", " );
        buf .append( FORMAT.format(vector.z) );
	}

    private void exportShape( String shapeName, Polyhedron poly )
    {
        output .print( "#declare " + shapeName + " = " );
        List<AlgebraicVector> vertices = poly .getVertexList();
        output .println( "mesh {" );
        poly .getTriangleFaces();
        for (Polyhedron.Face.Triangle face : poly .getTriangleFaces()) {
            output .print( "triangle {" );
            for ( int index : face.vertices ) {
                AlgebraicVector loc = vertices .get( index );
                StringBuffer buf = new StringBuffer();
                buf .append( "<" );
                appendVector( loc, buf );
                buf .append( ">" );
                output .print( buf.toString() );
            }
            output .println( "}" );
        }
        output .println( "}" );
        output .flush();
    }

    
    private void exportTransform( String name, AlgebraicMatrix transform )
    {
        AlgebraicField field = mModel .getField();
        output .print( "#declare " + name + " = transform { matrix < " );
        StringBuffer buf = new StringBuffer();
        // Now we generate the transpose of the transform matrix... I don't recall why.
        //  Perhaps something to do with POV-Ray's left-handed coordinate system.
        for ( int i = 0; i < 3; i++ )
        {
            AlgebraicVector columnSelect = field .basisVector( 3, i );
            AlgebraicVector columnI = transform .timesColumn( columnSelect );
            appendVector( columnI, buf );
            buf .append( ", " );
        }
        output .print( buf );
        output .println( " 0, 0, 0 > }" );
        output .flush();
    }
    
    @Override
    public String getFileExtension()
    {
        return "pov";
    }
}


