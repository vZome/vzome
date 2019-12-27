
//(c) Copyright 2011, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.Component;

import org.vorthmann.j3d.J3dComponentFactory;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.vzome.core.render.Colors;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;

public class JoglFactory implements J3dComponentFactory
{
    public JoglFactory( Colors colors, Boolean useEmissiveColor ) {}

    @Override
    public Component createRenderingComponent( boolean isSticky, boolean isOffScreen, Controller3d controller )
    {
        GLProfile glprofile = GLProfile .getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        JoglScene scene = new JoglScene( controller .getSceneLighting(), isSticky );
        RenderingViewer viewer = new JoglRenderingViewer( scene, glcanvas );
        controller .attachViewer( viewer, scene, glcanvas );
        return glcanvas;
    }
}
