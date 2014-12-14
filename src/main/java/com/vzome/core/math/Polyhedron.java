package com.vzome.core.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.symmetry.Direction;


public class Polyhedron {

	protected int numVertices = 0;

	protected final Map m_vertices = new HashMap();
    
	protected final List m_vertexList = new ArrayList();

	protected final Set m_faces = new HashSet();

	/**
	 * This is the "render-ready" state that is consumed by an OpenGL-based
	 * renderer.  It will be used on the native side to construct index and vertex arrays
	 * for use in glMultiDrawArrays.
	 */
	protected int[] mIndexArray = null;
	protected float[] mVertexArray = null;
    
    private final AlgebraicField field;

	public Polyhedron( AlgebraicField field )
    {
        this.field = field;
    }
    
    public AlgebraicField getField()
    {
        return field;
    }
    
    private String name;

    private Direction orbit;

    private int[] length;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

/*
    public int[] getIndexArray()
	{
	    if ( mIndexArray == null )
	        computeArrays();
        return mIndexArray;
    }
	

	public float[] getVertexArray()
	{
	    if ( mIndexArray == null )
	        computeArrays();
	    return mVertexArray;
    }
	

	private synchronized void computeArrays()
	{
	    int numFaces = m_faces .size();
	    System .out .println( "numFaces " + numFaces );
        int intsRequired = numFaces * 2 + 2;
        int numVertices = 0;
        Iterator faces = m_faces .iterator();
        while ( faces .hasNext() ) {
            Face face = (Face) faces .next();
            numVertices += face .size();
        }
        
        mIndexArray = new int[ intsRequired ];
        IntBuffer ibuf = IntBuffer .wrap( mIndexArray );
        mVertexArray = new float[ numVertices * 6 ]; // doubling here for normals
        FloatBuffer fbuf = FloatBuffer .wrap( mVertexArray );

        ibuf .put( numFaces );
        ibuf .put( numVertices );
        faces = m_faces .iterator();
        int index = numFaces + 2;
        int first = 0;
        int count = 0;
        while ( faces .hasNext() ) {
            Face face = (Face) faces .next();
            count = face .size();
            ibuf .put( first );
//    	    System .out .println( "first " + first + " count " + count + " index " + index );
            ibuf .put( index++, count );
            
            int[] v0 = (int[]) m_vertexList .get( face .getVertex( 0 ) );
            int[] v1 = (int[]) m_vertexList .get( face .getVertex( 1 ) );
            v1 = RationalVectors .subtract( v1, v0 );
            int[] v2 = (int[]) m_vertexList .get( face .getVertex( 2 ) );
            v2 = RationalVectors .subtract( v2, v0 );
            v0 = RationalVectors. cross( v1, v2, null );
            for ( int i = 0; i < count; i++ ) {
                int[] vertex = (int[]) m_vertexList .get( face .getVertex( i ) );
                vertex .write( fbuf );
                v0 .location() .normalize() .write( fbuf, ( numVertices + first + i ) * 3 );
            }
            first += count;
        }
        
        System .out .println( "Polyhedron .computeArrays " + intsRequired + " ints, " + numVertices + " vertices" );
    }
	*/

	public Integer addVertex( int[] location ) throws Error
	{
		Integer vertexObj = (Integer) m_vertices.get( location );
		if ( vertexObj == null ) {
			m_vertexList .add( location );
			// IMPORTANT: the incremented value is not returned
			m_vertices .put( location, vertexObj = new Integer( numVertices++ ) );
		}
		return vertexObj;
	}

	public void addFace( Face face )
	{
        face .computeNormal( m_vertexList );
		face .canonicallyOrder(); // so the contains comparison works
		if ( ! m_faces .contains( face ) ) {
			m_faces .add( face );
			
//			System .out .println( "--------face" );
//			for ( Iterator vertices = face .iterator(); vertices .hasNext(); )
//			    System .out .println( m_vertexList .get(((Integer) vertices.next()) .intValue()) );
		}
	}
	
	/**
	 * @return a list of int[]s
	 */
	public List getVertexList(){
		return m_vertexList;
	}

	/**
	 * @return a set of Faces
	 */
	public Set getFaceSet(){
		return m_faces;
	}


    public Face newFace()
    {
        return new Face();
    }
	

	public class Face extends ArrayList
    {
        private int[] mNormal;
        
        private Face(){}
		
		public int getVertex( int index )
        {
            if ( index >= size() )
            {
                Logger .getLogger( "com.vzome.core.math.Polyhedron" ) .severe( "index larger than Face size" );
                throw new IllegalStateException( "index larger than Face size" );
            }
			return ((Integer) get( index )) .intValue();
		}
        
        public void computeNormal( List vertices )
        {
            int[] v0 = (int[]) vertices .get( getVertex( 0 ) );
            int[] v1 = (int[]) vertices .get( getVertex( 1 ) );
            int[] v2 = (int[]) vertices .get( getVertex( 2 ) );
            v1 = field .subtract( v1, v0 );
            v2 = field .subtract( v2, v0 );
            mNormal = field .cross( v1, v2 );
        }
		
		public void canonicallyOrder()
		{
			int minIndex = -1;
			int minVertex = Integer.MAX_VALUE;
			int sz = size();
			for ( int i = 0; i < sz; i++ )
				if ( getVertex(i) <= minVertex ){
					minVertex = getVertex(i);
					minIndex = i;
				}
			Object[] temp = new Object[ sz ];	
			for ( int j = 0; j < sz; j++  ) {
				temp[j] = get( (j+minIndex) % sz );
			}
			for ( int k = 0; k < sz; k++ )
				set( k, temp[k] );
		}
		
		public int hashCode()
		{
			int tot = 0;
			for ( int i = 0; i < size(); i++ )
				tot += getVertex(i);
			return tot;
		}
		
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
        
        public int[] getNormal()
        {
            return mNormal;
        }
	}
	
	


    public void setOrbit( Direction orbit )
    {
        this .orbit = orbit;
    }

    public void setLength( int[] length )
    {
        this .length = length;
    }

    public Direction getOrbit()
    {
        return this .orbit;
    }

    public int[] getLength()
    {
        return this .length;
    }
}


