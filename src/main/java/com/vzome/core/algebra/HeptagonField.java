
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;


public class HeptagonField extends AlgebraicField
{    
    public HeptagonField()
    {
        super( "heptagon" );

        SIGMA_INV = fromIntArray( new int[]{ 0,1,0,1,1,1 } ) .reciprocal();
    };
    
    private static final double RHO_VALUE = 1.8019377d, SIGMA_VALUE = 2.2469796d;
    
    public static final double ALTITUDE = Math.sqrt( SIGMA_VALUE * SIGMA_VALUE - 0.25d );

    private static final int A = 0, B = 1, C = 2;
    
    private final AlgebraicNumber SIGMA_INV;
    
    private AlgebraicNumber fromIntArray( int[] ints )
    {
        return new AlgebraicNumber( this, new BigRational( ints[0], ints[1] ), new BigRational( ints[2], ints[3] ), new BigRational( ints[4], ints[5] ) );
    }
    
    public AlgebraicNumber sigmaReciprocal()
    {
        return SIGMA_INV;
    }
    
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
    
    public int getOrder()
    {
        return 3;
    }

    public AlgebraicNumber getDefaultStrutScaling()
    {
        return this .one();
    }

    public int getNumIrrationals()
    {
        return 2;
    }

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
        result += factors[ A ] .getReal();
        result += RHO_VALUE * factors[ B ] .getReal();
        result += SIGMA_VALUE * factors[ C ] .getReal();
        return result;
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        if ( whichIrrational == A )
            return factors;
        else if ( whichIrrational == B )
            return new BigRational[]{ factors[ 1 ], factors[ 0 ] .plus( factors[ 2 ] ), factors[ 1 ] .plus( factors[ 2 ] ) };
        else
            return new BigRational[]{ factors[ 2 ], factors[ 1 ] .plus( factors[ 2 ] ), factors[ 0 ] .plus( factors[ 1 ] ) .plus( factors[ 2 ] ) };
    }
}
