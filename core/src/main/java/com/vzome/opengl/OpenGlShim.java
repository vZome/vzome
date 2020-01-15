package com.vzome.opengl;

import java.nio.FloatBuffer;

public interface OpenGlShim {
    
    String getGLSLVersionString();

	int glCreateProgram();

	int glCreateVertexShader();

	int glCreateFragmentShader();

	void glAttachShader(int mGlProgram, int shader);

	void glLinkProgram(int mGlProgram);

	void glShaderSource(int shader, String shaderSource);

	void glCompileShader(int shader);

	void glGetShaderStatus(int shader, int[] compileStatus, int i);

	String glGetShaderInfoLog(int shader);

	void glDeleteShader(int shader);

	int glGetError();

	int glGetUniformLocation(int mGlProgram, String string);

	int glGetAttribLocation(int mGlProgram, String string);

	void glUseProgram(int mGlProgram);

	void glUniformMatrix4fv(int mModelViewProjectionParam, int i, boolean b, float[] modelViewProjection, int j);

	void glUniform3f(int mLightPosParam, float f, float g, float h);

    void glUniform1f( int mColorParam, float f );

    void glUniform4f(int mColorParam, float f, float g, float h, float i);

    void glUniform1i(int i, int v);

	void glEnableVertexAttribArray(int mPositionParam);

	void glBindBuffer( int vbo);
	
	void glEnableDepth();
	
	void glClear( float r, float g, float b, float alpha );

	void glVertexAttribDivisor(int mPositionParam, int i);

	void glVertexAttribPointer(int mPositionParam, int coordsPerVertex, boolean b, int i, int j);

	void glVertexAttribPointer(int mPositionParam, int coordsPerVertex, boolean b, int i, FloatBuffer vertices);

    void glDrawTrianglesInstanced( int i, int vertexCount, int instanceCount);

    void glLineWidth( float width );

    void glDrawLinesInstanced( int i, int vertexCount, int instanceCount);

	void glDrawLines( int i, int vertexCount);

	void glDrawTriangles( int i, int vertexCount);

	void glGenBuffers(int numBuffers, int[] buffers, int i);

	void glBufferData( int i, FloatBuffer clientBuffer );

    void glDeleteBuffer( int oldId );

	void multiplyMM( float[] view, float[] eyeTransform, float[] camera );

	void invertM(float[] viewInverse, float[] view);

	void transposeM(float[] worldInverseTranspose, float[] worldInverse);

	void multiplyMV(float[] lightPosInEyeSpace, float[] view, float[] mlightposinworldspace );

    void glPolygonOffset(float f, float g);
}
