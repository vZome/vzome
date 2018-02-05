
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

public class HeptagonField extends AlgebraicField
{    
    public HeptagonField()
    {
        super( "heptagon", 3 );
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

    @Override
    public void defineMultiplier( StringBuffer buf, int i )
    {
        if ( i == B )
        {
            buf .append( "rho = " );
            buf .append( RHO_VALUE );
        }
        if ( i == C )
        {
            buf .append( "sigma = " );
            buf .append( SIGMA_VALUE );
        }
    }
    
    /**
     * scalar for an affine heptagon
     */
    @Override
    public AlgebraicNumber getAffineScalar() {
        return getUnitTerm( 2 );
    }

    @Override
    public AlgebraicNumber getDefaultStrutScaling()
    {
        return this .one();
    }

    @Override
    public String getIrrational( int which, int format )
    {
        if ( format == DEFAULT_FORMAT )
            if ( which == B )
                return "\u03C1";
            else
                return "\u03C3";
        else
            if ( which == B )
                return "rho";
            else
                return "sigma";
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
        if ( whichIrrational == A )
            return factors;
        else if ( whichIrrational == B )
            return new BigRational[]{ factors[ 1 ], factors[ 0 ] .plus( factors[ 2 ] ), factors[ 1 ] .plus( factors[ 2 ] ) };
        else if ( whichIrrational == C )
            return new BigRational[]{ factors[ 2 ], factors[ 1 ] .plus( factors[ 2 ] ), factors[ 0 ] .plus( factors[ 1 ] ) .plus( factors[ 2 ] ) };
        else 
        	throw new IllegalArgumentException(whichIrrational + " is not a valid irrational in this field");
    }
}
