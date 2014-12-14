
package com.vzome.core.viewing;

import java.util.List;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.parts.StrutGeometry;

public class ExportedVEFStrutGeometry implements StrutGeometry
{    
    private final List prototypeVertices;  // the polyhedron from which others are derived
    
    private final List prototypeFaces;
    
    private final AlgebraicField field;
    
    private final int[] prototypeVector; // the prototype strut vector from the symmetry group
    
    private final Set motileVertices;  // the polyhedron vertices that must adjust for different strut lengths
    
    public ExportedVEFStrutGeometry( List vertices, List faces, int[] prototype, Set motileVertices, AlgebraicField field )
    {
        prototypeVertices = vertices;
        prototypeFaces = faces;
        prototypeVector = prototype;
        this.motileVertices = motileVertices;
        this.field = field;
    }

    public Polyhedron getStrutPolyhedron( int[] /*AlgebraicNumber*/ length )
    {
        int[] tipVertex = field .scaleVector( prototypeVector, length );
        if ( field .getName() .equals( "snubDodec" ) )
        {
            RealVector rproto = field .getRealVector( prototypeVector );
            System .out .println( "proto length = " + rproto .length() );
            double lend = field .evaluateNumber( length );
            System .out .println( "strut length = " + lend );
            RealVector rtip = field .getRealVector( tipVertex );
            System .out .println( "tip length = " + rtip .length() );
        }
        Polyhedron result = new Polyhedron( field );
        for ( int i = 0; i < prototypeVertices .size(); i ++ )
        {
            int[] vertex = (int[]) prototypeVertices .get( i );
            if ( motileVertices .contains( new Integer( i ) ) )
                vertex = field .add( vertex, tipVertex );
            result .addVertex( vertex );
        }
        for ( int j = 0; j < prototypeFaces .size(); j ++ )
        {
            List prototypeFace = (List) prototypeFaces .get( j );
            Polyhedron.Face face = result .newFace();
            face .addAll( prototypeFace );
            result .addFace( face );
        }
        return result;
    }
}
