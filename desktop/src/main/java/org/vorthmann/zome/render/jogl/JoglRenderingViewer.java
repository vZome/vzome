
//(c) Copyright 2014, Scott Vorthmann.

package org.vorthmann.zome.render.jogl;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Ray;
import com.jogamp.opengl.math.geom.AABBox;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
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
        this .solids .setView( this.modelView, projection );
        this .outlines .setView( this.modelView, projection );
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
        this .forceRender  = true;
    }

    // These are GLEventListener methods

    @Override
    public void init( GLAutoDrawable drawable )
    {
        this .glShim = new JoglOpenGlShim( drawable .getGL() .getGL2() );
        this .solids = new SolidRenderer( this .glShim );
        this .outlines = new OutlineRenderer( this .glShim );
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
        int mouseX = e .getX();
        int mouseY = e .getY();
        Component canvas = e .getComponent();
        
        Ray ray = new Ray();
        FloatUtil .mapWinToRay(
                (float) mouseX, (float) (canvas .getHeight() - mouseY - 1), 0.1f, 0.3f,
                this.modelView, 0,
                this.projection, 0,
                new int[] { 0, 0, canvas .getWidth(), canvas .getHeight() }, 0,
                ray,
                new float[16], new float[16], new float[4] );

        // The scene will loop over all RMs.  Rendered balls will support the RM.isHit(intersector)
        //   method.  We try to sort the hits as we go.
        NearestPicker picker = new NearestPicker( ray );
        this .scene .pick( picker );
        return picker .getNearest();
    }
    
    private class NearestPicker implements RenderedManifestation.Intersector
    {
        NearestPicker( Ray ray )
        {
            super();
            this.ray = ray;
        }

        private final float[] dpyTmp1V3 = new float[3];
        private final float[] dpyTmp2V3 = new float[3];
        private final float[] dpyTmp3V3 = new float[3];
        
        private Ray ray;
        private RenderedManifestation nearest = null;
        private float nearestZ = Float .MAX_VALUE;

        @Override
        public void intersectAABBox( float[] min, float[] max, RenderedManifestation rm )
        {
            AABBox sbox = new AABBox( min, max );
            float[] result = new float[3];
            if( sbox.intersectsRay(ray) ) {
                float[] intersection = sbox .getRayIntersection( result, ray, FloatUtil.EPSILON, true, dpyTmp1V3, dpyTmp2V3, dpyTmp3V3 );
                if( null == intersection ) {
                    System.out.println( "Failure to getRayIntersection" );
                } else {
                    float[] intInWorldCoords = new float[] { intersection[0], intersection[1], intersection[0], 1 };
                    float[] intInViewCoords = new float[4];
                    FloatUtil .multMatrixVec( modelView, intInWorldCoords, intInViewCoords );
                    float[] intInDeviceCoords = new float[4];
                    FloatUtil .multMatrixVec( projection, intInViewCoords, intInDeviceCoords );
                    if ( intInDeviceCoords[2] < nearestZ ) {
                        nearest = rm;
                        nearestZ = intInDeviceCoords[2];
                    }
                }
            }
        }
        
        RenderedManifestation getNearest()
        {
            return this .nearest;
        }
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
        // Key parts of this copied from TestGLOffscreenAutoDrawableBug1044AWT in the Github jogl repo
        
        GLProfile glprofile = GLProfile .getDefault();
        final GLDrawableFactory fac = GLDrawableFactory .getFactory( glprofile );
        final GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glcapabilities .setDepthBits( 32 );
        // Without line below, there is an error on Windows.
        glcapabilities .setDoubleBuffered( false );
        final GLDrawable drawable = fac .createOffscreenDrawable( null, glcapabilities, null, this.width, this.height );
        drawable .setRealized(true);
        final GLContext context = drawable .createContext(null);
        context .makeCurrent();

        System.err.println( "Chosen: " + drawable .getChosenGLCapabilities() );

        final GL2 gl2 = context .getGL() .getGL2();

        Renderer solids = new SolidRenderer( new JoglOpenGlShim( gl2 ) );
        solids .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        solids .setView( this.modelView, projection );
        Renderer outlines = new OutlineRenderer( new JoglOpenGlShim( gl2 ) );
        outlines .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        outlines .setView( this.modelView, projection );
        this .scene .render( solids, outlines, true );

        final AWTGLReadBufferUtil agb = new AWTGLReadBufferUtil( glprofile, true );
        final BufferedImage image = agb .readPixelsToBufferedImage( gl2, true );
        
        capture .captureImage( image );

        context .destroy();
        drawable .setRealized( false );
        System.out.println( "Done!" );
    }
}
