

package com.vzome.core.math;

class GoldenNumberVector extends GoldenVector
{
    public static final GoldenVector ORIGIN = new GoldenNumberVector( GoldenNumber.ZERO, GoldenNumber.ZERO, GoldenNumber.ZERO );

    public static final GoldenVector X = new GoldenNumberVector( GoldenNumber.ONE, GoldenNumber.ZERO, GoldenNumber.ZERO );

    public static final GoldenVector Y = new GoldenNumberVector( GoldenNumber.ZERO, GoldenNumber.ONE, GoldenNumber.ZERO );

    public static final GoldenVector Z = new GoldenNumberVector( GoldenNumber.ZERO, GoldenNumber.ZERO, GoldenNumber.ONE );

    public static final GoldenVector W = new GoldenNumberVector( GoldenNumber.ZERO, GoldenNumber.ZERO, GoldenNumber.ZERO, GoldenNumber.ONE, GoldenNumber.ONE );
    
    static GoldenVector.Factory FACTORY; // set from GoldenField
    
    public GoldenNumberVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w, IntegralNumber factor )
    {
        super( x, y, z, w, factor );
    }
    
    public 
    GoldenNumberVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w )
    {
        this( x, y, z, w, GoldenNumber.ONE );
    }

    public 
    GoldenNumberVector( IntegralNumber x, IntegralNumber y, IntegralNumber z )
    {
        this( x, y, z, GoldenNumber.ZERO, GoldenNumber.ONE );
    }


    public GoldenVector createGoldenVector( IntegralNumber x,
            IntegralNumber y, IntegralNumber z, IntegralNumber w,
            IntegralNumber factor )
    {
        return new GoldenNumberVector( x, y, z, w, factor );
    }

    public GoldenVector.Factory getFactory()
    {
        return FACTORY;
    }

}
