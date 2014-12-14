
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.math;


class RootTwoNumber extends GoldenNumber
{
    public static final double SQUARE_ROOT_OF_TWO = Math.sqrt( 2d );

    public static final RootTwoNumber ZERO = new RootTwoNumber( 0, 0, 1, 0 );
    
    public static final RootTwoNumber ONE = new RootTwoNumber( 0, 1, 1, 0 );
    
    public static final RootTwoNumber ROOT_TWO = new RootTwoNumber( 1, 0, 1, 0 );

    public static final GoldenNumber TWO = new RootTwoNumber( 0, 2, 1, 0 );
    
    static IntegralNumber.Factory R2FACTORY; // set by the factory, to avoid circular dependency

    
    public RootTwoNumber( int rootTwos, int ones, int divisor, int power )
    {
        super( rootTwos, ones, divisor, power );
    }

    protected IntegralNumber multiply( IntegralNumber multiplicand )
    {
        // MUST NOT denormalize or normalize here!
        RootTwoNumber mcand = (RootTwoNumber) multiplicand;
        int a1 = mcand .phis;
        int b1 = mcand .ones;
        int a2 = phis;
        int b2 = ones;
        return new RootTwoNumber( a1*b2 + a2*b1, 2*a1*a2 + b1*b2, divisor * mcand .divisor, power + mcand .power );
    }
    
    public  IntegralNumber plus( IntegralNumber arg )
    {
        RootTwoNumber addend = (RootTwoNumber) arg;
        if ( addend .equals( ZERO ) )
            return this;
        if ( this .equals( ZERO ) )
            return addend;
        if ( power == addend.power )
            if ( divisor == addend .divisor )
                return new RootTwoNumber( phis + addend .phis, ones + addend .ones, divisor, power );
            else
                return new RootTwoNumber( addend.divisor*phis + divisor*addend.phis, addend.divisor*ones + divisor*addend.ones,
                                            divisor*addend.divisor, power ) .normalize();
        else {
            boolean addendBigger = addend .power > power;
            RootTwoNumber bigger = addendBigger? addend : this;
            RootTwoNumber smaller = addendBigger? this : addend;
            int diff = bigger .power - smaller .power;
            RootTwoNumber factor = sqrt2( diff );
            bigger = (RootTwoNumber) bigger .scale( -diff );
            return smaller .plus( factor .multiply( bigger) );
        }
    }



    public IntegralNumber inverse()
    {
        return new RootTwoNumber( divisor*phis, -divisor*ones, determinant(), -power );
    }
    
    
    public int determinant()
    {
        return 2*phis*phis - ones*ones;
    }

    /**
     * Normalizes for all but DEFAULT_FORMAT
     */
    public String toString( int format )
    {
        RootTwoNumber num = (RootTwoNumber) normalize();
        int A = num .getPhis();
        int B = num .getOnes();
        String result = "";
        switch ( format ) {

        case ZOMIC_FORMAT:
            return B + " " + A;

        case VEF_FORMAT:
            return "(" + A + "," + B + ")";

        case EXPRESSION_FORMAT:
            result = "*sqrt2";
            if ( A == 0 ) {
                if ( B == 0 )
                    return "0";
                result = Integer .toString( B );
            }
            else {
                result = A + result;
                if ( B != 0 )
                    if ( B < 0 )
                        result += B;
                    else
                        result += "+" + B;
            }
            int divisor = num .getDivisor();
            if ( divisor != 1 )
                result = "(" + result + ")/" + divisor;
            return result;
            
        case DEFAULT_FORMAT:
        default:
            result = "sqrt2";
            if ( this.phis == 0 ) {
                if ( this.ones == 0 )
                    return "0";
                result = Integer .toString( this.ones );
            }
            else {
                if ( phis == - 1 ) {
                    result = "-" + result;
                }
                else {
                    if ( phis != 1 ) {
                        result = phis + result;
                    }
                }
                if ( ones != 0 ) {
                    if ( ones < 0 ) {
                        result += ones;
                    }
                    else {
                        result += "+" + ones;
                    }
                }
            }
            if ( this.divisor != 1 || power != 0 ) {
                result = "(" + result + ")";
                if ( this.divisor != 1 )
                    result += "/" + this.divisor;
                if ( power != 0 )
                    result = "sqrt2^" + power + "*" + result;
            }
            return result;
        }
    }
    
    protected IntegralNumber normalize()
    {
        if ( normalized == null ) {
            if ( power == 0 && divisor == 1 )
                normalized = this;
            normalized = new RootTwoNumber( phis, ones, divisor, 0 );
            if ( power != 0 ) {
                // normalize is called by equals(), which is called by most public methods,
                //   so we cannot call times() or tau() here.
                normalized = (RootTwoNumber) normalized .multiply( sqrt2( power ) );
            }
            // TODO reduce fractions correctly
            while ( normalized.divisor % 2 == 0 && (normalized.phis % 2 == 0) && (normalized.ones % 2 == 0) )
            {
                normalized = new RootTwoNumber( normalized.phis/2, normalized.ones/2, normalized.divisor/2, 0 );
            }
        }
        return normalized;
    }
    
    public RootTwoNumber sqrt2( int power )
    {
        boolean invert = power < 0;
        if ( invert )
            power = -power;
        RootTwoNumber result;
        if ( power % 2==0 )
            result = new RootTwoNumber( 0, (int) Math .pow( 2, power/2 ), 1, 0 );
        else
            result = new RootTwoNumber( (int) Math .pow( 2, power/2 ), 0, 1, 0 );
        if ( invert )
            return (RootTwoNumber) result .inverse();
        else
            return result;
    }

    public double value()
    {
        if ( this .equals( ZERO ) )
            return 0d;
        if ( mValue == 0d ) {
            mValue = phis * SQUARE_ROOT_OF_TWO + ones;
            if ( divisor != 1 )
                mValue /= divisor;
            if ( power != 0 )
                mValue *= sqrt2( power ) .value();
        }
        return mValue;
    }


    public Factory getFactory()
    {
        return R2FACTORY;
    }
}
