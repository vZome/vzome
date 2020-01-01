
package com.vzome.opengl;

import java.nio.FloatBuffer;

import com.vzome.core.render.SymmetryRendering;

/**
* Created by vorth on 7/28/14.
*/
public class Renderer
{
    protected static final String MODEL_VIEW_PROJECTION = "u_MVP";
    protected static final String COLOR = "u_Color";
    protected static final String A_POSITION = "a_Position";

    protected int mGlProgram;

    private int mPositionParam;
    private int mColorParam;
    private int mModelViewProjectionParam;

    // We keep the light always position just above the user.
    protected static final float[] mLightPosInWorldSpace = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

    protected static final int COORDS_PER_VERTEX = 3;

    public Renderer( OpenGlShim gl )
    {
        this .loadShaders( gl );

        this .getUniforms( gl );
    }

    protected void getUniforms( OpenGlShim gl )
    {
        mModelViewProjectionParam = gl.glGetUniformLocation( mGlProgram, MODEL_VIEW_PROJECTION );
        checkGLError( gl, "worldViewProjection");

        mPositionParam = gl.glGetAttribLocation(mGlProgram, A_POSITION );
        mColorParam = gl.glGetUniformLocation(mGlProgram, COLOR );
        checkGLError( gl, COLOR );
    }

    protected void loadShaders( OpenGlShim gl )
    {
        int vertexShader = loadGLShader( gl, this .getVertexShaderSource(), true );
        int fragmentShader = loadGLShader( gl, this .getFragmentShaderSource(), false );

        mGlProgram = gl.glCreateProgram();
        checkGLError( gl, "glCreateProgram");
        gl.glAttachShader(mGlProgram, vertexShader);
        checkGLError( gl, "glAttachShader vertexShader");
        gl.glAttachShader(mGlProgram, fragmentShader);
        checkGLError( gl, "glAttachShader fragmentShader");
        gl.glLinkProgram(mGlProgram);
        checkGLError( gl, "glLinkProgram");
    }

    protected String getFragmentShaderSource()
    {
        return  "varying vec4 v_Color;\n" + 

	    		"void main() {\n" + 
	    		"  gl_FragColor = v_Color;\n" + 
	    		"}";
    }

