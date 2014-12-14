package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
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
	    this .field = model .getField();
	}

	public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
	{
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format) .applyPattern( "0.0000" );
        }		
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = ((RenderedManifestation) rms .next()) .getManifestation();
            
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

    private Map vertexData = new HashMap();
    
    private final StringBuffer vertices = new StringBuffer();

    private final StringBuffer struts = new StringBuffer();

    protected final AlgebraicField field;
    
	private final NumberFormat format = NumberFormat .getNumberInstance( Locale .US );

	protected Integer getVertexIndex( int[] vertexVector )
    {
        Integer obj = (Integer) vertexData .get( new AlgebraicVector( vertexVector ) );
        if ( obj == null )
        {
            AlgebraicVector key = new AlgebraicVector( vertexVector );
            int index = vertexData .size();
            obj = new Integer( index );
            vertexData .put( key, obj );
            vertices .append( "v " );
            RealVector vertex = key .toRealVector( field );
            vertices .append( format .format( vertex.x ) + " " );
            vertices .append( format .format( vertex.y ) + " " );
            vertices .append( format .format( vertex.z ) + " " );
            vertices .append( "\n" );
        }
        return obj;
    }

}


