
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Ray;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vzome.core.math.Line;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.opengl.OutlineRenderer;
import com.vzome.opengl.Renderer;
import com.vzome.opengl.SolidRenderer;

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
    private Renderer outlines = null;
    private Renderer solids = null;
    private FPSAnimator animator;

    private float near = 100, far = 2000, fovX = 0.5f;
    private float[] projection = new float[16];
    private float[] modelView = new float[16]; // stored in column-major order, for JOGL-friendliness
    private float aspectRatio = 1f;
    private float halfEdgeX;
    private float[][] lightDirections;
    private float[][] lightColors;
    private float[] ambientLight;
    private int width;
    private int height;
    private boolean forceRender = true;
    private float fogFront;

    public JoglRenderingViewer( Lights lights, JoglScene scene, GLAutoDrawable drawable )
    {
        this .scene = scene;

        if ( drawable == null )
            return;

        int num = lights .size();
        this .lightDirections = new float[num][];
        this .lightColors = new float[num][];
        for ( int i = 0; i < num; i++ ) {
            Vector3f direction = new Vector3f();
            Color color = lights.getDirectionalLight( i, direction ); // sets direction as a side-effect
            this .lightColors[i] = new float[4];
            color .getRGBColorComponents( this .lightColors[i] );
            this .lightDirections[i] = new float[3];
            this .lightDirections[i][0] = direction .x;
            this .lightDirections[i][1] = direction .y;
            this .lightDirections[i][2] = direction .z;
        }
        Color color = lights .getAmbientColor();
        this .ambientLight = new float[4];
        color .getRGBColorComponents( this .ambientLight );

        drawable .addGLEventListener( this );

        // Start animator (which should be a field).
        this .animator = new FPSAnimator( drawable, 60 );
        this .animator .start();
    }

    // Private methods
    
    private void render()
    {
        if ( this .solids == null )
            return; // still initializing
        
        this .solids .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        this .solids .setView( this .modelView, this .projection, this .near, this .fogFront, this .far, this .fovX != 0f );
        // OutlineRenderer doesn't currently use lights, but calling setLights() doesn't hurt and is consistent with captureImage()
        this .outlines .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        this .outlines .setView( this.modelView, projection, this .near, this .fogFront, this .far, this .fovX != 0f );
        this .scene .render( this .solids, this .outlines, this .forceRender );
        this .forceRender = false;
    }
    
    private void setProjection()
    {
        if ( this .fovX == 0f ) {
            float halfEdgeY = this .halfEdgeX / this .aspectRatio;
            FloatUtil .makeOrtho( projection, 0, true, -halfEdgeX, halfEdgeX, -halfEdgeY, halfEdgeY, this .near, this .far );
        } else {
            // The Camera model is set up for fovX, but FloatUtil.makePerspective wants fovY
            float fovY = this .fovX / this .aspectRatio;
            FloatUtil .makePerspective( projection, 0, true, fovY, this .aspectRatio, this .near, this .far );
        }
        float diff = ( this .far - this .near )/5; // offset from near and far by 20%
        this .fogFront = near + 2 * diff;
        this .forceRender  = true;
    }

    // These are GLEventListener methods

    @Override
    public void init( GLAutoDrawable drawable )
    {
        this .glShim = new JoglOpenGlShim( drawable .getGL() .getGL2() );
        boolean useVBOs = true;  // this context will rendered many, many times
        this .solids = new SolidRenderer( this .glShim, useVBOs );
        this .outlines = new OutlineRenderer( this .glShim, useVBOs );
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

    // These are RenderingViewer methods
    
    @Override
    public void setEye( int eye ) {}

    @Override
    public void setViewTransformation( Matrix4d trans, int eye )
    {
        if ( eye != MONOCULAR )
            return;
        Matrix4d copy = new Matrix4d();
        copy .invert( trans );
        int i = 0;
        // JOGL requires column-major order
        for ( int column = 0; column < 4; column++ )
            for ( int row = 0; row < 4; row++ )
            {
                this .modelView[ i ] = (float) copy .getElement( row, column );
                ++i;
            }
        this .forceRender  = true;
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
    {
        this.width = width;
        this.height = height;
        this .aspectRatio = (float) width / (float) height;
        this .setProjection();
    }

    @Override
    public void setPerspective( double fovX, double aspectRatio, double near, double far )
    {
        this .fovX = (float) fovX;
        this .near = (float) near;
        this .far = (float) far;
        this .setProjection();
    }

    @Override
    public void setOrthographic( double halfEdgeX, double near, double far )
    {
        this .halfEdgeX = (float) halfEdgeX;
        this .fovX = 0;
        this .near = (float) near;
        this .far = (float) far;
        this .setProjection();
    }

    @Override
    public RenderedManifestation pickManifestation( MouseEvent e )
    {
        Line ray = this .pickRay( e );

        // The scene will loop over all RMs.  Rendered balls will support the RM.isHit(intersector)
        //   method.  We sort the hits as we go.
        NearestPicker picker = new NearestPicker( ray, this .modelView, this .projection );
        this .scene .pick( picker );
        return picker .getNearest();
    }

    /* (non-Javadoc)
     * @see com.vzome.desktop.controller.RenderingViewer#pickRay(java.awt.event.MouseEvent, com.vzome.core.math.RealVector, com.vzome.core.math.RealVector)
     */
    @Override
    public Line pickRay( MouseEvent e )
    {
        Component canvas = e .getComponent();
        int width = canvas .getWidth();
        int height = canvas .getHeight();
        int mouseX = e .getX();
        int mouseY = e .getY();
        
        // I wasted a lot of time here looking at the Java3d implementation, which had a bug.
        //  Furthermore, it was based on the Java3d notion of "image plate" coordinates, which
        //  are hard to understand in terms of OpenGL, at least from the documentation I could
        //  find.  I'm very happy to be rid of Java3d.
        
        Ray ray = new Ray();
        FloatUtil .mapWinToRay(
                mouseX, height - mouseY, 0f, 1f,  // Y is reversed from AWT to GL.  Z must be zero or very small.
                this.modelView, 0,
                this.projection, 0,
                new int[] { 0, 0, width, height }, 0,
                ray,
                new float[16], new float[16], new float[4] );
        
        // Ray is expressed in world coordinates.
        
        return new Line( new RealVector( ray.orig[0], ray.orig[1], ray.orig[2] ), new RealVector( ray.dir[0], ray.dir[1], ray.dir[2] ) );
    }
    
    @Override
    public Collection<RenderedManifestation> pickCube()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RenderingChanges getRenderingChanges()
    {
        return this .scene;
    }

    @Override
    public void captureImage( int maxSize, boolean withAlpha, ImageCapture capture )
    {
        // Key parts of this copied from TestGLOffscreenAutoDrawableBug1044AWT in the Github jogl repo,
        //   now modified with changes to fix the capture on Windows, thanks to David Hall.
        
        int imageWidth = this .width;
        int imageHeight = this .height;
        if ( maxSize > 0 ) {
            imageWidth = maxSize;
            imageHeight = imageWidth * 4 / 5;
        }
        
        GLProfile glprofile = GLProfile .getDefault();
        final GLDrawableFactory fac = GLDrawableFactory .getFactory( glprofile );
        final GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glcapabilities .setDepthBits( 32 );
        // Without line below, there is an error on Windows.
        glcapabilities .setDoubleBuffered( false );
        final GLOffscreenAutoDrawable drawable = fac.createOffscreenAutoDrawable( null, glcapabilities, null, imageWidth, imageHeight );
        drawable.display();
        final GLContext context = drawable .getContext();
        context .makeCurrent();

        System.err.println( "Chosen: " + drawable .getChosenGLCapabilities() );

        final GL2 gl2 = context .getGL() .getGL2();
        JoglOpenGlShim shim = new JoglOpenGlShim( gl2 );
        boolean useVBOs = false;  // this context will be discarded after a single rendering
        
        Renderer tempSolids = new SolidRenderer( shim, useVBOs  );
        tempSolids .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        tempSolids .setView( this .modelView, this .projection, this .near, this .fogFront, this .far, this .fovX != 0f );
        Renderer tempOutlines = new OutlineRenderer( shim, useVBOs );
        // OutlineRenderer doesn't currently use lights, but calling setLights() doesn't hurt anything
        tempOutlines .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        tempOutlines .setView( this .modelView, this .projection, this .near, this .fogFront, this .far, this .fovX != 0f );
        this .scene .render( tempSolids, tempOutlines, true );

        final AWTGLReadBufferUtil agb = new AWTGLReadBufferUtil( glprofile, withAlpha );
        final BufferedImage image = agb .readPixelsToBufferedImage( gl2, true );
        
        capture .captureImage( image );

        context .destroy();
        drawable .setRealized( false );
        System.out.println( "Done!" );
    }
}
