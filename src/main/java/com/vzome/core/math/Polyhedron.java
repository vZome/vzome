package com.vzome.core.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.symmetry.Direction;


public class Polyhedron {

	protected int numVertices = 0;

	protected final Map m_vertices = new HashMap();
    
	protected final List m_vertexList = new ArrayList();

	protected final Set m_faces = new HashSet();
    
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

    private AlgebraicNumber length;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
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
        private AlgebraicVector mNormal;
        
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
            AlgebraicVector v0 = (AlgebraicVector) vertices .get( getVertex( 0 ) );
            AlgebraicVector v1 = (AlgebraicVector) vertices .get( getVertex( 1 ) );
            AlgebraicVector v2 = (AlgebraicVector) vertices .get( getVertex( 2 ) );
            v1 = v1 .minus( v0 );
            v2 = v2 .minus( v0 );
            mNormal = v1 .cross( v2 );
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

    public Direction getOrbit()
    {
        return this .orbit;
    }

    public AlgebraicNumber getLength()
    {
        return this .length;
    }
}


