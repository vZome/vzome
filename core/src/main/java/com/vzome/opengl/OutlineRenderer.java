package com.vzome.opengl;

import java.nio.FloatBuffer;

import com.vzome.core.render.SymmetryRendering;

/**
* Created by vorth on 7/28/14.
*/
public class OutlineRenderer implements InstancedGeometry.BufferStorage, Renderer
{
    private final OpenGlShim gl;

    private int programId;
    private int a_InstanceData;
    private int a_Vertex;
    private int u_Color;
    private int u_ProjMatrix;
    private int u_MVMatrix;
    private int[] u_Orientations = new int[60];

    private static final int COORDS_PER_VERTEX = 3;

    private final boolean useVBOs;

    public OutlineRenderer( OpenGlShim gl, boolean useVBOs )
    {
        this .gl = gl;
        this .useVBOs = useVBOs;
        String version = gl .getGLSLVersionString();

        String vertexShaderSrc = version + "\n" +
                    "uniform mat4 u_ProjMatrix;\n" +
                    "uniform mat4 u_MVMatrix;\n" +
                    "uniform mat4 u_Orientations[60];\n" +
                    "uniform vec4 u_Color;\n" +
                    "\n" +
                    "attribute vec4 a_Vertex;\n" + 
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
                    "   vec4 oriented = ( u_Orientations[ orientation ] * a_Vertex );\n" + 
                    "   vec4 pos = oriented + location;\n" + 
                    "   vec4 devicePos = (u_ProjMatrix * u_MVMatrix) * pos;\n" + 
                    "   gl_Position = (u_ProjMatrix * u_MVMatrix) * pos;\n" + 
                    "\n" + 
                    "   v_Color = u_Color;\n" + 
                    "}";
        int vertexShader = OpenGlUtilities.loadGLShader( gl, true, vertexShaderSrc );

        String fragmentShaderSrc = version + "\n" +
                "varying vec4 v_Color;\n" + 
                "\n" + 
                "void main() {\n" + 
                "  gl_FragColor = v_Color;\n" + 
                "}";
        int fragmentShader = OpenGlUtilities.loadGLShader( gl, false, fragmentShaderSrc );

        programId = gl.glCreateProgram();
        OpenGlUtilities.checkGLError( gl, "glCreateProgram");
        gl.glAttachShader(programId, vertexShader);
        OpenGlUtilities.checkGLError( gl, "glAttachShader vertexShader");
        gl.glAttachShader(programId, fragmentShader);
        OpenGlUtilities.checkGLError( gl, "glAttachShader fragmentShader");
        gl.glLinkProgram(programId);
        OpenGlUtilities.checkGLError( gl, "glLinkProgram");

        u_MVMatrix = gl .glGetUniformLocation( programId, "u_MVMatrix" );
        u_ProjMatrix = gl .glGetUniformLocation( programId, "u_ProjMatrix" );
        u_Color = gl .glGetUniformLocation( programId, "u_Color" );
        for ( int i = 0; i < u_Orientations.length; i++ )
            u_Orientations[ i ] = gl.glGetUniformLocation( programId, "u_Orientations[" + i + "]" );
        OpenGlUtilities.checkGLError( gl, "glGetUniformLocation");

        a_Vertex = gl .glGetAttribLocation( programId, "a_Vertex" );
        a_InstanceData = gl .glGetAttribLocation( programId, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation+glow)
        OpenGlUtilities.checkGLError( gl, "glGetAttribLocation");
    }

    @Override
    public void setLights( float[][] lightDirections, float[][] lightColors, float[] ambientLight )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glUniform4f( u_Color, 0f, 0f, 0f, 0f ); // only support black lines for now
        gl .glLineWidth( 2f );
    }

    @Override
    public void setView( float[] modelView, float[] projection )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl.glUniformMatrix4fv( u_MVMatrix, 1, false, modelView, 0);
        gl.glUniformMatrix4fv( u_ProjMatrix, 1, false, projection, 0 );
    }
    
    @Override
    public void clear( float[] background ) {} // MUST DO NOTHING, or you will overwrite solid rendering
    
    @Override
    public void renderSymmetry( SymmetryRendering symmetryRendering )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        float[][] orientations = symmetryRendering .getOrientations();
        for ( int i = 0; i < orientations.length; i++ )
            gl.glUniformMatrix4fv( u_Orientations[ i ], 1, false, orientations[ i ], 0 );
        OpenGlUtilities.checkGLError( gl, "mOrientationsParam");
        
        for( InstancedGeometry shapeClass : symmetryRendering .getGeometries() )
            this .renderShape( shapeClass );
    }

    public void renderShape( InstancedGeometry shape )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        int instanceCount = shape .prepareToRender( this .useVBOs ? this : null ); // will call back to storeBuffer()
        if ( instanceCount == 0 )
            return;
        
        if ( this .useVBOs ) {
            OpenGlUtilities .setVBO( gl, a_Vertex,       shape .getLineVerticesVBO(),  false, COORDS_PER_VERTEX );
            OpenGlUtilities .setVBO( gl, a_InstanceData, shape .getInstancesVBO(), true, 4 );
        }
        else {
            OpenGlUtilities .setBuffer( gl, a_Vertex,       shape .getLineVerticesBuffer(),  false, COORDS_PER_VERTEX );
            OpenGlUtilities .setBuffer( gl, a_InstanceData, shape .getInstancesBuffer(), true, 4 );
        }

        gl.glDrawLinesInstanced( 0, shape .getLineVertexCount(), instanceCount );
        OpenGlUtilities.checkGLError( gl, "Drawing a shape");
    }

    @Override
    public int storeBuffer( FloatBuffer clientBuffer, int oldId )
    {
        return OpenGlUtilities .storeBuffer( this .gl, clientBuffer, oldId );
    }
}
