
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.opengl.RenderingProgram;

/**
 * A lower-level, and hopefully more performant alternative to Java3dRenderingViewer.
 * 
 * Note that even JOGL is behind the times, still supporting immediate mode:
 * 
 *   https://www.khronos.org/opengl/wiki/Legacy_OpenGL
 *   
 * I have not found any modern tutorials for JOGL, so I'm working from
 * 
 *   http://www.opengl-tutorial.org/
 * 
 * @author vorth
 *
 */
public class JoglRenderingViewer implements RenderingViewer
{
    private final JoglScene scene;
    private FPSAnimator animator;
    private final GLU glu = new GLU();
    private double near = 100, far = 2000, fov = 0;
    private float[] matrix = new float[16]; // stored in column-major order, for JOGL-friendliness

    public JoglRenderingViewer( JoglScene scene, GLCanvas canvas )
    {
        this .scene = scene;

        if ( canvas == null )
            return;

        canvas .addGLEventListener( new GLEventListener()
        {
            JoglOpenGlShim glShim;
            RenderingProgram renderer = null;
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height )
            {
                if ( this .renderer == null ) {
                    this .glShim = new JoglOpenGlShim( glautodrawable .getGL() .getGL2() );
                    this .renderer = new RenderingProgram( this .glShim, true, true );
                }
                // TODO: update the stored width and height
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
                if ( this .glShim .isSameContext( glautodrawable .getGL() .getGL2() ) )
                    JoglRenderingViewer.this .scene .render( this .renderer, matrix, glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
                else
                    System.out.println( "Different GL2!" );
            }
        });

        // Start animator (which should be a field).
        this .animator = new FPSAnimator( canvas, 60 );
        this .animator .start();
    }

    @Override
    public void setEye( int eye ) {}

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
                this .matrix[ i ] = (float) copy .getElement( row, column );
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
