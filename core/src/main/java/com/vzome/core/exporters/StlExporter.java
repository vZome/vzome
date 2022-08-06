package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.render.RenderedManifestation;

// This exporter has been tuned to produce a format identical to that produced by Meshlab,
//   to guarantee a seamless upload to Shapeways.com

public class StlExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
			
    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.000000E00" );
        }

        output = new PrintWriter( writer );
        output .println( "solid vcg" );
        
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel )
            {
                Panel panel = (Panel) man;
                RealVector norm = mModel .renderVector( panel .getNormal() ) .normalize();
                RealVector v0 = null, v1 = null;
                for (AlgebraicVector vert : panel) {
                    RealVector vertex = mModel .renderVector( vert );
                    vertex = vertex .scale( RZOME_MM_SCALING );
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


