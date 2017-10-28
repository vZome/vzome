
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class SnubDodecField extends AlgebraicField
{    
    public SnubDodecField( AlgebraicField pentField )
    {
        super( "snubDodec", pentField );

        defaultStrutScaling = createAlgebraicNumber( new int[]{ 1, 0, 0, 0, 0, 0 } );
    };
    
    public static final double PHI_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;

    private static final double XI_VALUE = 1.7155615d;
    
    private static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;

    /*
     * Implemented by applying regex changes to Corrado's Mathematica notebook,
     * so it should be bulletproof.
     */
    @Override
    public BigRational[] multiply( BigRational[] a, BigRational[] b )
    {
        BigRational[] result = new BigRational[ this .getOrder() ];

        BigRational factor = a[ A ] .times( b[ A ] );

        factor = factor .plus( a[ B ] .times( b[ B ] ) );
        
        factor = factor .plus( a[ F ] .times( b[ C ] ) );
        
        factor = factor .plus( a[ E ] .times( b[ D ] ) );
        factor = factor .plus( a[ F ] .times( b[ D ] ) );
        
        factor = factor .plus( a[ D ] .times( b[ E ] ) );
        
        factor = factor .plus( a[ C ] .times( b[ F ] ) );
        factor = factor .plus( a[ D ] .times( b[ F ] ) );
        result[ A ] = factor;

        factor = a[ B ] .times( b[ A ] );
        
        factor = factor .plus( a[ A ] .times( b[ B ] ) );
        factor = factor .plus( a[ B ] .times( b[ B ] ) );
        
        factor = factor .plus( a[ E ] .times( b[ C ] ) );
        factor = factor .plus( a[ F ] .times( b[ C ] ) );
        
        factor = factor .plus( a[ E ] .times( b[ D ] ) );
        factor = factor .plus( a[ F ] .times( b[ D ] ) );
        factor = factor .plus( a[ F ] .times( b[ D ] ) ); // doubling
        
        factor = factor .plus( a[ C ] .times( b[ E ] ) );
        factor = factor .plus( a[ D ] .times( b[ E ] ) );
        
        factor = factor .plus( a[ C ] .times( b[ F ] ) );
        factor = factor .plus( a[ D ] .times( b[ F ] ) );
        factor = factor .plus( a[ D ] .times( b[ F ] ) ); // doubling
        result[ B ] = factor;

        
        factor = a[ C ] .times( b[ A ] );
        
        factor = factor .plus( a[ D ] .times( b[ B ] ) );
        
        factor = factor .plus( a[ A ] .times( b[ C ] ) );
        factor = factor .plus( a[ E ] .times( b[ C ] ) );
        factor = factor .plus( a[ E ] .times( b[ C ] ) );  // doubling
        
        factor = factor .plus( a[ B ] .times( b[ D ] ) );
        factor = factor .plus( a[ F ] .times( b[ D ] ) );
        factor = factor .plus( a[ F ] .times( b[ D ] ) );  // doubling
        
        factor = factor .plus( a[ C ] .times( b[ E ] ) );
        factor = factor .plus( a[ C ] .times( b[ E ] ) );  // doubling
        factor = factor .plus( a[ F ] .times( b[ E ] ) );
        
        factor = factor .plus( a[ D ] .times( b[ F ] ) );
        factor = factor .plus( a[ D ] .times( b[ F ] ) );  // doubling
        factor = factor .plus( a[ E ] .times( b[ F ] ) );
        factor = factor .plus( a[ F ] .times( b[ F ] ) );
        result[ C ] = factor;

        factor = a[ D ] .times( b[ A ] );
        
        factor = factor .plus( a[ C ] .times( b[ B ] ) );
        factor = factor .plus( a[ D ] .times( b[ B ] ) );
        
        factor = factor .plus( a[ B ] .times( b[ C ] ) );
        factor = factor .plus( a[ F ] .times( b[ C ] ) );
        factor = factor .plus( a[ F ] .times( b[ C ] ) );  // doubling
        
        factor = factor .plus( a[ A ] .times( b[ D ] ) );
        factor = factor .plus( a[ B ] .times( b[ D ] ) );
        factor = factor .plus( a[ E ] .times( b[ D ] ) );
        factor = factor .plus( a[ E ] .times( b[ D ] ) );  // doubling
        factor = factor .plus( a[ F ] .times( b[ D ] ) );
        factor = factor .plus( a[ F ] .times( b[ D ] ) );  // doubling
        
        factor = factor .plus( a[ D ] .times( b[ E ] ) );
        factor = factor .plus( a[ D ] .times( b[ E ] ) );  // doubling
        factor = factor .plus( a[ E ] .times( b[ E ] ) );
        factor = factor .plus( a[ F ] .times( b[ E ] ) );
        
        factor = factor .plus( a[ C ] .times( b[ F ] ) );
        factor = factor .plus( a[ C ] .times( b[ F ] ) );  // doubling
        factor = factor .plus( a[ D ] .times( b[ F ] ) );
        factor = factor .plus( a[ D ] .times( b[ F ] ) );  // doubling
        factor = factor .plus( a[ E ] .times( b[ F ] ) );
        factor = factor .plus( a[ F ] .times( b[ F ] ) );
        factor = factor .plus( a[ F ] .times( b[ F ] ) );  // doubling
        result[ D ] = factor;

        factor = a[ E ] .times( b[ A ] );
        
        factor = factor .plus( a[ F ] .times( b[ B ] ) );
        
        factor = factor .plus( a[ C ] .times( b[ C ] ) );
        
        factor = factor .plus( a[ D ] .times( b[ D ] ) );
        
        factor = factor .plus( a[ A ] .times( b[ E ] ) );
        factor = factor .plus( a[ E ] .times( b[ E ] ) );
        factor = factor .plus( a[ E ] .times( b[ E ] ) );  // doubling
        
        factor = factor .plus( a[ B ] .times( b[ F ] ) );
        factor = factor .plus( a[ F ] .times( b[ F ] ) );
        factor = factor .plus( a[ F ] .times( b[ F ] ) );  // doubling
        result[ E ] = factor;

        factor = a[ F ] .times( b[ A ] );
        
        factor = factor .plus( a[ E ] .times( b[ B ] ) );
        factor = factor .plus( a[ F ] .times( b[ B ] ) );
        
        factor = factor .plus( a[ D ] .times( b[ C ] ) );
        
        factor = factor .plus( a[ C ] .times( b[ D ] ) );
        factor = factor .plus( a[ D ] .times( b[ D ] ) );
        
        factor = factor .plus( a[ B ] .times( b[ E ] ) );
        factor = factor .plus( a[ F ] .times( b[ E ] ) );
        factor = factor .plus( a[ F ] .times( b[ E ] ) );  // doubling
        
        factor = factor .plus( a[ A ] .times( b[ F ] ) );
        factor = factor .plus( a[ B ] .times( b[ F ] ) );
        factor = factor .plus( a[ E ] .times( b[ F ] ) );
        factor = factor .plus( a[ E ] .times( b[ F ] ) );  // doubling
        factor = factor .plus( a[ F ] .times( b[ F ] ) );
        factor = factor .plus( a[ F ] .times( b[ F ] ) );  // doubling
        result[ F ] = factor;
        
        return result;
    }
    
    @Override
    public void defineMultiplier( StringBuffer buf, int i )
    {
        if ( i == B )
        {
            buf .append( "phi = " );
            buf .append( PHI_VALUE );
        }
        else if ( i == C )
        {
            buf .append( "xi = " );
            buf .append( XI_VALUE );
        }
        else
        {
            buf .append( "" );
        }
    }
    
    @Override
    public int getOrder()
    {
        return 6;
    }

    @Override
    public int getNumIrrationals()
    {
        return 3;
    }

    private final AlgebraicNumber defaultStrutScaling;

    @Override
    public AlgebraicNumber getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    @Override
    public String getIrrational( int which, int format )
    {
        if ( format == DEFAULT_FORMAT )
        {
            switch ( which ) {
            case A:
                return "";

            case B:
                return "\u03C6";

            case C:
                return "\u03BE";

            case D:
                return "\u03C6\u03BE";

            case E:
                return "\u03BE\u00B2";

            case F:
                return "\u03C6\u03BE\u00B2";

            default:
                throw new IllegalArgumentException( which + " is not a valid irrational in this field" );
            }
        }
        else
        {
            switch ( which ) {
            case B:
                return "phi";

            case C:
                return "xi";

            case D:
                return "phi*xi";

            case E:
                return "xi^2";

            case F:
                return "phi*xi^2";

            default:
                throw new IllegalArgumentException( which + " is not a valid irrational in this field" );
            }
        }
    
    }

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        double result = 0d;
        result += factors[ A ] .getReal();
        result += PHI_VALUE * factors[ B ] .getReal();
        result += XI_VALUE * factors[ C ] .getReal();
        result += PHI_VALUE * XI_VALUE * factors[ D ] .getReal();
        result += XI_VALUE * XI_VALUE * factors[ E ] .getReal();
        result += XI_VALUE * XI_VALUE * PHI_VALUE * factors[ F ] .getReal();
        return result;
    }

