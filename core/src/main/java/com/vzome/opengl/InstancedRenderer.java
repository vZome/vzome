package com.vzome.opengl;

public class InstancedRenderer extends LightingRenderer
{
    private int instanceData;
    private int[] mOrientationsParam;

	public InstancedRenderer( OpenGlShim gl )
	{
		super( gl );
		this .mOrientationsParam = new int[60];
	}

	protected String getVertexShaderSource()
	{
		return  "uniform mat4 " + MODEL_VIEW_PROJECTION + ";\n" + 
				"uniform mat4 u_MVMatrix;\n" + 
				"uniform mat4 u_Model;\n" + 
				"uniform vec3 u_LightPos;\n" + 
				"uniform vec4 " + COLOR + ";\n" + 
				"uniform mat4 u_Orientations[60];\n" + 

				"attribute vec4 " + A_POSITION + ";\n" + 
				"attribute vec3 a_Normal;\n" + 
				"attribute vec4 a_InstanceData;\n" + 

				"varying vec4 v_Color;\n" + 

				"void main()\n" + 
				"{\n" + 
				"   // unpack a_InstanceData\n" + 
				"   float orientationAsFloat = a_InstanceData.w;\n" + 
				"   vec4 location = vec4( a_InstanceData.xyz, 1.0 );\n" + 

				"   float orientation = clamp( orientationAsFloat, 0.0, 59.0 );\n" + 
				"   vec4 oriented = ( u_Orientations[ int(orientation) ] * " + A_POSITION + " );\n" + 
				"   vec4 normal = ( u_Orientations[ int(orientation) ] * vec4( a_Normal, 0.0 ) );\n" + 
				"   vec4 pos = oriented + location;\n" + 
				"   gl_Position = " + MODEL_VIEW_PROJECTION + " * pos;\n" + 

				"   // original lighting, using a point source\n" + 
				"   vec3 modelViewVertex = vec3(u_MVMatrix * pos);\n" + 
				"   vec3 modelViewNormal = vec3( u_MVMatrix * vec4( a_Normal, 0.0 ) );\n" + 
				"   float distance = length( u_LightPos - modelViewVertex );\n" + 
				"   vec3 lightVector = normalize( u_LightPos - modelViewVertex );\n" + 
				"   float diffuse = max(dot(modelViewNormal, lightVector), 0.5 );\n" + 
				"   diffuse = diffuse * (1.0 / (1.0 + (0.0001 * distance * distance)));\n" + 
				"   diffuse = 0.3;\n" +
				"   v_Color = " + COLOR + " * diffuse;\n" + 
				"}";
	}

	protected String getFragmentShaderSource()
    {
		return  "varying vec4 v_Color;\n" + 

	    		"void main() {\n" + 
	    		"  gl_FragColor = v_Color;\n" + 
	    		"  gl_FragColor = vec4 (0.0, 1.0, 0.0, 1.0);\n" + 
	    		"}";
	}

    protected void getUniforms( OpenGlShim gl )
    {
    	super .getUniforms( gl );

		this .mOrientationsParam = new int[60];
        for ( int i = 0; i < 60; i++ ) {
            mOrientationsParam[ i ] = gl.glGetUniformLocation( mGlProgram, "u_Orientations[" + i + "]" );
        }

        instanceData = gl.glGetAttribLocation( mGlProgram, "a_InstanceData" ); // a_InstanceData will actually store (x,y,z,orientation)
    }

    public void setUniforms( OpenGlShim gl, float[] model, float[] camera, float[] eyeTransform, float[] eyePerspective, float[][] fixedOrientations )
    {
    	super .setUniforms( gl, model, camera, eyeTransform, eyePerspective, fixedOrientations );

    	for ( int i = 0; i < fixedOrientations.length; i++ )
    		gl.glUniformMatrix4fv( mOrientationsParam[ i ], 1, false, fixedOrientations[ i ], 0 );
    	checkGLError( gl, "mOrientationsParam");
    }

    protected void drawArrays( OpenGlShim gl, ShapeClass shape )
    {
        // Set the positions of the shapes
        int vbo = shape .getPositionsVBO();
        if ( shape .usesVBOs() )
        	gl.glBindBuffer( vbo );
        gl.glEnableVertexAttribArray( instanceData );
        gl.glVertexAttribDivisor( instanceData, 1);  // SV: this one is instanced
        if ( shape .usesVBOs() ) {
        	gl.glVertexAttribPointer( instanceData , 4, false, 0, 0 );
        	gl.glBindBuffer( 0);
        }
        else
        	gl.glVertexAttribPointer( instanceData, 4, false, 0, shape .getPositions() );

        gl.glDrawArraysInstanced( 0, shape .getVertexCount(), shape .getInstanceCount() );
    }
}
