package com.vzome.jogl;

import android.util.Log;

import com.vzome.api.Ball;
import com.vzome.api.Strut;
import com.vzome.api.Vector;

import org.vorthmann.zome.algebra.AlgebraicField;
import org.vorthmann.zome.math.Polyhedron;
import org.vorthmann.zome.math.RealVector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* Created by vorth on 7/28/14.
*/
class ShapeClass
{
    private static final float MODEL_SCALE_FACTOR = 0.05f;

    private FloatBuffer mInstancePositions;
    private FloatBuffer mVertices;
    private FloatBuffer mNormals = null;
    private float[] mColor;
    private int instanceCount = 0;
    private int vertexCount;

    private static final String TAG = "ShapeClass";

    private static final int COORDS_PER_VERTEX = 3;

    public static class Config
    {
        public Set instances = new HashSet();
        public float[] color;
    }

    public static ShapeClass create( Polyhedron shape, Set parts, float[] color )
    {
        List vertices = new ArrayList();
        List normals = new ArrayList();
        AlgebraicField field = shape.getField();
        List vertexList = shape.getVertexList();
        Set<Polyhedron.Face> faces = shape.getFaceSet();
        for (Polyhedron.Face face : faces) {
            int[] normal = face.getNormal();
            RealVector rn = field.getRealVector(field.negate(normal)).normalize();
            int count = face.size();
            int[] vertex = (int[]) vertexList.get(face.getVertex(0));
            RealVector rv0 = field.getRealVector(vertex);
            vertex = (int[]) vertexList.get(face.getVertex(1));
            RealVector rvPrev = field.getRealVector(vertex);
            for (int i = 2; i < count; i++) {
                vertex = (int[]) vertexList.get(face.getVertex(i));
                RealVector rv = field.getRealVector(vertex);
                vertices.add(rv0);
                normals.add(rn);
                vertices.add(rvPrev);
                normals.add(rn);
                vertices.add(rv);
                normals.add(rn);
                rvPrev = rv;
            }
        }

        float[] verticesArray = new float[COORDS_PER_VERTEX * vertices.size()];
        float[] normalsArray = new float[COORDS_PER_VERTEX * normals.size()];  // same size!
        for (int i = 0; i < vertices.size(); i++) {
            RealVector vector = (RealVector) vertices.get(i);
            vector = vector .scale( MODEL_SCALE_FACTOR );
            verticesArray[i * COORDS_PER_VERTEX] = (float) vector.x;
            verticesArray[i * COORDS_PER_VERTEX + 1] = (float) vector.y;
            verticesArray[i * COORDS_PER_VERTEX + 2] = (float) vector.z;
            vector = (RealVector) normals.get(i);
            normalsArray[i * COORDS_PER_VERTEX] = (float) vector.x;
            normalsArray[i * COORDS_PER_VERTEX + 1] = (float) vector.y;
            normalsArray[i * COORDS_PER_VERTEX + 2] = (float) vector.z;
        }

        float[] offsets = new float[4 * parts.size()];
        int i = 0;
        for( Object part : parts ) {
            Vector vector = null;
            int zone = 0;
            if ( part instanceof Ball) {
                vector = ((Ball) part).location();
                Log.v(TAG, "  ball center  : " + vector);
            }
            else {
                vector = ((Strut) part).location();
                zone = ((Strut) part).getZone();
                Log.v( TAG, "  strut center  : " + vector + ", zone : " + zone );
            }
            offsets[i * 4 + 0] = MODEL_SCALE_FACTOR * (float) vector.getX().value();
            offsets[i * 4 + 1] = MODEL_SCALE_FACTOR * (float) vector.getY().value();
            offsets[i * 4 + 2] = MODEL_SCALE_FACTOR * (float) vector.getZ().value();
            offsets[i * 4 + 3] = (float) zone;
            ++i;
        }
        return new ShapeClass( verticesArray, normalsArray, offsets, color );
    }

    public static ShapeClass create( Object[] parts, float[] color )
    {
        float[] verticesArray = new float[COORDS_PER_VERTEX * 2 * parts.length];
        for (int i = 0; i < parts.length; i++) {
            Vector vector = ((Strut) parts[i]).location();
            verticesArray[i * 2 * COORDS_PER_VERTEX + 0] = (float) vector.getX().value()/2;  // TODO: figure out why we need "/2"
            verticesArray[i * 2 * COORDS_PER_VERTEX + 1] = (float) vector.getY().value()/2;
            verticesArray[i * 2 * COORDS_PER_VERTEX + 2] = (float) vector.getZ().value()/2;
            Vector offset = ((Strut) parts[i]).offset();
            vector = vector .plus( offset );
            verticesArray[i * 2 * COORDS_PER_VERTEX + 3] = (float) vector.getX().value()/2;
            verticesArray[i * 2 * COORDS_PER_VERTEX + 4] = (float) vector.getY().value()/2;
            verticesArray[i * 2 * COORDS_PER_VERTEX + 5] = (float) vector.getZ().value()/2;
        }
        return new ShapeClass( verticesArray, null, null, color );
    }

    public ShapeClass(float[] verticesArray, float[] normalsArray, float[] offsets, float[] color)
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
        {
            ByteBuffer bbOffsets = ByteBuffer.allocateDirect( offsets.length * 4 );
            bbOffsets.order(ByteOrder.nativeOrder());
            mInstancePositions = bbOffsets.asFloatBuffer();
            mInstancePositions.put( offsets );
            mInstancePositions.position(0);
            instanceCount = offsets.length / 4;

            Log.i( TAG, instanceCount + " instances! %%%%%%%%%%%%%%%%%%        %%%%%%%%%%%%     %%%%%%%%" );
        }
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
}
