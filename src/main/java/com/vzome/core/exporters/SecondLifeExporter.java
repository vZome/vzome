package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;


public class SecondLifeExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	
	public SecondLifeExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}

    private static final String VERTEX_PRELUDE =
        "list vertices = [";

    private static final String VERTEX_POSTLUDE =
        "];\n\nlist edgeIndices = [";

    private static final String EDGE_POSTLUDE =
        "\n];";


	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        output = new PrintWriter( writer );
        
        FORMAT .setMaximumFractionDigits( 6 );

        AlgebraicField field = mModel .getField();
        AlgebraicNumber scale = field .createPower( -5 );
        
        int numBalls = 0;
        StringBuffer vertices = new StringBuffer();
        Map<AlgebraicVector, Integer> ballIndices = new HashMap<>( numBalls );
        boolean first = true;
        for ( Iterator<RenderedManifestation> rms = mModel .iterator(); rms .hasNext(); )
        {
            Manifestation man = rms .next() .getManifestation();
            if ( man instanceof Connector )
            {
                AlgebraicVector loc = ((Connector) man) .getLocation();
                if ( ballIndices .containsKey( loc ) )
                    continue;
                ballIndices .put(loc, numBalls++);
                loc = loc .scale( scale );
                RealVector rv = loc .toRealVector();
                if ( first )
                    first = false;
                else
                    vertices .append( ",\n" );
                vertices .append( "<" );
                vertices .append( FORMAT.format( rv .x ) );
                vertices .append( "," );
                vertices .append( FORMAT.format( rv .y ) );
                vertices .append( "," );
                vertices .append( FORMAT.format( rv .z ) );
                vertices .append( ">" );
            }
//            else if ( man instanceof Strut )
//                ++ numStruts;
//            else if ( man instanceof Panel )
//                ++ numPanels;
        }
        output .println( VERTEX_PRELUDE );
        output .println( vertices .toString() );
        output .println( VERTEX_POSTLUDE );

        first = true;
        for ( Iterator<RenderedManifestation> rms = mModel .iterator(); rms .hasNext(); )
        {
            RenderedManifestation rm = rms .next();
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut )
            {
                Strut strut = (Strut) man;
                if ( first )
                    first = false;
                else
                    output .println( "," );
                output .print( ballIndices .get( strut .getLocation() ) + "," );
                output .print( ballIndices .get( strut .getEnd() ) );
            }
        }
//        
//        output .println( "\n" );
//        for ( Iterator<RenderedManifestation> rms = mModel .getRenderedManifestations(); rms .hasNext(); )
//        {
//            RenderedManifestation rm = rms .next();
//            Manifestation man = rm .getManifestation();
//            if ( man instanceof Panel )
//            {
//                List<Integer> vs = new ArrayList<>();
//                for ( Iterator<AlgebraicVector> verts = ((Panel) man) .iterator(); verts .hasNext(); )
//                    vs .add( ballIndices .get( verts .next() ) );
//                output .print( vs .size() );
//                for (Integer v : vs) {
//                    output.print(" " + (v));
//                }
//                output .println();
//            }
//        }

        output .println( EDGE_POSTLUDE );
        output .flush();
	}


    public String getFileExtension()
    {
        return "2life";
    }

}


