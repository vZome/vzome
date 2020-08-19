
package com.vzome.desktop.controller;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;

import com.vzome.core.math.Line;
import com.vzome.core.render.ManifestationPicker;

public interface RenderingViewer extends CameraController.Viewer, ManifestationPicker
{
    Component getCanvas();
    
    Line pickRay( MouseEvent e );
	        
    void captureImage( int maxSize, boolean withAlpha, ImageCapture capture );

    public interface ImageCapture
    {
        void captureImage( RenderedImage image );
    }
}