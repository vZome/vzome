package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RealZomeScaling;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.Camera;

public abstract class Exporter3d implements RealZomeScaling
{
	protected transient PrintWriter output;
	
	protected transient Camera mScene;
	protected transient Colors mColors;
	protected transient Lights mLights;
	protected transient RenderedModel mModel;
	
	public Exporter3d( Camera scene, Colors colors, Lights lights, RenderedModel model )
	{
	    mScene = scene;
	    mColors = colors;
	    mLights = lights;
        mModel = model;
	}

	/**
	 * This is what most subclasses override.
	 */
	public abstract void doExport( File file, Writer writer, int height, int width ) throws Exception;

    public abstract String getFileExtension();
    
    public String getContentType()
    {
        return "text/plain";
    }

    /**
     * Subclasses can override this if they don't rely on Manifestations and therefore can operate on article pages
     * See the comments below DocumentModel.getNaiveExporter() for a more complete explanation.
     */
    public boolean needsManifestations() {
        return true;
    }

    /**
     * Subclasses can override this if they need to export history or the lesson model.
     */
    public void exportDocument( DocumentModel doc, File file, Writer writer, int height, int width ) throws Exception
    {
        mScene = doc .getCamera();
        mModel = doc .getRenderedModel();
        mLights = doc .getSceneLighting();
        this .doExport( file, writer, height, width );
        mScene = null;
        mModel = null;
        mLights = null;
    }
}


