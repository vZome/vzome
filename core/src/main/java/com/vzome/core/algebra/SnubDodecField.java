
package com.vzome.core.algebra;

public class SnubDodecField extends AbstractAlgebraicField
{
    public static final String FIELD_NAME = "snubDodec";
    
    /**
     * 
     * @return the coefficients of this AlgebraicField class. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        return new double[] { 
            1.0d, 
            PHI_VALUE,
                        XI_VALUE,
            PHI_VALUE * XI_VALUE,
                        XI_VALUE * XI_VALUE,
            PHI_VALUE * XI_VALUE * XI_VALUE
        };
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }

    public SnubDodecField( AlgebraicNumberFactory factory )
    {
        super( FIELD_NAME, 6, factory );
    };
    
    public static final double PHI_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;

    // specified to more precision than a double can retain so that value is as exact as possible: within one ulp().
    public static final double XI_VALUE = 1.71556149969736783d; // root of x^3 -2x -PHI_VALUE 
    
    private static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;
    
    /*
     * Implemented by applying regex changes to Corrado Falcolini's Mathematica notebook,
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
    public int getNumMultipliers()
    {
        return 2; // only two primitive elements, phi and xi
    }

    /**
     * scalar for an affine pentagon
     * @return 
     */
    @Override
    public AlgebraicNumber getAffineScalar()
    {
        return getUnitTerm( 1 );
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(1);
    }
    
    // No need to override convertGoldenNumberPairs() as long as phi is the first irrational

    @Override
    public String getIrrational( int which, int format )
    {
        if ( format == DEFAULT_FORMAT ) {
            switch ( which ) {
            case A:
                return " ";

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
            }
        } else {
            switch ( which ) {
            case A:
                return " ";

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
            }
        }
        throw new IllegalArgumentException( which + " is not a valid irrational in this field" );    
    }

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        double result = 0d;
        result += factors[ A ] .evaluate();
        result += PHI_VALUE * factors[ B ] .evaluate();
        result += XI_VALUE * factors[ C ] .evaluate();
        result += PHI_VALUE * XI_VALUE * factors[ D ] .evaluate();
        result += XI_VALUE * XI_VALUE * factors[ E ] .evaluate();
        result += XI_VALUE * XI_VALUE * PHI_VALUE * factors[ F ] .evaluate();
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
        case A:
        	return factors;
        	
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
