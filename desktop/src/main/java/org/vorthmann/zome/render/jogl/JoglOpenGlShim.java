package org.vorthmann.zome.render.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.vzome.opengl.OpenGlShim;

import java.nio.FloatBuffer;

/**
 * Created by vorth on 2019-12-26, initially copying from
 * vzome-cardboard AndroidOpenGlShim.  That class, and everything
 * in com.vzome.opengl, is clearly set up for modern OpenGL,
 * requiring VBOs and custom shaders.  JOGL may not be well-prepared
 * for this, being a bit outdated now.
 * 
 * See https://www.khronos.org/opengl/wiki/Legacy_OpenGL.
 */
public class JoglOpenGlShim implements OpenGlShim
{
    private final GL2 gl2;

    public JoglOpenGlShim( GL2 gl2 )
    {
        this.gl2 = gl2;
    }

    @Override
    public int glCreateProgram() {
        return gl2.glCreateProgram();
    }

    @Override
    public void glAttachShader(int i, int i2) {
        gl2.glAttachShader( i, i2 );
    }

    @Override
    public void glLinkProgram(int i) {
        gl2.glLinkProgram(i);
    }

    @Override
    public int glCreateVertexShader() {
        return gl2.glCreateShader( GL2ES2.GL_VERTEX_SHADER );
    }

    @Override
    public int glCreateFragmentShader() {
        return gl2.glCreateShader( GL2ES2.GL_FRAGMENT_SHADER );
    }

    @Override
    public void glShaderSource( int i, String s ) {
//        gl2.glShaderSource( i, s );
    }

    @Override
    public void glCompileShader(int i) {
        gl2.glCompileShader(i);
    }

    @Override
    public void glGetShaderStatus(int i, int[] ints, int i2) {
        gl2.glGetShaderiv(i, GL2ES2.GL_COMPILE_STATUS, ints, i2);
    }

    @Override
    public String glGetShaderInfoLog(int i) {
        return null; // gl2.glGetShaderInfoLog( i );
    }

    @Override
    public void glDeleteShader(int i) {
        gl2.glDeleteShader( i );
    }

    @Override
    public int glGetError() {
        return gl2.glGetError();
    }

    @Override
    public int glGetUniformLocation(int i, String s) {
        return gl2.glGetUniformLocation( i, s );
    }

    @Override
    public int glGetAttribLocation(int i, String s) {
        return gl2.glGetAttribLocation( i, s );
    }

    @Override
    public void glUseProgram(int i) {
        gl2.glUseProgram( i );
    }

    @Override
    public void glUniformMatrix4fv(int i, int i2, boolean b, float[] floats, int i3) {
        gl2.glUniformMatrix4fv( i, i2, b, floats, i3 );
    }

    @Override
    public void glUniform3f(int i, float v, float v2, float v3) {
        gl2.glUniform3f( i, v, v2, v3 );
    }

    @Override
    public void glUniform4f(int i, float v, float v2, float v3, float v4) {
        gl2.glUniform4f( i, v, v2, v3, v4 );
    }

    @Override
    public void glEnableVertexAttribArray(int i) {
        gl2.glEnableVertexAttribArray( i );
    }

    @Override
    public void glBindBuffer( int i) {
        gl2.glBindBuffer( GL.GL_ARRAY_BUFFER, i );
    }

    @Override
    public void glVertexAttribDivisor(int i, int i2) {
        gl2.glVertexAttribDivisor( i, i2 );
    }

    @Override
    public void glVertexAttribPointer(int i, int i2, boolean b, int i3, int i4) {
        gl2.glVertexAttribPointer( i, i2, GL.GL_FLOAT, b, i3, i4 );
    }

    @Override
    public void glVertexAttribPointer(int i, int i2, boolean b, int i3, FloatBuffer floatBuffer) {
        gl2.glVertexAttribPointer( i, i2, GL.GL_FLOAT, b, i3, floatBuffer );
    }

    @Override
    public void glDrawArraysInstanced( int i, int i2, int i3) {
        gl2.glDrawArraysInstanced( GL.GL_TRIANGLES, i, i2, i3 );
    }

    @Override
    public void glDrawTriangles( int i, int i2) {
        gl2.glDrawArrays( GL.GL_TRIANGLES, i, i2 );
    }

    @Override
    public void glDrawLines( int i, int i2) {
        gl2.glDrawArrays( GL.GL_LINES, i, i2 );
    }

    @Override
    public void glGenBuffers(int i, int[] ints, int i2) {
        gl2.glGenBuffers( i, ints, i2 );
    }

    @Override
    public void glBufferData( int i, FloatBuffer floatBuffer ) {
        gl2.glBufferData( GL.GL_ARRAY_BUFFER, i, floatBuffer, GL.GL_STATIC_DRAW );
    }

    @Override
    public void multiplyMM(float[] floats, float[] floats2, float[] floats3) {
//        Matrix.multiplyMM( floats, 0, floats2, 0, floats3, 0 );
    }

    @Override
    public void invertM(float[] floats, float[] floats2) {
//        Matrix.invertM( floats, 0, floats2, 0 );
    }

    @Override
    public void transposeM(float[] floats, float[] floats2) {
//        Matrix.transposeM( floats, 0, floats2, 0 );
    }

    @Override
    public void multiplyMV(float[] floats, float[] floats2, float[] floats3 ) {
//        Matrix.multiplyMV( floats, 0, floats2, 0, floats3, 0 );
    }
}
