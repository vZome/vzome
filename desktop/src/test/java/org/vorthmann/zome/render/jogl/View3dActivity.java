/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vorthmann.zome.render.jogl;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.net.URL;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Ray;
import com.jogamp.opengl.math.geom.AABBox;
import com.jogamp.opengl.util.FPSAnimator;
import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.SymmetryRendering;
import com.vzome.opengl.OpenGlShim;
import com.vzome.opengl.RenderingProgram;

/**
 * This is a stripped-down version of the vzome-cardboard View3dActivity,
 * taken from commit d695f0a7acf7427acc11ff7637f2103bdc6b6724, just before
 * I started using VBOs.
 * 
 * My intention is to get this working as a standalone JOGL AWT example,
 * since I know the rendering was correct in vzome-cardboard.
 */
public class View3dActivity implements GLEventListener
{
    private RenderingProgram renderer;
    private SymmetryRendering scene = null;
    private boolean failedLoad = false;
    private JoglOpenGlShim glShim;

    private float[] mCamera;
    private float[] projection;
    private float[][] lightDirections;
    private float[][] lightColors;

    private Application vZome;
    
    private static final float SCALE = 1f;

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     * @param savedInstanceState
     */
    public void onCreate()
    {
        mCamera = new float[16];
        projection = new float[16];
        lightDirections = new float[][] { new float[] {0f, 0f, -1f}, new float[] {-1f, -1f, -1f} };
        lightColors = new float[][] { new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.5f, 0.5f, 0.5f} };

        vZome = new Application();
    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     * @param config The EGL configuration used when creating the surface.
     */
    public void onSurfaceCreated( OpenGlShim gl, int width, int height )
    {
        this .renderer = new RenderingProgram( gl );

        gl.glEnableDepth();

        // Build the camera matrix and apply it to the ModelView.
        FloatUtil.makeLookAt( mCamera, 0, new float[]{0.0f, 0.0f, 60f}, 0, new float[]{0.0f, 0.0f, 0.0f}, 0, new float[]{0.0f, 1.0f, 0.0f}, 0, new float[16] );
        
        float aspectRatio = (float)width/(float)height;
        FloatUtil.makePerspective( projection, 0, true, 0.44f / aspectRatio, aspectRatio, 0.1f, 200f );
        
        ((JoglOpenGlShim) gl) .glViewport( 0, 0, width, height );
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the camera) is passed in as
     * a parameter.
     * @param glShim  
     * @param transform The transformations to apply to render this eye.
     */
    public void onDrawEye( OpenGlShim gl )
    {
        if ( failedLoad )
        {
            gl .glClear( 0.5f, 0f, 0f, 1f );
        }
        else if ( this .scene != null )
        {
            this .renderer .setView( mCamera, projection );
            this .renderer .setLights( this .lightDirections, this .lightColors );
            this .renderer .clear( new float[] { 0.5f, 0.6f, 0.7f, 1f } );
            this .renderer .renderSymmetry( scene );
        }
    }
    
    private final float[] dpyTmp1V3 = new float[3];
    private final float[] dpyTmp2V3 = new float[3];
    private final float[] dpyTmp3V3 = new float[3];

