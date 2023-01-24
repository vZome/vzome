package com.vzome.core.exporters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;

import com.vzome.core.render.Colors;
import com.vzome.core.render.RealZomeScaling;
import com.vzome.core.render.RenderedModel;

public abstract class GeometryExporter implements RealZomeScaling
{
	protected transient PrintWriter output;
	
	protected transient Colors mColors;
	protected transient RenderedModel mModel;
		
	public GeometryExporter()
	{
	    super();
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
    public boolean needsManifestations()
    {
        return true;
    }
    
    protected String getBoilerplate( String resourcePath )
    {
        InputStream input = getClass() .getClassLoader() .getResourceAsStream( resourcePath );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        try {
            while ( ( num = input .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String( out .toByteArray() );
    }

    /**
     * Subclasses can override this if they need to export history, the lesson model, or the selection.
     */
    public void exportGeometry( RenderedModel model, File file, Writer writer, int height, int width ) throws Exception
    {
        mModel = model;
        this .doExport( file, writer, height, width );
        mModel = null;
    }
}


