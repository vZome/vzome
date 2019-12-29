package com.vzome.opengl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;

/**
* Created by vorth on 7/28/14.
*/
public class RenderingProgram
{
    private final OpenGlShim gl;

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
    private static final float[] mLightPosInWorldSpace = new float[] {2.0f, 5.0f, -30.0f, 1.0f};

    private static final int COORDS_PER_VERTEX = 3;

    public RenderingProgram( OpenGlShim gl, boolean lighting, boolean instanced )
    {
        this.gl = gl;
        String version = gl .getGLSLVersionString();
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
        int vertexShader = loadGLShader( true, vertexShaderSrc );

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
        int fragmentShader = loadGLShader( false, fragmentShaderSrc );

        mGlProgram = gl.glCreateProgram();
        checkGLError( "glCreateProgram");
        gl.glAttachShader(mGlProgram, vertexShader);
        checkGLError( "glAttachShader vertexShader");
        gl.glAttachShader(mGlProgram, fragmentShader);
        checkGLError( "glAttachShader fragmentShader");
        gl.glLinkProgram(mGlProgram);
        checkGLError( "glLinkProgram");

        mModelViewProjectionParam = gl.glGetUniformLocation( mGlProgram, "u_MVP" );
        checkGLError( "worldViewProjection");

        mPositionParam = gl.glGetAttribLocation(mGlProgram, "a_Position");
        mColorParam = gl.glGetUniformLocation(mGlProgram, "u_Color");
        checkGLError( "u_Color");

        if ( this .doLighting )
        {
            {
                mLightPosParam = gl.glGetUniformLocation(mGlProgram, "u_LightPos");
                mModelViewParam = gl.glGetUniformLocation( mGlProgram, "u_MVMatrix" );
                mModelParam = gl.glGetUniformLocation( mGlProgram, "u_Model" );
            }

            normalParam = gl.glGetAttribLocation( mGlProgram, "a_Normal" );
            checkGLError( "a_Normal");

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
    private int loadGLShader( boolean isVertexShader, String code )
    {
        int shader = isVertexShader? gl.glCreateVertexShader() : gl.glCreateFragmentShader();

        gl.glShaderSource( shader, code );
        
        checkGLError( "glShaderSource" );
        gl.glCompileShader(shader);
        checkGLError( "glCompileShader" );

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        gl.glGetShaderStatus(shader, compileStatus, 0);
        checkGLError( "glGetShaderiv" );

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            String problem = gl.glGetShaderInfoLog(shader);
            System .out.println(  "Error compiling shader: " + problem );
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
    public void checkGLError( String func )
    {
        int error;
        while ((error = gl.glGetError()) != GL2.GL_NO_ERROR) {
            System.out.println( func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    public void setOrientations( float[][] icosahedralOrientations )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( "glUseProgram" );  // a compile / link problem seems to fail only now!
        if ( this .isInstanced ) {
            for ( int i = 0; i < 60; i++ )
                gl.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, icosahedralOrientations[ i ], 0 );
            checkGLError( "mOrientationsParam");
        }
    }

    public void setUniforms( float[] model, float[] camera, float[] projection )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( "glUseProgram" );  // a compile / link problem seems to fail only now!

        float[] modelViewProjection = new float[16];
        float[] modelView = new float[16];
        float[] lightPosInEyeSpace = new float[4];

        // Build the ModelView and ModelViewProjection matrices
        //  ASSUME ALL MATRICES ARE IN COLUMN-MAJOR ORDER!
        FloatUtil.multMatrix( camera, model, modelView );
        FloatUtil.multMatrix( projection, modelView, modelViewProjection );

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
        }
    }
    
    public void renderScene( Scene scene )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glEnableDepth();
        float[] rgba = scene .getBackground();
        gl .glClear( rgba[0], rgba[1], rgba[2], rgba[3] );
        
        for( ShapeClass shapeClass : scene )
            this .renderShape( shapeClass );
    }

    public void renderShape( ShapeClass shape )
    {
        float[] color = shape .getColor();
        gl.glUniform4f( mColorParam, color[0], color[1], color[2], color[3] );

        // Set the vertices of the shape
        gl.glEnableVertexAttribArray(mPositionParam);
        gl.glVertexAttribDivisor(mPositionParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        gl.glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX,
                false, 0, shape .getVertices() );
        checkGLError( "mPositionParam");

        if ( doLighting ) {
            gl.glEnableVertexAttribArray(normalParam);
            gl.glVertexAttribDivisor(normalParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl.glVertexAttribPointer(normalParam, COORDS_PER_VERTEX,
                    false, 0, shape .getNormals() );
            checkGLError( "normalParam");

            if ( this .isInstanced ) {
                // Set the positions of the shapes
                gl.glEnableVertexAttribArray( instanceData );
                gl.glVertexAttribDivisor( instanceData, 1);  // SV: this one is instanced
                gl.glVertexAttribPointer( instanceData, 4, false, 0, shape .getPositions() );

                gl.glDrawArraysInstanced( 0, shape .getVertexCount(), shape .getInstanceCount() );
            }
            else {
                gl.glDrawTriangles( 0, shape .getVertexCount() );
            }
        }
        else {
            gl.glDrawLines( 0, shape .getVertexCount() );
        }
        checkGLError( "Drawing a shape");
    }
}
