package com.vzome.core.apps;

import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.render.Colors;

public class DumpSymmetrySystemJson
{
	public static void main(String[] args)
	{
        Application app = new Application( false, null, new Properties() );

        String field = ( args.length > 1 )? args[1] : "golden";  // note that symmetry is the first argument, not field
		FieldApplication fieldApp = app .getDocumentKind( field );

        String symmetry = ( args.length > 0 )? args[0] : "icosahedral";  // note that symmetry is the first argument, not field
        if ( symmetry == null )
            symmetry = "icosahedral";
        SymmetryPerspective perspective = fieldApp .getSymmetryPerspective( symmetry );
		
		Colors colors = app .getColors();
		SymmetrySystem system = new SymmetrySystem( null, perspective, null, colors, true );
		ObjectMapper mapper = new ObjectMapper();
		try {
			System .out .println( mapper .writerWithDefaultPrettyPrinter()
                    .withView( View.class )
			        .writeValueAsString( system ) );
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
