
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.math;

import com.vzome.core.algebra.AlgebraicField;

public class PerspectiveProjection implements Projection
{
    private final AlgebraicField field;
    
    private final int[] cameraDist;
    
    public PerspectiveProjection( AlgebraicField field, int[] cameraDist )
    {
        super();
        this .field = field;
        this .cameraDist = cameraDist;
    }
    
    private int[] minDenom;
    private double minDenomValue;

    public int[] /*AlgebraicVector*/ projectImage( int[] /*AlgebraicVector*/ source, boolean wFirst )
    {
        /*
         * from my WebGL vertex shader:
         * 
            float denom = cameraDist - position .w;
            denom = max( denom, 0.0001 );
            position3d .x = position .x / denom;
            position3d .y = position .y / denom;
            position3d .z = position .z / denom;
            position3d .w = 1.0;
         */
        int[] result = this .field .origin( 4 );
        int[] w = field .getVectorComponent( source, 0 );
        int[] denom = field .subtract( cameraDist, w );
        if ( minDenom == null )
        {
            minDenom = field .createPower( -5 );
            minDenomValue = field .evaluateNumber( minDenom );
        }
        double denomValue = field .evaluateNumber( denom );
        if ( denomValue < minDenomValue )
        {
            denom = minDenom;
        }
        
        field .setVectorComponent( result, 0, field .createPower( 0 ) );
        field .setVectorComponent( result, 1, field .divide( field .getVectorComponent( source, 1 ), denom ) );
        field .setVectorComponent( result, 2, field .divide( field .getVectorComponent( source, 2 ), denom ) );
        field .setVectorComponent( result, 3, field .divide( field .getVectorComponent( source, 3 ), denom ) );
        return result;
    }
}