    protected void mouseClicked( MouseEvent e )
    {
        int mouseX = e .getX();
        int mouseY = e .getY();
        Component canvas = e .getComponent();
        
        // This code was done following https://www.youtube.com/watch?v=DLK
        //  It is probably perfectly functional, but it doesn't help me use AABBox.getRayIntersection.
        //
//        float x = (2f*mouseX) / canvas .getWidth() - 1f;
//        float y = -( (2f*mouseY) / canvas .getHeight() - 1f );
////        System .out .println( "normalized dev:   x = " + x + "   y = " + y );
//        
//        float[] clipCoords = new float[] { x, y, -1, 1 }; // ray straight out means no need to invert perspective
//        
//        float[] inverseProjection = new float[16];
//        FloatUtil .invertMatrix( projection, inverseProjection );
//        float[] eyeCoords = new float[4];
//        FloatUtil .multMatrixVec( inverseProjection, clipCoords, eyeCoords );
//        eyeCoords[2] = -1f;
//        eyeCoords[3] = 0;
////        System.out.println( "eyeCoords = " + eyeCoords[0] + " " + eyeCoords[1] + " " + eyeCoords[2] + " " + eyeCoords[3] );
//        
//        float[] inverseCamera = new float[16];
//        FloatUtil .invertMatrix( mCamera, inverseCamera );
//        float[] rayWorld = new float[4];
//        FloatUtil .multMatrixVec( inverseCamera, eyeCoords, rayWorld );
//        VectorUtil .normalizeVec3( rayWorld );
//        System.out.println( "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" );
//        System.out.println( "rayWorld = " + rayWorld[0] + " " + rayWorld[1] + " " + rayWorld[2]  );

        Ray ray = new Ray();
        FloatUtil .mapWinToRay(
                (float) mouseX, (float) (canvas .getHeight() - mouseY - 1), 0.1f, 0.3f,
                mCamera, 0,
                projection, 0,
                new int[] { 0, 0, canvas .getWidth(), canvas .getHeight() }, 0,
                ray,
                new float[16], new float[16], new float[4] );
        System.out.println( "ray.orig = " + ray.orig[0] + " " + ray.orig[1] + " " + ray.orig[2]  );
        System.out.println( "ray.dir = " + ray.dir[0] + " " + ray.dir[1] + " " + ray.dir[2]  );

        RenderedManifestation result = this .scene .pick( new RenderedManifestation.Intersector()
        {
            @Override
            public boolean intersectAABBox( float[] min, float[] max )
            {
//                VectorUtil .scaleVec3( min, min, SCALE );
//                VectorUtil .scaleVec3( max, max, SCALE );
                AABBox sbox = new AABBox( min, max );
                float[] result = new float[3];
                if( sbox.intersectsRay(ray) ) {
                    if( null == sbox .getRayIntersection( result, ray, FloatUtil.EPSILON, true, dpyTmp1V3, dpyTmp2V3, dpyTmp3V3 ) ) {
                        System.out.println( "Failure to getRayIntersection" );
                    } else
                        return true;
                }
                return false;
            }
        });
        if ( result != null ) {
            System.out.println( "PICKED: " + result .toString() );
            float glow = result .getGlow();
            if ( glow == 0.0f )
                result .setGlow( 0.8f );
            else
                result .setGlow( 0.0f );
            this .scene .refresh();
        }
    }

    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            URL url = new URL( urls[ 0 ] );
            System.out.println( "%%%%%%%%%%%%%%%% opening: " + url);
            InputStream instream = url.openStream();
            Document doc = vZome.loadDocument(instream);
            instream.close();
            System.out.println( "%%%%%%%%%%%%%%%% finished: " + url );

            this .scene = doc .getSymmetryRendering( SCALE );
        }
        catch (Exception e) {
            this.failedLoad = true;
            System.out.println( "%%%%%%%%%%%%%%%% FAILED: " + urls[ 0 ] );
            e .printStackTrace();
        }
        return "OK";
    }

    // Lifted from https://jogamp.org/wiki/index.php?title=Using_JOGL_in_AWT_SWT_and_Swing#JOGL_in_AWT
    //
    public static void main( String[] args )
    {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glcapabilities .setDepthBits( 32 );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        
        // new code for this vZome example
        View3dActivity view3dActivity = new View3dActivity();
        view3dActivity .onCreate();

        glcanvas .addGLEventListener( view3dActivity );
        FPSAnimator animator = new FPSAnimator( glcanvas, 60 );
        animator .start();
        
        glcanvas .addMouseListener( new MouseAdapter() {
            
            @Override
            public void mouseClicked( MouseEvent e )
            {
                view3dActivity .mouseClicked( e );
            }
        });

        final Frame frame = new Frame( "One Triangle AWT" );
        frame.add( glcanvas );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                frame.remove( glcanvas );
                frame.dispose();
                System.exit( 0 );
            }
        });

        frame.setSize( 20+987, 610 );
        frame.setVisible( true );

        new Runnable() {
            
            @Override
            public void run() {
                view3dActivity .doInBackground( args );
                glcanvas .display();
            }
        } .run();
    }

    @Override
    public void init( GLAutoDrawable drawable )
    {}

    @Override
    public void dispose( GLAutoDrawable drawable )
    {}

    @Override
    public void display( GLAutoDrawable drawable )
    {
        if ( this .glShim .isSameContext( drawable .getGL() .getGL2() ) )
            this .onDrawEye( glShim );
        else
            System.out.println( "Different GL2!" );
    }

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
    {
        if ( this .glShim == null ) {
            this .glShim = new JoglOpenGlShim( drawable .getGL() .getGL2() );
        }
        this .onSurfaceCreated( glShim, width, height );
    }
}
