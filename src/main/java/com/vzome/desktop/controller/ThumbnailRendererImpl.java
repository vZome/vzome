
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.desktop.controller;

import java.awt.image.RenderedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.core.viewing.ViewModel;


public class ThumbnailRendererImpl implements ThumbnailRenderer
{
    private final RenderingChanges scene;
    private final RenderingViewer viewer;
    private final ViewPlatformModel vpm;
    
    private static Logger logger = Logger.getLogger( "org.vorthmann.zome.thumbnails" );

    public ThumbnailRendererImpl( RenderingViewer.Factory rvFactory, Lights sceneLighting )
    {
        scene = rvFactory .createRenderingChanges( sceneLighting, false );
        viewer = rvFactory .createRenderingViewer( scene, null );
        vpm = new ViewPlatformModel();
        vpm .addViewer( viewer );
    }

    /* (non-Javadoc)
     * @see org.vorthmann.ui3d.ThumbnailRenderer#captureSnapshot(org.vorthmann.zome.render.RenderedModel, org.vorthmann.zome.viewing.ViewModel, int, org.vorthmann.ui3d.ThumbnailRendererImpl.Listener)
     */
    @Override
    public void captureSnapshot( RenderedModel snapshot, ViewModel view, int maxSize, final Listener callback )
    {
        vpm .restoreView( view );
        scene .reset();
        
        if ( logger .isLoggable( Level.FINER ) )
        {
            logger .finer( "%%%%%%%%%%%%%%%%%%%%%%%%%%  START THUMBNAIL" );
        }
        synchronized ( snapshot )
        {
            RenderedModel .renderChange( new RenderedModel( null, null ), snapshot, scene );
        }
        if ( logger .isLoggable( Level.FINER ) )
        {
            logger .finer( "%%%%%%%%%%%%%%%%%%%%%%%%%%    END THUMBNAIL" );
        }
        viewer .captureImage( 80, new RenderingViewer.ImageCapture()
        {
            public void captureImage( RenderedImage image )
            {
                callback .thumbnailReady( image );
            }
        } );
    }
}
