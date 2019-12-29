
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import com.jogamp.opengl.math.FloatUtil;
import com.vzome.core.render.Colors;
import com.vzome.core.render.OpenGlSceneLoader;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.opengl.OpenGlShim;
import com.vzome.opengl.RenderingProgram;
import com.vzome.opengl.Scene;
import com.vzome.opengl.ShapeClass;

public class JoglScene implements RenderingChanges
{
	private RenderedModel model;
    private final Colors colors;

    JoglScene( Lights lights, Colors colors, boolean isSticky )
	{
        this.colors = colors;
	}

    void render( OpenGlShim gl )
    {
        gl .glEnableDepth();
        gl .glClear( 0.4f, 0.7f, 1.0f, 1.0f );
        
        Scene scene = OpenGlSceneLoader .getOpenGlScene( this .model, this .colors );
        RenderingProgram renderer = new RenderingProgram( gl, true, true );
//        renderer .bindBuffers( gl, scene );
        
        float[] projection = new float[16];
        float[] objectTranslate = new float[16];
        float[] camera = new float[16];

        // Object first appears directly in front of user
        FloatUtil.makeTranslation( objectTranslate, true, 0, 0, -1f );

        // Build the camera matrix and apply it to the ModelView.
        FloatUtil.makeLookAt( camera, 0, new float[]{0.0f, 0.0f, 0.01f}, 0, new float[]{0.0f, 0.0f, 0.0f}, 0, new float[]{0.0f, 1.0f, 0.0f}, 0, new float[16] );
        
        FloatUtil.makePerspective( projection, 0, true, 0.6f, 1.0f, 0.1f, 1000f );

        renderer .setUniforms( gl, objectTranslate, camera, projection, scene .getOrientations() );
        for( ShapeClass shapeClass : scene )
            renderer .renderShape( gl, shapeClass );
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
