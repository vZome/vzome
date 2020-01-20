
package com.vzome.desktop.controller;

import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;

import com.vzome.core.math.Line;
import com.vzome.core.render.ManifestationPicker;
import com.vzome.core.render.RenderingChanges;

public interface RenderingViewer extends CameraController.Viewer, ManifestationPicker
{
    Line pickRay( MouseEvent e );
	
    RenderingChanges getRenderingChanges();
    
//    org.vorthmann.zome.scenegraph.Factory getSceneGraphFactory();
    
    void captureImage( int maxSize, boolean withAlpha, ImageCapture capture );

    public interface ImageCapture
    {
        void captureImage( RenderedImage image );
    }

    //    Group getSceneGraphRoot();
}