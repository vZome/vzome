

package org.vorthmann.j3d;

import java.awt.Component;

import com.vzome.desktop.controller.Controller3d;

public interface J3dComponentFactory
{
    Component createRenderingComponent( boolean isSticky, boolean isOffScreen, Controller3d controller );
}
