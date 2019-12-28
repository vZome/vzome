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

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vzome.api.Ball;
import com.vzome.api.Application;
import com.vzome.api.Document;
import com.vzome.api.Strut;

import com.vzome.core.math.Polyhedron;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;

/**
 * A Cardboard sample application.
 */
public class View3dActivity
{
    private static final String TAG = "View3dActivity";

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.1f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private RenderingProgram instancedRenderer, floorRenderer, lineRenderer, experimentalRenderer;
    private Set<ShapeClass> shapes = new HashSet<ShapeClass>();
    private boolean loading = true;
    private boolean failedLoad = false;
    private boolean experimental = false;
    private float[][] orientations;

    private void addShapeClass( Polyhedron shape, ShapeClass.Config config )
    {
        ShapeClass shapeClass = ShapeClass .create( shape, config.instances, config.color );
        shapes .add( shapeClass );
    }

    private ShapeClass mFloor, struts;

    private float[] mModelCube;
    private float[] mCamera;
    private float[] mHeadView;

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
        mHeadView = new float[16];

        vZome = new Application();
    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     * @param config The EGL configuration used when creating the surface.
     */
    public void onSurfaceCreated()
    {
        this .lineRenderer = new RenderingProgram( getResources(), false, false, false );

        this .floorRenderer = new RenderingProgram( getResources(), true, false, false );

        this .instancedRenderer = new RenderingProgram( getResources(), true, true, false );

        this .experimentalRenderer = new RenderingProgram( getResources(), true, true, true );

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // Object first appears directly in front of user
        Matrix.setIdentityM(mModelCube, 0);
        Matrix.translateM(mModelCube, 0, 0, 0, -mObjectDistance);

        Matrix.setIdentityM(mModelFloor, 0);
        Matrix.translateM(mModelFloor, 0, 0, -mFloorDepth, 0); // Floor appears below user
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     * @param headTransform The head transformation in the new frame.
     */
    public void onNewFrame( HeadTransform headTransform )
    {
        // Build the Model part of the ModelView matrix.
//        Matrix.rotateM(mModelCube, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);

        headTransform.getHeadView(mHeadView, 0);

        // an attempt at orbit mode... does not work at all
        float[] inv = new float[16];
        Matrix.invertM( inv, 0, mHeadView, 0 );
        float[] camStart = new float[]{ 0f, 0f, CAMERA_Z, 0f };
        float[] camEnd = new float[4];
        Matrix .multiplyMV( camEnd, 0, inv, 0, camStart , 0 );
        float[] upStart = new float[]{ 0.0f, 1.0f, 0.0f, 0f };
        float[] upEnd = new float[4];
        Matrix .multiplyMV( upEnd, 0, inv, 0, upStart , 0 );
        Matrix.setLookAtM( mCamera, 0, camEnd[0], camEnd[1], camEnd[2], 0.0f, 0.0f, 0.0f, upEnd[0], upEnd[1], upEnd[2] );

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM( mCamera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f );
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the camera) is passed in as
     * a parameter.
     * @param transform The transformations to apply to render this eye.
     */
    @Override
    public void onDrawEye()
    {
        if ( failedLoad )
            GLES30.glClearColor( 0.5f, 0f, 0f, 1f );
        else if ( loading )
            GLES30.glClearColor( 0.2f, 0.3f, 0.4f, 0.5f );
        else
            GLES30.glClearColor( 0.5f, 0.6f, 0.7f, 0.5f );

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        RenderingProgram .checkGLError("glClear");

        if ( ! loading )
        {
            if ( this .experimental )
            {
                this.experimentalRenderer.setUniforms( mModelCube, mCamera, transform, orientations );
                for( ShapeClass shapeClass : shapes )
                    this.experimentalRenderer.renderShape( shapeClass );
            }
            else {
                this.instancedRenderer.setUniforms( mModelCube, mCamera, transform, orientations );
                for( ShapeClass shapeClass : shapes )
                    this.instancedRenderer.renderShape( shapeClass );
            }
        }
        else
        {
            if (struts != null) {
                this.lineRenderer.setUniforms( mModelCube, mCamera, transform, orientations );
                this.lineRenderer.renderShape( struts );
            }
        }

        this .floorRenderer .setUniforms( mModelFloor, mCamera, transform, orientations );
        this .floorRenderer .renderShape( mFloor );
    }

    protected String doInBackground(String... urls) {{

        // params comes from the execute() call: params[0] is the url.
        try {
            URL url = new URL( urls[ 0 ] );
            Log.i(TAG, "%%%%%%%%%%%%%%%% opening: " + url);
            InputStream instream = url.openStream();
            Document doc = vZome.loadDocument(instream);
            instream.close();
            Log.i( TAG, "%%%%%%%%%%%%%%%% finished: " + url );

            Colors colors = vZome .getColors();
            View3dActivity.this.orientations = doc .getOrientations();

            //                float[] white = new float[] { 1f, 1f, 1f, 1f };
            //                View3dActivity.this.balls = ShapeClass .create( vZome .getBallShape(), doc .getBalls(), white );

            float[] black = new float[] { 0f, 0f, 0f, 1f };
            View3dActivity.this.struts = ShapeClass .create( doc .getStruts().toArray(), black );

            Map<Polyhedron,ShapeClass.Config> shapeClasses = new HashMap<Polyhedron,ShapeClass.Config>();
            RenderedModel rmodel = doc .getRenderedModel();
            Iterator rms = rmodel .getRenderedManifestations();
            while ( rms .hasNext() ) {
                RenderedManifestation rman = (RenderedManifestation) rms .next();

                Polyhedron shape = rman .getShape();
                ShapeClass.Config scc = shapeClasses .get( shape );
                if ( scc == null ) {
                    Log.i( TAG, "%%%%%%%%%%%%%%%% new shape" );
                    scc = new ShapeClass.Config();
                    shapeClasses .put( shape, scc );
                    Color color = colors .getColor( rman .getColorName() );
                    float[] rgb = new float[3];
                    color .getRGBColorComponents( rgb );
                    scc .color = new float[]{ rgb[0], rgb[1], rgb[2], 1f };
                }

                Manifestation man = rman.getManifestation();
                Object instance = null;
                if ( man instanceof Connector) {
                    instance = new Ball( rmodel.getField(), (Connector) man );
                }
                else if ( man instanceof org.vorthmann.zome.model.real.Strut ) {
                    int zone = rman .getStrutZone();

                    instance = new Strut( rmodel.getField(), (org.vorthmann.zome.model.real.Strut) man, zone );
                }
                else {
                    Log.w( TAG, "%%%%%%%%%%%%%%%% missing panel!" );
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
            Log.e( TAG, "%%%%%%%%%%%%%%%% FAILED: " + urls[ 0 ] );
            e .printStackTrace();
        }
        return "OK";
    }}
}
