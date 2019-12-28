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

package com.vzome.jogl;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.FloatUtil;
import com.vzome.api.Application;
import com.vzome.api.Ball;
import com.vzome.api.Document;
import com.vzome.api.Strut;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;

/**
 * This is a stripped-down version of the vzome-cardboard View3dActivity,
 * taken from commit d695f0a7acf7427acc11ff7637f2103bdc6b6724, just before
 * I started using VBOs.
 * 
 * My intention is to get this working as a standalone JOGL AWT example,
 * since I know the rendering was correct in vzome-cardboard.
 */
public class View3dActivity
{
    private static final float CAMERA_Z = 0.01f;

    private RenderingProgram instancedRenderer, lightingRenderer, lineRenderer;
    private Set<ShapeClass> shapes = new HashSet<ShapeClass>();
    private boolean loading = true;
    private boolean failedLoad = false;
    private float[][] orientations;

    private void addShapeClass( Polyhedron shape, ShapeClass.Config config )
    {
        ShapeClass shapeClass = ShapeClass .create( shape, config.instances, config.color );
        shapes .add( shapeClass );
    }

    private ShapeClass mFloor, struts;

    private float[] mModelCube;
    private float[] mCamera;
    private float[] projection;

    private float[] mModelFloor;

    private float mObjectDistance = 1f;
    private float mFloorDepth = 20f;

