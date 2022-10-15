package com.vzome.jsweet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.PlaneOrbitSet;

public class JsAdapter
{
    public static AlgebraicVector mapVectorToJava( int[][] vector, JsAlgebraicField field )
    {
        AlgebraicNumber[] ans = (AlgebraicNumber[]) Stream.of( vector )
                .map( ints -> new JsAlgebraicNumber( field, ints ) )
                .toArray();
        return new AlgebraicVector( ans );
    }
    
    public static int[][] mapVectorToJavascript( AlgebraicVector vector )
    {
        return (int[][]) Stream.of( vector .getComponents() )
                .map( an -> an .toTrailingDivisor() )
                .toArray();
    }

    public static int[][][] getZoneGrid( OrbitSource orbits, int[][] planeNormal )
    {
        ArrayList<int[][]> gridPoints = new ArrayList<>();
        JsAlgebraicField field = (JsAlgebraicField) orbits .getSymmetry() .getField();
        AlgebraicVector normal = mapVectorToJava( planeNormal, field );
        
        PlaneOrbitSet planeOrbits = new PlaneOrbitSet( orbits.getOrbits(), normal );
        for ( Iterator<Axis> iterator = planeOrbits.zones(); iterator.hasNext();) {
            Axis zone = (Axis) iterator.next();
            Direction orbit = zone .getDirection();
            if ( ! orbit .isStandard() )
                continue;
            AlgebraicNumber scale = orbit .getUnitLength();
            for ( int i = 0; i < 5; i++ ) {
                scale = scale .times( field .createPower( 1 ) );
                AlgebraicVector gridPoint = zone .normal() .scale( scale );
                gridPoints .add( mapVectorToJavascript( gridPoint ) );
            }
        }
        return gridPoints .stream() .toArray( size -> new int[size][][] );
    }
}
