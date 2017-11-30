
package com.vzome.core.algebra;


/*************************************************************************
 *
 *  Immutable ADT for arbitrarily large Rational numbers. 
 * 
 *  Invariants
 *  ----------
 *   -  gcd(num, den) = 1, i.e., rational number is in reduced form
 *   -  den >= 1, i.e., the denominator is always a positive integer
 *   -  0/1 is the unique representation of zero
 *
 *  % java BigRational
 *  5/6
 *  1
 *  1/120000000
 *  1073741789/12
 *  1
 *  841/961
 *  -1/3
 *  0
 *  true
 *  Exception in thread "main" java.lang.RuntimeException: Denominator is zero
 *
 *************************************************************************/

import java.math.BigInteger;

public class BigRational implements Comparable<BigRational>, Fields.BigRationalElement<BigInteger, BigRational> {

    public final static BigRational ZERO = new BigRational(0);
    public final static BigRational ONE = new BigRational(1);

    private BigInteger bigNum;   // the numerator
    private BigInteger bigDen;   // the denominator

    // INVARIANT: either the BigIntegers are null, and the longs are the truth,
    //   or the longs are both == 0, and the BigIntegers are the truth.
    
    private long num;
    private long den;

    // create and initialize a new BigRational object
    public BigRational( long numerator, long denominator )
    {
        init( numerator, denominator );
    }

    // create and initialize a new BigRational object
    public BigRational( long numerator )
    {
        this .num = numerator;
        this .den = 1l;
    }

    // create and initialize a new BigRational object from a string, e.g., "-343/1273"
    public BigRational( String s )
    {
        String[] tokens = s.split("/");
        try {
            if (tokens.length == 2)
                init( Long.parseLong( tokens[0] ), Long.parseLong( tokens[1] ) );
            else if (tokens.length == 1)
                init( Long.parseLong( tokens[0] ), 1l );
            else
                throw new IllegalArgumentException("Parsing error: '" + s + "'");
        }
        catch ( NumberFormatException e )
        {
            if (tokens.length == 2)
                init(new BigInteger(tokens[0]), new BigInteger(tokens[1]));
            else if (tokens.length == 1)
                init(new BigInteger(tokens[0]), BigInteger.ONE);
            else
            	throw new IllegalArgumentException("Parsing error: '" + s + "'");
        }
    }

    // create and initialize a new BigRational object
    public BigRational( BigInteger numerator )
    {
        this( numerator, BigInteger.ONE );
    }

    public BigRational( BigInteger numerator, BigInteger denominator )
    {
        init( numerator, denominator );
    }

    private void init( long numerator, long denominator ) {

        // deal with x / 0
        if ( denominator == 0l ) {
           throw new IllegalArgumentException("Denominator is zero");
        }

        // reduce fraction
        long g = gcd( numerator, denominator );
        this .num = numerator / g;
        this .den = denominator / g;

        // to ensure invariant that denominator is positive
        if ( this .den < 0l ) {
            this .den = - this .den;
            this .num = - this .num;
        }
    }

    private void init( BigInteger numerator, BigInteger denominator )
    {
        // deal with x / 0
        if (denominator.equals(BigInteger.ZERO)) {
           throw new IllegalArgumentException("Denominator is zero");
        }

        // reduce fraction
        BigInteger g = numerator.gcd(denominator);
        bigNum = numerator.divide(g);
        bigDen = denominator.divide(g);

        // to ensure invariant that denominator is positive
        if (bigDen.compareTo(BigInteger.ZERO) < 0) {
            bigDen = bigDen.negate();
            bigNum = bigNum.negate();
        }
    }
    
    private final static long gcd( long u, long v )
    {
        // TODO implement faster binary GCD, ala Knuth 4.5.2 (see the BigInteger implementation)
        u = Math.abs( u );
        v = Math.abs( v );
        while ( v != 0 )
        {
            long r = u % v;
            u = v;
            v = r;
        }
        return u;
    }
    
    // return string representation of (this)
    @Override
    public String toString()
    {
        if ( this.bigNum == null )
        {
            if ( this .den == 1l )
                return this .num + "";
            else
                return this .num + "/" + this .den;
        }
        else
        {
            if ( this .bigDen .equals( BigInteger.ONE ) )
                return bigNum + "";
            else
                return bigNum + "/" + bigDen;
        }
    }
    
    private static final boolean multOverflow( long a, long b )
    {
        return false; // TODO check for overflow and throw an exception
    }

