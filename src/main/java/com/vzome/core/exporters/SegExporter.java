package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;


public class SegExporter extends Exporter3d
{
	public SegExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}

	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        this .field = this .mModel .getField();
        this .vertices = new StringBuffer();
        this .struts = new StringBuffer();
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format) .applyPattern( "0.0000" );
        }		
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            if ( man instanceof Strut )
            {
                Strut strut = (Strut) man;
                struts .append( "s " );
                struts .append( getVertexIndex( strut .getLocation() ) );
                struts .append( " " );
                struts .append( getVertexIndex( strut .getEnd() ) );
                struts .append( "\n" );
            }
        }
        writer .append( vertices .toString() );
        writer .append( struts .toString() );
        writer .close();
	}

    public String getFileExtension()
    {
        return "seg";
    }

    private Map<AlgebraicVector, Integer> vertexData = new HashMap<>();
    
    private transient StringBuffer vertices;

    private transient StringBuffer struts;

    protected transient AlgebraicField field;
    
	private final NumberFormat format = NumberFormat .getNumberInstance( Locale .US );

	protected Integer getVertexIndex( AlgebraicVector vertexVector )
    {
        Integer val = vertexData .get( vertexVector );
        if ( val == null )
        {
            AlgebraicVector key = vertexVector;
            int index = vertexData .size();
            val = index;
            vertexData .put( key, val );
            vertices .append( "v " );
            RealVector vertex =  vertexVector .toRealVector();
            vertices .append( format .format( vertex.x ) + " " );
            vertices .append( format .format( vertex.y ) + " " );
            vertices .append( format .format( vertex.z ) + " " );
            vertices .append( "\n" );
        }
        return val;
    }

}


