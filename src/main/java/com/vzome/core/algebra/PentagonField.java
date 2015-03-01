//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

public final class PentagonField extends AlgebraicField
{
    public PentagonField()
    {
        super( "golden" );

        defaultStrutScaling = createAlgebraicNumber( -1, 1, 2, 0 );
    };

    public static final double PHI_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;
    
    // I haven't dug into why this is true, at the moment
    //
    public static final double B1_LENGTH = 2d * PHI_VALUE * PHI_VALUE * PHI_VALUE;

    private static final int ONES_PLACE = 0, PHIS_PLACE = 1;
    
    double evaluateNumber( BigRational[] factors )
    {
        return factors[ ONES_PLACE ] .getReal() + PHI_VALUE * factors[ PHIS_PLACE ] .getReal();
    }

    public int getOrder()
    {
        return 2;
    }

    private final AlgebraicNumber defaultStrutScaling;
    
    public final BigRational[] multiply( BigRational[] v1, BigRational[] v2 )
    {
    	BigRational phis = v1[PHIS_PLACE] .times( v2[ONES_PLACE] ) .plus( v1[ONES_PLACE] .times( v2[PHIS_PLACE] ) ) .plus( v1[PHIS_PLACE] .times( v2[PHIS_PLACE] ) );
    	BigRational ones = v1[ONES_PLACE] .times( v2[ONES_PLACE] ) .plus( v1[PHIS_PLACE] .times( v2[PHIS_PLACE] ) );
    	
    	return new BigRational[]{ ones, phis };
    }

    protected BigRational[] reciprocal( BigRational[] v2 )
    {
        BigRational denominator = v2[0].times(v2[0]) .plus( v2[0].times(v2[1]) ) .minus( v2[1].times(v2[1]) );
        
        BigRational ones = v2[1] .plus( v2[0] ) .divides( denominator );
        BigRational phis = v2[1] .negate() .divides( denominator );
        
        return new BigRational[]{ ones, phis };
    }

    public void defineMultiplier( StringBuffer buf, int which )
    {
        buf.append( "phi = ( 1 + sqrt(5) ) / 2" );
    }

    public AlgebraicNumber getDefaultStrutScaling()
    {
        return defaultStrutScaling;
    }

    public String getIrrational( int which )
    {
        return this .getIrrational( which, DEFAULT_FORMAT );
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        if ( whichIrrational == 0 )
            return factors;
        else
            return new BigRational[]{ factors[ 1 ], factors[ 0 ] .plus( factors[ 1 ] ) };
    }

    @Override
    public String getIrrational( int which, int format )
    {
        if ( format == DEFAULT_FORMAT )
            return "\u03C6";
        else
            return "phi";
    }

    public final AlgebraicVector conjugate( AlgebraicVector v )
    {
        int order = getOrder();
        if ( order != 2 )
            throw new IllegalArgumentException( "method only supported for order-2 fields" );

        AlgebraicVector result = new AlgebraicVector( this, v .dimension() );
        for ( int i = 0; i < v .dimension(); i++ ) {
            result .setComponent( i, this .conjugate( v .getComponent( i ) ) );
        }
        return result;
    }

    private AlgebraicNumber conjugate( AlgebraicNumber component )
    {
        // TODO
//        for ( int i = 0; i < v.length / 2; i += 2 ) {
//            RationalNumbers.negate( v, i + 1, result, i + 0 );
//            RationalNumbers.add( v, i + 0, v, i + 1, result, i + 1 );
//        }
        return component;
    }

    public AlgebraicNumber parseLegacyNumber( String string )
    {
        int div = 1;
        if ( string .startsWith( "(" ) ) {
            int closeParen = string .indexOf( ")" );
            div = Integer .parseInt( string .substring( closeParen+2 ) );
            string = string .substring( 1, closeParen );
        }
        
        int phis = 0;
        int phiIndex = string .indexOf( "phi" );
        if ( phiIndex >= 0 ) {
            String part = string .substring( 0, phiIndex );
            if ( part .length() == 0 )
                phis = 1;
            else if ( part .equals( "-" ) )
                phis = -1;
            else
                phis = Integer .parseInt( part );
            string = string .substring( phiIndex+3 );
        }
        
        int ones;
        if ( string .length() == 0 )
            ones = 0;
        else {
            if ( string .startsWith( "+" ) )
                string = string .substring( 1 );
            ones = Integer .parseInt( string );
        }
        return createAlgebraicNumber( phis, ones, div, 0 );
    }
}
