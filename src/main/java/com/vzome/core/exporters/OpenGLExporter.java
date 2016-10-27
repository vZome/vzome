package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.Camera;

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 */
public class OpenGLExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
    
	public OpenGLExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}

    @Override
	public boolean needsManifestations() {
        return false;
    }

    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
	    output = new PrintWriter( writer );
       
//		Vector3d lookDir = new Vector3d(), upDir = new Vector3d();
//		mScene .getViewOrientation( lookDir, upDir );
		FORMAT .setMaximumFractionDigits( 8 );

        StringBuffer shape_vertices = new StringBuffer( "GLfloat vZome_shape_vertices[] = {\n" );
        StringBuffer shape_normals = new StringBuffer( "GLfloat vZome_shape_normals[] = {\n" );
        StringBuffer shape_indices = new StringBuffer( "GLshort vZome_shape_indices[] = {\n" );
        StringBuffer instance_transforms = new StringBuffer( "GLshort vZome_instance_transforms[] = {\n" );
        StringBuffer instance_shapes = new StringBuffer( "GLuint vZome_instance_shapes[] = {\n" );
        StringBuffer instance_offsets = new StringBuffer( "GLfloat vZome_instance_offsets[] = {\n" );
        StringBuffer instance_colors = new StringBuffer( "GLfloat vZome_instance_colors[] = {\n" );
        StringBuffer transformations = new StringBuffer( "GLfloat vZome_transformations[] = {\n" );
        int num_instances = 0;
		int numShapes = 0; // this is actually the number of ints written to shape_indices
		int numTransforms = 0; // this is actually the number of arrays written to transformations
        int numVertices = 0;
		HashMap<Polyhedron, Integer>[] shapes = TwoMaps.inAnArray();
		Map<AlgebraicMatrix, Integer> transforms = new HashMap<>();
        for (RenderedManifestation rm : mModel) {
            Polyhedron shape = rm .getShape();
            boolean flip = rm .reverseOrder(); // need to reverse face vertex order
            Integer shapeIndex = shapes[ flip?1:0 ] .get( shape );
            if (shapeIndex == null) {
                shapeIndex = numShapes;
                shapes[ flip?1:0 ] .put( shape, shapeIndex );
                List<AlgebraicVector> vertices = shape .getVertexList();
                Set<Polyhedron.Face> faceSet = shape .getFaceSet();
                ++numShapes;
                shape_indices .append( faceSet .size() + ",\n" ); // TODO add a comment giving some idea of this shape
                for (Polyhedron.Face face : shape .getFaceSet()) {
                    int arity = face .size();
                    ++numShapes;
                    shape_indices .append( arity + ",   " );
                    RealVector normal = mModel .renderVector( face .getNormal() );
                    for ( int j = 0; j < arity; j++ ){
                        int index = face .get( flip? arity-j-1 : j );
                        AlgebraicVector av = vertices .get( index );
                        RealVector vertex = mModel .renderVector( av );
                        shape_vertices .append( vertex + ",\n" );
                        shape_normals .append( normal + ",\n" );
                        ++numShapes;
                        shape_indices .append( (numVertices++) + "," );
                    }
                    shape_indices .append( "\n" );
                }
            }
            AlgebraicMatrix transform = rm .getOrientation();
            Integer transformIndex = transforms .get( transform );
            if ( transformIndex == null ){
                transformIndex = numTransforms++;
                transforms .put( transform, transformIndex );
                
                transformations .append( FORMAT .format( transform .getElement( 0, 0 ) .evaluate() ) );
                transformations .append( ", " );
                transformations .append( FORMAT .format( transform .getElement( 1, 0 ) .evaluate() ) );
                transformations .append( ", " );
                transformations .append( FORMAT .format( transform .getElement( 2, 0 ) .evaluate() ) );
                transformations .append( ", 0, " );
                transformations .append( FORMAT .format( transform .getElement( 0, 1 ) .evaluate() ) );
                transformations .append( ", " );
                transformations .append( FORMAT .format( transform .getElement( 1, 1 ) .evaluate() ) );
                transformations .append( ", " );
                transformations .append( FORMAT .format( transform .getElement( 2, 1 ) .evaluate() ) );
                transformations .append( ", 0, " );
                transformations .append( FORMAT .format( transform .getElement( 0, 2 ) .evaluate() ) );
                transformations .append( ", " );
                transformations .append( FORMAT .format( transform .getElement( 1, 2 ) .evaluate() ) );
                transformations .append( ", " );
                transformations .append( FORMAT .format( transform .getElement( 2, 2 ) .evaluate() ) );
                transformations .append( ", 0, 0, 0, 0, 1,\n" );
            }
            instance_transforms .append( transformIndex + ",\n" );
            instance_shapes .append( shapeIndex + ",\n" );
            instance_offsets .append( rm .getLocation() + ",\n" );
            appendColor( rm .getColor(), instance_colors );
            ++ num_instances;
        }

        output .println( transformations .toString() );
        output .println( "};\n" );
        output .println( shape_indices .toString() );
        output .println( "};\n" );
        output .println( instance_transforms .toString() );
        output .println( "};\n" );
        output .println( instance_shapes .toString() );
        output .println( "};\n" );
        output .println( instance_offsets .toString() );
        output .println( "};\n" );
        output .println( instance_colors .toString() );
        output .println( "};\n" );
        output .println( "const GLuint vZome_num_instances = " + num_instances + ";\n" );
        output .println( "const GLfloat vZome_viewing_setback = " + mScene .getViewDistance() + ";\n" );
        output .println( shape_vertices .toString() );
        output .println( "};\n" );
        output .println( shape_normals .toString() );
        output .println( "};\n" );
		output .flush();
	}
        
    
    protected void appendColor( Color color, StringBuffer buf )
    {
        float[] rgb = (color == null ? Color.WHITE : color) .getRGBColorComponents( new float[4] );
        buf .append( FORMAT.format(rgb[0]) + ", " );
        buf .append( FORMAT.format(rgb[1]) + ", " );
        buf .append( FORMAT.format(rgb[2]) + ", " );
        buf .append( FORMAT.format(rgb[3]) + ",\n" );
    }
    
    
    protected void appendLocation( AlgebraicVector loc, StringBuffer buf )
    {
        buf .append( mModel .renderVector( loc ) );
        buf .append( ",\n" );
    }


    @Override
    public String getFileExtension()
    {
        return "h";
    }

}


