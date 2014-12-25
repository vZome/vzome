
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import com.jogamp.opengl.util.FPSAnimator;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.RenderingViewer;

public class JoglRenderingViewer implements RenderingViewer
{
	private final JoglScene scene;
	private FPSAnimator animator;
	private final GLU glu = new GLU();
	private int width, height;
	private double near = 100, far = 2000, fov = 45;
	private double halfEdge = 100;

	public JoglRenderingViewer( JoglScene scene, GLCanvas canvas )
	{
		this .scene = scene;

		if ( canvas == null )
			return;
		
		canvas .addGLEventListener( new GLEventListener()
		{    
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height )
            {
                JoglRenderingViewer.this .width = width;
                JoglRenderingViewer.this .height = height;
                JoglRenderingViewer.this .updateView( glautodrawable .getGL() .getGL2() );
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {}
            
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {}
            
            @Override
            public void display( GLAutoDrawable glautodrawable )
            {
                JoglRenderingViewer.this .updateView( glautodrawable .getGL() .getGL2() );
            	// GL commands to render the scene here
            	JoglRenderingViewer.this .scene .render( glautodrawable .getGL() .getGL2() );
            }
        });
		
        // Start animator (which should be a field).
        this .animator = new FPSAnimator( canvas, 60 );
        this .animator .start();
	}

    private void updateView( GL2 gl2 )
    {
        gl2 .glMatrixMode( GL2.GL_PROJECTION );
        gl2 .glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        if ( this .fov == 0 )
        	gl2 .glOrtho( - this .halfEdge, this .halfEdge, - this .halfEdge, this .halfEdge, this .near, this .far );
        else
        	this .glu .gluPerspective( this .fov, this .width / this .height, this .near, this .far );
        glu .gluLookAt( 0, 0, (this .far - this .near) / 2f, 0, 0, 0, 0, 1, 0 );

        gl2 .glMatrixMode( GL2.GL_MODELVIEW );
        gl2 .glLoadIdentity();

        gl2 .glViewport( 0, 0, this .width, this .height );
    }

	@Override
	public void setEye( int eye )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setViewTransformation( Matrix4d trans, int eye )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPerspective( double fov, double aspectRatio, double near, double far )
	{
		this .fov = fov;
		this .near = near;
		this .far = far;
	}

	@Override
	public void setOrthographic( double halfEdge, double near, double far )
	{
		this .fov = 0;
		this .halfEdge = halfEdge / 70;
		this .near = near;
		this .far = far;
	}

	@Override
	public RenderedManifestation pickManifestation( MouseEvent e )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection pickCube()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pickPoint( MouseEvent e, Point3d imagePt, Point3d eyePt )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public RenderingChanges getRenderingChanges()
	{
		return this .scene;
	}

	@Override
	public void captureImage( int maxSize, ImageCapture capture )
	{
		// TODO Auto-generated method stub
	}

}
