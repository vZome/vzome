//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

public final class PentagonField extends AlgebraicField
{
    public PentagonField()
    {
        super( "golden" );
        createRepresentation( new int[] { 0, 1, 1, 1 }, 0, TIMES_TAU, 0, 0 );
        createRepresentation( new int[] { - 1, 1, 1, 1 }, 0, DIV_TAU, 0, 0 );

        int[] half = new int[] { 1, 2, 0, 1 };
        half = RationalMatrices.transform( DIV_TAU, half );
        defaultStrutScaling = RationalMatrices.transform( DIV_TAU, half );
    };

    public static final double TAU_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;
    
    // I haven't dug into why this is true, at the moment
    //
    public static final double B1_LENGTH = 2d * TAU_VALUE * TAU_VALUE * TAU_VALUE;

    public void createRepresentation( int[] number, int i, int[][] rep, int j, int k )
    {
        int a = i + 0;
        int b = i + 1;
        RationalNumbers.copy( number, a, rep[j + 0], k + 0 );
        RationalNumbers.copy( number, b, rep[j + 0], k + 1 );
        RationalNumbers.copy( number, b, rep[j + 1], k + 0 );
        RationalNumbers .add( number, a, number, b, rep[j + 1], k + 1 );
    }

    public double evaluateNumber( int[] representation )
    {
        return RationalNumbers.getReal( representation, 0 ) + TAU_VALUE * RationalNumbers.getReal( representation, 1 );
    }

    public void getNumberExpression( StringBuffer buf, int[] vector, int coord, int format )
    {
        int order = getOrder();
        int offset = ( format == VEF_FORMAT ) ? 1 : 0;
        if ( format == VEF_FORMAT )
            buf.append( "(" );
        buf.append( RationalNumbers.toString( vector, coord * order + offset % 2 ) );
        if ( format == VEF_FORMAT )
            buf.append( "," );
        else
            buf.append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf.append( "tau*" );
        buf.append( RationalNumbers.toString( vector, coord * order + ( offset + 1 ) % 2 ) );
        if ( format == DEFAULT_FORMAT )
            buf.append( " \u03C4" );
        if ( format == VEF_FORMAT )
            buf.append( ")" );
    }

    public int getOrder()
    {
        return 2;
    }

    private static final int[][] TIMES_TAU = new int[2][4], DIV_TAU = new int[2][4];

    private final int[] defaultStrutScaling;

    public final int[] createAlgebraicNumber( int ones, int taus, int denominator, int scale )
    {
        int[] result = { ones, 1, taus, 1 };
        if ( scale != 0 ) {
            int[][] factor = ( scale > 0 ) ? TIMES_TAU : DIV_TAU;
            scale = Math.abs( scale );
            for ( int i = 0; i < scale; i++ )
                result = RationalMatrices.transform( factor, result );
        }
        if ( denominator != 1 ) {
            int[] divisor = { denominator, 1 };
            RationalNumbers.divide( result, 0, divisor, 0, result, 0 );
            RationalNumbers.divide( result, 1, divisor, 0, result, 1 );
        }
        return result;
    }
    
    public final BigRational[] multiply( BigRational[] v1, BigRational[] v2 )
    {
    	BigRational phis = v1[1].times(v2[0]) .plus( v1[0].times(v2[1]) ) .plus( v1[1].times(v2[1]) );
    	BigRational ones = v1[0].times(v2[0]) .plus( v1[1].times(v2[1]) );
    	
    	return new BigRational[]{ ones, phis };
    }
    
    public final BigRational[] divide( BigRational[] v1, BigRational[] v2 )
    {
    	BigRational denominator = v2[0].times(v2[0]) .plus( v2[0].times(v2[1]) ) .minus( v2[1].times(v2[1]) );
    	
    	BigRational ones = v1[0].times(v2[1]) .plus( v1[0].times(v2[0]) ) .minus( v1[1].times(v2[1]) ) .divides( denominator );
    	BigRational phis = v2[0].times(v1[1]) .minus( v1[0].times(v2[1]) ) .divides( denominator );
    	
    	return new BigRational[]{ ones, phis };
    }
    
    public final int[] divide( int[] v1, int[] v2 )
    {
    	BigRational[] v1br = this .makeBigElement( v1 );
    	BigRational[] v2br = this .makeBigElement( v2 );
    	
    	BigRational[] quotient = this .divide( v1br, v2br );
    	    	
    	return new int[]{ quotient[0].intNumerator(), quotient[0].intDenominator(), quotient[1].intNumerator(), quotient[1].intDenominator() };
    }

    public void defineMultiplier( StringBuffer buf, int i )
    {
        buf.append( "tau = ( 1 + sqrt(5) ) / 2" );
    }

    public int[] getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    public String getIrrational( int which )
    {
        return "\u03C4";
    }
}
