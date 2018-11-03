package com.vzome.core.apps;

import java.util.ArrayList;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Polygon;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;

public class Dump4dPolytopeVson
{
	public static class VsonBuilder implements Listener
	{
        public SortedSet<AlgebraicVector> vertices = new TreeSet<>();
        public ArrayList<AlgebraicVector[]> edges = new ArrayList<>();

        @Override
        public Object addVertex( AlgebraicVector v )
        {
            vertices .add( v );
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
            AlgebraicVector[] result = new AlgebraicVector[]{ (AlgebraicVector) p1, (AlgebraicVector) p2 };
            edges .add( result );
            return result;
        }
        
        
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
        
        Listener vsonBuilder = new VsonBuilder();
		fieldApp .constructPolytope( groupName, index, index, null, vsonBuilder  );
		        
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
    
	private static class View implements AlgebraicNumber.Views.Rational, Polygon.Views.Polygons {}
}
