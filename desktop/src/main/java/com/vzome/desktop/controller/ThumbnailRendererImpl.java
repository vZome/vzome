
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.desktop.controller;

import java.awt.Component;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vorthmann.j3d.J3dComponentFactory;

import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.ThumbnailRenderer;


public class ThumbnailRendererImpl extends CameraController implements ThumbnailRenderer, Controller3d
{
    private RenderingChanges scene;
    private RenderingViewer viewer;
    
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.thumbnails" );

    public ThumbnailRendererImpl( J3dComponentFactory rvFactory )
    {
        super( new Camera() );
        rvFactory .createRenderingComponent( true, true, this );
    }

    /* (non-Javadoc)
     * @see org.vorthmann.ui3d.ThumbnailRenderer#captureSnapshot(org.vorthmann.zome.render.RenderedModel, org.vorthmann.zome.viewing.ViewModel, int, org.vorthmann.ui3d.ThumbnailRendererImpl.Listener)
     */
    @Override
    public void captureSnapshot( RenderedModel snapshot, Camera camera, int maxSize, final Listener callback )
    {
        super .restoreView( camera );
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
            @Override
            public void captureImage( RenderedImage image )
            {
                callback .thumbnailReady( image );
            }
        } );
    }

    @Override
    public void attachViewer( RenderingViewer viewer, RenderingChanges scene, Component canvas )
    {
        // no picking, etc., so we don't care about the canvas
        this .scene = scene;
        this .viewer = viewer;
        super .addViewer( viewer );
    }
}
