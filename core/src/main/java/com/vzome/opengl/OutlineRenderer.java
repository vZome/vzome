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
    
    // uniforms for the vertex shader
    private int u_ProjMatrix;
    private int u_MVMatrix;
    private int u_Embedding;
    private int[] u_Orientations = new int[60];

    // uniforms for the fragment shader
    private int u_LineColor;
    private int u_Perspective;
    private int u_FogColor;
    private int u_Near;
    private int u_Far;
    private int u_FogMin;

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
                    "uniform mat4 u_Embedding;\n" +
                    "uniform mat4 u_Orientations[60];\n" +
                    "\n" +
                    "attribute vec4 a_Vertex;\n" + 
                    "attribute vec4 a_InstanceData;\n" + 
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
                    "   vec4 world = oriented + location;\n" + 
                    "   gl_Position = u_ProjMatrix * u_MVMatrix * u_Embedding * world;\n" + 
                    "}";
        int vertexShader = OpenGlUtilities.loadGLShader( gl, true, vertexShaderSrc );

        // See https://learnopengl.com/Advanced-OpenGL/Depth-testing
        
        String fragmentShaderSrc = version + "\n" + 
                "\n" + 
                "uniform vec4 u_LineColor;\n" +
                "uniform vec4 u_FogColor;\n" + 
                "uniform float u_Near;\n" + 
                "uniform float u_Far;\n" + 
                "uniform float u_FogMin;\n" + 
                "uniform int u_Perspective;\n" + 
                "\n" + 
                "float getFogFactor( float d )\n" + 
                "{\n" + 
                "    if ( d >= 1 ) return 1.0;\n" + 
                "    if ( d <= u_FogMin ) return 0.0;\n" + 
                "    return 1 - (1 - d) / (1 - u_FogMin);\n" + 
                "}\n" + 
                "float LinearizeDepth(float depth) \n" + 
                "{\n" + 
                "    if ( u_Perspective == 0 ) { \n" +
                "        return depth * (u_Far-u_Near) + u_Near; \n" +
                "    } else { \n" +
                "        float z = depth * 2.0 - 1.0; // back to NDC \n" + 
                "        return (2.0 * u_Near * u_Far) / (u_Far + u_Near - z * (u_Far - u_Near));    \n" + 
                "    }\n" +
                "}\n" + 
                "\n" + 
                "void main(void)\n" + 
                "{\n" + 
                "    float depth = LinearizeDepth(gl_FragCoord.z) / u_Far; // divide by far for demonstration\n" + 
                "    float fogFactor = getFogFactor( depth );\n" + 
                "\n" + 
                "    gl_FragColor = mix( u_LineColor, u_FogColor, fogFactor );\n" + 
                "    // gl_FragColor = vec4(vec3(fogFactor), 1.0); // visualize fogFactor \n" + 
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
        u_Embedding = gl.glGetUniformLocation( programId, "u_Embedding" );
        u_ProjMatrix = gl .glGetUniformLocation( programId, "u_ProjMatrix" );
        for ( int i = 0; i < u_Orientations.length; i++ )
            u_Orientations[ i ] = gl.glGetUniformLocation( programId, "u_Orientations[" + i + "]" );
        OpenGlUtilities.checkGLError( gl, "vertex shader uniforms");

        u_LineColor = gl .glGetUniformLocation( programId, "u_LineColor" );
        u_Perspective = gl.glGetUniformLocation( programId, "u_Perspective" );
        u_FogColor = gl.glGetUniformLocation( programId, "u_FogColor" );
        u_Near = gl.glGetUniformLocation( programId, "u_Near" );
        u_Far = gl.glGetUniformLocation( programId, "u_Far" );
        u_FogMin = gl.glGetUniformLocation( programId, "u_FogMin" );
        OpenGlUtilities.checkGLError( gl, "fragment shader uniforms");

        a_Vertex = gl .glGetAttribLocation( programId, "a_Vertex" );
        a_InstanceData = gl .glGetAttribLocation( programId, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation+glow)
        OpenGlUtilities.checkGLError( gl, "glGetAttribLocation");
    }

    @Override
    public void setLights( float[][] lightDirections, float[][] lightColors, float[] ambientLight )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glUniform4f( u_LineColor, 0f, 0f, 0f, 1f ); // only support black lines for now
        gl .glLineWidth( 1f );
    }

    @Override
    public void setView( float[] modelView, float[] projection, float near, float fogFront, float far, boolean perspective )
    {
        gl .glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glUniformMatrix4fv( u_MVMatrix, 1, false, modelView, 0);
        gl .glUniformMatrix4fv( u_ProjMatrix, 1, false, projection, 0 );
        gl .glUniform1f( u_FogMin, ( fogFront - near ) / ( far - near ) );
        gl .glUniform1f( u_Near, near );
        gl .glUniform1f( u_Far, far );
        gl .glUniform1i( u_Perspective, perspective? 1 : 0 );
    }
    
    @Override
    public void clear( float[] background )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        // DO NOT CLEAR, or you will overwrite solid rendering
        gl .glUniform4f( u_FogColor, background[0], background[1], background[2], background[3] );
    }
    
    @Override
    public void renderSymmetry( SymmetryRendering symmetryRendering )
    {
        gl.glUseProgram( programId );
        OpenGlUtilities.checkGLError( gl, "glUseProgram" );  // a compile / link problem seems to fail only now!

        gl .glUniformMatrix4fv( u_Embedding, 1, false, symmetryRendering .getEmbedding(), 0);

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
