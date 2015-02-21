
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class RootTwoField extends AlgebraicField
{
    public RootTwoField()
    {
        super( "rootTwo" );
        
        // we start with 1/2 just because we did in the golden field
        defaultStrutScaling = createAlgebraicNumber( 1, 0, 2, -3 );
    };

    public void defineMultiplier( StringBuffer buf, int which )
    {
        buf .append( "" );
    }
    
    public int getOrder()
    {
        return 2;
    }
    
    public static final double ROOT_2 = Math.sqrt( 2d );
    
    private static final BigRational TWO = new BigRational( 2 );
    
    private final AlgebraicNumber defaultStrutScaling;

    private static final int ONES_PLACE = 0, SQRT2_PLACE = 1;

    public final BigRational[] multiply( BigRational[] first, BigRational[]  second )
    {
        BigRational sqrt2s = first[ SQRT2_PLACE ].times( second[ ONES_PLACE ]) .plus( first[ ONES_PLACE ].times( second[ SQRT2_PLACE ]) );
        BigRational ones = first[ ONES_PLACE ].times( second[ ONES_PLACE ] ) .plus( first[ SQRT2_PLACE ].times( second[ SQRT2_PLACE ] ) .times( TWO ) );
        
        return new BigRational[]{ ones, sqrt2s };
    }

    public AlgebraicNumber getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    public String getIrrational( int which, int format )
    {
        if ( format == DEFAULT_FORMAT )
            return "\u221A2";
        else
            return "sqrt(2)";
    }

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        return factors[ ONES_PLACE ] .getReal() + ROOT_2 * factors[ SQRT2_PLACE ] .getReal();
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        if ( whichIrrational == 0 )
            return factors;
        else
            return new BigRational[]{ factors[ 1 ] .plus( factors[ 1 ] ), factors[ 0 ] };
    }
}
