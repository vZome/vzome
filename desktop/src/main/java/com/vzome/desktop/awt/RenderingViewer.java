
package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.vzome.core.math.Line;
import com.vzome.core.render.ManifestationPicker;
import com.vzome.desktop.controller.CameraController;

public interface RenderingViewer extends CameraController.Viewer, ManifestationPicker
{
    Component getCanvas();
    
    Line pickRay( MouseEvent e );
	        
    BufferedImage captureImage( int maxSize, boolean withAlpha );
}