
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.RenderingViewer;

public class JoglRenderingViewer implements RenderingViewer
{
    private final JoglScene scene;
    private FPSAnimator animator;
    private final GLU glu = new GLU();
    private double near = 100, far = 2000, fov = 0;
    private double[] matrix = new double[16]; // stored in column-major order, for JOGL-friendliness

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
                JoglRenderingViewer.this .updateView( glautodrawable .getGL() .getGL2(), width, height );
            }

            @Override
            public void init( GLAutoDrawable glautodrawable ) {}

            @Override
            public void dispose( GLAutoDrawable glautodrawable )
            {
                animator .stop();
            }

            @Override
            public void display( GLAutoDrawable glautodrawable )
            {
                JoglRenderingViewer.this .updateView( glautodrawable .getGL() .getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
                // GL commands to render the scene here
                JoglRenderingViewer.this .scene .render( glautodrawable .getGL() .getGL2() );
            }
        });

        // Start animator (which should be a field).
        this .animator = new FPSAnimator( canvas, 60 );
        this .animator .start();
    }

    private void updateView( GL2 gl2, int width, int height )
    {
        gl2 .glMatrixMode( GL2.GL_PROJECTION );
        gl2 .glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        if ( this .fov == 0 )
            this .glu .gluOrtho2D( 0.0f, width, 0.0f, height );
        else
            this .glu .gluPerspective( this .fov, width / height, this .near, this .far );
        glu .gluLookAt( 0, 0, (this .far - this .near) / 2f, 0, 0, 0, 0, 1, 0 );

        gl2 .glMatrixMode( GL2.GL_MODELVIEW );
        gl2 .glLoadMatrixd( this .matrix, 0 );

        gl2 .glViewport( 0, 0, width, height );
    }

    @Override
    public void setEye( int eye )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setViewTransformation( Matrix4d trans, int eye )
    {
        Matrix4d copy = new Matrix4d();
        copy .invert( trans );
        int i = 0;
        // JOGL requires column-major order
        for ( int column = 0; column < 4; column++ )
            for ( int row = 0; row < 4; row++ )
            {
                this .matrix[ i ] = copy .getElement( row, column );
                ++i;
            }
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
    public Collection<RenderedManifestation> pickCube()
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
