package com.vzome.core.exporters;

import java.io.File;
import java.io.Writer;

import com.vzome.core.viewing.CameraIntf;
import com.vzome.core.viewing.Lights;

public abstract class DocumentExporter extends GeometryExporter
{
    protected transient Lights mLights;
    protected transient CameraIntf mScene;

    /**
     * Subclasses can override this if they need to export history, the lesson model, or the selection.
     */
    public void exportDocument( DocumentIntf doc, File file, Writer writer, int height, int width ) throws Exception
    {
        mScene = doc .getCameraModel();
        mLights = doc .getSceneLighting();
        this .exportGeometry( doc .getRenderedModel(), file, writer, height, width );
        mScene = null;
        mLights = null;
    }
}
