
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math;

public class Quaternion extends GoldenNumberVector   // TODO generalize to all GoldenVectors
{
    public Quaternion( IntegralNumber w, IntegralNumber x, IntegralNumber y, IntegralNumber z )
    {
        super( x, y, z, w, w .one() );
    }
    
    public static GoldenVector conjugate( GoldenVector q )
    {
        return new GoldenNumberVector( q .getX().neg(), q .getY().neg(), q .getZ().neg(), q .getW() );
    }
    
    public static GoldenVector reflect( GoldenVector v, GoldenVector mirror )
    {
        return multiply( mirror, conjugate( v ), mirror ) .neg();
    }
    
    public static GoldenVector multiply( GoldenVector left, GoldenVector middle, GoldenVector right )
    {
        return multiply( left, multiply( middle, right ) );
    }


    public static GoldenVector multiply( GoldenVector left, GoldenVector right )
    {
        IntegralNumber w1 = left .getW();
        IntegralNumber x1 = left .getX();
        IntegralNumber y1 = left .getY();
        IntegralNumber z1 = left .getZ();
        IntegralNumber w2 = right .getW();
        IntegralNumber x2 = right .getX();
        IntegralNumber y2 = right .getY();
        IntegralNumber z2 = right .getZ();
        return new GoldenNumberVector(
                w1.times(x2) .plus(  x1.times(w2) ) .plus(  y1.times(z2) ) .minus( z1.times(y2) ),
                w1.times(y2) .minus( x1.times(z2) ) .plus(  y1.times(w2) ) .plus(  z1.times(x2) ),
                w1.times(z2) .plus(  x1.times(y2) ) .minus( y1.times(x2) ) .plus(  z1.times(w2) ),
                w1.times(w2) .minus( x1.times(x2) ) .minus( y1.times(y2) ) .minus( z1.times(z2) )
                );
    }
    
    public static GoldenVector multiplyIncorrect( GoldenVector left, GoldenVector right )
    {
        IntegralNumber w = left .getX();
        IntegralNumber x = left .getY();
        IntegralNumber y = left .getZ();
        IntegralNumber z = left .getW();
        IntegralNumber a = right .getX();
        IntegralNumber b = right .getY();
        IntegralNumber c = right .getZ();
        IntegralNumber d = right .getW();
        return new GoldenNumberVector(
                a.times(w) .minus( b.times(x) ) .minus( c.times(y) ) .minus( d.times(z) ),
                a.times(x) .plus(  b.times(w) ) .minus( c.times(z) ) .plus(  d.times(y) ),
                a.times(y) .plus(  b.times(z) ) .plus(  c.times(w) ) .minus( d.times(x) ),
                a.times(z) .minus( b.times(y) ) .plus(  c.times(x) ) .plus(  d.times(w) )
                );
    }

}
