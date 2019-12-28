package com.vzome.jogl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jogamp.opengl.GL2;

/**
* Created by vorth on 7/28/14.
*/
class RenderingProgram
{
    private static final String TAG = "RenderingProgram";

    private int mGlProgram;
    private boolean isInstanced;
    private boolean doLighting;
    private boolean isNiceLighting;
    private int instanceData;
    private int mPositionParam;
    private int normalParam;
    private int mColorParam;
    private int mModelViewProjectionParam;
    private int mLightPosParam;
    private int mModelViewParam;
    private int mModelParam;
    private int worldInverseTranspose;
    private int viewInverse;
    private int[] mOrientationsParam = new int[60];

    // We keep the light always position just above the user.
    private static final float[] mLightPosInWorldSpace = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

    private static final int COORDS_PER_VERTEX = 3;

    public RenderingProgram( boolean lighting, boolean instanced, boolean fancy )
    {
        this .doLighting = lighting;
        this .isInstanced = lighting && instanced;
        this .isNiceLighting = lighting && instanced && fancy;

        int vertexShaderRsrc = R.raw.simple_vertex;
        if ( this .isNiceLighting )
            vertexShaderRsrc = R.raw.nice_lighting_vertex_instanced;
        else if ( this .isInstanced )
            vertexShaderRsrc = R.raw.light_vertex_instanced;
        else if ( this .doLighting )
            vertexShaderRsrc = R.raw.light_vertex;
        int vertexShader = loadGLShader( resources, GLES30.GL_VERTEX_SHADER, vertexShaderRsrc );

        int fragmentShaderRsrc = R.raw.simple_fragment;
        if ( this .isNiceLighting )
            fragmentShaderRsrc = R.raw.nice_lighting_fragment;
        else if ( this .isInstanced )
            fragmentShaderRsrc = R.raw.simple_fragment;
        else if ( this .doLighting )
            fragmentShaderRsrc = R.raw.grid_fragment;
        int fragmentShader = loadGLShader( resources, GLES30.GL_FRAGMENT_SHADER, fragmentShaderRsrc );

        mGlProgram = GL2.glCreateProgram();
        checkGLError("glCreateProgram");
        GLES30.glAttachShader(mGlProgram, vertexShader);
        checkGLError("glAttachShader vertexShader");
        GLES30.glAttachShader(mGlProgram, fragmentShader);
        checkGLError("glAttachShader fragmentShader");
        GLES30.glLinkProgram(mGlProgram);
        checkGLError("glLinkProgram");

        if ( this .isNiceLighting )
            mModelViewProjectionParam = GLES30.glGetUniformLocation( mGlProgram, "worldViewProjection" );
        else
            mModelViewProjectionParam = GLES30.glGetUniformLocation( mGlProgram, "u_MVP" );
        checkGLError("worldViewProjection");

        mPositionParam = GLES30.glGetAttribLocation(mGlProgram, "a_Position");
        mColorParam = GLES30.glGetUniformLocation(mGlProgram, "u_Color");
        checkGLError("u_Color");

        if ( this .doLighting )
        {
            if ( this .isNiceLighting ) {
                mLightPosParam = GLES30.glGetUniformLocation(mGlProgram, "lightWorldPos");
                worldInverseTranspose = GLES30.glGetUniformLocation( mGlProgram, "worldInverseTranspose" );
                viewInverse = GLES30.glGetUniformLocation( mGlProgram, "viewInverse" );
            }
            else {
                mLightPosParam = GLES30.glGetUniformLocation(mGlProgram, "u_LightPos");
                mModelViewParam = GLES30.glGetUniformLocation( mGlProgram, "u_MVMatrix" );
                mModelParam = GLES30.glGetUniformLocation( mGlProgram, "u_Model" );
            }

            normalParam = GLES30.glGetAttribLocation( mGlProgram, "a_Normal" );
            checkGLError("a_Normal");

            if ( this .isInstanced ) {

                for ( int i = 0; i < 60; i++ )
                    mOrientationsParam[ i ] = GLES30.glGetUniformLocation( mGlProgram, "u_Orientations[" + i + "]" );

                instanceData = GLES30.glGetAttribLocation( mGlProgram, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation)
            }
        }
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return
     */
    private static int loadGLShader( Resources resources, int type, int resId )
    {
        String code = readRawTextFile( resources, resId );
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, code);
        checkGLError( "glShaderSource" );
        GLES30.glCompileShader(shader);
        checkGLError( "glCompileShader" );

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        checkGLError( "glGetShaderiv" );

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e( TAG, "Error compiling shader: " + GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     * @param func
     */
    public static void checkGLError(String func) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    /**
     * Converts a raw text file into a string.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return
     */
    private static String readRawTextFile( Resources resources, int resId )
    {
        InputStream inputStream = resources .openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setUniforms( float[] model, float[] camera, EyeTransform transform, float[][] icosahedralOrientations )
    {
        GLES30.glUseProgram( mGlProgram );
        checkGLError("glUseProgram");  // a compile / link problem seems to fail only now!

        float[] modelViewProjection = new float[16];
        float[] worldInverse = new float[16];
        float[] worldInverseTranspose = new float[16];
        float[] modelView = new float[16];
        float[] view = new float[16];
        float[] viewInverse = new float[16];
        float[] lightPosInEyeSpace = new float[4];

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM( view, 0, transform.getEyeView(), 0, camera, 0 );
        Matrix.invertM( viewInverse, 0, view, 0 );
        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        Matrix.multiplyMM( modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM( modelViewProjection, 0, transform.getPerspective(), 0, modelView, 0);
        Matrix.invertM( worldInverse, 0, model, 0 );
        Matrix.transposeM( worldInverseTranspose, 0, worldInverse, 0 );

        // Set the position of the light
        Matrix.multiplyMV( lightPosInEyeSpace, 0, view, 0, mLightPosInWorldSpace, 0 );

        // Set the ModelViewProjection matrix in the shader.
        GLES30.glUniformMatrix4fv( mModelViewProjectionParam, 1, false, modelViewProjection, 0 );

        if ( doLighting ) {
            GLES30.glUniform3f( mLightPosParam, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2] );
            if ( this .isNiceLighting )
            {
                GLES30.glUniformMatrix4fv( this.viewInverse, 1, false, model, 0);
                GLES30.glUniformMatrix4fv( this.worldInverseTranspose, 1, false, worldInverseTranspose, 0);
            }
            else {
                GLES30.glUniformMatrix4fv( mModelParam, 1, false, model, 0);
                GLES30.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
            }
            if ( this .isInstanced ) {
                for ( int i = 0; i < 60; i++ )
                    GLES30.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, icosahedralOrientations[ i ], 0 );
                checkGLError("mOrientationsParam");
            }
        }
    }

    public void renderShape( ShapeClass shape )
    {

        float[] color = shape .getColor();
        GLES30.glUniform4f( mColorParam, color[0], color[1], color[2], color[3] );

        // Set the vertices of the shape
        GLES30.glEnableVertexAttribArray(mPositionParam);
        GLES30.glVertexAttribDivisor(mPositionParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        GLES30.glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX, GLES30.GL_FLOAT,
                false, 0, shape .getVertices() );
        checkGLError("mPositionParam");

        if ( doLighting ) {
            GLES30.glEnableVertexAttribArray(normalParam);
            GLES30.glVertexAttribDivisor(normalParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            GLES30.glVertexAttribPointer(normalParam, COORDS_PER_VERTEX, GLES30.GL_FLOAT,
                    false, 0, shape .getNormals() );
            checkGLError("normalParam");

            if ( this .isInstanced ) {
                // Set the positions of the shapes
                GLES30.glEnableVertexAttribArray( instanceData );
                GLES30.glVertexAttribDivisor( instanceData, 1);  // SV: this one is instanced
                GLES30.glVertexAttribPointer( instanceData, 4, GLES30.GL_FLOAT, false, 0, shape .getPositions() );

                GLES30.glDrawArraysInstanced( GLES30.GL_TRIANGLES, 0, shape .getVertexCount(), shape .getInstanceCount() );
            }
            else {
                GLES30.glDrawArrays( GLES30.GL_TRIANGLES, 0, shape .getVertexCount() );
            }
        }
        else {
            GLES30.glDrawArrays( GLES30.GL_LINES, 0, shape .getVertexCount() );
        }
        checkGLError("Drawing a shape");
    }
}
