
package com.vzome.desktop.awt;

import java.awt.event.MouseEvent;

import com.vzome.core.math.Line;
import com.vzome.core.render.ManifestationPicker;
import com.vzome.desktop.controller.CameraController;

public interface RenderingViewer extends CameraController.Viewer, ManifestationPicker, GraphicsViewer
{
    Line pickRay( MouseEvent e );
}