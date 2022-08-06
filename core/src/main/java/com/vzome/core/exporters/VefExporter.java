package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.RenderedManifestation;


public class VefExporter extends Exporter3d
{
    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
	    AlgebraicField field = mModel .getField();
        VefModelExporter exporter = new VefModelExporter( writer, field );
		
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();
            exporter .exportManifestation( man );
        }
        
        exporter .finish();
	}

    @Override
    public String getFileExtension()
    {
        return "vef";
    }
}


