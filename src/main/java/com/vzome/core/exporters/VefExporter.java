package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.model.Exporter;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;


public class VefExporter extends Exporter3d
{
	public VefExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}

	public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
	{
	    AlgebraicField field = mModel .getField();
        int[] scale = field .createPower( -5 );
	    Exporter exporter = new VefModelExporter( writer, field, scale );
		
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            Manifestation man = ((RenderedManifestation) rms .next()) .getManifestation();
            
            exporter .exportManifestation( man );
        }
        
        exporter .finish();
	}

    public String getFileExtension()
    {
        return "vef";
    }
}