    protected String getVertexShaderSource()
    {
        return  "attribute vec4 " + A_POSITION + ";\n" + 
                "uniform mat4 " + MODEL_VIEW_PROJECTION + ";\n" + 
                "uniform vec4 " + COLOR + ";\n" +

	    		"varying vec4 v_Color;\n" + 

	    		"void main() {\n" + 
	    		"  v_Color = " + COLOR + ";\n" + 
	    		"  gl_Position = " + MODEL_VIEW_PROJECTION + " * " + A_POSITION + ";\n" + 
	    		"}";
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader
     * @param type The type of shader we will be creating.
     * @return
     */
    private static int loadGLShader( OpenGlShim gl, String shaderSource, boolean isVertexShader )
    {
        int shader = isVertexShader? gl.glCreateVertexShader() : gl.glCreateFragmentShader();
        gl.glShaderSource(shader, shaderSource);
        checkGLError( gl,  "glShaderSource" );
        gl.glCompileShader(shader);
        checkGLError( gl,  "glCompileShader" );

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        gl.glGetShaderStatus(shader, compileStatus, 0);
        checkGLError( gl,  "glGetShaderiv" );

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            String problem = gl.glGetShaderInfoLog(shader);
            System .out.println( problem );
            //            Log.e( TAG, "Error compiling shader: " + gl.glGetShaderInfoLog(shader));
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
    public static void checkGLError( OpenGlShim gl, String func ) {
        int error;
        while ((error = gl.glGetError()) != 0 /*should be GL.GL_NO_ERROR */) {
            //            Log.e(TAG, func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    public void bindBuffers( OpenGlShim gl, SymmetryRendering symmetryRendering )
    {
        // First, generate as many buffers as we need.
        // This will give us the OpenGL handles for these buffers.
        int numBuffers = 3 * symmetryRendering .numGeometries();
        
        if ( numBuffers == 0 )
            return; // must have had only panels?
        
        final int buffers[] = new int[ numBuffers ];
        gl.glGenBuffers( numBuffers, buffers, 0 );

        int bufferTriple = 0;
        for( InstancedGeometry shape : symmetryRendering .getGeometries() )
        {
            // Bind to the buffer. glBufferData will affect this buffer specifically.
            gl.glBindBuffer( buffers[ bufferTriple + 0 ] );
            // Transfer data from client memory to the buffer.
            // We can release the client memory after this call.
            FloatBuffer clientBuffer = shape .getVertices();
            gl.glBufferData( clientBuffer .capacity() * 4, clientBuffer );
            // IMPORTANT: Unbind from the buffer when we're done with it.
            gl.glBindBuffer( 0 );

            // Bind to the buffer. glBufferData will affect this buffer specifically.
            gl.glBindBuffer( buffers[ bufferTriple + 1 ] );
            // Transfer data from client memory to the buffer.
            // We can release the client memory after this call.
            clientBuffer = shape .getNormals();
            gl.glBufferData( clientBuffer .capacity() * 4, clientBuffer );
            // IMPORTANT: Unbind from the buffer when we're done with it.
            gl.glBindBuffer( 0 );

            // Bind to the buffer. glBufferData will affect this buffer specifically.
            gl.glBindBuffer( buffers[ bufferTriple + 2 ] );
            // Transfer data from client memory to the buffer.
            // We can release the client memory after this call.
            clientBuffer = shape .getPositions();
            gl.glBufferData( clientBuffer .capacity() * 4, clientBuffer );
            // IMPORTANT: Unbind from the buffer when we're done with it.
            gl.glBindBuffer( 0 );

            shape .setBuffers( buffers[ bufferTriple + 0 ], buffers[ bufferTriple + 1 ], buffers[ bufferTriple + 2 ], -1 ); // TODO implement colors buffer
            bufferTriple += 3;
            //            Log.i( TAG, "another buffer triple loaded into GPU" );
        }
        checkGLError( gl, "loadVBOs" );
    }

    public void setUniforms( OpenGlShim gl, float[] model, float[] camera, float[] eyeTransform, float[] eyePerspective, float[][] fixedOrientations )
    {
        gl.glUseProgram( mGlProgram );
        checkGLError( gl, "glUseProgram");  // a compile / link problem seems to fail only now!

        // Apply the eye transformation to the camera.
        float[] view = new float[16];
        gl .multiplyMM( view, eyeTransform, camera );

        float[] viewInverse = new float[16];
        gl .invertM( viewInverse, view );

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        float[] modelView = new float[16];
        gl .multiplyMM( modelView, view, model);

        float[] modelViewProjection = new float[16];
        gl .multiplyMM( modelViewProjection, eyePerspective, modelView );

        // Set the ModelViewProjection matrix in the shader.
        gl.glUniformMatrix4fv( mModelViewProjectionParam, 1, false, modelViewProjection, 0 );
    }

    public void renderScene( OpenGlShim gl, float[] mModelCube, float[] mCamera, float[] eyeTransform, float[] eyePerspective, SymmetryRendering symmetryRendering )
    {
//        this.setUniforms( gl, mModelCube, mCamera, eyeTransform, eyePerspective, scene .getOrientations() );
        for( InstancedGeometry shape : symmetryRendering .getGeometries() )
        {
            // Set the vertices of the shape
            int vbo = shape .getVerticesVBO();
            if ( shape .usesVBOs() )
                gl.glBindBuffer( vbo );
            gl.glEnableVertexAttribArray( mPositionParam );
            gl.glVertexAttribDivisor( mPositionParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            if ( shape .usesVBOs() ) {
                gl.glVertexAttribPointer( mPositionParam , COORDS_PER_VERTEX, false, 0, 0 );
                gl.glBindBuffer( 0);
            }
            else
                gl.glVertexAttribPointer( mPositionParam , COORDS_PER_VERTEX, false, 0, shape .getVertices() );
            checkGLError( gl, "mPositionParam");

            this .renderShape( gl, shape );

            checkGLError( gl, "Drawing a shape");
        }
    }

    protected void renderShape( OpenGlShim gl, InstancedGeometry shape )
    {
        gl.glDrawLines( 0, shape .getVertexCount() );
    }
}
