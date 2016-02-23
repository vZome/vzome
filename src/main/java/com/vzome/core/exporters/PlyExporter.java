package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.viewing.Lights;


public class PlyExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
    private Map<AlgebraicVector, Integer> vertexData;

    private StringBuffer vertices;

	public PlyExporter( Colors colors, Lights lights )
	{
	    super( null, colors, lights, null );

        if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.000000E00" );
        }
	}

    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
    	int numPanels = 0;
        StringBuffer panels = new StringBuffer();
        vertexData = new LinkedHashMap<>();
        vertices = new StringBuffer();

        PrintWriter output = new PrintWriter( writer );
        output .println( "ply" );
        output .println( "format ascii 1.0" );
        output .println( "comment   Exported by vZome, http://vzome.com" );
        output .println( "comment     All vertex data is in inches" );

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Panel )
            {
                ++ numPanels;
                List<Integer> vs = new ArrayList<>();
                for (AlgebraicVector vertex : ((Panel) man)) {
                    vs .add( getVertexIndex( vertex ) );
                }
                panels .append( vs .size() );
    			for (Integer v : vs) {
    				panels .append( " " );
    				panels .append(v);
    			}
                panels .append( "\n" );
            }
        }

        output .println( "element vertex " + vertexData .size() );
        output .println( "property float x" );
        output .println( "property float y" );
        output .println( "property float z" );

        output .println( "element face " + numPanels );
        output .println( "property list uchar int vertex_indices" );

        output .println( "end_header" );

        output .print( vertices );
        
        output .print( panels );

		output .flush();
	}
    
    protected Integer getVertexIndex( AlgebraicVector vertexVector )
    {
        Integer obj = vertexData .get( vertexVector );
        if ( obj == null )
        {
            AlgebraicVector key = vertexVector;
            int index = vertexData .size();
            obj = index;
            vertexData .put( key, obj );
            vertices .append( vertexVector .toRealVector() .spacedString() + "\n" );
        }
        return obj;
    }

    @Override
    public String getFileExtension()
    {
        return "ply";
    }

}


