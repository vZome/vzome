package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.RenderedManifestation;

/**
 * @author vorth
 */
public class VRMLExporter extends GeometryExporter 
{
    private static final String PREAMBLE_FILE = "com/vzome/core/exporters/vrml/preamble.wrl";
	
    private static final double SCALE = 0.350d;

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

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws Exception
    {
        output = new PrintWriter( writer );

        output .println( this.getBoilerplate( PREAMBLE_FILE ) );
        output .println();

        AlgebraicField field = null;
        StringBuffer instances = new StringBuffer();
        int numShapes = 0;
        HashMap<Polyhedron, String> shapes = new HashMap<>();
        Map<Color, String> colors = new HashMap<>();
        for (RenderedManifestation rm : mModel) {
            Polyhedron shape = rm .getShape();
            if ( field == null )
                field = shape .getField();
            String shapeName = shapes .get( shape );
            if ( shapeName == null ) {
                shapeName = "shape" + numShapes++;
                shapes .put( shape, shapeName );
                exportShape( shapeName, shape );
            }
            AlgebraicMatrix transform = rm .getOrientation();
            
            // note... this code still does not work for half of the black directions... uncomment next two statements?
            // Seems wrong, would give an improper rotation (determinant = -1)
            
//            if ( flip )
//                transform = transform .neg();
            
            // TODO: use the Embedding
            // TODO this is a pretty clumsy way to get the rows of the matrix out
            RealVector mx = mModel .renderVector( transform .timesRow( field .basisVector( 3, AlgebraicVector.X ) ) ); // TODO do we really want renderVector here?
            RealVector my = mModel .renderVector( transform .timesRow( field .basisVector( 3, AlgebraicVector.Y ) ) );
            RealVector mz = mModel .renderVector( transform .timesRow( field .basisVector( 3, AlgebraicVector.Z ) ) );
            
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
            if (color == null) {
                color = Color.WHITE;
            }
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


    private void exportShape( String shapeName, Polyhedron poly )
    {
        output .println( "PROTO " + shapeName + " [] { IndexedFaceSet{ solid FALSE convex FALSE colorPerVertex FALSE" );
        output .println( "   coord Coordinate{ point [" );
        
        for (AlgebraicVector gv : poly .getVertexList()) {
            RealVector v = mModel .renderVector( gv );
            output .println( v .scale( SCALE ) .spacedString() + "," );
        }
        output .println( "] } coordIndex [" );
        for (Polyhedron.Face face : poly .getFaceSet()) {
            int arity = face .size();
            for ( int j = 0; j < arity; j++ ){
                Integer index = face .get( j );
                output .print( index + ", " );
            }
            output .println( "-1," );
        }
        output .println( "]}}" );
        output .flush();
    }

    @Override
    public String getFileExtension()
    {
        return "wrl";
    }
	
}
