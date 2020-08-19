package com.vzome.core.apps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;

public class Dump4dPolytopeVson
{
    public static class IndexPair
    {
        public Integer start;
        public Integer end;
    }
    
    private static class Edge
    {
        final AlgebraicVector p1, p2;
        
        public Edge( AlgebraicVector p1, AlgebraicVector p2 )
        {
            this.p1 = p1;
            this.p2 = p2;
        }
        
        public IndexPair asIndexPair( List<AlgebraicVector> vertices )
        {
            IndexPair result = new IndexPair();
            result .start = vertices .indexOf( p1 );
            result.end = vertices .indexOf( p2 );
            return result;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            // NOT supporting null p1 or p2
            result = prime * result + p1.hashCode() + p2.hashCode();
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Edge other = (Edge) obj;
            // NOT supporting null p1 or p2
            if (p1.equals(other.p1) && p2.equals(other.p2))
                return true;
            if (p1.equals(other.p2) && p2.equals(other.p1))
                return true;
            return false;
        }
    }

    public static class VsonBuilder implements Listener
	{
        private SortedSet<AlgebraicVector> vertexPoints = new TreeSet<>();
        private Set<Edge> edges = new HashSet<>();
        
        public VsonBuilder( String field )
        {
            this .field = field;
        }

        @Override
        public Object addVertex( AlgebraicVector v )
        {
            vertexPoints .add( v );
            return v;
        }
        
        @Override
        public Object addFace( Object[] vertices )
        {
            return null;
        }
        
        @Override
        public Object addEdge( Object p1, Object p2 )
        {
            Edge result = new Edge( (AlgebraicVector) p1, (AlgebraicVector) p2 );
            edges .add( result );
            return result;
        }
        
        /**
         * Must be called after all the addVertex and addEdge calls have happened,
         * and before the JSON serialization access the fields below.
         */
        public void index()
        {
            vertices = new ArrayList<>(vertexPoints);
            struts = edges .stream()
                        .map( edge -> edge .asIndexPair( vertices ) )
                        .collect( Collectors.toList() );
        }
        
        public List<Integer> getBalls()
        {
            return IntStream .range( 0, vertices .size() ) .boxed() .collect( Collectors.toList() ); 
        }
        
        // These are public to show up in JSON
        public String field;
        public List<AlgebraicVector> vertices;
        public List<IndexPair> struts;
    };

    public static void main(String[] args)
	{
        Application app = new Application( false, null, new Properties() );
        
        // Note that arguments are processed here in reverse order.

        String field = ( args.length > 2 )? args[2] : "golden";          // optional third argument
		FieldApplication fieldApp = app .getDocumentKind( field );

        String groupName = ( args.length > 1 )? args[1] : "H4";          // optional second argument

        String indexStr = ( args.length > 0 )? args[0] : "1000";          // optional first argument
        int index = Integer.parseInt( indexStr, 2 );
        
        VsonBuilder vsonBuilder = new VsonBuilder( field );
		fieldApp .constructPolytope( groupName, index, index, null, vsonBuilder  );
		vsonBuilder .index();
		        
		ObjectMapper mapper = new ObjectMapper();
		try {
			System .out .println( mapper .writerWithDefaultPrettyPrinter()
                    .withView( View.class )
			        .writeValueAsString( vsonBuilder ) );
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    // JSON serialization
    //
    //  The view mechanism works fine, but there is only ONE view at a time.
    //  Fortunately, the mechanism uses "isAssignableFrom", so you can have
    //  a view class that extends others or implements interfaces.
    
	private static class View implements AlgebraicNumber.Views.Rational, Polyhedron.Views.Polygons {}
}