//  private static final String[][] REPRESENTATION_RECIPE =
//  {
//      {
//          "a+",         "b+",         "f+",         "e+f+",       "d+",      "c+d+"
//      },
//      {
//          "b+",         "a+b+",       "e+f+",       "e+f2",       "c+d+",    "c+d2"
//      },
//      {
//          "c+",         "d+",         "a+e2",       "b+f2",       "f+c2",    "d2e+f+"
//      },
//      {
//          "d+",         "c+d+",       "b+f2",       "a+b+e2f2",   "e+f+d2",  "c2d2e+f2"
//      },
//      {
//          "e+",         "f+",         "c+",         "d+",         "a+e2",    "b+f2"
//      },
//      {
//          "f+",         "e+f+",       "d+",         "c+d+",       "b+f2",    "a+b+e2f2"
//      }
//  };

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        switch ( whichIrrational ) {
        case B:
            return new BigRational[]{ factors[ B ],
                                    factors[ A ] .plus( factors[ B ] ),
                                    factors[ D ],
                                    factors[ C ] .plus( factors[ D ] ),
                                    factors[ F ],
                                    factors[ E ] .plus( factors[ F ] ) };

        case C:
            return new BigRational[]{ factors[ F ],
                                    factors[ E ] .plus( factors[ F ] ),
                                    factors[ A ] .plus( factors[ E ] ) .plus( factors[ E ] ),
                                    factors[ B ] .plus( factors[ F ] ) .plus( factors[ F ] ),
                                    factors[ C ],
                                    factors[ D ] };

        case D:
            return this .scaleBy( this .scaleBy( factors, B ), C );

        case E:
            return this .scaleBy( this .scaleBy( factors, C ), C );

        case F:
            return this .scaleBy( this .scaleBy( factors, D ), C );

        default:
            throw new IllegalArgumentException( whichIrrational + " is not a valid irrational in this field" );
        }
    }
}
