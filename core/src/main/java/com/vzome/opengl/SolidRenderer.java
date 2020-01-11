package com.vzome.opengl;

import java.nio.FloatBuffer;

import com.vzome.core.render.SymmetryRendering;

/**
* Created by vorth on 7/28/14.
*/
public class SolidRenderer implements InstancedGeometry.BufferStorage, Renderer
{
    private final OpenGlShim gl;

    private int programId;

    private int a_Vertex;
    private int a_Normal;
    private int a_InstanceData;
    private int a_Color;
    
    private int u_ProjMatrix;
    private int u_MVMatrix;
    private int u_AmbientLight;
    private int u_NumLights;
    private int[] u_LightDirections = new int[10];
    private int[] u_LightColors = new int[10];
    private int[] u_Orientations = new int[60];

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
                    "attribute vec4 a_Vertex;\n" + 
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
                    "   vec4 oriented = ( u_Orientations[ orientation ] * a_Vertex );\n" + 
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

        programId = gl.glCreateProgram();
        OpenGlUtilities.checkGLError( gl, "glCreateProgram");
        gl.glAttachShader(programId, vertexShader);
        OpenGlUtilities.checkGLError( gl, "glAttachShader vertexShader");
        gl.glAttachShader(programId, fragmentShader);
        OpenGlUtilities.checkGLError( gl, "glAttachShader fragmentShader");
        gl.glLinkProgram(programId);
        OpenGlUtilities.checkGLError( gl, "glLinkProgram");

        u_MVMatrix = gl.glGetUniformLocation( programId, "u_MVMatrix" );
        u_ProjMatrix = gl.glGetUniformLocation( programId, "u_ProjMatrix" );
        OpenGlUtilities.checkGLError( gl, "projection modelview");

        u_AmbientLight = gl.glGetUniformLocation( programId, "u_AmbientLight" );
        u_NumLights = gl.glGetUniformLocation( programId, "u_NumLights" );
        for ( int i = 0; i < u_LightDirections.length; i++ ) {
            u_LightDirections[i] = gl.glGetUniformLocation(programId, "u_LightDirections[" + i + "]");
            u_LightColors[i] = gl.glGetUniformLocation(programId, "u_LightColors[" + i + "]");
        }
        OpenGlUtilities.checkGLError( gl, "light params");

        for ( int i = 0; i < u_Orientations.length; i++ )
            u_Orientations[ i ] = gl.glGetUniformLocation( programId, "u_Orientations[" + i + "]" );

        a_Vertex = gl .glGetAttribLocation( programId, "a_Vertex" );
        a_Color = gl .glGetAttribLocation( programId, "a_Color" );
        a_Normal = gl .glGetAttribLocation( programId, "a_Normal" );
        OpenGlUtilities.checkGLError( gl, "a_Normal");

        a_InstanceData = gl.glGetAttribLocation( programId, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation+glow)
    }

    public void setLights( float[][] lightDirections, float[][] lightColors, float[] ambientLight )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        // offset the solid polygons back, so that outlines will appear in front
        gl .glPolygonOffset( 1f, -1f );

        gl.glUniform3f( u_AmbientLight, ambientLight[0], ambientLight[1], ambientLight[2] );
        gl.glUniform1i( u_NumLights, lightDirections.length );
        for (int i = 0; i < lightDirections.length; i++) {
            gl.glUniform3f( u_LightDirections[i], lightDirections[i][0], lightDirections[i][1], lightDirections[i][2] );
            gl.glUniform3f( u_LightColors[i], lightColors[i][0], lightColors[i][1], lightColors[i][2] );
            OpenGlUtilities.checkGLError( gl, "light " + i );
        }
    }

    public void setView( float[] modelView, float[] projection )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl.glUniformMatrix4fv( u_MVMatrix, 1, false, modelView, 0);
        gl.glUniformMatrix4fv( u_ProjMatrix, 1, false, projection, 0 );
    }
    
    public void clear( float[] background )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glEnableDepth();
        gl .glClear( background[0], background[1], background[2], background[3] );
    }
    
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
            gl .glBindBuffer( shape .getVerticesVBO() );
            gl .glEnableVertexAttribArray( a_Vertex );
            gl .glVertexAttribDivisor( a_Vertex, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( a_Vertex, COORDS_PER_VERTEX, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "mPositionParam");

            gl .glBindBuffer( shape .getNormalsVBO() );
            gl .glEnableVertexAttribArray( a_Normal );
            gl .glVertexAttribDivisor( a_Normal, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( a_Normal, COORDS_PER_VERTEX, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "normalParam");

            gl .glBindBuffer( shape .getInstancesVBO() );
            gl .glEnableVertexAttribArray( a_InstanceData );
            gl .glVertexAttribDivisor( a_InstanceData, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( a_InstanceData, 4, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "instanceData");

            gl .glBindBuffer( shape .getColorsVBO() );
            gl .glEnableVertexAttribArray( a_Color );
            gl .glVertexAttribDivisor( a_Color, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( a_Color, 4, false, 0, 0 );
            gl .glBindBuffer( 0 );
            OpenGlUtilities.checkGLError( gl, "mColorParam");
        }
        else {
            gl .glEnableVertexAttribArray( a_Vertex );
            gl .glVertexAttribDivisor( a_Vertex, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( a_Vertex, COORDS_PER_VERTEX, false, 0, shape .getVerticesBuffer() );
            OpenGlUtilities.checkGLError( gl, "mPositionParam");

            gl .glEnableVertexAttribArray( a_Normal );
            gl .glVertexAttribDivisor( a_Normal, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
            gl .glVertexAttribPointer( a_Normal, COORDS_PER_VERTEX, false, 0, shape .getNormalsBuffer() );
            OpenGlUtilities.checkGLError( gl, "normalParam");

            gl .glEnableVertexAttribArray( a_InstanceData );
            gl .glVertexAttribDivisor( a_InstanceData, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( a_InstanceData, 4, false, 0, shape .getInstancesBuffer() );
            OpenGlUtilities.checkGLError( gl, "instanceData");

            gl .glEnableVertexAttribArray( a_Color );
            gl .glVertexAttribDivisor( a_Color, 1 );  // SV: this one is instanced
            gl .glVertexAttribPointer( a_Color, 4, false, 0, shape .getColorsBuffer() );
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
