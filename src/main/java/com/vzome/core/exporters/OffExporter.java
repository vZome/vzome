package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.Camera;


public class OffExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	
	public OffExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        output = new PrintWriter( writer );
        output .println( "OFF" );
		
		FORMAT .setMaximumFractionDigits( 16 );
        
		int numBalls = 0, numStruts = 0, numPanels = 0;
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Connector )
                ++ numBalls;
            else if ( man instanceof Strut )
                ++ numStruts;
            else if ( man instanceof Panel )
                ++ numPanels;
        }
        output .println( numBalls + " " + numPanels + " " + numStruts );
        
        Map<AlgebraicVector, Integer> ballIndices = new HashMap<>( numBalls );
        numBalls = 0;
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Connector ) {
                AlgebraicVector loc = ((Connector) man) .getLocation();
                RealVector rv = loc .toRealVector();
                output .print( FORMAT.format( rv .x ) + " " );
                output .print( FORMAT.format( rv .y ) + " " );
                output .println( FORMAT.format( rv .z ) );
                ballIndices .put(loc, numBalls++);
            }
        }
        
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if (man instanceof Panel) {
                Panel panel = (Panel) man;
                List<Integer> vs = new ArrayList<>();
                for (AlgebraicVector vert : panel) {
                    vs .add( ballIndices .get( vert ) );
                }
                output .print( vs .size() );
                for (Integer v : vs) {
                    output.print(" " + v);
                }
                output .println();
            }
        }
        
		output .flush();
	}


    public String getFileExtension()
    {
        return "off";
    }

}


