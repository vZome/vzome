
//(c) Copyright 2011, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.Component;
import java.util.List;

import org.vorthmann.j3d.J3dComponentFactory;

import com.jogamp.nativewindow.CapabilitiesImmutable;
import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.vzome.core.render.Colors;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;

public class JoglFactory implements J3dComponentFactory
{
    public JoglFactory( Colors colors, Boolean useEmissiveColor ) {}
    
    // MSAA (anti-aliasing) implemented per
    //   https://jogamp.org/jogl-demos/src/demos/multisample/Multisample.java
    
    // Simple class to warn if results are not going to be as expected
    static class MultisampleChooser extends DefaultGLCapabilitiesChooser {
      public int chooseCapabilities(GLCapabilities desired,
                                    List<? extends CapabilitiesImmutable> available,
                                    int windowSystemRecommendedChoice) {
        boolean anyHaveSampleBuffers = false;
        for (int i = 0; i < available.size(); i++) {
          GLCapabilitiesImmutable caps = (GLCapabilitiesImmutable) available.get(i);
          if (caps != null && caps.getSampleBuffers()) {
            anyHaveSampleBuffers = true;
            break;
          }
        }
        int selection = super.chooseCapabilities(desired, available, windowSystemRecommendedChoice);
        if (!anyHaveSampleBuffers) {
          System.err.println("WARNING: antialiasing will be disabled because none of the available pixel formats had it to offer");
        } else if(selection>=0) {
          GLCapabilitiesImmutable caps = (GLCapabilitiesImmutable) available.get(selection);
          if (!caps.getSampleBuffers()) {
            System.err.println("WARNING: antialiasing will be disabled because the DefaultGLCapabilitiesChooser didn't supply it");
          }
        }
        return selection;
      }
    }

    @Override
    public Component createRenderingComponent( boolean isSticky, boolean isOffScreen, Controller3d controller )
    {
        GLProfile glprofile = GLProfile .getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        GLCapabilitiesChooser chooser = new MultisampleChooser();

        glcapabilities .setSampleBuffers(true);
        glcapabilities .setNumSamples(4);

        glcapabilities .setDepthBits( 24 );
        GLCanvas glcanvas = new GLCanvas( glcapabilities, chooser, null );
        
        Lights lights = controller .getSceneLighting();
        JoglScene scene = new JoglScene( controller, lights, isSticky );
        RenderingViewer viewer = new JoglRenderingViewer( lights, scene, glcanvas );

        controller .attachViewer( viewer, scene, glcanvas );

        return glcanvas;
    }
}
