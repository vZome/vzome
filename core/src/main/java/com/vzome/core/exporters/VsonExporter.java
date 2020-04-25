package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vzome.core.mesh.ColoredMeshJson;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


public class VsonExporter extends Exporter3d
{			
    public VsonExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws IOException
    {
        ColoredMeshJson .generate( mModel .getManifestations(), mModel .getField(), writer );
    }

    @Override
    public String getFileExtension()
    {
        return "vson";
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
}


