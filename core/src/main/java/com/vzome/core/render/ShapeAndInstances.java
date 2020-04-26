
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
import com.vzome.core.model.Color;
import com.vzome.opengl.InstancedGeometry;

/**
* Should this be merged with RenderedModel?
*/
public class ShapeAndInstances implements InstancedGeometry
{
    public interface Intersector
    {        
        void intersectTriangle( float[] verticesArray, int i, RenderedManifestation rm, float scale, float[] embedding );
        
        void intersectTriangle( float[] verticesArray, int i, RenderedManifestation rm, float scale, float[] embedding, float[] orientation, float[] location );
    }

    private FloatBuffer verticesBuffer, normalsBuffer, lineVerticesBuffer, positionsBuffer, colorsBuffer;
    int lineVerticesVBO = -1, verticesVBO = -1, normalsVBO = -1, positionsVBO = -1, colorsVBO = -1;
    private int vertexCount, lineVertexCount;
    private final float globalScale;
    private final Polyhedron shape;
    
    private final float[] verticesArray; // used for panel picking
    
    private boolean hasChanges;
    
    // Must use a Set here, or the performance is awful.  However, RM.hashCode()
    //   must use a GUID, or else the preview strut removal eats real struts.
    private Collection<RenderedManifestation> instances = new HashSet<>();

    public static final int COORDS_PER_VERTEX = 3;

    public ShapeAndInstances( Polyhedron shape, float globalScale )
    {
        this .shape = shape;
        this .globalScale = globalScale;

        // Note, we don't need any Embedding here.  This shape will be
        //  oriented for each instance, in the shader, and we cannot do
        //  embedding before that.
        
        List<RealVector> vertices = new ArrayList<>();
        List<RealVector> normals = new ArrayList<>();
        List<RealVector> lineVertices = new ArrayList<>();
        List<AlgebraicVector> vertexList = shape .getVertexList();
        Set<Polyhedron.Face> faces = shape .getFaceSet();
        for ( Polyhedron.Face face : faces ) {
            int count = face.size();
            AlgebraicVector vertex = vertexList .get( face .getVertex(0) );
            RealVector rv0 = vertex .toRealVector();
            vertex = vertexList.get( face .getVertex(1) );
            RealVector rv1 = vertex .toRealVector();
            vertex = vertexList.get( face .getVertex(2) );
            RealVector rvLast = vertex .toRealVector();
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
                RealVector rv = vertex .toRealVector();
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
        this .verticesArray = new float[ ShapeAndInstances.COORDS_PER_VERTEX * this .vertexCount ]; // we'll use this later for panel picking
        float[] normalsArray = new float[ ShapeAndInstances.COORDS_PER_VERTEX * this .vertexCount ];  // same size!
        for (int i = 0; i < this .vertexCount; i++) {
            RealVector vector = vertices.get(i);
            vector = vector .scale( globalScale );
            this .verticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX] = (float) vector.x;
            this .verticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 1] = (float) vector.y;
            this .verticesArray[i * ShapeAndInstances.COORDS_PER_VERTEX + 2] = (float) vector.z;
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

        this .verticesBuffer = ByteBuffer.allocateDirect( this .verticesArray .length * 4 ) .order(ByteOrder.nativeOrder())
                .asFloatBuffer() .put( this .verticesArray );
        this .verticesBuffer .position(0);

        this .normalsBuffer = ByteBuffer.allocateDirect( normalsArray.length * 4 ) .order(ByteOrder.nativeOrder())
                .asFloatBuffer() .put( normalsArray );
        this .normalsBuffer .position(0);

        this .lineVerticesBuffer = ByteBuffer.allocateDirect( lineVerticesArray.length * 4 ) .order(ByteOrder.nativeOrder())
                .asFloatBuffer() .put( lineVerticesArray );
        this .lineVerticesBuffer .position(0);
    }

