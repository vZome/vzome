
package com.vzome.core.math;

class RootTwoVector extends GoldenVector
{
    public static final GoldenVector ORIGIN = new RootTwoVector( RootTwoNumber.ZERO, RootTwoNumber.ZERO, RootTwoNumber.ZERO );

    public static final GoldenVector X = new RootTwoVector( RootTwoNumber.ONE, RootTwoNumber.ZERO, RootTwoNumber.ZERO );

    public static final GoldenVector Y = new RootTwoVector( RootTwoNumber.ZERO, RootTwoNumber.ONE, RootTwoNumber.ZERO );

    public static final GoldenVector Z = new RootTwoVector( RootTwoNumber.ZERO, RootTwoNumber.ZERO, RootTwoNumber.ONE );

    public static final GoldenVector W = new RootTwoVector( RootTwoNumber.ZERO, RootTwoNumber.ZERO, RootTwoNumber.ZERO, RootTwoNumber.ONE, RootTwoNumber.ONE );
    
    static GoldenVector.Factory FACTORY; // set from RootTwoField
    
    public RootTwoVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w, IntegralNumber factor )
    {
        super( x, y, z, w, factor );
    }
    
    public 
    RootTwoVector( IntegralNumber x, IntegralNumber y, IntegralNumber z, IntegralNumber w )
    {
        this( x, y, z, w, RootTwoNumber.ONE );
    }

    public 
    RootTwoVector( IntegralNumber x, IntegralNumber y, IntegralNumber z )
    {
        this( x, y, z, RootTwoNumber.ZERO, RootTwoNumber.ONE );
    }


    public GoldenVector createGoldenVector( IntegralNumber x,
            IntegralNumber y, IntegralNumber z, IntegralNumber w,
            IntegralNumber factor )
    {
        return new RootTwoVector( x, y, z, w, factor );
    }

    public GoldenVector.Factory getFactory()
    {
        return FACTORY;
    }

}