    private Application vZome;

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     * @param savedInstanceState
     */
    public void onCreate()
    {
        mModelCube = new float[16];
        mCamera = new float[16];
        mModelFloor = new float[16];
        projection = new float[16];

        vZome = new Application();
    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     * @param config The EGL configuration used when creating the surface.
     */
    public void onSurfaceCreated( GL2 gl )
    {
        this .lineRenderer = new RenderingProgram( gl, false, false );

        this .lightingRenderer = new RenderingProgram( gl, true, false );

//        this .instancedRenderer = new RenderingProgram( gl, true, true );

        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Object first appears directly in front of user
//        Matrix.setIdentityM(mModelCube, 0);
//        Matrix.translateM(mModelCube, 0, 0, 0, -mObjectDistance);
        FloatUtil.makeTranslation( mModelCube, true, 0, 0, -mObjectDistance );

//        Matrix.setIdentityM(mModelFloor, 0);
//        Matrix.translateM(mModelFloor, 0, 0, -mFloorDepth, 0); // Floor appears below user
        FloatUtil.makeTranslation( mModelFloor, true, 0, -mFloorDepth, 0 );

        // Build the camera matrix and apply it to the ModelView.
//        Matrix.setLookAtM( mCamera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f );
        FloatUtil.makeLookAt( mCamera, 0, new float[]{0.0f, 0.0f, CAMERA_Z}, 0, new float[]{0.0f, 0.0f, 0.0f}, 0, new float[]{0.0f, 1.0f, 0.0f}, 0, new float[16] );
        
        FloatUtil.makePerspective( projection, 0, true, 0.6f, 1.0f, 0.1f, 1000f );
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the camera) is passed in as
     * a parameter.
     * @param transform The transformations to apply to render this eye.
     */
    public void onDrawEye( GL2 gl )
    {
        if ( failedLoad )
            gl.glClearColor( 0.5f, 0f, 0f, 1f );
        else if ( loading )
            gl.glClearColor( 0.2f, 0.3f, 0.4f, 0.5f );
        else
            gl.glClearColor( 0.5f, 0.6f, 0.7f, 0.5f );

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        RenderingProgram .checkGLError( gl, "glClear" );

        if ( ! loading )
        {
            this.lightingRenderer.setUniforms( gl, mModelCube, mCamera, projection, orientations );
            for( ShapeClass shapeClass : shapes )
                this.lightingRenderer.renderShape( gl, shapeClass );
        }
        else
        {
            if (struts != null) {
                this.lineRenderer.setUniforms( gl, mModelCube, mCamera, projection, orientations );
                this.lineRenderer.renderShape( gl, struts );
            }
        }

//        this .floorRenderer .setUniforms( gl, mModelFloor, mCamera, projection, orientations );
//        this .floorRenderer .renderShape( gl, mFloor );
    }

    protected String doInBackground(String... urls) {{

        // params comes from the execute() call: params[0] is the url.
        try {
            URL url = new URL( urls[ 0 ] );
            System.out.println( "%%%%%%%%%%%%%%%% opening: " + url);
            InputStream instream = url.openStream();
            Document doc = vZome.loadDocument(instream);
            instream.close();
            System.out.println( "%%%%%%%%%%%%%%%% finished: " + url );

            View3dActivity.this.orientations = doc .getOrientations();

            //                float[] white = new float[] { 1f, 1f, 1f, 1f };
            //                View3dActivity.this.balls = ShapeClass .create( vZome .getBallShape(), doc .getBalls(), white );

            float[] black = new float[] { 0f, 0f, 0f, 1f };
            View3dActivity.this.struts = ShapeClass .create( doc .getStruts().toArray(), black );

            Map<Polyhedron,ShapeClass.Config> shapeClasses = new HashMap<Polyhedron,ShapeClass.Config>();
            RenderedModel rmodel = doc .getRenderedModel();
            for ( RenderedManifestation rman : rmodel ) {

                Polyhedron shape = rman .getShape();
                ShapeClass.Config scc = shapeClasses .get( shape );
                if ( scc == null ) {
                    System.out.println( "%%%%%%%%%%%%%%%% new shape" );
                    scc = new ShapeClass.Config();
                    shapeClasses .put( shape, scc );
                    Color color = rman .getColor();
                    float[] rgb = new float[3];
                    color .getRGBColorComponents( rgb );
                    scc .color = new float[]{ rgb[0], rgb[1], rgb[2], 1f };
                }

                Manifestation man = rman.getManifestation();
                Object instance = null;
                if ( man instanceof Connector) {
                    instance = new Ball( (Connector) man );
                }
                else if ( man instanceof com.vzome.core.model.Strut ) {
                    int zone = rman .getStrutZone();

                    instance = new Strut( (com.vzome.core.model.Strut) man, zone );
                }
                else {
                    System.out.println( "%%%%%%%%%%%%%%%% missing panel!" );
                    continue;
                }
                scc .instances .add( instance );
            }
            for( Map.Entry<Polyhedron, ShapeClass.Config> entry : shapeClasses.entrySet() )
            {
                View3dActivity.this.addShapeClass( entry .getKey(), entry .getValue() );
            }
            View3dActivity.this.loading = false;
        }
        catch (Exception e) {
            View3dActivity.this.failedLoad = true;
            System.out.println( "%%%%%%%%%%%%%%%% FAILED: " + urls[ 0 ] );
            e .printStackTrace();
        }
        return "OK";
    }}

    // Lifted from https://jogamp.org/wiki/index.php?title=Using_JOGL_in_AWT_SWT_and_Swing#JOGL_in_AWT
    //
    public static void main( String[] args )
    {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        
        // new code for this vZome example
        View3dActivity view3dActivity = new View3dActivity();
        view3dActivity .onCreate();

        glcanvas.addGLEventListener( new GLEventListener() {
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height )
            {
//                OneTriangle.setup( glautodrawable.getGL().getGL2(), width, height );
                GL2 gl = glautodrawable.getGL().getGL2();
                view3dActivity .onSurfaceCreated( gl );
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {}
            
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {}
            
            @Override
            public void display( GLAutoDrawable glautodrawable )
            {
//                OneTriangle.render( glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
                GL2 gl = glautodrawable.getGL().getGL2();
                view3dActivity .onDrawEye( gl );
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

        frame.setSize( 640, 480 );
        frame.setVisible( true );

        new Runnable() {
            
            @Override
            public void run() {
                view3dActivity .doInBackground( args );
            }
        } .run();
    }
}
