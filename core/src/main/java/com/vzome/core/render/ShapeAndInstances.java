
package com.vzome.core.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vzome.opengl.InstancedGeometry;

/**
* Should this be merged with RenderedModel?
*/
public class ShapeAndInstances implements InstancedGeometry
{
    private FloatBuffer mInstancePositions;
    private FloatBuffer mVertices;
    private FloatBuffer mNormals = null;
    int positionsVBO = -1, normalsVBO = -1, verticesVBO = -1;
    private boolean usesVBOs = false;
    private float[] mColor;
    private int instanceCount = 0;
    private int vertexCount;
    
    private Set<RenderedManifestation> instances = new HashSet<>();  // needed when we remove something


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

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getVertices()
     */
    @Override
    public FloatBuffer getVertices()
    {
        return this .mVertices;
    }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getNormals()
     */
    @Override
    public FloatBuffer getNormals()
    {
        return this .mNormals;
    }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getPositions()
     */
    @Override
    public FloatBuffer getPositions()
    {
        return this .mInstancePositions;
    }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getColor()
     */
    @Override
    public float[] getColor()
    {
        return mColor;
    }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getVertexCount()
     */
    @Override
    public int getVertexCount()
    {
        return this .vertexCount;
    }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getInstanceCount()
     */
    @Override
    public int getInstanceCount()
    {
        return this .instanceCount;
    }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#usesVBOs()
     */
    @Override
    public boolean usesVBOs() { return this .usesVBOs; }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getVerticesVBO()
     */
    @Override
    public int getVerticesVBO() { return this .verticesVBO; }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getNormalsVBO()
     */
    @Override
    public int getNormalsVBO() { return this .normalsVBO; }

    /* (non-Javadoc)
     * @see com.vzome.opengl.InstancedGeometry#getPositionsVBO()
     */
    @Override
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

    public void addInstance( RenderedManifestation rm )
    {
        this .instances .add( rm );
    }

    public void removeInstance( RenderedManifestation rm )
    {
        this .instances .remove( rm );
    }

    public Collection<RenderedManifestation> getInstances()
    {
        return this .instances;
    }
}
