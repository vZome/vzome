
package com.vzome.desktop.awt;

//import java.awt.image.BaseMultiResolutionImage;  // waiting for Java 11!
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vorthmann.j3d.J3dComponentFactory;

import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.Scene;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.desktop.controller.CameraController;


public class ThumbnailRendererImpl extends CameraController implements ThumbnailRenderer
{
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.thumbnails" );
    private J3dComponentFactory rvFactory;

    public ThumbnailRendererImpl( Lights sceneLighting, int maxOrientations )
    {
        super( new Camera(), sceneLighting, maxOrientations );
    }
    
    public void setFactory( J3dComponentFactory rvFactory )
    {
        this .rvFactory = rvFactory;
    }

    /* (non-Javadoc)
     * @see org.vorthmann.ui3d.ThumbnailRenderer#captureSnapshot(org.vorthmann.zome.render.RenderedModel, org.vorthmann.zome.viewing.ViewModel, int, org.vorthmann.ui3d.ThumbnailRendererImpl.Listener)
     */
    @Override
    public void captureSnapshot( RenderedModel snapshot, Camera camera, int maxSize, final Listener callback )
    {
        if ( snapshot == null ) {
            // hope we're using VSCode to debug a vZome file with lastStickyEdit = -1
            return; // avoid NPE
        }
        super .restoreView( camera );

        Scene scene = new Scene( this .sceneLighting, false, 60 ); // TODO: reuse the shapes from the main scene

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

        RenderingViewer viewer = this .rvFactory .createRenderingViewer( scene );
        this .addViewer( viewer );
        viewer .setSize( 80, 70 ); // to control the aspect ratio
//        BufferedImage lowRes = viewer .captureImage( 80, true );
        BufferedImage hiRes = viewer .captureImage( 120, true );
        this .removeViewer( viewer );
//        BaseMultiResolutionImage baseMultiResolutionImage = new BaseMultiResolutionImage( lowRes, hiRes );
        callback .thumbnailReady( hiRes );
    }
}
