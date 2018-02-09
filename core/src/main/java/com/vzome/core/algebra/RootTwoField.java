
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

    @Override
    public void defineMultiplier( StringBuffer buf, int which )
    {
        buf .append( "" );
    }
    
    @Override
    public int getOrder()
    {
        return 2;
    }
    
    public static final double ROOT_2 = Math.sqrt( 2d );
    
    private static final BigRational TWO = new BigRational( 2 );
    
    private final AlgebraicNumber defaultStrutScaling;

    private static final int ONES_PLACE = 0, SQRT2_PLACE = 1;

    @Override
    public final BigRational[] multiply( BigRational[] first, BigRational[]  second )
    {
        BigRational sqrt2s = first[ SQRT2_PLACE ].times( second[ ONES_PLACE ]) .plus( first[ ONES_PLACE ].times( second[ SQRT2_PLACE ]) );
        BigRational ones = first[ ONES_PLACE ].times( second[ ONES_PLACE ] ) .plus( first[ SQRT2_PLACE ].times( second[ SQRT2_PLACE ] ) .times( TWO ) );
        
        return new BigRational[]{ ones, sqrt2s };
    }

    @Override
    public AlgebraicNumber getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    @Override
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
        return factors[ ONES_PLACE ] .evaluate() + ROOT_2 * factors[ SQRT2_PLACE ] .evaluate();
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        if ( whichIrrational == 0 )
            return factors;
        else
            return new BigRational[]{ factors[ 1 ] .plus( factors[ 1 ] ), factors[ 0 ] };
    }
    
    @Override
    public AlgebraicNumber parseLegacyNumber( String string )
    {
        int div = 1;
        boolean hasDiv = string .startsWith( "(" );
        if ( hasDiv )
            string = string .substring( 1 );
        int phiIndex = string .indexOf( "sqrt2" );
        int sqrt2s = 0;
        if ( phiIndex >= 0 ) {
            String part = string .substring( 0, phiIndex );
            if ( part .length() == 0 )
                part = "1";
            else if ( part .equals( "-" ) )
                part = "-1";
            sqrt2s = Integer .parseInt( part );
            string = string .substring( phiIndex+5 );
        }
        if ( hasDiv ) {
            int closeParen = string .indexOf( ")" );
            String part = string .substring( closeParen+2 );
            string = string .substring( 0, closeParen );
            div = Integer .parseInt( part );
        }
        if ( string .length() == 0 )
            string = "0";
        else if ( string .startsWith( "+" ) )
            string = string .substring( 1 );
        int ones = Integer .parseInt( string );
        return createAlgebraicNumber( ones, sqrt2s, div, 0 );
    }
}
