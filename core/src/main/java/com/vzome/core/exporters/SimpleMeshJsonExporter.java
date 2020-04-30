package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vzome.core.model.SimpleMeshJson;


public class SimpleMeshJsonExporter extends Exporter3d
{			
    public SimpleMeshJsonExporter()
    {
        super( null, null, null, null );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        SimpleMeshJson .generate( mModel .getManifestations(), mModel .getField(), writer );
    }

    @Override
    public String getFileExtension()
    {
        return "mesh.json";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
}
