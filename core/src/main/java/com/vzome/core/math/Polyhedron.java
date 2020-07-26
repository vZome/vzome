package com.vzome.core.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.math.Polyhedron.Face.Triangle;
import com.vzome.core.math.symmetry.Direction;


public class Polyhedron implements Cloneable
{
    private static Logger logger = Logger .getLogger( "com.vzome.core.math.Polyhedron" );
    
    protected int numVertices = 0;

    protected final Map<AlgebraicVector, Integer> m_vertices = new HashMap<>();
    
    protected List<AlgebraicVector> m_vertexList = new ArrayList<>();

    protected Set<Face> m_faces = new HashSet<>();
    
    private final AlgebraicField field;
    
    private Polyhedron evilTwin; // for struts in symmetries with truly chiral orbits
    
    private boolean isEvil = false;
    
    private boolean isPanel = false;

    private final UUID guid = UUID .randomUUID();

    public Polyhedron( AlgebraicField field )
    {
        this.field = field;
    }
    
    /**
     * Get the mirror twin of this Polyhedron.
     * The vertices are transformed by the given reflection.
     * The faces are oriented in reverse, so that when oriented with
     * a mirroring transformation, the face normals will still point
     * outward.
     * @return
     */
    public Polyhedron getEvilTwin( AlgebraicMatrix reflection )
    {
        if ( this .evilTwin == null )
        {
            try {
                this .evilTwin = (Polyhedron) this .clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this .evilTwin .isEvil = true;
            
            this .evilTwin .m_vertexList = new ArrayList<>();
            // this loop should preserve the order, and thus indices for the faces below
            for ( AlgebraicVector vertex : m_vertexList ) {
                this .evilTwin .addVertex( reflection .timesColumn( vertex ) );
            }
            
            this .evilTwin .m_faces = new HashSet<Face>();
            for ( Face face : m_faces ) {
                Face mirrorFace = (Face) face .clone();
                Collections .reverse( mirrorFace );
                this .evilTwin .addFace( mirrorFace );
            }
        }
        return this .evilTwin;
    }
    
    @JsonIgnore
    public AlgebraicField getField()
    {
        return field;
    }
    
    private String name;

    private Direction orbit;

    private AlgebraicNumber length;
    
    @JsonIgnore
    public void setName( String name )
    {
        this.name = name;
    }
    
    @JsonIgnore
    public String getName()
    {
        return name;
    }

    public void addVertex( AlgebraicVector location ) throws Error
    {
    m_vertexList .add( location );
    }

    /**
     * Only used in ZomicPolyhedronModelInterpreter.
     * This used to be the implementation of addVertex, but all other callers
     * don't use the return value, and have already assigned their own indices,
     * so the collisions here are a bad idea.
     * @param halfLoc
     * @return
     */
    public Integer addIndexedVertex( AlgebraicVector location )
    {
        Integer vertexObj = m_vertices.get( location );
        if ( vertexObj == null ) {
            m_vertexList .add( location );
            // IMPORTANT: the incremented value is not returned
            m_vertices .put( location, vertexObj = numVertices++ );
        }
        return vertexObj;
    }

    public void addFace( Face face )
    {
        face .computeNormal( m_vertexList );
        face .canonicallyOrder(); // so the contains comparison works
        if ( ! m_faces .contains( face ) ) {
            m_faces .add( face );
            
//          System .out .println( "--------face" );
//          for ( Iterator<Integer> vertices = face .iterator(); vertices .hasNext(); )
//              System .out .println( m_vertexList .get(vertices.next() ) );
        }
    }

    public Face newFace()
    {
        return new Face();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isEvil ? 1231 : 1237);
        result = prime * result + ((length == null) ? 0 : length.hashCode());
        result = prime * result + ((m_faces == null) ? 0 : m_faces.hashCode());
        result = prime * result
                + ((m_vertexList == null) ? 0 : m_vertexList.hashCode());
        result = prime * result + numVertices;
        result = prime * result + ((orbit == null) ? 0 : orbit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Polyhedron other = (Polyhedron) obj;
        if (isEvil != other.isEvil) {
            return false;
        }
        if (length == null) {
            if (other.length != null) {
                return false;
            }
        } else if (!length.equals(other.length)) {
            return false;
        }
        if (m_faces == null) {
            if (other.m_faces != null) {
                return false;
            }
        } else if (!m_faces.equals(other.m_faces)) {
            return false;
        }
        if (m_vertexList == null) {
            if (other.m_vertexList != null) {
                return false;
            }
        } else if (!m_vertexList.equals(other.m_vertexList)) {
            return false;
        }
        if (numVertices != other.numVertices) {
            return false;
        }
        if (orbit == null) {
            if (other.orbit != null) {
                return false;
            }
        } else if (!orbit.equals(other.orbit)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("serial")
    public class Face extends ArrayList<Integer> implements Cloneable
    {
        private AlgebraicVector mNormal;

        private Face(){}

        @JsonIgnore
        public int getVertex( int index )
        {
            if ( index >= size() )
            {
                String msg = "index larger than Face size";
                logger .severe( msg );
                throw new IllegalStateException( msg );
            }
            return get( index );
        }

        public class Triangle
        {
            public int[] vertices = new int[3];
            public RealVector normal;
            
            public Triangle( int v0, int v1, int v2, RealVector normal )
            {
                this .vertices[ 0 ] = v0;
                this .vertices[ 1 ] = v1;
                this .vertices[ 2 ] = v2;
                this .normal = normal;
            }
        }
        
        public List<Triangle> getTriangles()
        {
            int arity = this .size();
            RealVector normal = this .mNormal .toRealVector();
            ArrayList<Triangle> result = new ArrayList<>();
            int v0 = -1, v1 = -1;
            for ( int j = 0; j < arity; j++ ){
                Integer index = this .get( j );
                if ( v0 == -1 )
                {
                    v0 = index;
                }
                else if ( v1 == -1 )
                {
                    v1 = index;
                }
                else
                {
                    Triangle triangle = new Triangle( v0, v1, index, normal );
                    result .add( triangle );
                    v1 = index;
                }
            }
            return result;
        }
        
        void computeNormal( List<AlgebraicVector> vertices )
        {
            // TODO: Don't depend on the first three vertices to be non-collinear
            mNormal = AlgebraicVectors.getNormal(
                    vertices .get( getVertex( 0 ) ), 
                    vertices .get( getVertex( 1 ) ), 
                    vertices .get( getVertex( 2 ) ) ); 
        }

        void canonicallyOrder()
        {
            int minIndex = -1;
            int minVertex = Integer.MAX_VALUE;
            int sz = size();
            for ( int i = 0; i < sz; i++ )
                if ( getVertex(i) <= minVertex ){
                    minVertex = getVertex(i);
                    minIndex = i;
                }
            Integer[] temp = new Integer[ sz ]; 
            for ( int j = 0; j < sz; j++  ) {
                temp[j] = get( (j+minIndex) % sz );
            }
            for ( int k = 0; k < sz; k++ )
                set( k, temp[k] );
        }

        @Override
        public int hashCode()
        {
            int tot = 0;
            for ( int i = 0; i < size(); i++ )
                tot += getVertex(i);
            return tot;
        }

        @Override
        public boolean equals( Object other )
        {
            if( other == null )
                return false;
            if ( other == this )
                return true;
            if ( ! ( other instanceof Face ) )
                return false;
            Face otherFace = (Face) other;
            if ( otherFace .size() != size() )
                return false;
            for ( int i = 0; i < size(); i++ )
                // this relies on both faces being in canonical order
                if ( ! get(i) .equals( otherFace .get(i)) )
                    return false;
            return true;
        }

        @JsonIgnore
        public AlgebraicVector getNormal()
        {
            return mNormal;
        }
    }
    
    public void setOrbit( Direction orbit )
    {
        this .orbit = orbit;
    }

    public void setLength( AlgebraicNumber length )
    {
        this .length = length;
    }

    @JsonIgnore
    public Direction getOrbit()
    {
        return this .orbit;
    }

    @JsonIgnore
    public AlgebraicNumber getLength()
    {
        return this .length;
    }

    @JsonIgnore
    public boolean isPanel()
    {
        return isPanel;
    }

    public void setPanel( boolean isPanel )
    {
        this.isPanel = isPanel;
    }
    
    // JSON serialization
    //
    //  The view mechanism works fine, but there is only ONE view at a time.
    //  Fortunately, the mechanism uses "isAssignableFrom", so you can have
    //  a view class that extends others or implements interfaces.
    //
    //  I thought I needed a customer serializer here, but that turned out
    //  to be unworkable, as it would not pass along the SerializerProvider,
    //  which carries the view.
    
    @JsonProperty( "vertices" )
    public List<AlgebraicVector> getVertexList(){
        return m_vertexList;
    }

    @JsonProperty( "polygons" )
    @JsonView( Views.Polygons.class )
    public Set<Face> getFaceSet(){
        return m_faces;
    }

    @JsonProperty( "id" )
    @JsonView( Views.Triangles.class )
    public UUID getGuid()
    {
        return this .guid;
    }

    @JsonProperty( "faces" )
    @JsonView( Views.Triangles.class )
    public List<Face.Triangle> getTriangleFaces()
    {
        ArrayList<Face.Triangle> result = new ArrayList<>();
        for ( Face face : m_faces ) {
            result .addAll( face .getTriangles() );
        }
        return result;
    }

    // These views will be used for JSON serialization
    public static class Views {
        public interface UnityMesh{}
        public interface Triangles{}
        public interface Polygons{}
    }


    @JsonProperty( "triangles" )
    @JsonView( Views.UnityMesh.class )
    public List<Integer> getTriangles()
    {
        int index = 0;
        ArrayList<Integer> result = new ArrayList<>();
        for ( Face face : m_faces ) {
            for ( Triangle triangle : face .getTriangles() ) {
                // The old indices don't matter here, since we are creating a new tvertices list,
                //   and normals to match.
                result .add( index++ );
                result .add( index++ );
                result .add( index++ );
            }
        }
        return result;
    }

    @JsonProperty( "tvertices" )
    @JsonView( Views.UnityMesh.class )
    public List<RealVector> getTriangleVertices()
    {
        ArrayList<RealVector> result = new ArrayList<>();
        for ( Face face : m_faces ) {
            for ( Triangle triangle : face .getTriangles() ) {
                for ( int index : triangle .vertices ) {
                    AlgebraicVector vertex = this .m_vertexList .get( index );
                    result .add( vertex .toRealVector() );
                }
            }
        }
        return result;
    }

    @JsonProperty( "normals" )
    @JsonView( Views.UnityMesh.class )
    public List<RealVector> getNormals()
    {
        ArrayList<RealVector> result = new ArrayList<>();
        for ( Face face : m_faces ) {
            for ( Triangle triangle : face .getTriangles() ) {
                result .add( triangle .normal );
                result .add( triangle .normal );
                result .add( triangle .normal );
            }
        }
        return result;
    }
}
