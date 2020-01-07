
package com.vzome.core.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.opengl.InstancedGeometry;

/**
* Should this be merged with RenderedModel?
*/
public class ShapeAndInstances implements InstancedGeometry
{
    private FloatBuffer mVertices, mNormals, lineVertexBuffer;
    int lineVerticesVBO = -1, verticesVBO = -1, normalsVBO = -1, positionsVBO = -1, colorsVBO = -1;
    private int vertexCount, lineVertexCount;
    private final float globalScale;
    private final Polyhedron shape;

    private boolean hasChanges;
    
    // Must use a Set here, or the performance is awful.  However, RM.hashCode()
    //   must use a GUID, or else the preview strut removal eats real struts.
    private Collection<RenderedManifestation> instances = new HashSet<>();

    public static final int COORDS_PER_VERTEX = 3;

    public ShapeAndInstances( Polyhedron shape, Embedding embedding, float globalScale )
    {
        this .shape = shape;
        this .globalScale = globalScale;

        List<RealVector> vertices = new ArrayList<>();
        List<RealVector> normals = new ArrayList<>();
        List<RealVector> lineVertices = new ArrayList<>();
        List<AlgebraicVector> vertexList = shape .getVertexList();
        Set<Polyhedron.Face> faces = shape .getFaceSet();
        for ( Polyhedron.Face face : faces ) {
            int count = face.size();
            AlgebraicVector vertex = vertexList .get( face .getVertex(0) );
            RealVector rv0 = embedding .embedInR3( vertex );
            vertex = vertexList.get( face .getVertex(1) );
            RealVector rv1 = embedding .embedInR3( vertex );
            vertex = vertexList.get( face .getVertex(2) );
            RealVector rvLast = embedding .embedInR3( vertex );
            RealVector normal = rv0.minus(rv1) .cross( rvLast.minus(rv0) ) .normalize();
            vertices .add( rv0 );
            normals .add( normal );
            vertices .add( rv1 );
            normals .add( normal );
            vertices .add( rvLast );
            normals .add( normal );
            lineVertices .add( rv0 );
            lineVertices .add( rv1 );
            lineVertices .add( rv1 );
            lineVertices .add( rvLast );
            for (int i = 3; i < count; i++) {
                vertex = vertexList .get( face .getVertex(i) );
                RealVector rv = embedding .embedInR3( vertex );
                vertices .add( rv0 );
                vertices .add( rvLast );
                vertices .add( rv );
                normals .add( normal );
                normals .add( normal );
                normals .add( normal );
                lineVertices .add( rvLast );
                lineVertices .add( rv );
                rvLast = rv;
            }
            lineVertices .add( rvLast );
            lineVertices .add( rv0 );
        }

        this .vertexCount = vertices .size();
        float[] verticesArray = new float[ ShapeAndInstances.COORDS_PER_VERTEX * this .vertexCount ];
        float[] normalsArray = new float[ ShapeAndInstances.COORDS_PER_VERTEX * this .vertexCount ];  // same size!
        for (int i = 0; i < this .vertexCount; i++) {
            RealVector vector = vertices.get(i);
            vector = vector .scale( globalScale );
            verticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX] = (float) vector.x;
            verticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 1] = (float) vector.y;
            verticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 2] = (float) vector.z;
            vector = normals.get(i);
            normalsArray[i * ShapeAndInstances.COORDS_PER_VERTEX] = (float) vector.x;
            normalsArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 1] = (float) vector.y;
            normalsArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 2] = (float) vector.z;
        }

        this .lineVertexCount = lineVertices .size();
        float[] lineVerticesArray = new float[ ShapeAndInstances.COORDS_PER_VERTEX * this .lineVertexCount ];
        for (int i = 0; i < this .lineVertexCount; i++) {
            RealVector vector = lineVertices .get(i);
            vector = vector .scale( globalScale );
            lineVerticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX] = (float) vector.x;
            lineVerticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 1] = (float) vector.y;
            lineVerticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 2] = (float) vector.z;
        }

        this .mVertices = ByteBuffer.allocateDirect( verticesArray.length * 4 ) .order(ByteOrder.nativeOrder())
                .asFloatBuffer() .put( verticesArray );
        this .mVertices .position(0);

        this .mNormals = ByteBuffer.allocateDirect( normalsArray.length * 4 ) .order(ByteOrder.nativeOrder())
                .asFloatBuffer() .put( normalsArray );
        this .mNormals .position(0);

        this .lineVertexBuffer = ByteBuffer.allocateDirect( lineVerticesArray.length * 4 ) .order(ByteOrder.nativeOrder())
                .asFloatBuffer() .put( lineVerticesArray );
        this .lineVertexBuffer .position(0);
    }

    public ShapeAndInstances( Polyhedron shape, Embedding embedding, Collection<RenderedManifestation> instances, float globalScale )
    {
        this( shape, embedding, globalScale );
        this .instances .addAll( instances );  // we are changing shapes
    }

    protected Polyhedron getShape()
    {
        return shape;
    }
    
    @Override
    public int getVertexCount()
    {
        return this .vertexCount;
    }
    
    @Override
    public int getLineVertexCount()
    {
        return this .lineVertexCount;
    }
    
    @Override
    public int getLineVerticesVBO() { return this .lineVerticesVBO; }
    
    @Override
    public int getVerticesVBO() { return this .verticesVBO; }

    @Override
    public int getNormalsVBO() { return this .normalsVBO; }

    @Override
    public int getPositionsVBO() { return this .positionsVBO; }

    @Override
    public int getColorsVBO() {  return this .colorsVBO;  }

    public void rebuildInstanceData()
    {
        this .hasChanges = true;
    }

    public int prepareToRender( BufferStorage storage )
    {
        if ( this .verticesVBO == -1 ) {
            this .verticesVBO = storage .storeBuffer( this .mVertices );
            this .normalsVBO = storage .storeBuffer( this .mNormals );
            this .lineVerticesVBO = storage .storeBuffer( this .lineVertexBuffer );
        }
        if ( this .hasChanges ) {

            float[] offsets = new float[4 * instances.size()];
            float[] colors = new float[4 * instances.size()];
            int i = 0;
            for( RenderedManifestation part : instances ) {
                RealVector vector = part .getLocation();
                int zone = part .getStrutZone();
                float orientationAndGlow = part .getGlow() + (float) zone;
                offsets[i * 4 + 0] = globalScale * (float) vector .x;
                offsets[i * 4 + 1] = globalScale * (float) vector .y;
                offsets[i * 4 + 2] = globalScale * (float) vector .z;
                offsets[i * 4 + 3] = orientationAndGlow;

                float[] rgba = new float[4];
                Color color = part .getColor();
                if ( color == null )
                    color = Color.WHITE;
                color .getRGBColorComponents( rgba );
                colors[i * 4 + 0] = rgba[ 0 ];
                colors[i * 4 + 1] = rgba[ 1 ];
                colors[i * 4 + 2] = rgba[ 2 ];
                colors[i * 4 + 3] = rgba[ 3 ];

                ++i;
            }

            ByteBuffer bytes = ByteBuffer .allocateDirect( offsets.length * 4 );
            bytes .order( ByteOrder.nativeOrder() );
            FloatBuffer buffer = bytes .asFloatBuffer();
            buffer .put( offsets );
            buffer .position( 0 );
            //        this .mInstancePositions = buffer;
            this .positionsVBO = storage .storeBuffer( buffer );

            bytes = ByteBuffer .allocateDirect( colors.length * 4 );
            bytes .order( ByteOrder.nativeOrder() );
            buffer = bytes .asFloatBuffer();
            buffer .put( colors );
            buffer .position( 0 );
            //        this .mInstanceColors = buffer;
            this .colorsVBO = storage .storeBuffer( buffer );

            this .hasChanges = false;
        }
        return this .instances .size();
    }

    public void addInstance( RenderedManifestation rm )
    {
        this .instances .add( rm );
        this .hasChanges = true;
    }

    public void removeInstance( RenderedManifestation rm )
    {
        this .instances .remove( rm );
        this .hasChanges = true;
    }

    public Collection<RenderedManifestation> getInstances()
    {
        return this .instances;
    }

    public void removeInstances()
    {
        this .instances .clear();
        this .hasChanges = true;
    }

    public RenderedManifestation pick( RenderedManifestation.Intersector intersector )
    {
        for ( RenderedManifestation rm : instances ) {
            if ( rm .isHit( intersector ) )
                return rm;
        }
        return null;
    }
}
