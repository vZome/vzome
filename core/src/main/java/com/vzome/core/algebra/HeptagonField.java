
package com.vzome.core.algebra;

public class HeptagonField extends AbstractAlgebraicField
{
    public static final String FIELD_NAME = "heptagon";
    
    /**
     * 
     * @return the coefficients of this AlgebraicField class. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        return new double[] { 1.0d, RHO_VALUE, SIGMA_VALUE };
    }
    
    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }

    public HeptagonField()
    {
        super( FIELD_NAME, 3, AlgebraicNumberImpl.FACTORY );
    };
    
    // specified to more precision than a double can retain so that values are as exact as possible: within one ulp().
    private static final double RHO_VALUE   = 1.80193773580483825d;  // root of x^3 - x^2 -2x +1
    private static final double SIGMA_VALUE = 2.24697960371746706d;  // root of x^3 -2x^2 - x +1

    private static final int A = 0, B = 1, C = 2;
    
    @Override
    public final BigRational[] multiply( BigRational[] first, BigRational[] second )
    {
        BigRational a = first[ A ], b = first[ B ], c = first[ C ];
        BigRational d = second[ A ], e = second[ B ], f = second[ C ];
        BigRational ad = a .times( d ), ae = a .times( e ), af = a .times( f );
        BigRational bd = b .times( d ), be = b .times( e ), bf = b .times( f );
        BigRational cd = c .times( d ), ce = c .times( e ), cf = c .times( f );
        BigRational ones = ad .plus( be ) .plus(  cf );
        BigRational rhos = ae .plus( bd ) .plus(  bf ) .plus(  ce ) .plus(  cf );
        BigRational sigmas = af .plus( be ) .plus(  bf ) .plus(  cd ) .plus(  ce ) .plus(  cf );
        
        return new BigRational[]{ ones, rhos, sigmas };
    }

    /**
     * scalar for an affine heptagon
     * @return 
     */
    @Override
    public AlgebraicNumber getAffineScalar()
    {
        return getUnitTerm( C );
    }

    @Override
    public String getIrrational( int which, int format )
    {
        if ( format == DEFAULT_FORMAT ) {
            switch(which) {
            case A:
                return " ";
            case B:
                return "\u03C1";
            case C:
                return "\u03C3";
            }
        } else {
            switch(which) {
            case A:
                return " ";
            case B:
                return "rho";
            case C:
                return "sigma";
            }
        }
        throw new IllegalArgumentException( which + " is not a valid irrational in this field" );
    }

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        double result = 0d;
        result += factors[ A ] .evaluate();
        result += RHO_VALUE * factors[ B ] .evaluate();
        result += SIGMA_VALUE * factors[ C ] .evaluate();
        return result;
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        switch (whichIrrational) {
            case A:
                return factors;
            case B:
                return new BigRational[]{ factors[ 1 ], factors[ 0 ] .plus( factors[ 2 ] ), factors[ 1 ] .plus( factors[ 2 ] ) };
            case C:
                return new BigRational[]{ factors[ 2 ], factors[ 1 ] .plus( factors[ 2 ] ), factors[ 0 ] .plus( factors[ 1 ] ) .plus( factors[ 2 ] ) };
            default:
                throw new IllegalArgumentException(whichIrrational + " is not a valid irrational in this field");
        }
    }
}
