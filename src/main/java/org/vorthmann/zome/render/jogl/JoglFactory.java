
//(c) Copyright 2011, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.Component;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.vorthmann.j3d.J3dComponentFactory;

import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.desktop.controller.RenderingViewer.Factory;

public class JoglFactory implements Factory, J3dComponentFactory
{
    public JoglFactory( Colors colors, Boolean useEmissiveColor )
    {
    }
    
    @Override
	public Component createJ3dComponent( String name )
	{
        GLProfile glprofile = GLProfile .getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        return glcanvas;
    }

	@Override
	public RenderingChanges createRenderingChanges( Lights lights, boolean isSticky )
	{
		return new JoglScene( lights, isSticky );
	}

	@Override
	public RenderingViewer createRenderingViewer( RenderingChanges scene, Component canvas )
	{
		return new JoglRenderingViewer( (JoglScene) scene, (GLCanvas) canvas );
	}
}
