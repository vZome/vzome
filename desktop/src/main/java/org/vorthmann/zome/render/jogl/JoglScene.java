
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;
import com.vzome.core.render.Colors;
import com.vzome.core.render.OpenGlSceneLoader;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.InstancedRenderer;
import com.vzome.opengl.OpenGlShim;
import com.vzome.opengl.Renderer;
import com.vzome.opengl.Scene;

public class JoglScene implements RenderingChanges
{
	private RenderedModel model;
    private final Colors colors;

    JoglScene( Lights lights, Colors colors, boolean isSticky )
	{
        this.colors = colors;
	}

    void render( OpenGlShim glShim )
    {
        Scene scene = OpenGlSceneLoader .getOpenGlScene( this .model, this .colors );
        Renderer renderer = new InstancedRenderer( glShim );
        renderer .bindBuffers( glShim, scene );
        
        float[] perspective = new float[16];
        FloatUtil .makePerspective( perspective, 0, true, 1.0f, 1.0f, 0.2f, 1000f );
        float[] identity = new float[16];
        FloatUtil .makeIdentity( identity );
        float[] camera = new float[16];
        float[] temp = new float[16];
        FloatUtil .makeLookAt( camera, 0, new float[]{0,0,0}, 0, new float[]{0,0,-50}, 0, new float[]{0,1,0}, 0, temp );
        renderer .renderScene( glShim, identity, camera, identity, perspective, scene );
    }

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationAdded( RenderedManifestation manifestation )
	{
		this .model = manifestation .getModel();
	}

	@Override
	public void manifestationRemoved( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void glowChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void colorChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void locationChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void orientationChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void shapeChanged( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

	}

}
