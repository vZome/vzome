

package org.vorthmann.j3d;

import java.awt.Component;

import com.vzome.desktop.controller.Controller3d;

public interface J3dComponentFactory
{
    /**
     * Create a canvas for 3D rendering of RenderedManifestations.
     * Underneath the covers, a scene graph and a viewer will also be constructed.
     * All three will be then attached to the controller.
     * @param isOffScreen
     * @param controller
     * @return
     */
    Component createRenderingComponent( boolean isOffScreen, Controller3d controller );
}