    // return { -1, 0, + 1 } if a < b, a = b, or a > b
    @Override
    public int compareTo( BigRational b )
    {
        BigRational a = this;
        if ( a .bigNum == null && b .bigNum == null )
        {
            if ( multOverflow( a.num, b.den ) || multOverflow( a.den, b.num ) )
                return a .big() .compareTo( b .big() );
            else
                return Long .compare( a.num * b.den, a.den * b.num );
        }
        else
        {
            // either one may be non-big
            a = a .big();
            b = b .big();
            return a .bigNum .multiply( b.bigDen ) .compareTo( a.bigDen .multiply( b.bigNum ) );
        }
    }

    /**
     * Convert to BigInteger form, if necessary.
     * @return
     */
    private BigRational big()
    {
        if ( this .bigNum == null )
            // this is expensive, but should be necessary only very rarely
            return new BigRational( new BigInteger( Long .toString( this .num ) ), new BigInteger( Long .toString( this .den ) ) );
        else
            return this;
    }

    // is this BigRational negative, zero, or positive?
    @Override
    public boolean isZero()     { return this .equals(ZERO); }
    public boolean isPositive() { return compareTo(ZERO)  > 0; }
    public boolean isNegative() { return compareTo(ZERO)  < 0; }

    @Override
    public boolean isOne() { return this.equals(ONE); }
    @Override
    public boolean isBig()      { return bigNum != null; }
    @Override
    public boolean notBig()     { return bigNum == null; }
    
    // is this Rational object equal to y?
    @Override
    public boolean equals( Object y )
    {
        if ( y == this ) return true;
        if ( y == null ) return false;  
        if ( y .getClass() != this .getClass() ) return false;
        BigRational b = (BigRational) y;
        return this .compareTo( b ) == 0;
    }
        
    // hashCode consistent with equals() and compareTo()
    @Override
    public int hashCode()
    {
        return this .toString() .hashCode();
    }

    @Override
    public double evaluate()
    {
        if ( this.bigNum == null )
            return ((double) this .num) / ((double) this .den);
        else
            return this .bigNum .doubleValue() / this .bigDen .doubleValue();
    }

    // return a * b
    @Override
    public BigRational times( BigRational b )
    {
        if ( b .equals( ZERO ) )
            return ZERO;
        if ( b .equals( ONE ) )
            return this;
        BigRational a = this;
        if ( a .bigNum == null && b .bigNum == null )
        {
            if ( multOverflow( a.num, b.num ) || multOverflow( a.den, b.den ) )
                return a .big() .times( b .big() );
            else
                return new BigRational( a.num * b.num, a.den * b.den );
        }
        else
        {
            // either one may be non-big
            a = a .big();
            b = b .big();
            return new BigRational( a .bigNum .multiply( b .bigNum ), a .bigDen .multiply( b .bigDen ) );
        }
    }

    // return a + b
    @Override
    public BigRational plus( BigRational b )
    {
        if ( b .equals( ZERO ) )
            return this;
        BigRational a = this;
        if ( a .bigNum == null && b .bigNum == null )
        {
            if ( multOverflow( a.num, b.den ) || multOverflow( a.den, b.num ) || multOverflow( a.den, b.den ) )
                return a .big() .plus( b .big() );
            else
                return new BigRational( a.num * b.den + a.den * b.num, a.den * b.den );
        }
        else
        {
            // either one may be non-big
            a = a .big();
            b = b .big();
            BigInteger numerator   = a .bigNum .multiply( b .bigDen ) .add( b .bigNum .multiply( a .bigDen ) );
            BigInteger denominator = a .bigDen .multiply( b .bigDen );
            return new BigRational( numerator, denominator );
        }
    }

    @Override
    public BigInteger getNumerator()
    {
        if ( this.bigNum == null )
            return new BigInteger( this .num + "" );
        else
            return this .bigNum;
    }
    
    @Override
    public BigInteger getDenominator()
    {
        if ( this.bigNum == null )
            return new BigInteger( this .den + "" );
        else
            return this .bigDen;
    }
    
    // return -a
    @Override
    public BigRational negate()
    {
        if ( this .equals( ZERO ) )
            return this;
        if ( this.bigNum == null )
        {
            return new BigRational( - this.num, this.den );
        }
        else
        {
            return new BigRational( bigNum .negate(), bigDen );
        }
    }

    // return a - b
    @Override
    public BigRational minus( BigRational b )
    {
        if ( b .equals( ZERO ) )
            return this;
        BigRational a = this;
        return a .plus( b. negate() );
    }

    // return 1 / a
    @Override
    public BigRational reciprocal()
    {
        if ( this .equals( ONE ) )
            return this;
        if ( this.bigNum == null )
        {
            return new BigRational( this.den, this.num );
        }
        else
        {
            return new BigRational( bigDen, bigNum );
        }
    }

    // return a / b
    @Override
    public BigRational dividedBy( BigRational b )
    {
        if ( b .equals( ONE ) )
            return this;
        BigRational a = this;
        return a .times( b .reciprocal() );
    }

}