    public ShapeAndInstances( Polyhedron shape, Collection<RenderedManifestation> instances, float globalScale )
    {
        this( shape, globalScale );
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
    public int getInstancesVBO() { return this .positionsVBO; }

    @Override
    public int getColorsVBO() {  return this .colorsVBO;  }

    public void rebuildInstanceData()
    {
        this .hasChanges = true;
    }

    public int prepareToRender( BufferStorage storage )
    {
        if ( storage != null && this .verticesVBO == -1 ) {
            this .verticesVBO = storage .storeBuffer( this .verticesBuffer, -1 );
            this .normalsVBO = storage .storeBuffer( this .normalsBuffer, -1 );
            this .lineVerticesVBO = storage .storeBuffer( this .lineVerticesBuffer, -1 );
        }
        if ( this .hasChanges ) {

            float[] offsets = new float[4 * instances.size()];
            float[] colors = new float[4 * instances.size()];
            int i = 0;
            for( RenderedManifestation part : instances ) {
                AlgebraicVector vector = part .getLocationAV();
                // Embedding will be handled in the shader
                RealVector rv = ( vector == null )? new RealVector() : vector .toRealVector();
                int zone = part .getStrutZone();
                if ( zone < 0 )
                    zone = 0;
                float orientationAndGlow = part .getGlow() + (float) zone;
                offsets[i * 4 + 0] = globalScale * (float) rv .x;
                offsets[i * 4 + 1] = globalScale * (float) rv .y;
                offsets[i * 4 + 2] = globalScale * (float) rv .z;
                offsets[i * 4 + 3] = orientationAndGlow;

                float[] rgba = new float[4];
                Color color = part .getColor();
                if ( color == null )
                    color = Color.WHITE;
                color .getRGBColorComponents( rgba );
                float opacity = 1f - part .getTransparency();
                if ( opacity > 0.999f )
                    opacity = rgba[ 3 ];
                colors[i * 4 + 0] = rgba[ 0 ];
                colors[i * 4 + 1] = rgba[ 1 ];
                colors[i * 4 + 2] = rgba[ 2 ];
                colors[i * 4 + 3] = opacity;

                ++i;
            }

            this .positionsBuffer = ByteBuffer.allocateDirect( offsets.length * 4 ) .order(ByteOrder.nativeOrder())
                    .asFloatBuffer() .put( offsets );
            this .positionsBuffer .position( 0 );
            if ( storage != null )
                this .positionsVBO = storage .storeBuffer( this .positionsBuffer, this .positionsVBO );

            this .colorsBuffer = ByteBuffer.allocateDirect( colors.length * 4 ) .order(ByteOrder.nativeOrder())
                    .asFloatBuffer() .put( colors );
            this .colorsBuffer .position( 0 );
            if ( storage != null )
                this .colorsVBO = storage .storeBuffer( this .colorsBuffer, this .colorsVBO );

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
    
    public void pick( Intersector intersector, float[][] orientations, float[] embedding )
    {
        float scale = 1f / this .globalScale;
        if ( this .shape .isPanel() && ! this .instances .isEmpty() ) {
            // A panel shape has only a single instance, for now
            RenderedManifestation rm = this .instances .iterator() .next();
            
            // We know that we'll have 3 vertices per triangle, and a full set of triangles for both polygons.
            // We just intersect the triangles of one of the two polygons.
            int triangles = this .vertexCount / 6;
            for ( int i = 0; i < triangles; i++ ) {
                intersector .intersectTriangle( this .verticesArray, i * 9, rm, scale, embedding );
            }
        }
        else if ( this .shape .getOrbit() != null ) {
            // a strut shape
            for ( RenderedManifestation rm : instances ) {
                float[] orientation = orientations [ rm .getStrutZone() ];
                AlgebraicVector vector = rm .getLocationAV();
                // Embedding will be handled in the intersector
                RealVector rv = ( vector == null )? new RealVector() : vector .toRealVector();
                float[] location = new float[3];
                rv .addTo( location, location );
                int triangles = this .vertexCount / 3 ;
                for ( int i = 0; i < triangles; i++ ) {
                    intersector .intersectTriangle( this .verticesArray, i * 9, rm, scale, embedding, orientation, location );
                }
            }
        }
        else {
            // a ball shape
            for ( RenderedManifestation rm : instances ) {
                AlgebraicVector vector = rm .getLocationAV();
                // Embedding will be handled in the intersector
                RealVector rv = ( vector == null )? new RealVector() : vector .toRealVector();
                float[] location = new float[3];
                rv .addTo( location, location );
                int triangles = this .vertexCount / 3 ;
                for ( int i = 0; i < triangles; i++ ) {
                    intersector .intersectTriangle( this .verticesArray, i * 9, rm, scale, embedding, null, location );
                }
            }
        }
    }

    @Override
    public FloatBuffer getLineVerticesBuffer()
    {
        return this .lineVerticesBuffer;
    }

    @Override
    public FloatBuffer getInstancesBuffer()
    {
        return this .positionsBuffer;
    }

    @Override
    public FloatBuffer getVerticesBuffer()
    {
        return this .verticesBuffer;
    }

    @Override
    public FloatBuffer getNormalsBuffer()
    {
        return this .normalsBuffer;
    }

    @Override
    public FloatBuffer getColorsBuffer()
    {
        return this .colorsBuffer;
    }
}
