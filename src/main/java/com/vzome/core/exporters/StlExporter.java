package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

// This exporter has been tuned to produce a format identical to that produced by Meshlab,
//   to guarantee a seamless upload to Shapeways.com

public class StlExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
		
	protected final AlgebraicField field;	
	
	public StlExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	    field = model .getField();
	}


	public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
	{
        if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.000000E00" );
        }

        output = new PrintWriter( writer );
        // format version 6, with explicit "balls" section, not a ball for every vertex
        output .println( "solid vcg" );
        
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
        	RenderedManifestation rm = (RenderedManifestation) rms .next();
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut )
            {
            	Polyhedron shape = rm .getShape();
            	RealVector loc = rm .getLocation();
            	boolean reverseFaces = rm .reverseOrder();
            	shape .getFaceSet();
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
                    
                    RealVector v0 = null, v1 = null;
                    for ( int j = 0; j < arity; j++ ){
                        index = (Integer) face .get( reverseFaces? arity-j-1 : j );
                        gv = (int[]) faceVertices .get( index .intValue() );
                        RealVector vertex = loc .plus( field .getRealVector( gv ) );
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
            }
            else if ( man instanceof Panel )
            {
                Panel panel = (Panel) man;
                RealVector norm = field .getRealVector( panel .getNormal( field ) ) .normalize();
                RealVector v0 = null, v1 = null;
                for ( Iterator verts = ((Panel) man) .getVertices(); verts .hasNext(); )
                {
                    RealVector vertex = field .getRealVector( (int[]) verts .next() );
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
		
    public String getFileExtension()
    {
        return "stl";
    }

}


