package com.vzome.core.apps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.viewing.ExportedVEFShapes;

/*
 * This was created for vZome Online (mostly), to establish a new, more explicit data format
 * for strut shapes.  The initial Zome ball for Online was rendered from one of the JSON files
 * produced here, for a short time, but Online has since switched to using transpiled legacy
 * code for shapes.
 * 
 * See Git commit 9dfe8c150bfe047aacc4c73beb564ecd44ea1f30.
 */

public class PartsJsonConverter
{
    public static void main( String[] args )
    {
        File targetDir = new File( args[ 0 ] );
        ObjectMapper mapper = new ObjectMapper();
        Application application = new Application( false, null, new Properties() );
        for ( String name : application.getFieldNames() )
        {
            FieldApplication fApp = application.getDocumentKind( name );
            for ( SymmetryPerspective perspective : fApp.getSymmetryPerspectives() )
            {
                for ( Shapes shapes : perspective.getGeometries() )
                {
                    if ( shapes instanceof ExportedVEFShapes ) {
                        try {
                            System.out.println( shapes.getPackage() );
                            FileWriter writer = new FileWriter( new File( targetDir, shapes.getPackage() + ".json" ) );
                            writer .write( mapper .writerWithDefaultPrettyPrinter()
                                    .withView( View.class )
                                    .writeValueAsString( shapes ) );
                            writer .close();
                        } catch (JsonProcessingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    // JSON serialization
    //
    //  The view mechanism works fine, but there is only ONE view at a time.
    //  Fortunately, the mechanism uses "isAssignableFrom", so you can have
    //  a view class that extends others or implements interfaces.
    
    private static class View implements AlgebraicNumber.Views.Real, Polyhedron.Views.Triangles {}
}
