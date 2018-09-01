package com.vzome.core.apps;

import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.construction.Polygon;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.render.Shapes;

public class DumpShapeJson
{
	public static void main(String[] args)
	{
        Application app = new Application( false, null, new Properties() );
        
        // Note that arguments are processed here in reverse order.

        String field = ( args.length > 2 )? args[2] : "golden";          // optional third argument
		FieldApplication fieldApp = app .getDocumentKind( field );

        String symmetry = ( args.length > 1 )? args[1] : "icosahedral";  // optional second argument
        SymmetryPerspective perspective = fieldApp .getSymmetryPerspective( symmetry );
		
        String style = ( args.length > 0 )? args[0] : "default";         // optional first argument
        Shapes shapes = perspective .getGeometries() .stream()
                .filter( shape -> style .equals( shape .getName() ) )
                .findAny() .orElse( perspective .getDefaultGeometry() );
        
		ObjectMapper mapper = new ObjectMapper();
		try {
			System .out .println( mapper .writerWithDefaultPrettyPrinter()
                    .withView( View.class )
			        .writeValueAsString( shapes ) );
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
