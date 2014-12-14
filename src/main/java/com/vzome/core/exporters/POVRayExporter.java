package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 */
public class POVRayExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	private static final String PREAMBLE_FILE = "com/vzome/core/exporters/povray/preamble.pov";

	
	public POVRayExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}
    
    public void mapViewToWorld( ViewModel view, Vector3f vector )
    {
        Matrix4d viewTrans = new Matrix4d();
        view .getViewTransform( viewTrans, 0d );
        viewTrans .invert();
        viewTrans .transform( vector );
    }

	public void doExport( File povFile, File directory, Writer writer, Dimension screenSize ) throws IOException
	{
	    output = new PrintWriter( writer );
	    
		Vector3d lookDir = new Vector3d(), upDir = new Vector3d(), rightDir = new Vector3d();
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
		output .println( "#declare       canvas_width = " + screenSize.width + ";" );
        output .println();
		output .println( "#declare      canvas_height = " + screenSize.height + ";" );
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
			Color color = mLights .getDirectionalLight( i, dir );
			mapViewToWorld( mScene, dir );
			output .print( "light_source { -light_distance * " + printTuple3d( new Vector3d( dir ) ) );
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
		
		FORMAT .setMaximumFractionDigits( 3 );
        
		for ( Iterator cnames = mColors .getColorNames(); cnames .hasNext(); )
        {
		    String colorName = (String) cnames .next();
            if ( "background" .equals( colorName ) )
                continue;
		    declareColor( prepColorName( colorName ), mColors .getColor( colorName ) );
		}

		StringBuffer instances = new StringBuffer();

        AlgebraicField field = mModel .getField();
        for ( int o = 1; o < field .getOrder(); o++ )
        {
            instances .append( "#declare " );
            field .defineMultiplier( instances, o );
            instances .append( ";\n" );
        }
        output .print( instances );
        instances = new StringBuffer();
        
		int numShapes = 0, numTransforms = 0;
		Map[] shapes = new Map[]{ new HashMap(), new HashMap() };
		Map transforms = new HashMap();
		for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
		{
		    RenderedManifestation rm = (RenderedManifestation) rms .next();
		    Polyhedron shape = rm .getShape();
		    boolean flip = rm .reverseOrder(); // need to reverse face vertex order
		    String shapeName = (String) shapes[ flip?1:0 ] .get( shape );
		    if ( shapeName == null ) {
		        shapeName = "shape" + numShapes++;
		        shapes[ flip?1:0 ] .put( shape, shapeName );
		        exportShape( shapeName, shape, flip );
		    }
		    int[][] transform = rm .getOrientation();
		    String transformName = (String) transforms .get( transform );
		    if ( transformName == null ){
		        transformName = "trans" + numTransforms++;
		        transforms .put( transform, transformName );
		        exportTransform( transformName, transform );
		    }
			instances .append( "object { " + shapeName + " transform " + transformName + " translate " );
            instances .append( "(<" );
            Manifestation man = rm .getManifestation();
            if ( man != null )
            {
                int[] loc = man .getLocation();
                if ( loc == null )
                    loc = rm .getShape() .getField() .origin( 3 );
                appendLocation( loc, instances );
            }
            else
            {
                appendLocation( rm .getLocation(), instances );
            }
            instances .append( ">)" );
			instances .append( " transform anim texture { " + prepColorName( rm .getColorName() ) + " } }" );
			instances .append( System .getProperty( "line.separator" ) );
		}

		output .println( instances .toString() );
		output .flush();
		
		String filename = povFile .getName();
		int index = filename .lastIndexOf( ".pov" );
		if ( index > 0 )
		{
		    filename = filename .substring( 0, index );
		}
		File file = new File( directory, filename + ".ini" );
		output = new PrintWriter( new FileWriter( file ) );
		output .println( "+W" + 600 );  // TODO fix these values!
        output .println( "+H" + 600 );
        output .println( "+A" );
        output .println( "Input_File_Name=" + filename + ".pov" );
        output .println( "Output_File_Name=" + filename + ".png" );
		output .close();
	}


    String prepColorName( String colorName )
    {
        return "color_" + colorName .replace( '.', '_' ) .replace( ' ', '_' );
    }
   
    private String printTuple3d( Tuple3d t )
    {
    	StringBuffer buf = new StringBuffer( "<" );
    	buf .append( FORMAT.format(t.x) );
    	buf .append( "," );
    	buf .append( FORMAT.format(t.y) );
    	buf .append( "," );
    	buf .append( FORMAT.format(t.z) );
    	buf .append( ">" );
    	return buf .toString();
    }
    
    protected void declareColor( String name, Color color )
    {
		output .print( "#declare " + name .replace( '.', '_' ) + " = texture { pigment { " );
		printColor( color );
		output .println( " } };" );
    }
    
    private void printColor( Color color )
    {
		output .print( "color rgb <" );
		float[] rgb = color .getRGBColorComponents( new float[3] );
		output .print( FORMAT.format(rgb[0]) + "," );
		output .print( FORMAT.format(rgb[1]) + "," );
		output .print( FORMAT.format(rgb[2]) );
		output .print( ">" );
    }
    
    
    /*
     * writes out a AlgebraicVector as (<1/2,1/2,0>*tau+<-3/2,0/2,0>)
     */
	protected void appendLocation( int[] /*AlgebraicVector*/ loc, StringBuffer buf )
	{
        AlgebraicField field = mModel .getField();
        field .getNumberExpression( buf, loc, 0, AlgebraicField.EXPRESSION_FORMAT );
        buf .append( ", " );
        field .getNumberExpression( buf, loc, 1, AlgebraicField.EXPRESSION_FORMAT );
        buf .append( ", " );
        field .getNumberExpression( buf, loc, 2, AlgebraicField.EXPRESSION_FORMAT );
	}
 
    private void appendLocation( RealVector location, StringBuffer buf )
    {
        buf .append( location .x );
        buf .append( ", " );
        buf .append( location .y );
        buf .append( ", " );
        buf .append( location .z );
    }

    private void exportShape( String shapeName, Polyhedron poly, boolean reverseFaces )
    {
        output .print( "#declare " + shapeName + " = " );
        List vertices = poly .getVertexList();
        output .println( "union {" );
        for ( Iterator faces = poly .getFaceSet() .iterator(); faces .hasNext(); ){
            Polyhedron.Face face = (Polyhedron.Face) faces .next();
            int arity = face .size();
            int num = face .size() + 1;
            output .print( "polygon {" );
            output .print( num + ", " );
            
            for ( int j = 0; j <= arity; j++ ){
                // POV-Ray requires that you explicitly repeat the first vertex to close the polygon
                int m = j % arity;
                Integer index = (Integer) face .get( reverseFaces? arity-m-1 : m );
                int[] /*AlgebraicVector*/ loc = (int[]) vertices .get( index .intValue() );
                StringBuffer buf = new StringBuffer();
                buf .append( "(<" );
                appendLocation( loc, buf );
                buf .append( ">)" );
                output .print( buf.toString() );
            }
            output .println( "}" );
        }
        output .println( "}" );
        output .flush();
    }

    
    private void exportTransform( String name, int[][] transform )
    {
        AlgebraicField field = mModel .getField();
        output .print( "#declare " + name + " = transform { matrix < " );
        StringBuffer buf = new StringBuffer();
        // Now we generate the transpose of the transform matrix... I don't recall why.
        //  Perhaps something to do with POV-Ray's left-handed coordinate system.
        for ( int i = 0; i < 3; i++ )
        {
            int[] columnSelect = field .basisVector( 3, i );
            int[] columnI = field .transform( transform, columnSelect );
            appendLocation( columnI, buf );
            buf .append( ", " );
        }
        output .print( buf );
        output .println( " 0, 0, 0 > }" );
        output .flush();
    }
    

    public String getFileExtension()
    {
        return "pov";
    }


    public void doExport( File directory, Writer writer, Dimension screenSize )
            throws Exception
    {
        throw new IllegalStateException( "POV exporter only supports 4-argument doExport()" );
    }

}


