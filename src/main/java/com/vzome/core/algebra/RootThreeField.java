
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;



public class RootThreeField extends AlgebraicField
{
    public static final double ROOT_3 = Math.sqrt( 3d );
    
    private static final BigRational THREE = new BigRational( 3 );
    
    private final AlgebraicNumber defaultStrutScaling;
    
    public RootThreeField()
    {
        super( "rootThree" );
        
        // we start with 1/2 just because we did in the golden field
        defaultStrutScaling = createAlgebraicNumber( 1, 0, 2, -3 );
    };
    
    public void defineMultiplier( StringBuffer buf, int i )
    {
        buf .append( "" );
    }

    public final BigRational[] multiply( BigRational[] first, BigRational[]  second )
    {
        BigRational sqrt3s = first[ SQRT3_PLACE ].times( second[ ONES_PLACE ]) .plus( first[ ONES_PLACE ].times( second[ SQRT3_PLACE ]) );
        BigRational ones = first[ ONES_PLACE ].times( second[ ONES_PLACE ] ) .plus( first[ SQRT3_PLACE ].times( second[ SQRT3_PLACE ] ) .times( THREE ) );
        
        return new BigRational[]{ ones, sqrt3s };
    }

    public int getOrder()
    {
        return 2;
    }
    
    public AlgebraicNumber getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    private static final int ONES_PLACE = 0, SQRT3_PLACE = 1;

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        return factors[ ONES_PLACE ] .getReal() + ROOT_3 * factors[ SQRT3_PLACE ] .getReal();
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        if ( whichIrrational == 0 )
            return factors;
        else
            return new BigRational[]{ factors[ 1 ] .times( THREE ), factors[ 0 ] };
    }

    @Override
    public String getIrrational( int i, int format )
    {
        if ( format == DEFAULT_FORMAT )
            return "\u221A3";
        else
            return "sqrt(3)";
    }
}
