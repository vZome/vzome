package com.vzome.opengl;

import java.nio.FloatBuffer;

import com.vzome.core.render.SymmetryRendering;

/**
* Created by vorth on 7/28/14.
*/
public class OutlineRenderer implements InstancedGeometry.BufferStorage, Renderer
{
    private final OpenGlShim gl;

    private int mGlProgram;
    private int instanceData;
    private int mPositionParam;
    private int mColorParam;
    private int mProjectionParam;
    private int mModelViewParam;
    private int[] mOrientationsParam = new int[60];

    private static final int COORDS_PER_VERTEX = 3;

    public OutlineRenderer( OpenGlShim gl )
    {
        this.gl = gl;
        String version = gl .getGLSLVersionString();

        String vertexShaderSrc = version + "\n" +
                    "uniform mat4 u_ProjMatrix;\n" +
                    "uniform mat4 u_MVMatrix;\n" +
                    "uniform mat4 u_Orientations[60];\n" +
                    "uniform vec4 u_Color;\n" +
                    "\n" +
                    "attribute vec4 a_Position;\n" + 
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

        mGlProgram = gl.glCreateProgram();
        OpenGlUtilities.checkGLError( gl, "glCreateProgram");
        gl.glAttachShader(mGlProgram, vertexShader);
        OpenGlUtilities.checkGLError( gl, "glAttachShader vertexShader");
        gl.glAttachShader(mGlProgram, fragmentShader);
        OpenGlUtilities.checkGLError( gl, "glAttachShader fragmentShader");
        gl.glLinkProgram(mGlProgram);
        OpenGlUtilities.checkGLError( gl, "glLinkProgram");

        mModelViewParam = gl .glGetUniformLocation( mGlProgram, "u_MVMatrix" );
        mProjectionParam = gl .glGetUniformLocation( mGlProgram, "u_ProjMatrix" );
        mColorParam = gl .glGetUniformLocation( mGlProgram, "u_Color" );
        for ( int i = 0; i < mOrientationsParam.length; i++ )
            mOrientationsParam[ i ] = gl.glGetUniformLocation( mGlProgram, "u_Orientations[" + i + "]" );
        OpenGlUtilities.checkGLError( gl, "glGetUniformLocation");

        mPositionParam = gl .glGetAttribLocation( mGlProgram, "a_Position" );
        instanceData = gl .glGetAttribLocation( mGlProgram, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation+glow)
        OpenGlUtilities.checkGLError( gl, "glGetAttribLocation");
    }

    @Override
    public void setLights( float[][] lightDirections, float[][] lightColors, float[] ambientLight )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glUniform4f( mColorParam, 0f, 0f, 0f, 0f ); // only support black lines for now
        gl .glLineWidth( 2f );
    }

    @Override
    public void setView( float[] modelView, float[] projection )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
        gl.glUniformMatrix4fv( mProjectionParam, 1, false, projection, 0 );
    }
    
    @Override
    public void clear( float[] background ) {} // MUST DO NOTHING, or you will overwrite solid rendering
    
    @Override
    public void renderSymmetry( SymmetryRendering symmetryRendering )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        float[][] orientations = symmetryRendering .getOrientations();
        for ( int i = 0; i < orientations.length; i++ )
            gl.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, orientations[ i ], 0 );
        OpenGlUtilities.checkGLError( gl, "mOrientationsParam");
        
        for( InstancedGeometry shapeClass : symmetryRendering .getGeometries() )
            this .renderShape( shapeClass );
    }

    public void renderShape( InstancedGeometry shape )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        int instanceCount = shape .prepareToRender( this ); // will call back to storeBuffer()
        if ( instanceCount == 0 )
            return;
        
        gl .glBindBuffer( shape .getLineVerticesVBO() );
        gl .glEnableVertexAttribArray( mPositionParam );
        gl .glVertexAttribDivisor( mPositionParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        gl .glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX, false, 0, 0 );
        gl .glBindBuffer( 0 );
        OpenGlUtilities.checkGLError( gl, "mPositionParam");

        gl .glBindBuffer( shape .getPositionsVBO() );
        gl .glEnableVertexAttribArray( instanceData );
        gl .glVertexAttribDivisor( instanceData, 1 );  // SV: this one is instanced
        gl .glVertexAttribPointer( instanceData, 4, false, 0, 0 );
        gl .glBindBuffer( 0 );
        OpenGlUtilities.checkGLError( gl, "instanceData");

        gl.glDrawLinesInstanced( 0, shape .getLineVertexCount(), instanceCount );
        OpenGlUtilities.checkGLError( gl, "Drawing a shape");
    }

    @Override
    public int storeBuffer( FloatBuffer clientBuffer, int oldId )
    {
        return OpenGlUtilities .storeBuffer( this .gl, clientBuffer, oldId );
    }
}
