

package org.vorthmann.j3d;

import java.awt.Component;

import com.vzome.desktop.controller.Controller3d;

public interface J3dComponentFactory
{
    /**
     * Create a canvas for 3D rendering of RenderedManifestations.
     * Underneath the covers, a scene graph and a viewer will also be constructed.
     * All three will be then attached to the controller.
     * For any given RenderedModel, it is important that only one such component is sticky,
     * so that picking behavior will work correctly.
     * @param isSticky determines whether RenderedManifestations rendered here will do setGraphicsObject
     * @param isOffScreen
     * @param controller
     * @return
     */
    Component createRenderingComponent( boolean isSticky, boolean isOffScreen, Controller3d controller );
}
