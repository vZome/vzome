

package com.vzome.core.math;


class RootTwoMatrix extends GoldenMatrix
{
    public static final GoldenMatrix IDENTITY_MATRIX =
        new RootTwoMatrix( RootTwoVector.X, RootTwoVector.Y, RootTwoVector.Z, RootTwoVector.W );
    
    public RootTwoMatrix( GoldenVector x, GoldenVector y, GoldenVector z,
            GoldenVector w )
    {
        super( x, y, z, w );
    }

    public GoldenMatrix createGoldenMatrix( GoldenVector x, GoldenVector y,
            GoldenVector z, GoldenVector w )
    {
        return new RootTwoMatrix( x, y, z, w );
    }

    public static GoldenMatrix.Factory FACTORY; // externally set
    

    public Factory getFactory()
    {
        return FACTORY;
    }
}
