package com.vzome.opengl;

import java.nio.FloatBuffer;

public class OpenGlUtilities
{
    static int storeBuffer( OpenGlShim gl, FloatBuffer clientBuffer, int oldId )
    {
        if ( oldId != -1 ) {
            gl .glDeleteBuffer( oldId );
            OpenGlUtilities.checkGLError( gl, "glDeleteBuffer");
        }
        final int buffers[] = new int[1];
        gl .glGenBuffers( 1, buffers, 0 );
        OpenGlUtilities.checkGLError( gl, "glGenBuffers");
    
        // Bind to the buffer. glBufferData will affect this buffer specifically.
        gl .glBindBuffer( buffers[ 0 ] );
        // Transfer data from client memory to the buffer.
        // We can release the client memory after this call.
        gl .glBufferData( clientBuffer .capacity() * 4, clientBuffer );
        OpenGlUtilities.checkGLError( gl, "glBufferData");
        // IMPORTANT: Unbind from the buffer when we're done with it.
        gl .glBindBuffer( 0 );
    
        return buffers[ 0 ];
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     * @param func
     */
    static void checkGLError( OpenGlShim gl, String func )
    {
        int error;
        while ((error = gl.glGetError()) != 0) {
            System.out.println( func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return
     */
    static int loadGLShader( OpenGlShim gl, boolean isVertexShader, String code )
    {
        int shader = isVertexShader? gl.glCreateVertexShader() : gl.glCreateFragmentShader();
    
        gl.glShaderSource( shader, code );
        
        checkGLError( gl, "glShaderSource" );
        gl.glCompileShader(shader);
        checkGLError( gl, "glCompileShader" );
    
        // Get the compilation status.
        final int[] compileStatus = new int[1];
        gl.glGetShaderStatus(shader, compileStatus, 0);
        checkGLError( gl, "glGetShaderiv" );
    
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

    public static void setVBO( OpenGlShim gl, int param, int vbo, boolean instanced, int coordsPerVertex )
    {
        gl .glBindBuffer( vbo );
        gl .glEnableVertexAttribArray( param );
        gl .glVertexAttribDivisor( param, instanced? 1 : 0 );
        gl .glVertexAttribPointer( param, coordsPerVertex, false, 0, 0 );
        gl .glBindBuffer( 0 );
        OpenGlUtilities.checkGLError( gl, "setVBO");
    }

    public static void setBuffer( OpenGlShim gl, int param, FloatBuffer buffer, boolean instanced, int coordsPerVertex )
    {
        gl .glEnableVertexAttribArray( param );
        gl .glVertexAttribDivisor( param, instanced? 1 : 0 );
        gl .glVertexAttribPointer( param, coordsPerVertex, false, 0, buffer );
        OpenGlUtilities.checkGLError( gl, "setBuffer");
    }
}
