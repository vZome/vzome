package com.vzome.opengl;

import java.nio.FloatBuffer;

import com.vzome.core.render.SymmetryRendering;

/**
* Created by vorth on 7/28/14.
*/
public class SolidRenderer implements InstancedGeometry.BufferStorage, Renderer
{
    private final OpenGlShim gl;

    private int mGlProgram;
    private int instanceData;
    private int mPositionParam;
    private int normalParam;
    private int mColorParam;
    private int mProjectionParam;
    private int mModelViewParam;
    private int ambientLightParam;
    private int mNumLightsParam;
    private int[] mLightDirectionsParam = new int[10];
    private int[] mLightColorsParam = new int[10];
    private int[] mOrientationsParam = new int[60];

    private static final int COORDS_PER_VERTEX = 3;

    private final boolean useVBOs;

    public SolidRenderer( OpenGlShim gl, boolean useVBOs )
    {
        this .gl = gl;
        this .useVBOs = useVBOs;
        String version = gl .getGLSLVersionString();

        String vertexShaderSrc = version + "\n" +
                    "uniform mat4 u_ProjMatrix;\n" +
                    "uniform mat4 u_MVMatrix;\n" +
                    "uniform mat4 u_Orientations[60];\n" +
                    "\n" +
                    "#define MAX_LIGHTS 10\n" + 
                    "uniform int  u_NumLights;\n" + 
                    "uniform vec3 u_AmbientLight;\n" + 
                    "uniform vec3 u_LightDirections[MAX_LIGHTS];\n" + 
                    "uniform vec3 u_LightColors[MAX_LIGHTS];\n" + 
                    "\n" +
                    "attribute vec4 a_Position;\n" + 
                    "attribute vec3 a_Normal;\n" + 
                    "attribute vec4 a_InstanceData;\n" + 
                    "attribute vec4 a_Color;\n" +
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
                    "   gl_Position = (u_ProjMatrix * u_MVMatrix) * pos;\n" + 
                    "\n" + 
                    "   vec3 modelViewNormal = normalize( vec3( u_MVMatrix * normal ) );\n" + 
                    "   vec3 linearColor = vec3( fract(orientationAndGlow) );\n" + 
                    "   linearColor += u_AmbientLight * 0.9;\n" + 
                    "   for( int i = 0; i < u_NumLights; ++i ){\n" + 
                    "       vec3 lightVector = normalize( u_LightDirections[i] );\n" + 
                    "       float diffuse = max(dot(modelViewNormal, lightVector), 0.0 );\n" + 
                    "       linearColor += diffuse * a_Color.rgb * u_LightColors[i];\n" + 
                    "   }" +
                    "   v_Color = vec4( linearColor, a_Color.a );\n" + 
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

        mModelViewParam = gl.glGetUniformLocation( mGlProgram, "u_MVMatrix" );
        mProjectionParam = gl.glGetUniformLocation( mGlProgram, "u_ProjMatrix" );
        OpenGlUtilities.checkGLError( gl, "projection modelview");

        ambientLightParam = gl.glGetUniformLocation( mGlProgram, "u_AmbientLight" );
        mNumLightsParam = gl.glGetUniformLocation( mGlProgram, "u_NumLights" );
        for ( int i = 0; i < mLightDirectionsParam.length; i++ ) {
            mLightDirectionsParam[i] = gl.glGetUniformLocation(mGlProgram, "u_LightDirections[" + i + "]");
            mLightColorsParam[i] = gl.glGetUniformLocation(mGlProgram, "u_LightColors[" + i + "]");
        }
        OpenGlUtilities.checkGLError( gl, "light params");

        for ( int i = 0; i < mOrientationsParam.length; i++ )
            mOrientationsParam[ i ] = gl.glGetUniformLocation( mGlProgram, "u_Orientations[" + i + "]" );

        mPositionParam = gl .glGetAttribLocation( mGlProgram, "a_Position" );
        mColorParam = gl .glGetAttribLocation( mGlProgram, "a_Color" );
        normalParam = gl .glGetAttribLocation( mGlProgram, "a_Normal" );
        OpenGlUtilities.checkGLError( gl, "a_Normal");

        instanceData = gl.glGetAttribLocation( mGlProgram, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation+glow)
    }

