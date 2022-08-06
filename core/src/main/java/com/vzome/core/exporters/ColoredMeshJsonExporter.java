package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vzome.core.model.ColoredMeshJson;


public class ColoredMeshJsonExporter extends Exporter3d
{
    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        ColoredMeshJson .generate( mModel .getManifestations(), mModel .getField(), writer );
    }

    @Override
    public String getFileExtension()
    {
        return "cmesh.json";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
}


