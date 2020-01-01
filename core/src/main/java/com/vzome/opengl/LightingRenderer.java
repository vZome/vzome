package com.vzome.opengl;

public class LightingRenderer extends Renderer
{
    protected int normalParam;
    protected int mLightPosParam;
    private int mModelViewParam;
    private int mModelParam;

    public LightingRenderer( OpenGlShim gl )
    {
        super( gl );
    }

    protected String getVertexShaderSource()
    {
        return  "uniform mat4 " + MODEL_VIEW_PROJECTION + ";\n" + 
                "uniform mat4 u_MVMatrix;\n" + 
                "uniform mat4 u_Model;\n" + 
                "uniform vec3 u_LightPos;\n" + 
                "uniform vec4 " + COLOR + ";\n" + 

				"attribute vec4 " + A_POSITION + ";\n" + 
				"attribute vec3 a_Normal;\n" + 

				"varying vec4 v_Color;\n" + 
				"varying vec3 v_Grid;\n" + 

				"void main()\n" + 
				"{\n" + 
				"   vec3 modelVertex = vec3(u_Model * " + A_POSITION + ");\n" + 
				"   v_Grid = modelVertex;\n" + 

				"   vec3 modelViewVertex = vec3(u_MVMatrix * " + A_POSITION + ");\n" + 
				"   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" + 
				"   float distance = length(u_LightPos - modelViewVertex);\n" + 
				"   vec3 lightVector = normalize(u_LightPos - modelViewVertex);\n" + 
				"   float diffuse = max(dot(modelViewNormal, lightVector), 0.5   );\n" + 
				"   diffuse = diffuse * (1.0 / (1.0 + (0.00001 * distance * distance)));\n" + 
				"   v_Color = " + COLOR + " * diffuse;\n" + 
				"   gl_Position = " + MODEL_VIEW_PROJECTION + " * " + A_POSITION + ";\n" + 
				"}";
    }

    protected String getFragmentShaderSource()
    {
        return  "varying vec4 v_Color;\n" + 
                "varying vec3 v_Grid;\n" + 

				"void main() {\n" + 
				"    float depth = gl_FragCoord.z / gl_FragCoord.w; // calculate world-space distance\n" + 

				"    if ((mod(abs(v_Grid[0]), 10.0) < 0.1) || (mod(abs(v_Grid[2]), 10.0) < 0.1)) {\n" + 
				"        gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(1.0, 1.0, 1.0, 1.0)\n" + 
				"                + min(1.0, depth / 90.0) * v_Color;\n" + 
				"    } else {\n" + 
				"        gl_FragColor = v_Color;\n" + 
				"    }\n" + 
				"}";
    }

    protected void getUniforms( OpenGlShim gl )
    {
        super .getUniforms( gl );

        mLightPosParam = gl.glGetUniformLocation(mGlProgram, "u_LightPos");
        mModelViewParam = gl.glGetUniformLocation( mGlProgram, "u_MVMatrix" );
        mModelParam = gl.glGetUniformLocation( mGlProgram, "u_Model" );

        normalParam = gl.glGetAttribLocation( mGlProgram, "a_Normal" );
        checkGLError( gl, "a_Normal");
    }

    public void setUniforms( OpenGlShim gl, float[] model, float[] camera, float[] eyeTransform, float[] eyePerspective, float[][] fixedOrientations )
    {
        super .setUniforms( gl, model, camera, eyeTransform, eyePerspective, fixedOrientations );

        // Apply the eye transformation to the camera.
        float[] view = new float[16];
        gl .multiplyMM( view, eyeTransform, camera );

        // Set the position of the light
        float[] lightPosInEyeSpace = new float[4];
        gl .multiplyMV( lightPosInEyeSpace, view, mLightPosInWorldSpace );

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        float[] modelView = new float[16];
        gl .multiplyMM( modelView, view, model);

        gl.glUniform3f( mLightPosParam, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2] );
        gl.glUniformMatrix4fv( mModelParam, 1, false, model, 0);
        gl.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
    }

    protected void drawArrays( OpenGlShim gl, InstancedGeometry shape )
    {
        gl.glDrawTriangles( 0, shape .getVertexCount() );
    }

    protected void renderShape( OpenGlShim gl, InstancedGeometry shape )
    {
        int vbo = shape .getNormalsVBO();
        if ( shape .usesVBOs() )
            gl.glBindBuffer( vbo );
        gl.glEnableVertexAttribArray( normalParam );
        gl.glVertexAttribDivisor( normalParam, 0 );  // SV: this one is not instanced BUT WE HAVE TO SAY SO EXPLICITLY, OR NOTHING WORKS!
        if ( shape .usesVBOs() ) {
            gl.glVertexAttribPointer( normalParam , COORDS_PER_VERTEX, false, 0, 0 );
            gl.glBindBuffer( 0);
        }
        else
            gl.glVertexAttribPointer( normalParam, COORDS_PER_VERTEX, false, 0, shape .getNormals() );
        checkGLError( gl, "normalParam");

        this .drawArrays( gl, shape );
    }

}
