

package com.vzome.core.math;


class GoldenNumberMatrix extends GoldenMatrix
{
    public static final GoldenMatrix IDENTITY_MATRIX =
        new GoldenNumberMatrix( GoldenNumberVector.X, GoldenNumberVector.Y, GoldenNumberVector.Z, GoldenNumberVector.W );
    
    public GoldenNumberMatrix( GoldenVector x, GoldenVector y, GoldenVector z,
            GoldenVector w )
    {
        super( x, y, z, w );
    }

    public GoldenMatrix createGoldenMatrix( GoldenVector x, GoldenVector y,
            GoldenVector z, GoldenVector w )
    {
        return new GoldenNumberMatrix( x, y, z, w );
    }

    public static GoldenMatrix.Factory FACTORY; // externally set
    

    public Factory getFactory()
    {
        return FACTORY;
    }
}
