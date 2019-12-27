
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;

public class JoglScene implements RenderingChanges
{
	JoglScene( Lights lights, boolean isSticky )
	{
		// TODO Auto-generated constructor stub
	}

    void render( GL2 gl2 )
    {
        gl2.glClear( GL.GL_COLOR_BUFFER_BIT );

        gl2.glBegin( GL.GL_TRIANGLES );
        gl2.glColor3f( 1, 0, 0 );
        gl2.glVertex3f( 0, 0, 0 );
        gl2.glColor3f( 0, 1, 0 );
        gl2.glVertex3f( 1, 0, 0 );
        gl2.glColor3f( 0, 0, 1 );
        gl2.glVertex3f( 0, 1, 0 );
        gl2.glEnd();
    }

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void manifestationAdded( RenderedManifestation manifestation )
	{
		// TODO Auto-generated method stub

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
