package com.vzome.core.exporters;

import java.io.File;
import java.io.Writer;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;

public abstract class DocumentExporter extends GeometryExporter
{
    protected transient Lights mLights;
    protected transient Camera mScene;

    /**
     * Subclasses can override this if they need to export history, the lesson model, or the selection.
     */
    public void exportDocument( DocumentModel doc, File file, Writer writer, int height, int width ) throws Exception
    {
        mScene = doc .getCamera();
        mLights = doc .getSceneLighting();
        this .exportGeometry( doc .getRenderedModel(), file, writer, height, width );
        mScene = null;
        mLights = null;
    }
}
