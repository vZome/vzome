
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;



public class RootThreeField extends AlgebraicField
{    
    public RootThreeField()
    {
        super( "rootThree" );
        createRepresentation( new int[]{ 0,1, 1,1 }, 0, TIMES_SQRT3, 0, 0 );
        createRepresentation( new int[]{ 0,1, 1,3 }, 0, DIV_SQRT3, 0, 0 );
        
        // we start with 1/2 just because we did in the golden field
        int[] half = new int[]{ 1,2,0,1 };
        half = RationalMatrices .transform( DIV_SQRT3, half );
        defaultStrutScaling = RationalMatrices .transform( DIV_SQRT3, half );
    };

    private static final double SQRT3 = Math .sqrt( 3d );

    /**
     * Only returns the real part of the complex number.
     */
    public double evaluateNumber( int[] representation )
    {
        return RationalNumbers .getReal( representation, 0 )
            + SQRT3 * RationalNumbers .getReal( representation, 1 );
    }
    
    public void defineMultiplier( StringBuffer buf, int i )
    {
        if ( i == B )
            buf .append( "sqrt3 = sqrt(3)" );
    }
    
    public void getNumberExpression( StringBuffer buf, int[] vector, int coord, int format )
    {
        int order = getOrder();
        buf .append( RationalNumbers .toString( vector, coord * order ) );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "sqrt3*" );
        buf .append( RationalNumbers .toString( vector, coord * order + 1 ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u221A3" );
    }

    private static final int A = 0, B = 1;
    private static final int[][][] STRUCTURE = { { { 1,A }, { 3,B } },
                                                 { { 1,B }, { 1,A } } };
    
    public void createRepresentation( int[] number, int i, int[][] rep, int j, int k )
    {
        int[] factor = { 1,1 };
        for ( int m = 0; m < STRUCTURE.length; m++ )
            for ( int n = 0; n < STRUCTURE[0].length; n++ )
            {
                int[] item = STRUCTURE[ m ][ n ];
                int offset = item[ 1 ];
                if ( item[ 0 ] == 1 )
                    RationalNumbers .copy( number, i+offset, rep[j+m], k+n );
                else
                {
                    factor[ 0 ] = item[ 0 ];
                    RationalNumbers .multiply( factor, 0, number, i+offset, rep[j+m], k+n );
                }
            }
    }

    public int getOrder()
    {
        return 2;
    }

    private static final int[][] TIMES_SQRT3 = new int[2][4], DIV_SQRT3 = new int[2][4];
    
    private final int[] defaultStrutScaling;

    
    public final int[] createAlgebraicNumber( int ones, int irrat, int denominator, int power )
    {
        int[] result = { ones,1, irrat,1 };
        if ( power != 0 )
        {
            int[][] factor = (power>0)? TIMES_SQRT3 : DIV_SQRT3;
            power = Math.abs( power );
            for ( int i = 0; i < power; i++ )
                result = this .transform( factor, result );
        }
        if ( denominator != 1 ) {
            int[] divisor = { denominator, 1 };
            RationalNumbers .divide( result, 0, divisor, 0, result, 0 );
            RationalNumbers .divide( result, 1, divisor, 0, result, 1 );
        }
        return result;
    }
    
    public int[] getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    public String getIrrational( int which )
    {
        return "\u221A3";
    }
}
