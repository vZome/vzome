package com.vzome.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;

/**
* Created by vorth on 7/28/14.
*/
class RenderingProgram
{
    private int mGlProgram;
    private boolean isInstanced;
    private boolean doLighting;
    private int instanceData;
    private int mPositionParam;
    private int normalParam;
    private int mColorParam;
    private int mModelViewProjectionParam;
    private int mLightPosParam;
    private int mModelViewParam;
    private int mModelParam;
    private int[] mOrientationsParam = new int[60];

    // We keep the light always position just above the user.
    private static final float[] mLightPosInWorldSpace = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

    private static final int COORDS_PER_VERTEX = 3;

    public RenderingProgram( GL2 gl, boolean lighting, boolean instanced )
    {
        String version = gl .getContext() .getGLSLVersionString();
        this .doLighting = lighting;
        this .isInstanced = lighting && instanced;

//      int vertexShaderRsrc = R.raw.simple_vertex;
        String vertexShaderSrc = version + "\n" +
                "attribute vec4 a_Position;\n" + 
                "uniform mat4 u_MVP;\n" + 
                "uniform vec4 u_Color;\n" + 
                "varying vec4 v_Color;\n" + 
                "\n" + 
                "void main() {\n" + 
                "  v_Color = u_Color;\n" + 
                "  gl_Position = u_MVP * a_Position;\n" + 
                "}";
        if ( this .isInstanced )
//            vertexShaderRsrc = R.raw.light_vertex_instanced;
            vertexShaderSrc = version + "\n" + 
                    "uniform mat4 u_MVP;\n" + 
                    "uniform mat4 u_MVMatrix;\n" + 
                    "uniform mat4 u_Model;\n" + 
                    "uniform vec3 u_LightPos;\n" + 
                    "uniform vec4 u_Color;\n" + 
                    "uniform mat4 u_Orientations[60];\n" + 
                    "\n" + 
                    "attribute vec4 a_Position;\n" + 
                    "attribute vec3 a_Normal;\n" + 
                    "attribute vec4 a_InstanceData;\n" + 
                    "\n" + 
                    "varying vec4 v_Color;\n" + 
                    "\n" + 
                    "void main()\n" + 
                    "{\n" + 
                    "   // unpack a_InstanceData\n" + 
                    "   float orientationAsFloat = a_InstanceData.w;\n" + 
                    "   vec4 location = vec4( a_InstanceData.xyz, 1.0 );\n" + 
                    "\n" + 
                    "\n" + 
                    "   int orientation = int( max( 0, min( 59, orientationAsFloat ) ) );\n" + 
                    "   vec4 oriented = ( u_Orientations[ orientation ] * a_Position );\n" + 
                    "   vec4 normal = ( u_Orientations[ orientation ] * vec4( a_Normal, 0.0 ) );\n" + 
                    "   vec4 pos = oriented + location;\n" + 
                    "   gl_Position = u_MVP * pos;\n" + 
                    "\n" + 
                    "   // original lighting, using a point source\n" + 
                    "   vec3 modelViewVertex = vec3(u_MVMatrix * pos);\n" + 
                    "   vec3 modelViewNormal = vec3( u_MVMatrix * vec4( a_Normal, 0.0 ) );\n" + 
                    "   float distance = length( u_LightPos - modelViewVertex );\n" + 
                    "   vec3 lightVector = normalize( u_LightPos - modelViewVertex );\n" + 
                    "   float diffuse = max(dot(modelViewNormal, lightVector), 0.5 );\n" + 
                    "   diffuse = diffuse * (1.0 / (1.0 + (0.0001 * distance * distance)));\n" + 
                    "   v_Color = u_Color * diffuse;\n" + 
                    "}";
        else if ( this .doLighting )
//            vertexShaderRsrc = R.raw.light_vertex;
            vertexShaderSrc = version + "\n" +
                    "uniform mat4 u_MVP;\n" + 
                    "uniform mat4 u_MVMatrix;\n" + 
                    "uniform mat4 u_Model;\n" + 
                    "uniform vec3 u_LightPos;\n" + 
                    "uniform vec4 u_Color;\n" + 
                    "\n" + 
                    "attribute vec4 a_Position;\n" + 
                    "attribute vec3 a_Normal;\n" + 
                    "\n" + 
                    "varying vec4 v_Color;\n" + 
                    "varying vec3 v_Grid;\n" + 
                    "\n" + 
                    "void main()\n" + 
                    "{\n" + 
                    "   vec3 modelVertex = vec3(u_Model * a_Position);\n" + 
                    "   v_Grid = modelVertex;\n" + 
                    "\n" + 
                    "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);\n" + 
                    "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" + 
                    "   float distance = length(u_LightPos - modelViewVertex);\n" + 
                    "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);\n" + 
                    "   float diffuse = max(dot(modelViewNormal, lightVector), 0.5   );\n" + 
                    "   diffuse = diffuse * (1.0 / (1.0 + (0.00001 * distance * distance)));\n" + 
                    "   v_Color = u_Color * diffuse;\n" + 
                    "   gl_Position = u_MVP * a_Position;\n" + 
                    "}";
        int vertexShader = loadGLShader( gl, GL2.GL_VERTEX_SHADER, vertexShaderSrc );

//        int fragmentShaderRsrc = R.raw.simple_fragment;
        String fragmentShaderSrc = version + "\n" +
                "varying vec4 v_Color;\n" + 
                "\n" + 
                "void main() {\n" + 
                "  gl_FragColor = v_Color;\n" + 
                "}";
        if ( this .isInstanced )
//            fragmentShaderRsrc = R.raw.simple_fragment;
            ;
        else if ( this .doLighting )
//            fragmentShaderRsrc = R.raw.grid_fragment;
            fragmentShaderSrc = version + "\n" +
                    "varying vec4 v_Color;\n" + 
                    "varying vec3 v_Grid;\n" + 
                    "\n" + 
                    "void main() {\n" + 
                    "    float depth = gl_FragCoord.z / gl_FragCoord.w; // calculate world-space distance\n" + 
                    "\n" + 
                    "    if ((mod(abs(v_Grid[0]), 10.0) < 0.1) || (mod(abs(v_Grid[2]), 10.0) < 0.1)) {\n" + 
                    "        gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(1.0, 1.0, 1.0, 1.0)\n" + 
                    "                + min(1.0, depth / 90.0) * v_Color;\n" + 
                    "    } else {\n" + 
                    "        gl_FragColor = v_Color;\n" + 
                    "    }\n" + 
                    "}";
        int fragmentShader = loadGLShader( gl, GL2.GL_FRAGMENT_SHADER, fragmentShaderSrc );

        mGlProgram = gl.glCreateProgram();
        checkGLError( gl, "glCreateProgram");
        gl.glAttachShader(mGlProgram, vertexShader);
        checkGLError( gl, "glAttachShader vertexShader");
        gl.glAttachShader(mGlProgram, fragmentShader);
        checkGLError( gl, "glAttachShader fragmentShader");
        gl.glLinkProgram(mGlProgram);
        checkGLError( gl, "glLinkProgram");

        mModelViewProjectionParam = gl.glGetUniformLocation( mGlProgram, "u_MVP" );
        checkGLError( gl, "worldViewProjection");

        mPositionParam = gl.glGetAttribLocation(mGlProgram, "a_Position");
        mColorParam = gl.glGetUniformLocation(mGlProgram, "u_Color");
        checkGLError( gl, "u_Color");

        if ( this .doLighting )
        {
            {
                mLightPosParam = gl.glGetUniformLocation(mGlProgram, "u_LightPos");
                mModelViewParam = gl.glGetUniformLocation( mGlProgram, "u_MVMatrix" );
                mModelParam = gl.glGetUniformLocation( mGlProgram, "u_Model" );
            }

            normalParam = gl.glGetAttribLocation( mGlProgram, "a_Normal" );
            checkGLError( gl, "a_Normal");

            if ( this .isInstanced ) {

                for ( int i = 0; i < 60; i++ )
                    mOrientationsParam[ i ] = gl.glGetUniformLocation( mGlProgram, "u_Orientations[" + i + "]" );

                instanceData = gl.glGetAttribLocation( mGlProgram, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation)
            }
        }
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return
     */
    private static int loadGLShader( GL2 gl, int type, String code )
    {
        int shader = gl.glCreateShader(type);

//        gl.glShaderSource(shader, code);
        String[] vlines = new String[] { code };
        int[] vlengths = new int[] { vlines[0].length() };
        gl .glShaderSource( shader, vlines.length, vlines, vlengths, 0 );
        
        checkGLError( gl,  "glShaderSource" );
        gl.glCompileShader(shader);
        checkGLError( gl,  "glCompileShader" );

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, compileStatus, 0);
        checkGLError( gl,  "glGetShaderiv" );

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
//            Log.e( TAG, "Error compiling shader: " + gl.glGetShaderInfoLog(shader));
            int[] logLength = new int[1];
            gl .glGetShaderiv( shader, GL2.GL_INFO_LOG_LENGTH, logLength, 0 );
            byte[] log = new byte[logLength[0]];
            gl .glGetShaderInfoLog( shader, logLength[0], (int[])null, 0, log, 0 );
            System.out.println( "Error compiling shader: " + new String( log ) );
            gl.glDeleteShader(shader);
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
    public static void checkGLError( GL2 gl, String func )
    {
        int error;
        while ((error = gl.glGetError()) != GL2.GL_NO_ERROR) {
            System.out.println( func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    public void setUniforms( GL2 gl, float[] model, float[] camera, float[] projection, float[][] icosahedralOrientations )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( gl, "glUseProgram");  // a compile / link problem seems to fail only now!

        float[] modelViewProjection = new float[16];
        float[] worldInverse = new float[16];
        float[] modelView = new float[16];
        float[] lightPosInEyeSpace = new float[4];

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        //  ASSUME ALL MATRICES ARE IN COLUMN-MAJOR ORDER!
//        Matrix.multiplyMM( modelView, 0, camera, 0, model, 0);
        FloatUtil.multMatrix( camera, model, modelView );
//        Matrix.multiplyMM( modelViewProjection, 0, projection, 0, modelView, 0);
        FloatUtil.multMatrix( projection, modelView, modelViewProjection );
//        Matrix.invertM( worldInverse, 0, model, 0 );
        FloatUtil.invertMatrix( model, worldInverse );

        // Set the position of the light
        FloatUtil.multMatrixVec( camera, mLightPosInWorldSpace, lightPosInEyeSpace );

        // Set the ModelViewProjection matrix in the shader.
        gl.glUniformMatrix4fv( mModelViewProjectionParam, 1, false, modelViewProjection, 0 );

        if ( doLighting ) {
            gl.glUniform3f( mLightPosParam, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2] );
            {
                gl.glUniformMatrix4fv( mModelParam, 1, false, model, 0);
                gl.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
            }
            if ( this .isInstanced ) {
                for ( int i = 0; i < 60; i++ )
                    gl.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, icosahedralOrientations[ i ], 0 );
                checkGLError( gl, "mOrientationsParam");
            }
        }
    }

    public void renderShape( GL2 gl, ShapeClass shape )
    {

        float[] color = shape .getColor();
        gl.glUniform4f( mColorParam, color[0], color[1], color[2], color[3] );

        // Set the vertices of the shape
        gl.glEnableVertexAttribArray(mPositionParam);
        gl.glVertexAttribDivisor(mPositionParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        gl.glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX, GL2.GL_FLOAT,
                false, 0, shape .getVertices() );
        checkGLError( gl, "mPositionParam");

        if ( doLighting ) {
            gl.glEnableVertexAttribArray(normalParam);
            gl.glVertexAttribDivisor(normalParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl.glVertexAttribPointer(normalParam, COORDS_PER_VERTEX, GL2.GL_FLOAT,
                    false, 0, shape .getNormals() );
            checkGLError( gl, "normalParam");

            if ( this .isInstanced ) {
                // Set the positions of the shapes
                gl.glEnableVertexAttribArray( instanceData );
                gl.glVertexAttribDivisor( instanceData, 1);  // SV: this one is instanced
                gl.glVertexAttribPointer( instanceData, 4, GL2.GL_FLOAT, false, 0, shape .getPositions() );

                gl.glDrawArraysInstanced( GL2.GL_TRIANGLES, 0, shape .getVertexCount(), shape .getInstanceCount() );
            }
            else {
                gl.glDrawArrays( GL2.GL_TRIANGLES, 0, shape .getVertexCount() );
            }
        }
        else {
            gl.glDrawArrays( GL2.GL_LINES, 0, shape .getVertexCount() );
        }
        checkGLError( gl, "Drawing a shape");
    }
}
