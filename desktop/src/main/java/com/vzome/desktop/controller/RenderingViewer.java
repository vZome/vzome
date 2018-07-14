
package com.vzome.desktop.controller;

import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;

import javax.vecmath.Point3d;

import com.vzome.core.render.ManifestationPicker;
import com.vzome.core.render.RenderingChanges;

public interface RenderingViewer extends CameraController.Viewer, ManifestationPicker
{
    void pickPoint( MouseEvent e, Point3d imagePt, Point3d eyePt );
	
    RenderingChanges getRenderingChanges();
    
//    org.vorthmann.zome.scenegraph.Factory getSceneGraphFactory();
    
    void captureImage( int maxSize, ImageCapture capture );

    public interface ImageCapture
    {
        void captureImage( RenderedImage image );
    }

    //    Group getSceneGraphRoot();
}