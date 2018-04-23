
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

public class RootTwoField extends AlgebraicField
{
    public static final String FIELD_NAME = "rootTwo";
    
    /**
     * 
     * @return the coefficients of this AlgebraicField class. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        return new double[] { 1.0d, ROOT_2 };
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public RootTwoField()
    {
        super( FIELD_NAME, 2 );
        defaultStrutScaling = createAlgebraicNumber( 1, 0, 2, -3 );
    };

    @Override
    public void defineMultiplier( StringBuffer buf, int which )
    {
        buf .append( "" );
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
        switch (whichIrrational) {
            case 0:
                return factors;
            case 1:
                return new BigRational[]{ factors[ 1 ] .plus( factors[ 1 ] ), factors[ 0 ] };
            default:
                throw new IllegalArgumentException(whichIrrational + " is not a valid irrational in this field");
        }
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
            int closeParen = string .indexOf( ')' );
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
