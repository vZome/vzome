
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.FloatUtil;
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
public class JoglRenderingViewer implements RenderingViewer, GLEventListener
{
    private final JoglScene scene;
    private JoglOpenGlShim glShim;
    private RenderingProgram renderer = null;
    private FPSAnimator animator;

    private float near = 100, far = 2000, fovX = 0.5f;
    private float[] matrix = new float[16]; // stored in column-major order, for JOGL-friendliness
    private float aspectRatio = 1f;
    private float halfEdgeX;

    public JoglRenderingViewer( JoglScene scene, GLCanvas canvas )
    {
        this .scene = scene;

        if ( canvas == null )
            return;

        canvas .addGLEventListener( this );

        // Start animator (which should be a field).
        this .animator = new FPSAnimator( canvas, 60 );
        this .animator .start();
    }
    
    // These are GLEventListener methods

    @Override
    public void init( GLAutoDrawable drawable )
    {
        this .glShim = new JoglOpenGlShim( drawable .getGL() .getGL2() );
        this .renderer = new RenderingProgram( this .glShim );
        // store the scene geometry
    }

    @Override
    public void dispose( GLAutoDrawable drawable )
    {
        this .animator .stop();
    }

    @Override
    public void display( GLAutoDrawable drawable )
    {
        if ( this .glShim .isSameContext( drawable .getGL() .getGL2() ) ) {
            this .render();
        }
        else
            System.out.println( "Different GL2!" );
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
    {
        this .aspectRatio = (float) width / (float) height;
        this .render();
    }

    // Private methods
    
    private void render()
    {
        if ( this .renderer == null )
            return; // still initializing
        
        float[] projection = new float[16];
        float[] objectTrans = new float[16];

        // Object first appears directly in front of user
        FloatUtil.makeIdentity( objectTrans );
        
        if ( this .fovX == 0f ) {
            float halfEdgeY = this .halfEdgeX / this .aspectRatio;
            FloatUtil .makeOrtho( projection, 0, true, -halfEdgeX, halfEdgeX, -halfEdgeY, halfEdgeY, this .near, this .far );
        } else {
            // The Camera model is set up for fovX, but FloatUtil.makePerspective wants fovY
            float fovY = this .fovX / this .aspectRatio;
            FloatUtil .makePerspective( projection, 0, true, fovY, this .aspectRatio, this .near, this .far );
        }
        renderer .setUniforms( objectTrans, this.matrix, projection );

        this .scene .render( this .renderer );
    }

    // These are RenderingViewer methods
    
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
        this .render();
    }

    @Override
    public void setPerspective( double fovX, double aspectRatio, double near, double far )
    {
        this .fovX = (float) fovX;
        this .near = (float) near;
        this .far = (float) far;

        this .render();
    }

    @Override
    public void setOrthographic( double halfEdgeX, double near, double far )
    {
        this .halfEdgeX = (float) halfEdgeX;
        this .fovX = 0;
        this .near = (float) near;
        this .far = (float) far;
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
