package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.vzome.core.viewing.ViewModel;


public class OffExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	
	public OffExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        output = new PrintWriter( writer );
        output .println( "OFF" );
		
		FORMAT .setMaximumFractionDigits( 16 );
        
		int numBalls = 0, numStruts = 0, numPanels = 0;
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = ((RenderedManifestation) rms .next()) .getManifestation();
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
        for ( Iterator<RenderedManifestation> rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = rms .next() .getManifestation();
            if ( man instanceof Connector ) {
                AlgebraicVector loc = ((Connector) man) .getLocation();
                RealVector rv = loc .toRealVector();
                output .print( FORMAT.format( rv .x ) + " " );
                output .print( FORMAT.format( rv .y ) + " " );
                output .println( FORMAT.format( rv .z ) );
                ballIndices .put( loc, new Integer( numBalls++ ) );
            }
        }
        
        for ( Iterator<RenderedManifestation> rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            RenderedManifestation rm = rms .next();
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel ) {
                List<Integer> vs = new ArrayList<>();
                for ( Iterator<AlgebraicVector> verts = ((Panel) man) .getVertices(); verts .hasNext(); )
                    vs .add( ballIndices .get( verts .next() ) );
                output .print( vs .size() );
                for ( int i = 0; i < vs .size(); i++ )
                    output .print( " " + vs .get( i ) );
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


