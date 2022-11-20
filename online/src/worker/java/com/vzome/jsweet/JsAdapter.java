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
import com.vzome.core.construction.Color;

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

    public static def.js.Object getZoneGrid( OrbitSource orbits, int[][] planeNormal )
    {
        JsAlgebraicField field = (JsAlgebraicField) orbits .getSymmetry() .getField();
        AlgebraicVector normal = mapVectorToJava( planeNormal, field );
        String planeColor = orbits .getVectorColor( normal ) .toWebString();
        String planeName = orbits .getSymmetry() .getAxis( normal ) .getOrbit() .getName();

        ArrayList<def.js.Object> zonesList = new ArrayList<>();

        PlaneOrbitSet planeOrbits = new PlaneOrbitSet( orbits.getOrbits(), normal );
        for ( Iterator<Axis> iterator = planeOrbits.zones(); iterator.hasNext();) {
            Axis zone = (Axis) iterator.next();
            Direction orbit = zone .getDirection();
            if ( ! orbit .isStandard() )
                continue;
            ArrayList<AlgebraicVector> gridPoints = new ArrayList<>();
            AlgebraicVector zoneNormal = zone .normal();
            String zoneColor = orbits .getVectorColor( zoneNormal ) .toWebString();
            AlgebraicNumber scale = orbit .getUnitLength();
            for ( int i = 0; i < 5; i++ ) {
                scale = scale .times( field .createPower( 1 ) );
                AlgebraicVector gridPoint = zoneNormal .scale( scale );
                gridPoints .add( gridPoint );
            }
            AlgebraicVector[] vectors = gridPoints .stream() .toArray( size -> new AlgebraicVector[size] );
            
            def.js.Object zoneObj = new def.js.Object() {{ $set( "color", zoneColor ); $set( "vectors", vectors ); }};
            zonesList .add( zoneObj );
        }

        def.js.Object[] zones = zonesList .stream() .toArray( size -> new def.js.Object[size] );
        return new def.js.Object() {{ $set( "color", planeColor ); $set( "zones", zones ); }};
    }
}
