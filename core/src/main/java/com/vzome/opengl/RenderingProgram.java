package com.vzome.opengl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;
import com.vzome.core.render.SymmetryRendering;

/**
* Created by vorth on 7/28/14.
*/
public class RenderingProgram
{
    private final OpenGlShim gl;

    private int mGlProgram;
    private int instanceData;
    private int mPositionParam;
    private int normalParam;
    private int mColorParam;
    private int mModelViewProjectionParam;
    private int mModelViewParam;
    private int mNumLightsParam;
    private int[] mLightDirectionsParam = new int[1];
    private int[] mOrientationsParam = new int[60];

    private static final float[] HEADLIGHT = new float[] {0f, 0f, 1f, 1f};

    private static final int COORDS_PER_VERTEX = 3;

    public RenderingProgram( OpenGlShim gl )
    {
        this.gl = gl;
        String version = gl .getGLSLVersionString();

        String vertexShaderSrc = version + "\n" +
                    "uniform mat4 u_MVP;\n" +
                    "uniform mat4 u_MVMatrix;\n" +
                    "uniform vec4 u_Color;\n" +
                    "uniform mat4 u_Orientations[60];\n" +
                    "\n" +
                    "#define MAX_LIGHTS 10\n" + 
                    "uniform int  u_NumLights;\n" + 
                    "uniform vec3 u_LightDirections[MAX_LIGHTS];\n" + 
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
                    "   float orientationAndGlow = a_InstanceData.w;\n" + 
                    "   vec4 location = vec4( a_InstanceData.xyz, 1.0 );\n" + 
                    "\n" + 
                    "\n" + 
                    "   int orientation = int( max( 0, min( 59, floor(orientationAndGlow) ) ) );\n" + 
                    "   vec4 oriented = ( u_Orientations[ orientation ] * a_Position );\n" + 
                    "   vec4 normal = ( u_Orientations[ orientation ] * vec4( a_Normal, 0.0 ) );\n" + 
                    "   vec4 pos = oriented + location;\n" + 
                    "   gl_Position = u_MVP * pos;\n" + 
                    "\n" + 
                    "   vec3 modelViewNormal = vec3( u_MVMatrix * vec4( a_Normal, 0.0 ) );\n" + 
                    "   vec4 linearColor = vec4( fract(orientationAndGlow) );\n" + 
                    "   for( int i = 0; i < u_NumLights; ++i ){\n" + 
                    "       vec3 lightVector = normalize( u_LightDirections[i] );\n" + 
                    "       float diffuse = max(dot(modelViewNormal, lightVector), 0.5 );\n" + 
                    "       linearColor += u_Color * diffuse;\n" + 
                    "   }" +
                    "   v_Color = linearColor;\n" + 
                    "}";
        int vertexShader = loadGLShader( true, vertexShaderSrc );

        String fragmentShaderSrc = version + "\n" +
                "varying vec4 v_Color;\n" + 
                "\n" + 
                "void main() {\n" + 
                "  gl_FragColor = v_Color;\n" + 
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

        mNumLightsParam = gl.glGetUniformLocation( mGlProgram, "u_NumLights" );
        mLightDirectionsParam[0] = gl.glGetUniformLocation(mGlProgram, "u_LightDirections[0]");
        mModelViewParam = gl.glGetUniformLocation( mGlProgram, "u_MVMatrix" );

        normalParam = gl.glGetAttribLocation( mGlProgram, "a_Normal" );
        checkGLError( "a_Normal");

        for ( int i = 0; i < 60; i++ )
            mOrientationsParam[ i ] = gl.glGetUniformLocation( mGlProgram, "u_Orientations[" + i + "]" );

        instanceData = gl.glGetAttribLocation( mGlProgram, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation)
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

    public void setView( float[] modelView, float[] projection )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( "glUseProgram" );  // a compile / link problem seems to fail only now!

        float[] modelViewProjection = new float[16];
        float[] lightPosInEyeSpace = new float[4];

        // Build the ModelView and ModelViewProjection matrices
        //  ASSUME ALL MATRICES ARE IN COLUMN-MAJOR ORDER!
        FloatUtil.multMatrix( projection, modelView, modelViewProjection );

        // Set the position of the light
        FloatUtil.multMatrixVec( modelView, HEADLIGHT, lightPosInEyeSpace );

        // Set the ModelViewProjection matrix in the shader.
        gl.glUniformMatrix4fv( mModelViewProjectionParam, 1, false, modelViewProjection, 0 );

        gl.glUniform1i( mNumLightsParam, 1 );
        gl.glUniform3f( mLightDirectionsParam[0], lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2] );
        gl.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
    }
    
    public void clear( float[] background )
    {
        gl .glEnableDepth();
        gl .glClear( background[0], background[1], background[2], background[3] );
    }
    
    public void renderSymmetry( SymmetryRendering symmetryRendering )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( "glUseProgram" );  // a compile / link problem seems to fail only now!

        float[][] orientations = symmetryRendering .getOrientations();
        for ( int i = 0; i < orientations.length; i++ )
            gl.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, orientations[ i ], 0 );
        checkGLError( "mOrientationsParam");
        
        for( InstancedGeometry shapeClass : symmetryRendering .getGeometries() )
            this .renderShape( shapeClass );
    }

    public void renderShape( InstancedGeometry shape )
    {
        int count = shape .getInstanceCount();
        if ( count == 0 )
            return;
        
        float[] color = shape .getColor();
        gl.glUniform4f( mColorParam, color[0], color[1], color[2], color[3] );

        // Set the vertices of the shape
        gl.glEnableVertexAttribArray(mPositionParam);
        gl.glVertexAttribDivisor(mPositionParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        gl.glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX, false, 0, shape .getVertices() );
        checkGLError( "mPositionParam");

        gl.glEnableVertexAttribArray(normalParam);
        gl.glVertexAttribDivisor(normalParam, 0);  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        gl.glVertexAttribPointer(normalParam, COORDS_PER_VERTEX, false, 0, shape .getNormals() );
        checkGLError( "normalParam");

        // Set the positions of the shapes
        gl.glEnableVertexAttribArray( instanceData );
        gl.glVertexAttribDivisor( instanceData, 1);  // SV: this one is instanced
        gl.glVertexAttribPointer( instanceData, 4, false, 0, shape .getPositions() );

        gl.glDrawArraysInstanced( 0, shape .getVertexCount(), count );
        checkGLError( "Drawing a shape");
    }
}
