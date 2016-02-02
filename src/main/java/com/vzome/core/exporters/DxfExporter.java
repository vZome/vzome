package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Locale;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.Camera;


public class DxfExporter extends Exporter3d
{
	public DxfExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        output = new PrintWriter( writer );
        output .println( "0" );
        output .println( "SECTION" );
        output .println( "2" );
        output .println( "ENTITIES" );
		
        NumberFormat format = NumberFormat .getNumberInstance( Locale .US );
        format .setMaximumFractionDigits( 6 );

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut ) {
                output .println( "0" );
                output .println( "LINE" );
                output .println( "8" );
                output .println( "vZome" ); // this is the "layer" the line appears in; it need not be predefined
                AlgebraicVector start = ((Strut) man) .getLocation();
                AlgebraicVector end = ((Strut) man) .getEnd();
                RealVector rv = start .toRealVector();
                rv = rv .scale( RZOME_INCH_SCALING );
                output .println( "10" );
                output .println( format.format( rv .x ) );
                output .println( "20" );
                output .println( format.format( rv .y ) );
                output .println( "30" );
                output .println( format.format( rv .z ) );
                rv = end .toRealVector();
                rv = rv .scale( RZOME_INCH_SCALING );
                output .println( "11" );
                output .println( format.format( rv .x ) );
                output .println( "21" );
                output .println( format.format( rv .y ) );
                output .println( "31" );
                output .println( format.format( rv .z ) );
            }
        }
        
        output .println( "0" );
        output .println( "ENDSEC" );
        output .println( "0" );
        output .println( "EOF" );

		output .flush();
	}


    @Override
    public String getFileExtension()
    {
        return "dxf";
    }

}


