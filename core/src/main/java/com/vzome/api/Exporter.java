
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.io.Writer;

import com.vzome.core.exporters.Exporter3d;

public class Exporter
{
    private Exporter3d delegate;
    
    Exporter( Exporter3d privateExp )
    {
        this .delegate = privateExp;
    }
    
    public boolean isValid()
    {
        return this.delegate != null;
    }

    public void doExport( Document model, Writer out, int height, int width ) throws Exception
    {
        this .delegate .doExport( model .delegate, null, null, out, height, width );
    }

    public String getContentType()
    {
        return this .delegate .getContentType();
    }
}
