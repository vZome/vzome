package com.vzome.core.apps;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;

public class DumpBlueAngles {

    public static void main( String[] args )
    {
    	final double EPSILON = 5E-10f;

        Symmetry icosa = new IcosahedralSymmetry( new PentagonField() );
        Direction blue = icosa .getDirection( "blue" );
        
        Map<Double, Axis[]> blueAngles = new HashMap<>();
        RealVector baseRv = null;
        Axis baseZone = null;
        for (Axis zone : blue) {
            RealVector rv = zone .normal() .toRealVector() .normalize();
            if ( baseRv == null )
            {
                baseRv = rv;
                baseZone = zone;
            }
            else
            {
                double cos = Math .abs( rv .dot( baseRv ) );
                // don't want zero angle (zones are the same)
                if ( cos > EPSILON && cos != 1f )
                {
                    double angle = 180f *  Math .acos( cos ) / Math .PI;
                    blueAngles .put( angle, new Axis[]{ baseZone, zone } );
                }
            }
        }
        for (Double angle : blueAngles .keySet()) {
            System .out. print( angle + "  " );
            Axis[] zones = blueAngles .get( angle );
            System .out .print( zones[0] .getOrientation() + " " );
            System .out .println( zones[1] .getOrientation() + " " );
        }
    }

}
