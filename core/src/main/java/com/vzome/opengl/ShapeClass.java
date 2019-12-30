
package com.vzome.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
* Created by vorth on 7/28/14.
*/
public class ShapeClass
{
    private FloatBuffer mInstancePositions;
    private FloatBuffer mVertices;
    private FloatBuffer mNormals = null;
    int positionsVBO = -1, normalsVBO = -1, verticesVBO = -1;
    private boolean usesVBOs = false;
    private float[] mColor;
    private int instanceCount = 0;
    private int vertexCount;

    public static final int COORDS_PER_VERTEX = 3;

    public void setBuffers( int buffer, int buffer1, int buffer2 )
    {
        this .verticesVBO = buffer;
        this .normalsVBO = buffer1;
        this .positionsVBO = buffer2;
        this .mVertices = null;
        this .mInstancePositions = null;
        this .mNormals = null;
        this .usesVBOs = true;
    }

    public void setShapeData( float[] verticesArray, float[] normalsArray, float[] offsets, float[] color )
    {
        mColor = color;
        vertexCount = verticesArray.length / COORDS_PER_VERTEX;

        ByteBuffer bbVertices = ByteBuffer.allocateDirect( verticesArray.length * 4 );
        bbVertices.order(ByteOrder.nativeOrder());
        mVertices = bbVertices.asFloatBuffer();
        mVertices.put( verticesArray );
        mVertices.position(0);

        if ( normalsArray != null ) {
            ByteBuffer bbNormals = ByteBuffer.allocateDirect( normalsArray.length * 4 );
            bbNormals.order(ByteOrder.nativeOrder());
            mNormals = bbNormals.asFloatBuffer();
            mNormals.put(normalsArray);
            mNormals.position(0);
        }

        if ( offsets != null )
            replacePositions( offsets );
    }

    public FloatBuffer getVertices()
    {
        return this .mVertices;
    }

    public FloatBuffer getNormals()
    {
        return this .mNormals;
    }

    public FloatBuffer getPositions()
    {
        return this .mInstancePositions;
    }

    public float[] getColor()
    {
        return mColor;
    }

    public int getVertexCount()
    {
        return this .vertexCount;
    }

    public int getInstanceCount()
    {
        return this .instanceCount;
    }

    public boolean usesVBOs() { return this .usesVBOs; }

    public int getVerticesVBO() { return this .verticesVBO; }

    public int getNormalsVBO() { return this .normalsVBO; }

    public int getPositionsVBO() { return this .positionsVBO; }

    public void replacePositions( float[] offsets )
    {
        ByteBuffer bbOffsets = ByteBuffer.allocateDirect( offsets.length * 4 );
        bbOffsets.order(ByteOrder.nativeOrder());
        mInstancePositions = bbOffsets.asFloatBuffer();
        mInstancePositions.put( offsets );
        mInstancePositions.position(0);
        instanceCount = offsets.length / 4;
    }
}
