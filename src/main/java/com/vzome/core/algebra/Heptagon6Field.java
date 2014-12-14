//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class Heptagon6Field extends AlgebraicField
{
    public Heptagon6Field()
    {
        super( "heptagon6", true );
        createRepresentation( RHO, 0, TIMES_RHO, 0, 0 );
        createRepresentation( RHO_INV, 0, DIV_RHO, 0, 0 );
        // TODO make these right, below
        createRepresentation( SIGMA, 0, TIMES_SIGMA, 0, 0 );
        createRepresentation( SIGMA_INV, 0, DIV_SIGMA, 0, 0 );
    };

    private static final double RHO_VALUE = 1.8019377d, SIGMA_VALUE = 2.2469796d;

    private static final double MU_VALUE = Math.sqrt( 3.0d - SIGMA_VALUE );

    // public static final double ALTITUDE = Math.sqrt( SIGMA_VALUE *
    // SIGMA_VALUE - 0.25d );

    private static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;

    public static final int[] ZERO =
        {
                0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] ONE =
        {
                1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] MU =
        {
                0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1
        };

    public static final int[] MU_2 =
        {
                3, 1, 0, 1, -1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] SIGMA =
        {
                0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] SIGMA_2 =
        {
                1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] SIGMA_INV =
        {
                0, 1, - 1, 1, 1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] RHO =
        {
                0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] RHO_2 =
        {
                1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] RHO_INV =
        {
                1, 1, 1, 1, - 1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] RHO_SIGMA =
        {
                0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] RHO_OVER_SIGMA =
        {
                - 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1
        };

    public static final int[] SIGMA_OVER_RHO =
        {
                - 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1
        };

    public double evaluateNumber( int[] representation )
    {
        double result = 0d;
        result += RationalNumbers.getReal( representation, A );
        result += RHO_VALUE * RationalNumbers.getReal( representation, B );
        result += SIGMA_VALUE * RationalNumbers.getReal( representation, C );
        result += MU_VALUE * RationalNumbers.getReal( representation, D );
        result += MU_VALUE * RHO_VALUE * RationalNumbers.getReal( representation, E );
        result += MU_VALUE * SIGMA_VALUE * RationalNumbers.getReal( representation, F );
        return result;
    }

    public void defineMultiplier( StringBuffer buf, int i )
    {
        if ( i == B ) {
            buf.append( "rho = " );
            buf.append( RHO_VALUE );
        }
        if ( i == C ) {
            buf.append( "sigma = " );
            buf.append( SIGMA_VALUE );
        }
        if ( i == D ) {
            buf.append( "mu = " );
            buf.append( MU_VALUE );
        }
    }

    public void getNumberExpression( StringBuffer buf, int[] vector, int coord, int format )
    {
        int order = getOrder();
        buf.append( RationalNumbers.toString( vector, coord * order + A ) );
        buf.append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf.append( "rho*" );
        buf.append( RationalNumbers.toString( vector, coord * order + B ) );
        if ( format == DEFAULT_FORMAT )
            buf.append( " \u03C1" );
        buf.append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf.append( "sigma*" );
        buf.append( RationalNumbers.toString( vector, coord * order + C ) );
        if ( format == DEFAULT_FORMAT )
            buf.append( " \u03C3" );

        buf.append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf.append( "mu*" );
        buf.append( RationalNumbers.toString( vector, coord * order + D ) );
        if ( format == DEFAULT_FORMAT )
            buf.append( " \u03BC" );
        buf.append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf.append( "rho*mu*" );
        buf.append( RationalNumbers.toString( vector, coord * order + E ) );
        if ( format == DEFAULT_FORMAT )
            buf.append( " \u03C1\u03BC" );
        buf.append( " + " );
        if ( format == EXPRESSION_FORMAT )
            buf.append( "sigma*mu*" );
        buf.append( RationalNumbers.toString( vector, coord * order + F ) );
        if ( format == DEFAULT_FORMAT )
            buf.append( " \u03C3\u03BC" );
    }

    private static final String[][] CAYLEY_TABLE =
        {
                    {
                            "a+", "b+", "c+", "d3f-", "e2f-", "f2d-e-"
                    },
                    {
                            "b+", "a+c+", "b+c+", "e2f-", "d2e-f+", "e+f+d-"
                    },
                    {
                            "c+", "b+c+", "a+b+c+", "f2d-e-", "e+f+d-", "d2e-"
                    },
                    {
                            "d+", "e+", "f+", "a+", "b+", "c+"
                    },
                    {
                            "e+", "d+f+", "e+f", "b+", "a+c+", "b+c+"
                    },
                    {
                            "f+", "e+f+", "d+e+f+", "c+", "b+c+", "a+b+c+"
                    },
        };
    
    private static final int[] QUARTER = new int[]{ 1, 4 };

    public void createRepresentation( int[] number, int i, int[][] rep, int j, int k )
    {
        for ( int l = 0; l < 6; l++ )
            for ( int m = 0; m < 6; m++ ) {
                String expr = CAYLEY_TABLE[l][m];
                RationalNumbers.copy( RationalNumbers.ZERO, 0, rep[j+l], m+k );
                int argIndex = - 1;
                for ( int n = 0; n < expr.length(); n++ ) {
                    char ch = expr.charAt( n );
                    switch ( ch ) {
                    case 'a':
                        argIndex = 0;
                        break;
                    case 'b':
                        argIndex = 1;
                        break;
                    case 'c':
                        argIndex = 2;
                        break;
                    case 'd':
                        argIndex = 3;
                        break;
                    case 'e':
                        argIndex = 4;
                        break;
                    case 'f':
                        argIndex = 5;
                        break;
                    case '+':
                        RationalNumbers.add( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
                        break;
                    case '-':
                        RationalNumbers.subtract( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
                        break;
                    case '2':
                        RationalNumbers.add( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
                        RationalNumbers.add( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
                        break;
                    case '3':
                        RationalNumbers.add( rep[j+l], m+k, number, i + argIndex, rep[j+l], m+k );
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

    private static final int[][] TIMES_RHO = new int[6][12], DIV_RHO = new int[6][12];

    private static final int[][] TIMES_SIGMA = new int[6][12], DIV_SIGMA = new int[6][12];

    public int[] createAlgebraicNumber( int ones, int irrat, int denominator, int power )
    {
        int[] result =
            {
                    ones, 1, irrat, 1, 0, 1, 0, 1, 0, 1, 0, 1
            };
        if ( power != 0 ) {
            int[][] factor = ( power > 0 ) ? TIMES_RHO : DIV_RHO;
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
            RationalNumbers.divide( result, D, divisor, 0, result, D );
            RationalNumbers.divide( result, E, divisor, 0, result, E );
            RationalNumbers.divide( result, F, divisor, 0, result, F );
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
        return 2;
    }

    public String getIrrational( int which )
    {
        if ( which == 0 )
            return "\u03C1";
        else
            return "\u03C3";
    }
}
