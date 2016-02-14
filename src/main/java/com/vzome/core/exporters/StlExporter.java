package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.Camera;

// This exporter has been tuned to produce a format identical to that produced by Meshlab,
//   to guarantee a seamless upload to Shapeways.com

public class StlExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
			
	public StlExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.000000E00" );
        }

        output = new PrintWriter( writer );
        // format version 6, with explicit "balls" section, not a ball for every vertex
        output .println( "solid vcg" );
        
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if (man instanceof Strut) {
                Polyhedron shape = rm .getShape();
                RealVector loc = rm .getLocation();
                boolean reverseFaces = rm .reverseOrder();
                shape .getFaceSet();
                List<AlgebraicVector> faceVertices = shape .getVertexList();
                for (Polyhedron.Face face : shape .getFaceSet()) {
                    int arity = face .size();
                    int index = face .get( reverseFaces? arity-1 : 0 );
                    AlgebraicVector gv = faceVertices .get( index );
                    RealVector vert0 = gv .toRealVector();
                    index = face .get( reverseFaces? arity-2 : 1 );
                    gv = faceVertices .get(index);
                    RealVector vert1 = gv .toRealVector();
                    index = face .get( reverseFaces? arity-3 : 2 );
                    gv = faceVertices .get(index);
                    RealVector vert2 = gv .toRealVector();
                    RealVector edge1 = vert1 .minus( vert0 );
                    RealVector edge2 = vert2 .minus( vert1 );
                    RealVector norm = edge1 .cross( edge2 ) .normalize();
                    RealVector v0 = null, v1 = null;
                    for ( int j = 0; j < arity; j++ ){
                        index = face .get( reverseFaces? arity-j-1 : j );
                        gv = faceVertices .get(index);
                        RealVector vertex = loc .plus( gv .toRealVector() );
                        vertex = vertex .scale( RZOME_INCH_SCALING );

                        if ( v0 == null )
                            v0 = vertex;
                        else if ( v1 == null )
                            v1 = vertex;
                        else
                        {
                            output .print( "  facet normal " );
                            output .println( FORMAT .format( norm.x ) + " " + FORMAT .format( norm.y ) + " " + FORMAT .format( norm.z ) );
                            output .println( "    outer loop" );
                            output .println( "      vertex " + FORMAT .format( v0.x ) + " " + FORMAT .format( v0.y ) + " " + FORMAT .format( v0.z ) );
                            output .println( "      vertex " + FORMAT .format( v1.x ) + " " + FORMAT .format( v1.y ) + " " + FORMAT .format( v1.z ) );
                            output .println( "      vertex " + FORMAT .format( vertex.x ) + " " + FORMAT .format( vertex.y ) + " " + FORMAT .format( vertex.z ) );
                            output .println( "    endloop" );
                            output .println( "  endfacet" );
                            v1 = vertex;
                        }
                    }
                }
            } else if ( man instanceof Panel )
            {
                Panel panel = (Panel) man;
                RealVector norm = panel .getNormal() .toRealVector() .normalize();
                RealVector v0 = null, v1 = null;
                for (AlgebraicVector vert : panel) {
                    RealVector vertex = vert .toRealVector();
                    vertex = vertex .scale( VZOME_STRUT_MODEL_INCH_SCALING );
                    if ( v0 == null )
                        v0 = vertex;
                    else if ( v1 == null )
                        v1 = vertex;
                    else
                    {
                        output .print( "  facet normal " );
                        output .println( FORMAT .format( norm.x ) + " " + FORMAT .format( norm.y ) + " " + FORMAT .format( norm.z ) );
                        output .println( "    outer loop" );
                        output .println( "      vertex " + FORMAT .format( v0.x ) + " " + FORMAT .format( v0.y ) + " " + FORMAT .format( v0.z ) );
                        output .println( "      vertex " + FORMAT .format( v1.x ) + " " + FORMAT .format( v1.y ) + " " + FORMAT .format( v1.z ) );
                        output .println( "      vertex " + FORMAT .format( vertex.x ) + " " + FORMAT .format( vertex.y ) + " " + FORMAT .format( vertex.z ) );
                        output .println( "    endloop" );
                        output .println( "  endfacet" );
                        v1 = vertex;
                    }
                }
            }
        }
        

        output .println( "endsolid vcg" );

		output .flush();
	}
		
    @Override
    public String getFileExtension()
    {
        return "stl";
    }

}


