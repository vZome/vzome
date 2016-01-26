package com.vzome.core.exporters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
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
public class VRMLExporter extends Exporter3d 
{
	
	private static final String PREAMBLE_FILE = "com/vzome/core/exporters/vrml/preamble.wrl";
	
	private static final PentagonField FIELD = new PentagonField();

    private static final AlgebraicNumber MODEL_BALL_RADIUS = FIELD .createAlgebraicNumber( 6, 10, 1, 0 );
    /**
     * This scale factor is appropriate for making true-to-scale VRML renderings of strut models built
     *   in vZome with a model ball radius (blue) of one long blue strut.  In other terms, the edges of the
     *   ball model are short and medium blue struts.
     */
    private static final double SCALE = 0.350d / MODEL_BALL_RADIUS .evaluate();

    public VRMLExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }
    
    
    private void exportColor( String name, Color color )
    {
		output .println( "PROTO " + name + " [] {" );
		output .print( "    Appearance { material Material { diffuseColor " );
		// TODO implement transparency
		float[] rgb = color .getRGBColorComponents( new float[3] );
		output .print( rgb[0] + " " );
		output .print( rgb[1] + " " );
		output .print( rgb[2] );
		output .println( " }}}" );
    }

    private class ShapeMap extends HashMap<Polyhedron, String> {}
    
    public void doExport( File directory, Writer writer, int height, int width ) throws Exception
    {
        output = new PrintWriter( writer );

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

        AlgebraicField field = null;
        StringBuffer instances = new StringBuffer();
        int numShapes = 0;
        ShapeMap[] shapes = new ShapeMap[]{ new ShapeMap(), new ShapeMap() };
        Map<Color, String> colors = new HashMap<>();
        for ( Iterator<RenderedManifestation> rms = mModel .iterator(); rms .hasNext(); )
        {
            RenderedManifestation rm = rms .next();
            Polyhedron shape = rm .getShape();
            if ( field == null )
                field = shape .getField();
            boolean flip = rm .reverseOrder(); // need to reverse face vertex order
            String shapeName = shapes[ flip?1:0 ] .get( shape );
            if ( shapeName == null ) {
                shapeName = "shape" + numShapes++;
                shapes[ flip?1:0 ] .put( shape, shapeName );
                exportShape( shapeName, shape, flip );
            }
            AlgebraicMatrix transform = rm .getOrientation();
            
            // note... this code still does not work for half of the black directions... uncomment next two statements?
            // Seems wrong, would give an improper rotation (determinant = -1)
            
//            if ( flip )
//                transform = transform .neg();
            
            // TODO this is a pretty clumsy way to get the rows of the matrix out
            RealVector mx = transform .timesRow( field .basisVector( 3, AlgebraicVector.X ) ) .toRealVector();
            RealVector my = transform .timesRow( field .basisVector( 3, AlgebraicVector.Y ) ) .toRealVector();
            RealVector mz = transform .timesRow( field .basisVector( 3, AlgebraicVector.Z ) ) .toRealVector();
            
            // All this was first from Java3d's AxisAngle4f and Matrix3f, but that proved to be
            // inadequate, not handling rotation of Pi radians.
            // Fixed after reading "Rotation Representations and Performance Issues",
            // by Doug Eberly, www.geometrictools.com

            double x = (mz.y - my.z);
            double y = (mx.z - mz.x);
            double z = (my.x - mx.y);
            
            double cos = ( mx.x + my.y + mz.z - 1.0)*0.5;
            double sin = 0.5*Math.sqrt(x*x + y*y + z*z);
            float angle = (float)Math.atan2(sin, cos);
            
            if ( Math.abs( angle - Math.PI ) < 0.00001 ) {
                if ( ( mx.x >= my.y ) && ( mx.x >= mz.z ) ) {
                    x = Math.sqrt( mx.x - my.y - mz.z + 1.0) * 0.5;
                    y = mx.y / (2d*x);
                    z = mx.z / (2d*x);
                } else if ( ( my.y >= mz.z ) && ( my.y >= mx.x ) ) {
                    y = Math.sqrt( my.y - mx.x - mz.z + 1.0) * 0.5;
                    x = mx.y / (2d*y);
                    z = my.z / (2d*y);
                } else {
                    z = Math.sqrt( mz.z - my.y - mx.x + 1.0) * 0.5;
                    x = mx.z / (2d*z);
                    y = my.z / (2d*z);
                }
            }
            
            Color color = rm .getColor();
            String colorName = colors .get( color );
            if ( colorName == null ) {
                colorName = "color_" + color .toString() .replace( ',', '_' );
                colors .put( color, colorName );
                exportColor( colorName, color );
            }
            instances .append( "Transform { translation " );
            instances .append( rm .getLocation() .scale( SCALE ) .spacedString() );
            instances .append( " rotation " + x + " " + y + " " + z + " " + angle );
            instances .append( " children[ Shape{ geometry " + shapeName + "{} appearance " + colorName + "{}}]}\n" );
        }

        output .println( instances .toString() );
        output .flush();
        output .close();
    }


    private void exportShape( String shapeName, Polyhedron poly, boolean reverseFaces )
    {
        output .println( "PROTO " + shapeName + " [] { IndexedFaceSet{ solid FALSE convex FALSE colorPerVertex FALSE" );
        output .println( "   coord Coordinate{ point [" );
        
        for ( Iterator<AlgebraicVector> vertices = poly .getVertexList() .iterator(); vertices .hasNext(); ) {
            AlgebraicVector gv = vertices .next();
            if ( reverseFaces )
                gv = gv  .negate();
            RealVector v = gv .toRealVector();
            output .println( v .scale( SCALE ) .spacedString() + "," );
        }
        output .println( "] } coordIndex [" );
        for ( Iterator<Polyhedron.Face> faces = poly .getFaceSet() .iterator(); faces .hasNext(); ){
            Polyhedron.Face face = faces .next();
            int arity = face .size();
            for ( int j = 0; j < arity; j++ ){
                Integer index = face .get( /*reverseFaces? arity-j-1 :*/ j );
                output .print( index + ", " );
            }
            output .println( "-1," );
        }
        output .println( "]}}" );
        output .flush();
    }


    public String getFileExtension()
    {
        return "wrl";
    }
	
}


