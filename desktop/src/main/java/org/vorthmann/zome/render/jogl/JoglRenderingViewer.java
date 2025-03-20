
package org.vorthmann.zome.render.jogl;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Matrix4f;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesChooser;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Ray;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Line;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.Scene;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.awt.RenderingViewer;
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
    private final Scene scene;
    private final Component canvas;
    // cache the value of fixGLCanvasRescaling one time here to improve performance in pickRay() 
    // .... BUT, now that we never use the heavyweight GLCanvas, we never want this... see the usage further below.
    private final boolean fixGLCanvasRescaling = false; // "true".equals(System.getProperty("vzome.glcanvas.rescaling"));
    
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
    private boolean cameraChanged = true;
    private float fogFront;

    private static final Logger LOGGER = Logger.getLogger( "org.vorthmann.zome.render.jogl" );

    public JoglRenderingViewer( Scene scene, GLAutoDrawable drawable )
    {
        if ( LOGGER .isLoggable( Level .INFO ) )
            LOGGER .info( "JoglRenderingViewer() starting" );

        this .scene = scene;
        this .canvas = (Component) drawable;

        Lights lights = scene .getLighting();
        int num = lights .size();
        this .lightDirections = new float[num][];
        this .lightColors = new float[num][];
        for ( int i = 0; i < num; i++ ) {
            RealVector direction = lights .getDirectionalLightVector( i );
            Color color = lights.getDirectionalLightColor( i );
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
        
        if ( LOGGER .isLoggable( Level .INFO ) )
            LOGGER .info( "JoglRenderingViewer() finished" );
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
        this .scene .render( this .solids, this .outlines, this .cameraChanged );
        this .cameraChanged = false;
    }
    
    /**
     * COPIED FROM JOGL 2.4.0 FloatUtil
     * 
     * Make given matrix the orthogonal matrix based on given parameters.
     * <pre>
        Ortho matrix (Column Order):
        2/dx  0     0    0
        0     2/dy  0    0
        0     0     2/dz 0
        tx    ty    tz   1
     * </pre>
     * <p>
     * All matrix fields are only set if <code>initM</code> is <code>true</code>.
     * </p>
     * @param m 4x4 matrix in column-major order (also result)
     * @param m_offset offset in given array <i>m</i>, i.e. start of the 4x4 matrix
     * @param initM if true, given matrix will be initialized w/ identity matrix,
     *              otherwise only the orthogonal fields are set.
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param zNear
     * @param zFar
     * @return given matrix for chaining
     */
    private static float[] makeOrtho(final float[] m, final int m_offset, final boolean initM,
                                    final float left, final float right,
                                    final float bottom, final float top,
                                    final float zNear, final float zFar) {
        if( initM ) {
            // m[m_offset+0+4*0] = 1f;
            m[m_offset+1+4*0] = 0f;
            m[m_offset+2+4*0] = 0f;
            m[m_offset+3+4*0] = 0f;

            m[m_offset+0+4*1] = 0f;
            // m[m_offset+1+4*1] = 1f;
            m[m_offset+2+4*1] = 0f;
            m[m_offset+3+4*1] = 0f;

            m[m_offset+0+4*2] = 0f;
            m[m_offset+1+4*2] = 0f;
            // m[m_offset+2+4*2] = 1f;
            m[m_offset+3+4*2] = 0f;

            // m[m_offset+0+4*3] = 0f;
            // m[m_offset+1+4*3] = 0f;
            // m[m_offset+2+4*3] = 0f;
            // m[m_offset+3+4*3] = 1f;
        }
        final float dx=right-left;
        final float dy=top-bottom;
        final float dz=zFar-zNear;
        final float tx=-1.0f*(right+left)/dx;
        final float ty=-1.0f*(top+bottom)/dy;
        final float tz=-1.0f*(zFar+zNear)/dz;

        m[m_offset+0+4*0] =  2.0f/dx;

        m[m_offset+1+4*1] =  2.0f/dy;

        m[m_offset+2+4*2] = -2.0f/dz;

        m[m_offset+0+4*3] = tx;
        m[m_offset+1+4*3] = ty;
        m[m_offset+2+4*3] = tz;
        m[m_offset+3+4*3] = 1f;

        return m;
    }


    private void setProjection()
    {
        if ( this .fovX == 0f ) {
            float halfEdgeY = this .halfEdgeX / this .aspectRatio;
            
            makeOrtho( projection, 0, true, -halfEdgeX, halfEdgeX, -halfEdgeY, halfEdgeY, this .near, this .far );
        } else {
            // The Camera model is set up for fovX, but FloatUtil.makePerspective wants fovY
            float fovY = this .fovX / this .aspectRatio;
            FloatUtil .makePerspective( projection, 0, true, fovY, this .aspectRatio, this .near, this .far );
        }
        float diff = ( this .far - this .near )/5; // offset from near and far by 20%
        this .fogFront = near + 2 * diff;
        this .cameraChanged  = true;
    }

    // These are GLEventListener methods

    @Override
    public void init( GLAutoDrawable drawable )
    {
        if ( LOGGER .isLoggable( Level .INFO ) )
            LOGGER .info( "GLEventListener init() starting" );

        this .glShim = new JoglOpenGlShim( drawable .getGL() .getGL2() );
        boolean useVBOs = true;  // this context will rendered many, many times
        int maxOrientations = this .scene .getMaxOrientations();
        this .solids = new SolidRenderer( this .glShim, useVBOs, maxOrientations );
        this .outlines = new OutlineRenderer( this .glShim, useVBOs, maxOrientations );
        // store the scene geometry

        if ( LOGGER .isLoggable( Level .INFO ) )
            LOGGER .info( "GLEventListener init() finished" );
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
    public void setViewTransformation( Matrix4f trans )
    {
        Matrix4f copy = new Matrix4f();
        copy .invert( trans );
        int i = 0;
        // JOGL requires column-major order
        for ( int column = 0; column < 4; column++ )
            for ( int row = 0; row < 4; row++ )
            {
                this .modelView[ i ] = copy .getElement( row, column );
                ++i;
            }
        this .cameraChanged  = true;
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
    {
        if ( LOGGER .isLoggable( Level .INFO ) )
            LOGGER .info( "GLEventListener reshape() starting" );

        this .setSize( width, height );

        if ( LOGGER .isLoggable( Level .INFO ) )
            LOGGER .info( "GLEventListener reshape() starting" );
    }

    @Override
    public void setSize( int width, int height )
    {
        this.width = width;
        this.height = height;                               // width and height are only further used for image capture
        this .aspectRatio = (float) width / (float) height; // aspectRatio is used for rendering
        this .setProjection();
    }

    @Override
    public void setPerspective( double fovX, double near, double far )
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

    /**
     * COPIED FROM JOGL 2.4.0 FloatUtil
     * 
     * Map two window coordinates to two object coordinates,
     * distinguished by their z component.
     *
     * @param winx
     * @param winy
     * @param winz1
     * @param winz2
     * @param mat4PMvI inverse [projection] x [modelview] matrix, i.e. Inv(P x Mv)
     * @param viewport 4 component viewport vector
     * @param viewport_offset
     * @param obj1_pos 3 component object coordinate, the result for winz1
     * @param obj1_pos_offset
     * @param obj2_pos 3 component object coordinate, the result for winz2
     * @param obj2_pos_offset
     * @param vec4Tmp1 4 component vector for temp storage
     * @param vec4Tmp2 4 component vector for temp storage
     * @return true if successful, otherwise false (failed to invert matrix, or becomes infinity due to zero z)
     */
    public static boolean mapWinToObjCoords(final float winx, final float winy, final float winz1, final float winz2,
                                            final float[/*16*/] mat4PMvI,
                                            final int[] viewport, final int viewport_offset,
                                            final float[] obj1_pos, final int obj1_pos_offset,
                                            final float[] obj2_pos, final int obj2_pos_offset,
                                            final float[/*4*/] vec4Tmp1, final float[/*4*/] vec4Tmp2) {
      vec4Tmp1[0] = winx;
      vec4Tmp1[1] = winy;
      vec4Tmp1[3] = 1.0f;

      // Map x and y from window coordinates
      vec4Tmp1[0] = (vec4Tmp1[0] - viewport[0+viewport_offset]) / viewport[2+viewport_offset];
      vec4Tmp1[1] = (vec4Tmp1[1] - viewport[1+viewport_offset]) / viewport[3+viewport_offset];

      // Map to range -1 to 1
      vec4Tmp1[0] = vec4Tmp1[0] * 2 - 1;
      vec4Tmp1[1] = vec4Tmp1[1] * 2 - 1;

      //
      // winz1
      //
      vec4Tmp1[2] = winz1;
      vec4Tmp1[2] = vec4Tmp1[2] * 2 - 1;

      // object raw coords = Inv(P x Mv) *  winPos  -> mat4Tmp2
      FloatUtil .multMatrixVec(mat4PMvI, vec4Tmp1, vec4Tmp2);

      if (vec4Tmp2[3] == 0.0) {
        return false;
      }

      vec4Tmp2[3] = 1.0f / vec4Tmp2[3];

      obj1_pos[0+obj1_pos_offset] = vec4Tmp2[0] * vec4Tmp2[3];
      obj1_pos[1+obj1_pos_offset] = vec4Tmp2[1] * vec4Tmp2[3];
      obj1_pos[2+obj1_pos_offset] = vec4Tmp2[2] * vec4Tmp2[3];

      //
      // winz2
      //
      vec4Tmp1[2] = winz2;
      vec4Tmp1[2] = vec4Tmp1[2] * 2 - 1;

      // object raw coords = Inv(P x Mv) *  winPos  -> mat4Tmp2
      FloatUtil .multMatrixVec(mat4PMvI, vec4Tmp1, vec4Tmp2);

      if (vec4Tmp2[3] == 0.0) {
        return false;
      }

      vec4Tmp2[3] = 1.0f / vec4Tmp2[3];

      obj2_pos[0+obj2_pos_offset] = vec4Tmp2[0] * vec4Tmp2[3];
      obj2_pos[1+obj2_pos_offset] = vec4Tmp2[1] * vec4Tmp2[3];
      obj2_pos[2+obj2_pos_offset] = vec4Tmp2[2] * vec4Tmp2[3];

      return true;
    }


    /**
     * COPIED FROM JOGL 2.4.0 FloatUtil
     * 
     * Map two window coordinates w/ shared X/Y and distinctive Z
     * to a {@link Ray}. The resulting {@link Ray} maybe used for <i>picking</i>
     * using a {@link AABBox#getRayIntersection(Ray, float[]) bounding box}.
     * <p>
     * Notes for picking <i>winz0</i> and <i>winz1</i>:
     * <ul>
     *   <li>see {@link #getZBufferEpsilon(int, float, float)}</li>
     *   <li>see {@link #getZBufferValue(int, float, float, float)}</li>
     *   <li>see {@link #getOrthoWinZ(float, float, float)}</li>
     * </ul>
     * </p>
     * @param winx
     * @param winy
     * @param winz0
     * @param winz1
     * @param modelMatrix 4x4 modelview matrix
     * @param modelMatrix_offset
     * @param projMatrix 4x4 projection matrix
     * @param projMatrix_offset
     * @param viewport 4 component viewport vector
     * @param viewport_offset
     * @param ray storage for the resulting {@link Ray}
     * @param mat4Tmp1 16 component matrix for temp storage
     * @param mat4Tmp2 16 component matrix for temp storage
     * @param vec4Tmp2 4 component vector for temp storage
     * @return true if successful, otherwise false (failed to invert matrix, or becomes z is infinity)
     */
    public static boolean mapWinToRay(final float winx, final float winy, final float winz0, final float winz1,
                                      final float[] modelMatrix, final int modelMatrix_offset,
                                      final float[] projMatrix, final int projMatrix_offset,
                                      final int[] viewport, final int viewport_offset,
                                      final Ray ray,
                                      final float[/*16*/] mat4Tmp1, final float[/*16*/] mat4Tmp2, final float[/*4*/] vec4Tmp2) {
        // mat4Tmp1 = P x Mv
        FloatUtil .multMatrix(projMatrix, projMatrix_offset, modelMatrix, modelMatrix_offset, mat4Tmp1, 0);

        // mat4Tmp1 = Inv(P x Mv)
        if ( null == FloatUtil .invertMatrix(mat4Tmp1, mat4Tmp1) ) {
            return false;
        }
        float[] origin = new float[3];
        float[] direction = new float[3];
        if( mapWinToObjCoords(winx, winy, winz0, winz1, mat4Tmp1,
                              viewport, viewport_offset,
                              origin, 0, direction, 0,
                              mat4Tmp2, vec4Tmp2) ) {
            ray.orig .set( origin );
            ray.dir .set( direction );
            ray.dir .sub( ray.orig ) .normalize();
            return true;
        } else {
            return false;
        }
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
        
        // This work-around for scaling issues on Windows and Linux assumes that the canvas 
        // is a GLCanvas with getWidth() and getHeight() overridden 
        // to incorporate the scale factor as I've done in JoglFactory.createRenderingViewer() .
        //  HOWEVER, now we are never creating GLCanvas, so fixGLCanvasRescaling is always false.
        if(fixGLCanvasRescaling) {
        	AffineTransform t = canvas.getGraphicsConfiguration().getDefaultTransform(); 
            mouseX *= Double.valueOf(t.getScaleX()).intValue();
            mouseY *= Double.valueOf(t.getScaleY()).intValue();
        }
        // I wasted a lot of time here looking at the Java3d implementation, which had a bug.
        //  Furthermore, it was based on the Java3d notion of "image plate" coordinates, which
        //  are hard to understand in terms of OpenGL, at least from the documentation I could
        //  find.  I'm very happy to be rid of Java3d.
        
        Ray ray = new Ray();
        mapWinToRay(
                mouseX, height - mouseY, 0f, 1f,  // Y is reversed from AWT to GL.  Z must be zero or very small.
                this.modelView, 0,
                this.projection, 0,
                new int[] { 0, 0, width, height }, 0,
                ray,
                new float[16], new float[16], new float[4] );
        
        // Ray is expressed in world coordinates.
        
        return new Line( new RealVector( ray.orig.x(), ray.orig.y(), ray.orig.z() ), new RealVector( ray.dir.x(), ray.dir.y(), ray.dir.z() ) );
    }
    
    @Override
    public BufferedImage captureImage( int maxSize, boolean withAlpha )
    {
        // Key parts of this copied from TestGLOffscreenAutoDrawableBug1044AWT in the Github jogl repo,
        //   now modified with changes to fix the capture on Windows, thanks to David Hall.
        
        int imageWidth = this .width;
        int imageHeight = this .height;
        float aspectRatio = (float) imageHeight / (float) imageWidth;
        if ( maxSize > 0 ) {
            imageWidth = maxSize;
            if ( imageHeight > 0 ) {
                imageHeight = (int) (imageWidth * aspectRatio);  // This was hardcoded, now is correct.
            } else {
                imageHeight = imageWidth * 4 / 5;
            }
        }
        
        GLProfile glprofile = GLProfile .getDefault();
        final GLDrawableFactory fac = GLDrawableFactory .getFactory( glprofile );
        final GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glcapabilities .setDepthBits( 32 );
        // Without line below, there is an error on Windows.
        glcapabilities .setDoubleBuffered( false );
        
        GLCapabilitiesChooser chooser = null;// new JoglFactory.MultisampleChooser();  // THIS YIELDS BLACK OR EMPTY IMAGES!
//        glcapabilities .setSampleBuffers(true);
//        glcapabilities .setNumSamples(4);

        final GLOffscreenAutoDrawable drawable = fac.createOffscreenAutoDrawable( null, glcapabilities, chooser, imageWidth, imageHeight );
        drawable.display();
        final GLContext context = drawable .getContext();
        context .makeCurrent();

        System.err.println( "Chosen: " + drawable .getChosenGLCapabilities() );

        final GL2 gl2 = context .getGL() .getGL2();
        JoglOpenGlShim shim = new JoglOpenGlShim( gl2 );
        boolean useVBOs = false;  // this context will be discarded after a single rendering
        int maxOrientations = this .scene .getMaxOrientations();
        
        Renderer tempSolids = new SolidRenderer( shim, useVBOs, maxOrientations );
        tempSolids .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        tempSolids .setView( this .modelView, this .projection, this .near, this .fogFront, this .far, this .fovX != 0f );
        Renderer tempOutlines = new OutlineRenderer( shim, useVBOs, maxOrientations );
        // OutlineRenderer doesn't currently use lights, but calling setLights() doesn't hurt anything
        tempOutlines .setLights( this .lightDirections, this .lightColors, this .ambientLight );
        tempOutlines .setView( this .modelView, this .projection, this .near, this .fogFront, this .far, this .fovX != 0f );
        this .scene .render( tempSolids, tempOutlines, true );

        final AWTGLReadBufferUtil agb = new AWTGLReadBufferUtil( glprofile, withAlpha );
        final BufferedImage image = agb .readPixelsToBufferedImage( gl2, true );

        context .destroy();
        drawable .setRealized( false );
        
        return image;
    }

    @Override
    public Component getCanvas()
    {
        return this .canvas;
    }
}
