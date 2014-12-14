
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;




public class RootTwoField extends AlgebraicField
{
    public static final double ROOT_2 = Math.sqrt( 2d );
    
    private final int[] TWO = { 2,1 };
        
    public RootTwoField()
    {
        super( "rootTwo" );
        createRepresentation( new int[]{ 0,1,1,1 }, 0, TIMES_SQRT2, 0, 0 );
        createRepresentation( new int[]{ 0,1,1,2 }, 0, DIV_SQRT2, 0, 0 );
        
        // we start with 1/2 just because we did in the golden field
        int[] half = new int[]{ 1,2,0,1 };
        half = RationalMatrices .transform( DIV_SQRT2, half );
        half = RationalMatrices .transform( DIV_SQRT2, half );
        defaultStrutScaling = RationalMatrices .transform( DIV_SQRT2, half );
    };

    public void createRepresentation( int[] number, int i, int[][] rep, int j, int k )
    {
        int a = i+0;
        int b = i+1;
        RationalNumbers .copy( number, a, rep[j+0], k+0 ); RationalNumbers .multiply( number, b, TWO, 0, rep[j+0], k+1 );
        RationalNumbers .copy( number, b, rep[j+1], k+0 ); RationalNumbers .copy( number, a, rep[j+1], k+1 );
    }

    public double evaluateNumber( int[] representation )
    {
        return RationalNumbers .getReal( representation, 0 ) + ROOT_2 * RationalNumbers .getReal( representation, 1 );
    }
    
    public void defineMultiplier( StringBuffer buf, int i )
    {
        buf .append( "sqrt2 = sqrt(2)" );
    }
    
    public void getNumberExpression( StringBuffer buf, int[] vector, int coord, int format )
    {
        int order = getOrder();
        buf .append( RationalNumbers .toString( vector, coord * order ) );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "sqrt2*" );
        buf .append( RationalNumbers .toString( vector, coord * order + 1 ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u221A2" );
    }

    public int getOrder()
    {
        return 2;
    }
    
    private static final int[][] TIMES_SQRT2 = new int[2][4], DIV_SQRT2 = new int[2][4];
    
    private final int[] defaultStrutScaling;


    public final int[] createAlgebraicNumber( int ones, int irrat, int denominator, int power )
    {
        int[] result = { ones,1, irrat,1 };
        if ( power != 0 )
        {
            int[][] factor = (power>0)? TIMES_SQRT2 : DIV_SQRT2;
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
        return "\u221A2";
    }
}
