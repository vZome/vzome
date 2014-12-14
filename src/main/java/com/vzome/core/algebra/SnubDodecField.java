
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class SnubDodecField extends AlgebraicField
{    
    public SnubDodecField( AlgebraicField pentField )
    {
        super( "snubDodec", true, pentField );
        createRepresentation( TAU, 0, TIMES_TAU, 0, 0 );
        createRepresentation( TAU_INV, 0, DIV_TAU, 0, 0 );
        createRepresentation( XI, 0, TIMES_XI, 0, 0 );
        createRepresentation( XI_INV, 0, DIV_XI, 0, 0 );
    };
    
    public static final double TAU_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;

    private static final double XI_VALUE = 1.7155615d;
    
    private static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;

    public static final int[] ZERO = { 0,1,0,1,0,1,0,1,0,1,0,1 };
    public static final int[] ONE = { 1,1,0,1,0,1,0,1,0,1,0,1 };
    public static final int[] TAU = { 0,1,1,1,0,1,0,1,0,1,0,1 };
    public static final int[] TAU_INV = { -1,1,1,1,0,1,0,1,0,1,0,1 };
    public static final int[] XI = { 0,1,0,1,1,1,0,1,0,1,0,1 };
    public static final int[] XI_INV = { 2,1,-2,1,0,1,0,1,-1,1,1,1 };

    public static final int[] ALPHA = { -2,1,2,1,1,1,0,1,1,1,-1,1 };
    public static final int[] BETA = { -1,1,1,1,0,1,1,1,1,1,0,1 };

    public double evaluateNumber( int[] representation )
    {
        double result = 0d;
        result += RationalNumbers.getReal( representation, A );
        result += TAU_VALUE * RationalNumbers.getReal( representation, B );
        result += XI_VALUE * RationalNumbers.getReal( representation, C );
        result += TAU_VALUE * XI_VALUE * RationalNumbers.getReal( representation, D );
        result += XI_VALUE * XI_VALUE * RationalNumbers.getReal( representation, E );
        result += XI_VALUE * XI_VALUE * TAU_VALUE * RationalNumbers.getReal( representation, F );
        return result;
    }
    
    public void defineMultiplier( StringBuffer buf, int i )
    {
        if ( i == B )
        {
            buf .append( "tau = " );
            buf .append( TAU_VALUE );
        }
        if ( i == C )
        {
            buf .append( "xi = " );
            buf .append( XI_VALUE );
        }
        if ( i == D )
        {
            buf .append( "tauxi = " );
            buf .append( TAU_VALUE * XI_VALUE );
        }
        if ( i == E )
        {
            buf .append( "xixi = " );
            buf .append( XI_VALUE * XI_VALUE );
        }
        if ( i == F )
        {
            buf .append( "tauxixi = " );
            buf .append( XI_VALUE * XI_VALUE * TAU_VALUE );
        }
    }
    
    public void getNumberExpression( StringBuffer buf, int[] vector, int coord, int format )
    {
        if ( format == AlgebraicField .ZOMIC_FORMAT )
        {
            super .getNumberExpression( buf, vector, coord, format );
            return;
        }
        
        int order = getOrder();
        
        if ( format == AlgebraicField .VEF_FORMAT )
        {
            buf .append( "(" );
            for (int i = F; i >= A; i--)
            {
                buf .append( RationalNumbers .toString( vector, coord * order + i ) );
                if ( i > A )
                    buf .append( "," );
            }
            buf .append( ")" );
            return;
        }
        
        buf .append( RationalNumbers .toString( vector, coord * order + A ) );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "tau*" );
        buf .append( RationalNumbers .toString( vector, coord * order + B ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u03C4" );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "xi*" );
        buf .append( RationalNumbers .toString( vector, coord * order + C ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u03BE" );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "tau*xi*" );
        buf .append( RationalNumbers .toString( vector, coord * order + D ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u03C4*\u03BE" );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "xi^2*" );
        buf .append( RationalNumbers .toString( vector, coord * order + E ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u03BE\u00B2" );
        buf .append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf .append( "tau*xi^2*" );
        buf .append( RationalNumbers .toString( vector, coord * order + F ) );
        if ( format == DEFAULT_FORMAT )
            buf .append( " \u03C4*\u03BE\u00B2" );
    }

    /*
     * Implemented by applying regex changes to Corrado's Mathematica notebook,
     * so it should be bulletproof.
     */
    public int[] multiplyhyy( int[] a, int[] b )
    {
        int [] result = this .origin( 1 );
        int[] prod = new int[]{ 0,1 };

        RationalNumbers .multiply( a, 0, b, 0, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 1, b, 1, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 5, b, 2, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 4, b, 3, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 5, b, 3, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 3, b, 4, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 2, b, 5, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );
        RationalNumbers .multiply( a, 3, b, 5, prod, 0 );
        RationalNumbers .add( result, 0, prod, 0, result, 0 );

        RationalNumbers .multiply( a, 1, b, 0, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 0, b, 1, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 1, b, 1, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 4, b, 2, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 5, b, 2, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 4, b, 3, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 5, b, 3, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 2, b, 4, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 3, b, 4, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        RationalNumbers .multiply( a, 2, b, 5, prod, 0 );
        RationalNumbers .add( result, 1, prod, 0, result, 1 );  
        RationalNumbers .multiply( a, 3, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 1, prod, 0, result, 1 );
        
        RationalNumbers .multiply( a, 2, b, 0, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 3, b, 1, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 0, b, 2, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 4, b, 2, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 1, b, 3, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 5, b, 3, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 2, b, 4, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 5, b, 4, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 3, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 4, b, 5, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );
        RationalNumbers .multiply( a, 5, b, 5, prod, 0 );
        RationalNumbers .add( result, 2, prod, 0, result, 2 );

        RationalNumbers .multiply( a, 3, b, 0, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 2, b, 1, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 3, b, 1, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 1, b, 2, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 5, b, 2, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 0, b, 3, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 1, b, 3, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 4, b, 3, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 5, b, 3, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 3, b, 4, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 4, b, 4, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 5, b, 4, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 2, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 3, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 4, b, 5, prod, 0 );
        RationalNumbers .add( result, 3, prod, 0, result, 3 );
        RationalNumbers .multiply( a, 5, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 3, prod, 0, result, 3 );

        RationalNumbers .multiply( a, 4, b, 0, prod, 0 );
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 5, b, 1, prod, 0 );
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 2, b, 2, prod, 0 );
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 3, b, 3, prod, 0 );
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 0, b, 4, prod, 0 );
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 4, b, 4, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 1, b, 5, prod, 0 );
        RationalNumbers .add( result, 4, prod, 0, result, 4 );
        RationalNumbers .multiply( a, 5, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 4, prod, 0, result, 4 );

        RationalNumbers .multiply( a, 5, b, 0, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 4, b, 1, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 5, b, 1, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 3, b, 2, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 2, b, 3, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 3, b, 3, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 1, b, 4, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 5, b, 4, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 0, b, 5, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 1, b, 5, prod, 0 );
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 4, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 5, prod, 0, result, 5 );
        RationalNumbers .multiply( a, 5, b, 5, prod, 0 );
        RationalNumbers .add( prod, 0, prod, 0, prod, 0 );  // doubling
        RationalNumbers .add( result, 5, prod, 0, result, 5 );

        return result;
    }

    private static final String[][] REPRESENTATION_RECIPE =
    {
        {
            "a+",         "b+",         "f+",         "e+f+",       "d+",      "c+d+"
        },
        {
            "b+",         "a+b+",       "e+f+",       "e+f2",       "c+d+",    "c+d2"
        },
        {
            "c+",         "d+",         "a+e2",       "b+f2",       "f+c2",    "d2e+f+"
        },
        {
            "d+",         "c+d+",       "b+f2",       "a+b+e2f2",   "e+f+d2",  "c2d2e+f2"
        },
        {
            "e+",         "f+",         "c+",         "d+",         "a+e2",    "b+f2"
        },
        {
            "f+",         "e+f+",       "d+",         "c+d+",       "b+f2",    "a+b+e2f2"
        }
    };

    public void createRepresentation( int[] number, int i, int[][] rep, int j, int k )
    {
        for ( int l = 0; l < 6; l++ )
            for ( int m = 0; m < 6; m++ ) {
                String expr = REPRESENTATION_RECIPE[l][m];
                RationalNumbers.copy( RationalNumbers.ZERO, 0, rep[j+l], k+m );
                int argIndex = - 1;
                for ( int n = 0; n < expr.length(); n++ ) {
                    char ch = expr.charAt( n );
                    switch ( ch ) {
                    case 'a':
                        argIndex = A;
                        break;
                    case 'b':
                        argIndex = B;
                        break;
                    case 'c':
                        argIndex = C;
                        break;
                    case 'd':
                        argIndex = D;
                        break;
                    case 'e':
                        argIndex = E;
                        break;
                    case 'f':
                        argIndex = F;
                        break;
                    case '+':
                        RationalNumbers.add( rep[j+l], k+m, number, i + argIndex, rep[j+l], k+m );
                        break;
                    case '2':
                        RationalNumbers.add( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
                        RationalNumbers.add( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
                        break;
                    }
                }
            }
    }

    public int getOrder()
    {
        return 6;
    }

    private static final int[][] TIMES_TAU = new int[6][12], DIV_TAU = new int[6][12];

    private static final int[][] TIMES_XI = new int[6][12], DIV_XI = new int[6][12];

    public int[] createAlgebraicNumber( int ones, int irrat, int denominator, int power )
    {
        int[] result =
            {
                    ones, 1, irrat, 1, 0, 1, 0, 1, 0, 1, 0, 1
            };
        if ( power != 0 ) {
            int[][] factor = ( power > 0 ) ? TIMES_TAU : DIV_TAU;
            power = Math.abs( power );
            for ( int i = 0; i < power; i++ )
                result = RationalMatrices.transform( factor, result );
        }
        if ( denominator != 1 ) {
            int[] divisor =
                {
                        denominator, 1
                };
            RationalNumbers.divide( result, A, divisor, 0, result, A );
            RationalNumbers.divide( result, B, divisor, 0, result, B );
            RationalNumbers.divide( result, C, divisor, 0, result, C );
        }
        return result;
    }

    private static final int[] DEFAULT_STRUT_SCALING = new int[]
        {
                1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1
        };

    public int[] getDefaultStrutScaling()
    {
        return DEFAULT_STRUT_SCALING;
    }

    public int getNumIrrationals()
    {
        return 3;
    }

    public String getIrrational( int which )
    {
        if ( which == 0 )
            return "\u03C4";
        else if ( which == 1 )
            return "\u03BE";
        else
            return "\u03BE\u00B2";
    }
}
