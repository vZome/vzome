
package com.vzome.api;

import java.io.Writer;

import com.vzome.core.exporters.DocumentExporter;
import com.vzome.core.exporters.GeometryExporter;

public class Exporter
{
    private GeometryExporter delegate;
    
    Exporter( GeometryExporter privateExp )
    {
        this .delegate = privateExp;
    }
    
    public boolean isValid()
    {
        return this.delegate != null;
    }

    public void doExport( Document model, Writer out, int height, int width ) throws Exception
    {
        if ( this .delegate instanceof DocumentExporter )
            ((DocumentExporter) this .delegate) .exportDocument( model .delegate, null, out, height, width );
        else
            this .delegate .exportGeometry( model .delegate .getRenderedModel(), null, out, height, width );
    }

    public String getContentType()
    {
        return this .delegate .getContentType();
    }
}
