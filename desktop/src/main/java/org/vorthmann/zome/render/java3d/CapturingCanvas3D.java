
package org.vorthmann.zome.render.java3d;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Raster;
import javax.media.jai.JAI;
import javax.vecmath.Point3f;

import com.vzome.desktop.controller.RenderingViewer;

// some of this is from http://www.j3d.org/faq/capturing.html

public class CapturingCanvas3D extends Canvas3D
{
    public CapturingCanvas3D( GraphicsConfiguration gc, boolean isOffscreen )
    {
        super( gc, isOffscreen );
        postSwapCount_ = 0;
    }

    public transient RenderingViewer.ImageCapture m_imageHandler = null;
    public transient int maxImageSize = -1;
    private int postSwapCount_;

    public CapturingCanvas3D( GraphicsConfiguration gc )
    {
        super( gc );
        postSwapCount_ = 0;
    }

    //        public void setBounds( int x, int y, int width, int height )
    //        {
    //            super .setBounds( x, y, width, height );
    //            System .out .println( "canvas3d " + x + " " + y + " " + width + " " + height );
    //        }

    @Override
    public void postSwap()
    {
        if( m_imageHandler != null ) {
            GraphicsContext3D ctx = getGraphicsContext3D();
            Dimension size = this.getSize();
            int sizeX = size .width;
            int sizeY = size .height;

            //              float aspectRatio = ((float) sizeX) / ((float) sizeY);
            //				int max = m_imageHandler .getMaxSize();
            //				if ( aspectRatio > 1.0f )
            //				{
            //				    if ( sizeX > max )
            //				    {
            //				        sizeX = max;
            //				        sizeY = (int) Math .floor( max / aspectRatio );
            //				    }
            //				}
            //				else
            //				{
            //                    if ( sizeY > max )
            //                    {
            //                        sizeY = max;
            //                        sizeX = (int) Math .floor( max * aspectRatio );
            //                    }
            //				}

            // The raster components need all be set!
            Raster ras = new Raster(
                    new Point3f(-1.0f,-1.0f,-1.0f),
                    Raster.RASTER_COLOR,
                    0,0,
                    sizeX, sizeY,
                    new ImageComponent2D(
                            ImageComponent.FORMAT_RGB,
                            new BufferedImage( sizeX, sizeY,
                                    BufferedImage.TYPE_INT_RGB)),
                                    null);

            ctx.readRaster(ras);

            // Now strip out the image info
            BufferedImage img = ras.getImage().getImage();

            if ( maxImageSize > 0 )
            {
                float scale = 1.0f;
                int h = img .getHeight();
                int w = img .getWidth();
                if ( h > w )
                    w = h;
                if ( w > maxImageSize )
                {
                    scale = ((float) maxImageSize) / ((float) w);
                }
                ParameterBlock params = new ParameterBlock();
                params.addSource( img );
                params.add( scale );// x scale factor
                params.add( scale );// y scale factor
                params.add( 0.0f );// x translate
                params.add( 0.0f );// y translate

                Map<RenderingHints.Key, Object> map = new HashMap<>();
                map.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                map.put( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
                map.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
                map.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );

                RenderingHints hints = new RenderingHints( map );

                // Here's the important bit - use "SubsampleAverage" instead of "scale" 
                img = JAI .create( "scale", params, hints ) .getAsBufferedImage();
            }

            // since we're currently running on some Java3d rendering thread, we want to make sure
            //   that we do the capture (which triggers a repaint) on the AWT event handler thread.
            final BufferedImage bufImg = img;
            final RenderingViewer.ImageCapture capturer = this .m_imageHandler;
            EventQueue .invokeLater( new Runnable(){

				@Override
				public void run() {
					capturer .captureImage( bufImg );
				}} );

            postSwapCount_++;
            m_imageHandler = null;
            maxImageSize = -1;
        }
    }
}