    public void setLights( float[][] lightDirections, float[][] lightColors, float[] ambientLight )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        // offset the solid polygons back, so that outlines will appear in front
        gl .glPolygonOffset( 1f, -1f );

        gl.glUniform3f( ambientLightParam, ambientLight[0], ambientLight[1], ambientLight[2] );
        gl.glUniform1i( mNumLightsParam, lightDirections.length );
        for (int i = 0; i < lightDirections.length; i++) {
            gl.glUniform3f( mLightDirectionsParam[i], lightDirections[i][0], lightDirections[i][1], lightDirections[i][2] );
            gl.glUniform3f( mLightColorsParam[i], lightColors[i][0], lightColors[i][1], lightColors[i][2] );
            OpenGlUtilities.checkGLError( gl, "light " + i );
        }
    }

    public void setView( float[] modelView, float[] projection )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
        gl.glUniformMatrix4fv( mProjectionParam, 1, false, projection, 0 );
    }
    
    public void clear( float[] background )
    {
        gl.glUseProgram( mGlProgram );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glEnableDepth();
        gl .glClear( background[0], background[1], background[2], background[3] );
    }
    
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

        int instanceCount = shape .prepareToRender( this .useVBOs ? this : null ); // will call back to storeBuffer()
        if ( instanceCount == 0 )
            return;

        if ( this .useVBOs ) {
            gl .glBindBuffer( shape .getVerticesVBO() );
            gl .glEnableVertexAttribArray( mPositionParam );
            gl .glVertexAttribDivisor( mPositionParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "mPositionParam");

            gl .glBindBuffer( shape .getNormalsVBO() );
            gl .glEnableVertexAttribArray( normalParam );
            gl .glVertexAttribDivisor( normalParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( normalParam, COORDS_PER_VERTEX, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "normalParam");

            gl .glBindBuffer( shape .getPositionsVBO() );
            gl .glEnableVertexAttribArray( instanceData );
            gl .glVertexAttribDivisor( instanceData, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( instanceData, 4, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "instanceData");

            gl .glBindBuffer( shape .getColorsVBO() );
            gl .glEnableVertexAttribArray( mColorParam );
            gl .glVertexAttribDivisor( mColorParam, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( mColorParam, 4, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "mColorParam");
        }
        else {
            gl .glEnableVertexAttribArray( mPositionParam );
            gl .glVertexAttribDivisor( mPositionParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( mPositionParam, COORDS_PER_VERTEX, false, 0, shape .getVertices() );
            OpenGlUtilities.checkGLError( gl, "mPositionParam");

            gl .glEnableVertexAttribArray( normalParam );
            gl .glVertexAttribDivisor( normalParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( normalParam, COORDS_PER_VERTEX, false, 0, shape .getNormals() );
            OpenGlUtilities.checkGLError( gl, "normalParam");

            gl .glEnableVertexAttribArray( instanceData );
            gl .glVertexAttribDivisor( instanceData, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( instanceData, 4, false, 0, shape .getPositions() );
            OpenGlUtilities.checkGLError( gl, "instanceData");

            gl .glEnableVertexAttribArray( mColorParam );
            gl .glVertexAttribDivisor( mColorParam, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( mColorParam, 4, false, 0, shape .getColors() );
            OpenGlUtilities.checkGLError( gl, "mColorParam");
        }

        gl.glDrawTrianglesInstanced( 0, shape .getVertexCount(), instanceCount );
        OpenGlUtilities.checkGLError( gl, "Drawing a shape");
    }

    @Override
    public int storeBuffer( FloatBuffer clientBuffer, int oldId )
    {
        return OpenGlUtilities .storeBuffer( this .gl, clientBuffer, oldId );
    }
}
