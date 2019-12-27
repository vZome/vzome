package com.vzome.opengl;

public class FancyRenderer extends Renderer
{
    private int worldInverseTranspose;
    private int viewInverse;
    private int mLightPosParam;
    private int normalParam;

    public FancyRenderer( OpenGlShim gl )
    {
        super( gl );
    }

    private static final String VERTEX_SHADER =

            "uniform mat4 u_Orientations[60];\n" + 
                    // "uniform mat4 viewInverse;\n" + 
                    "uniform mat4 " + MODEL_VIEW_PROJECTION + ";\n" + 
                    "uniform mat4 worldInverseTranspose;\n" + 
                    "uniform vec4 " + COLOR + ";\n" + 
                    "uniform vec3 lightWorldPos;\n" + 

			"attribute vec4 " + A_POSITION + ";\n" + 
			"attribute vec3 a_Normal;\n" + 
			"attribute vec4 a_InstanceData;\n" + 

			"varying vec3 v_normal;\n" + 
			"varying vec3 v_surfaceToLight;\n" + 
			"varying vec3 v_surfaceToView;\n" + 
			"varying vec4 v_color;\n" + 

			"void main()\n" + 
			"{\n" + 
			"    // unpack a_InstanceData\n" + 
			"    float orientationAsFloat = a_InstanceData.w;\n" + 
			"    vec4 location = vec4( a_InstanceData.xyz, 0.0 );\n" + 

			"    int orientation = max( 0, min( 59, int(orientationAsFloat) ) );\n" + 
			"    vec4 oriented = u_Orientations[ orientation ] * " + A_POSITION + ";\n" + 
			"    vec4 orientedNormal = u_Orientations[ orientation ] * vec4( a_Normal, 0.0 );\n" + 
			"    vec4 wp = oriented + location;\n" + 
			"    gl_Position = " + MODEL_VIEW_PROJECTION + " * wp;\n" + 

			"    v_normal = (worldInverseTranspose * orientedNormal).xyz;\n" + 
			"    v_color = " + COLOR + ";\n" + 
			"    v_surfaceToLight = lightWorldPos - wp.xyz;\n" + 
			"    vec4 colSelector = vec4( 0, 0, 0, 1 );\n" + 
			//"    vec4 col4 = viewInverse * colSelector;\n" + 
			"    v_surfaceToView = colSelector.xyz - wp.xyz;\n" + 
			"}";

    private static final String FRAGMENT_SHADER =

            "precision highp float;\n" + 

			"varying vec3 v_normal;\n" + 
			"varying vec3 v_surfaceToLight;\n" + 
			"varying vec3 v_surfaceToView;\n" + 
			"varying vec4 v_color;\n" + 

			"vec4 specular = vec4(1,1,1,1);\n" + 
			"float shininess = 50.0;\n" + 
			"float specularFactor = 0.2;\n" + 

			"vec4 lit( float l ,float h, float m )\n" + 
			"{\n" + 
			"    return vec4( 1.0, max(l, 0.0), (l > 0.0) ? pow(max(0.0, h), m) : 0.0, 1.0 );\n" + 
			"}\n" + 

			"void main()\n" + 
			"{\n" + 
			"    vec3 normal = normalize( v_normal );\n" + 
			"    vec3 surfaceToLight = normalize( v_surfaceToLight );\n" + 
			"    vec3 surfaceToView = normalize( v_surfaceToView );\n" + 
			"    vec3 halfVector = normalize( surfaceToLight + surfaceToView );\n" + 
			"    vec4 litR = lit( dot( normal, surfaceToLight ), dot( normal, halfVector ), shininess );\n" + 
			"    gl_FragColor = vec4( ( vec4(1,1,1,1) * (v_color * litR.y + specular * litR.z * specularFactor) ).rgb, 1.0 );\n" + 
			"}";

    protected void getUniforms( OpenGlShim gl )
    {
        mLightPosParam = gl.glGetUniformLocation(mGlProgram, "lightWorldPos");
        worldInverseTranspose = gl.glGetUniformLocation( mGlProgram, "worldInverseTranspose" );
        viewInverse = gl.glGetUniformLocation( mGlProgram, "viewInverse" );

        normalParam = gl.glGetAttribLocation( mGlProgram, "a_Normal" );
        checkGLError( gl, "a_Normal");
    }
    
    public void setUniforms( OpenGlShim gl, float[] model, float[] camera, float[] eyeTransform, float[] eyePerspective, float[][] fixedOrientations )
    {
        super .setUniforms( gl, model, camera, eyeTransform, eyePerspective, fixedOrientations );

        float[] worldInverse = new float[16];
        gl .invertM( worldInverse, model );

        float[] worldInverseTranspose = new float[16];
        gl .transposeM( worldInverseTranspose, worldInverse );

        // Set the position of the light
        float[] lightPosInEyeSpace = new float[4];
        gl .multiplyMV( lightPosInEyeSpace, 0, view, 0, mLightPosInWorldSpace, 0 );

        gl.glUniform3f( mLightPosParam, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2] );
        if ( this .isNiceLighting )
        {
            gl.glUniformMatrix4fv( this.viewInverse, 1, false, model, 0);
            gl.glUniformMatrix4fv( this.worldInverseTranspose, 1, false, worldInverseTranspose, 0);
        }
        else {
            gl.glUniformMatrix4fv( mModelParam, 1, false, model, 0);
            gl.glUniformMatrix4fv( mModelViewParam, 1, false, modelView, 0);
        }
    }

    public void setUniforms( OpenGlShim gl, float[] model, float[] camera, float[] eyeTransform, float[] eyePerspective, float[][] fixedOrientations )
    {
        super .setUniforms( gl, model, camera, eyeTransform, eyePerspective, fixedOrientations );

        for ( int i = 0; i < fixedOrientations.length; i++ )
            gl.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, fixedOrientations[ i ], 0 );
        checkGLError( gl, "mOrientationsParam");
    }
}
