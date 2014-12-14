package com.vzome.core.exporters;

import java.awt.Dimension;
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


	public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
	{
        output = new PrintWriter( writer );
        
        FORMAT .setMaximumFractionDigits( 6 );

        AlgebraicField field = mModel .getField();
        int[] scale = field .createPower( -5 );
        
        int numBalls = 0, numStruts = 0, numPanels = 0;
        StringBuffer vertices = new StringBuffer();
        Map ballIndices = new HashMap( numBalls );
        boolean first = true;
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = ((RenderedManifestation) rms .next()) .getManifestation();
            if ( man instanceof Connector )
            {
                int[] loc = ((Connector) man) .getLocation();
                if ( ballIndices .containsKey( loc ) )
                    continue;
                ballIndices .put( new AlgebraicVector( loc ), new Integer( numBalls++ ) );
                loc = field .scaleVector( loc, scale );
                RealVector rv = field .getRealVector( loc );
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
            else if ( man instanceof Strut )
                ++ numStruts;
            else if ( man instanceof Panel )
                ++ numPanels;
        }
        output .println( VERTEX_PRELUDE );
        output .println( vertices .toString() );
        output .println( VERTEX_POSTLUDE );

        first = true;
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            RenderedManifestation rm = (RenderedManifestation) rms .next();
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut )
            {
                Strut strut = (Strut) man;
                if ( first )
                    first = false;
                else
                    output .println( "," );
                output .print( ballIndices .get( new AlgebraicVector( strut .getLocation() ) ) + "," );
                output .print( ballIndices .get( new AlgebraicVector( strut .getEnd() ) ) );
            }
        }
//        
//        output .println( "\n" );
//        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
//        {
//            RenderedManifestation rm = (RenderedManifestation) rms .next();
//            Manifestation man = rm .getManifestation();
//            if ( man instanceof Panel )
//            {
//                List vs = new ArrayList();
//                for ( Iterator verts = ((Panel) man) .getVertices(); verts .hasNext(); )
//                    vs .add( ballIndices .get( (GoldenVector) verts .next() ) );
//                output .print( vs .size() );
//                for ( int i = 0; i < vs .size(); i++ )
//                    output .print( " " + ((Integer) vs .get( i ) ) .intValue() );
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


