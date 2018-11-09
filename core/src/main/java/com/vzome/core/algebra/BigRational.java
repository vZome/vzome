package com.vzome.core.algebra;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Immutable Abstract Data Type for arbitrarily large rational numbers. 
 * 
 *  Invariants
 *  ----------
 *   -  gcd(numerator, denominator) = 1, i.e., rational number is in reduced form
 *   -  denominator >= 1, i.e., the denominator is always a positive integer
 *   -  0/1 is the unique representation of zero
 *   -  Either numerator and denominator will both be long or they will both be big
 *   -  When numerator and denominator are big, then both long values will be 0.
 *   -  numerator and denominator will both be represented by a long whenever possible
 *   -  unless either numerator or denominator equals Long.MIN_VALUE in which case they will both be big.
 *   -  A denominator of 0 will throw an IllegalArgumentException. 
 */
public class BigRational implements Comparable<BigRational>, Fields.BigRationalElement<BigInteger, BigRational>
{
    // indices into arrays used by reduce() and various c'tors 
    private final static int NUM = 0;
    private final static int DEN = 1;

    public final static BigRational ZERO = new BigRational(0);
    public final static BigRational ONE = new BigRational(1);

    private final long num;
    private final long den;
    private final BigInteger bigNum;  	// the big numerator
    private final BigInteger bigDen;  	// the big denominator

    private final boolean isOne;
    private final boolean isZero;
    private final boolean isWhole;
    private final boolean canAddInteger;
    private final boolean canMultiplyInteger;
    private final int signum;

    private final String toString; 
    private Integer hashCode = null;	// initialized on first use
    private Double doubleValue = null;	// initialized on first use

    // INVARIANT: either the BigIntegers are null, and longs are the truth,
    //   or the longs are 0, and the BigIntegers are the truth.
    // Therefore, whenever the long denominator is 0, the representation is using BigIntegers.
    // Conversely, whenever the long denominator is not 0, representation is using longs.

    private static boolean isOne(BigRational that) {
        return that.num == 1 && that.den == 1;
    }

    private static boolean isZero(BigRational that) {
        return that.num == 0 && that.den == 1;
    }

    private static boolean isWhole(BigRational that) {
        return that.den == 1 || (that.den == 0 && that.bigDen.equals(BigInteger.ONE));
    }

    private static int signum(BigRational that) { 
        return that.bigNum == null 
                ? Long.signum(that.num) 
                        : that.bigNum.signum(); 
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public BigRational( long numerator )
    {
        // no need to reduce since demoninator == 1
        if(numerator != Long.MIN_VALUE) {
            num = numerator;
            den = 1;
            bigNum = null;
            bigDen = null;
        } else {
            num = 0;
            den = 0;
            bigNum = BigInteger.valueOf(numerator);
            bigDen = BigInteger.ONE;
        }
        isZero = isZero(this);
        isOne = isOne(this);
        isWhole = isWhole(this);
        signum = signum(this);
        canAddInteger = canAddInteger(this);
        canMultiplyInteger = canMultiplyInteger(this);
        toString = toString(this);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public BigRational( long numerator, long denominator )
    {
        long[] factors = new long[] {numerator, denominator};
        if ( reduce(factors) ) {
            num = factors[NUM];
            den = factors[DEN];
            bigNum = null;
            bigDen = null;
        } else {
            num = 0;
            den = 0;
            bigNum = BigInteger.valueOf(factors[NUM]);
            bigDen = BigInteger.valueOf(factors[DEN]);
        }
        isZero = isZero(this);
        isOne = isOne(this);
        isWhole = isWhole(this); 
        signum = signum(this);
        canAddInteger = canAddInteger(this);
        canMultiplyInteger = canMultiplyInteger(this);
        toString = toString(this);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public BigRational( BigInteger numerator )
    {
        // no need to reduce since demoninator == 1
        if(fitsInLong(numerator)) {
            num = numerator.longValue();
            den = 1;
            bigNum = null;
            bigDen = null;
        } else {
            num = 0;
            den = 0;
            bigNum = numerator;
            bigDen = BigInteger.ONE;
        }
        isZero = isZero(this);
        isOne = isOne(this);
        isWhole = isWhole(this);
        signum = signum(this);
        canAddInteger = canAddInteger(this);
        canMultiplyInteger = canMultiplyInteger(this);
        toString = toString(this);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public BigRational( BigInteger numerator, BigInteger denominator )
    {
        BigInteger[] factors = new BigInteger[] {numerator, denominator};
        if (!reduce(factors) ) {
            num = factors[NUM].longValue(); // consider longValueExact() if there's any question
            den = factors[DEN].longValue(); // consider longValueExact() if there's any question
            bigNum = null;
            bigDen = null;
        } else {
            num = 0;
            den = 0;
            bigNum = factors[NUM];
            bigDen = factors[DEN];
        }
        isZero = isZero(this);
        isOne = isOne(this);
        isWhole = isWhole(this);
        signum = signum(this);
        canAddInteger = canAddInteger(this);
        canMultiplyInteger = canMultiplyInteger(this);
        toString = toString(this);
    }

    public BigRational( BigInteger numerator, long denominator )
    {
        this(numerator, BigInteger.valueOf(denominator));
    }

    public BigRational( long numerator, BigInteger denominator )
    {
        this(BigInteger.valueOf(numerator), denominator);
    }

    /**
     * 
     * @param numerator
     * @param denominator
     * @param bigNumerator
     * @param bigDenominator
     * Used internally to construct a new BigInteger from values that are known to already be validated and reduced
     */
    @SuppressWarnings("LeakingThisInConstructor")
    private BigRational( long numerator, long denominator, BigInteger bigNumerator, BigInteger bigDenominator )
    {
        num = numerator;
        den = denominator;
        bigNum = bigNumerator;
        bigDen = bigDenominator;
        isZero = isZero(this);
        isOne = isOne(this);
        isWhole = isWhole(this);
        signum = signum(this);
        canAddInteger = canAddInteger(this);
        canMultiplyInteger = canMultiplyInteger(this);
        toString = toString(this);
    }

    public static String stripLeadingZeros(String str) {
        if(str.startsWith("+")) {
            str = str.substring(1); // strip an optional leading + sign
        }
        str = str.replaceFirst("^-?0+(?!$)", ""); // skips over an optional leading - sign
        return str.equals("-0") ? "0" : str;
    }

    // create and initialize from a numeric string formatted as an integer or a fraction, e.g. "-343/1273"
    // Long.MIN_VALUE = -9223372036854775808 : 20 characters
    // Long.MAX_VALUE =  9223372036854775807 : 19 characters
    // So 18 characters will definitely fit in a long. 
    // 19 or more characters will be parsed as a BigInteger and reduced if possible.
    // check the string length after removing leading 0's but leaving any optional - or + sign
    // Avoid the overhead of reducing whenever we know it's absolutely unnecessary.
    @SuppressWarnings("LeakingThisInConstructor")
    public BigRational( String s )
    {
        final int definteLong = 18;
        String[] tokens = s.split("/");
        switch(tokens.length) {
        case 1:
            tokens[NUM] = stripLeadingZeros(tokens[NUM]);
            if(tokens[NUM].length() <= definteLong) {
                // will obviously fit in a long.
                // no need to reduce
                num = Long.parseLong( tokens[NUM] );
                den = 1;
                bigNum = null;
                bigDen = null;
            } else {
                // won't know if it can be long until it's validated 
                // and possibly reduced to long by the temp c'tor
                BigRational temp = new BigRational( new BigInteger( tokens[NUM] ) );
                num = temp.num;
                den = temp.den;
                bigNum = temp.bigNum;
                bigDen = temp.bigDen;
            }
            isZero = isZero(this);
            isOne = isOne(this);
            isWhole = isWhole(this);
            signum = signum(this);
            canAddInteger = canAddInteger(this);
            canMultiplyInteger = canMultiplyInteger(this);
            toString = toString(this);
            return;

        case 2:
        {
            tokens[NUM] = stripLeadingZeros(tokens[NUM]);
            tokens[DEN] = stripLeadingZeros(tokens[DEN]);
            // TODO: Add test case to ensure that stripLeadingZeros{} will never return "-0"
            // check for zero in the denominator
            if(tokens[DEN].equals("0")) { 
                throw new IllegalArgumentException("Denominator is zero");
            }

            BigRational temp = null;
            if(tokens[NUM].length() <= definteLong && tokens[DEN].length() <= definteLong) {
                // will obviously fit in a long
                if(tokens[DEN].equals("1") || tokens[NUM].equals("1")) {
                    // no need to reduce
                    temp = new BigRational( Long.parseLong( tokens[NUM] ), Long.parseLong( tokens[DEN] ), null, null );
                } else {
                    // may need to be reduced
                    temp = new BigRational( Long.parseLong( tokens[NUM] ), Long.parseLong( tokens[DEN] ) );
                }
            }
            if(temp == null) {
                if(tokens[DEN].equals("1") || tokens[NUM].equals("1")) {
                    // no need to reduce
                    temp = new BigRational( 0, 0, new BigInteger( tokens[NUM] ), new BigInteger( tokens[DEN] ) );
                } else {
                    // won't know if it can be long until it's validated and reduced by the temp c'tor
                    temp = new BigRational( new BigInteger( tokens[NUM] ), new BigInteger( tokens[DEN] ) );
                }
            }
            num = temp.num;
            den = temp.den;
            bigNum = temp.bigNum;
            bigDen = temp.bigDen;        	
            isZero = temp.isZero;
            isOne = temp.isOne;
            isWhole = temp.isWhole;
            signum = temp.signum;
            canAddInteger = temp.canAddInteger;
            canMultiplyInteger = temp.canMultiplyInteger;
            toString = temp.toString;
            return;
        }
        }
        throw new IllegalArgumentException("Parsing error: '" + s + "'");
    }

    /**
     * 
     * @param factors an array of two long integers.
     * factors[0] is the numerator
     * factors[1] is the denominator
     * The fraction is reduced in place, so the array will contain the reduced fraction.
     * The denominator will always be positive when true is returned.
     * @return false if either factor equals Long.MIN_VALUE after being reduced, otherwise true.
     * The factors will be unchanged if false is returned. 
     * @throws IllegalArgumentException if denominator is zero
     */
    private static boolean reduce(long[] factors) {
        long numerator = factors[NUM];
        long denominator = factors[DEN];
        if(denominator == 0) {
            throw new IllegalArgumentException("Denominator is zero");
        }
        if (denominator == 1) {
            // assert(numerator != Long.MIN_VALUE);
            return true;
        }
        if (numerator == 0) {
            factors[DEN]= 1;
            return true;
        }

        if(numerator == Long.MIN_VALUE || denominator == Long.MIN_VALUE ) {
            // Long.MIN_VALUE is an even number
            // If numerator and denominator are both even, 2 is a common denominator, even if not the gcd
            if(numerator % 2 == denominator % 2) {
                // both are even numbers so halve both of them 
                // so that neither will equal Long.MIN_VALUE.
                numerator = numerator / 2;
                denominator = denominator / 2;
            } else {
                // Long.MIN_VALUE is an even power of two. 
                // Since the other factor is odd, the gcd is 1 
                // and the reduced fraction will have Long.MIN_VALUE as a factor.
                return false;
            }
        }

        int sign = 1;
        if(numerator < 0 ^ denominator < 0) {
            sign = -1;
            numerator = Math.abs(numerator);
            denominator = Math.abs(denominator);
        }

        // reduce fraction
        long g = Gcd.gcd( numerator, denominator ); 
        numerator = numerator / g;
        denominator = denominator / g;

        // ensure invariant that denominator is positive
        factors[NUM] = Math.abs(numerator) * sign;
        factors[DEN] = Math.abs(denominator);
        return true;
    }

    /**
     * 
     * @param n BigRational to be evaluated 
     * @return true if any Integer can be added to n without BigInteger representation
     */
    static boolean canAddInteger(BigRational n) {
        return( n.den == 1 
                && n.num < Long.MAX_VALUE - Integer.MAX_VALUE
                && n.num > Long.MIN_VALUE - Integer.MIN_VALUE );
    }

    /**
     * 
     * @param n BigRational to be evaluated 
     * @return true if any Integer can be multiplied by n without BigInteger representation 
     */
    static boolean canMultiplyInteger(BigRational n) {
        return( n.den == 1 
                && n.num < Integer.MAX_VALUE
                && n.num > Integer.MIN_VALUE );
    }

    /**
     * 
     * @return isWhole() && fitsInLong( getNumerator() );
     */
    public boolean fitsInLong() {
        return isWhole() && fitsInLong( getNumerator() );
    }
    /**
     * 
     * @param n BigInteger to be tested
     * @return Long.MIN_VALUE < n <= Long.MAX_VALUE; in other words, n.abs() <= Long.MAX_VALUE
     * Note that a value of Long.MIN_VALUE will return false even though n would technically fit in a long.
     * This is intentional.
     * A return value of true also means that n.longValueExact() will not throw an exception
     */
    static boolean fitsInLong(BigInteger n) {
        final int SIGNED_LONG_BIT_LENGTH = Long.SIZE - 1;  // number of bits in long minus one for the sign bit = 63
        // TODO: I expect that testing the bitLength is significantly faster than using compareTo(), 
        // but this should be verified with some unit tests and benchmarks
        int bitLength = n.bitLength();
        if( bitLength < SIGNED_LONG_BIT_LENGTH) { 
            return true; // test for most common case first
        }    	
        if( bitLength > SIGNED_LONG_BIT_LENGTH) { 
            return false; // definitely too big
        }    	
        // assert( bitLength == SIGNED_LONG_BIT_LENGTH);     	
        return (n.signum() == -1)
                ? n.compareTo(MIN_LONG) != -1
                : true;
    }

    // static variable avoids the overhead of BigInteger c'tor in the fitsInLong() method 
    private final static BigInteger MIN_LONG = BigInteger.valueOf(-Long.MAX_VALUE); // same as Long.MIN_VALUE + 1;

    /**
     * 
     * @param factors an array of two BigIntegers.
     * factors[0] is the numerator
     * factors[1] is the denominator
     * The fraction is reduced in place, so the array will contain the reduced fraction.
     * The denominator will always be positive when true is returned.
     * @return true if the reduced fraction requires a BigInteger representation. 
     * In other words, if the absolute value of either factor is greater than Long.MAX_VALUE after being reduced. 
     * Returns false if both factors will fit in a long and neither equals Long.MIN_VALUE.
     * The factors will be less than Long.MAX_VALUE if false is returned.
     * @throws IllegalArgumentException if denominator is zero
     */
    private static boolean reduce(BigInteger[] factors) {
        BigInteger numerator = factors[NUM];
        BigInteger denominator = factors[DEN];
        if(denominator.signum() == 0) {
            throw new IllegalArgumentException("Denominator is zero");
        }
        if (denominator.equals(BigInteger.ONE)) {
            return !fitsInLong(numerator);
        }
        if (numerator.signum() == 0) {
            factors[DEN]= BigInteger.ONE;
            return false;
        }

        boolean isNegative = false;
        if(numerator.signum() == -1) {
            isNegative = true;
            numerator = numerator.abs();
        }
        if(denominator.signum() == -1) {
            isNegative = !isNegative;
            denominator = denominator.abs();
        }

        // reduce fraction
        BigInteger g = numerator.gcd(denominator); 
        numerator = numerator.divide(g);
        denominator = denominator.divide(g);

        // ensure invariant that denominator is positive
        factors[NUM] = isNegative ? numerator.negate() : numerator;
        factors[DEN] = denominator;
        return !fitsInLong(numerator) || !fitsInLong(denominator);
    }

    public final static class Gcd {
        private Gcd() {}
        // LOOKUP_SIZE must be a positive power of two between 1 and 256.
        // If it's too small, then the overhead of checking if the parameters are 
        // in the cached range negates any performance gained by the cache.
        // 256 requires 64K of memory and is probably overkill for most models. 
        // 128 requires 16K of memory.
        //  64 requires  4K of memory but is probably adequate for most models.
        // Even the bigest H4 polytope with all of its reflections enabled 
        // doesn't have any terms greater than 75, 
        // so a cache size of 128 would probably cover nearly all models. 
        static final int LOOKUP_SIZE = 128; 
        static final long LOOKUP_MASK = ~(LOOKUP_SIZE-1);
        private static final byte[][] LOOKUP_TABLE = calculateLookupTable(LOOKUP_SIZE);

        /**
         * 
         * @param size must be a positive power of two between 1 and 256. 64 or 128 is typical. 
         * @return initialized byte[size][size]
         */
        static byte[][] calculateLookupTable(int size)
        {
            //			long start = System.nanoTime();
            byte[][] result = new byte[size][size];
            for(int j = 0; j < size; j++) {
                for(int k = j; k < size; k++) {
                    result[j][k] = (byte) calculateGcd(j,k);
                    // table is symetrical diagonally, so just calculate once and copy it.
                    result[k][j] = result[j][k]; 
                }
            }
            //			double duration = (System.nanoTime() - start) / 1000000000D;
            //			System.out.println("GCD cache uses " + (size * size) + " bytes");
            //			System.out.printf("Initializing %1$d x %1$d GCD cache took %2$9.9f seconds.\n", size, duration);
            return result;
        }

        public static long gcd(long j, long k)
        {
            // The invocations gcd(Long.MIN_VALUE, Long.MIN_VALUE), gcd(Long.MIN_VALUE, 0L) 
            // and gcd(0L, Long.MIN_VALUE) should throw an ArithmeticException, 
            // because the result would be 2^63, which is too large for a long value.
            // But since this methos is only intended to be used internally by the BigRational class
            // and since it has adequate checks elsewhere to ensure that these conditions don't happen
            // I am going to comment out the exception tests since I am convinced that the BigRational class
            // can use it safely without the overhead of these exception tests
            // If that turns out to not be true, then this can be uncommented when the need arises.

            //		    	if(j == k) {
            //		    		if ((j == Long.MIN_VALUE)){
            //		                throw new ArithmeticException("OVERFLOW_64_Bits: " + j + ", " + k);
            //		            }
            //		            return Math.abs(j);
            //		    	} else  if ((j == 0) || (k == 0)) {
            //		            if ((j == Long.MIN_VALUE) || (k == Long.MIN_VALUE)){
            //		                throw new ArithmeticException("GCD_OVERFLOW_64_BITS: " + j + ", " + k);
            //		            }
            //		            return Math.abs(j) + Math.abs(k);
            //		        }

            // Note that Math.abs(Long.MIN_VALUE) returns Long.MIN_VALUE, 
            // otherwise, it returns a positive value
            // assert(j != Long.MIN_VALUE && k != Long.MIN_VALUE);

            j = Math.abs(j);
            k = Math.abs(k);
            return(((j|k) & BigRational.Gcd.LOOKUP_MASK) == 0L)
                    // since LOOKUP_TABLE uses signed bytes for the sake of memory
                    // we have to mask it with 0x00FF before converting to a long
                    // to remove the sign bit. This is only needed when LOOKUP_SIZE == 256
                    // since only the values from 128 to 255 would otherwise become negative. 
                    ? (long) 0x00FF & BigRational.Gcd.LOOKUP_TABLE[(int)j] [(int)k]
                            : calculateGcd(j, k);
        }

        // With a few minor tweaks, the code below is copied from Google Commons
        // See http://grepcode.com/file/repo1.maven.org/maven2/com.google.guava/guava/19.0-rc1/com/google/common/math/LongMath.java#LongMath.gcd%28long%2Clong%29
        /**
         * Returns the greatest common divisor of {@code a, b}. Returns {@code 0} if
         * {@code a == 0 && b == 0}.
         */
        static long calculateGcd(long a, long b) {
            //	      checkNonNegative("a", a);
            //	      checkNonNegative("b", b);
            if (a == 0) {
                // 0 % b == 0, so b divides a, but the converse doesn't hold.
                // BigInteger.gcd is consistent with this decision.
                return b;
            } else if (b == 0) {
                return a; // similar logic
            }
            /*
             * Uses the binary GCD algorithm; see http://en.wikipedia.org/wiki/Binary_GCD_algorithm.
             * This is >60% faster than the Euclidean algorithm in benchmarks.
             */
            int aTwos = Long.numberOfTrailingZeros(a);
            a >>= aTwos; // divide out all 2s
                int bTwos = Long.numberOfTrailingZeros(b);
                b >>= bTwos; // divide out all 2s
        while (a != b) { // both a, b are odd
            // The key to the binary GCD algorithm is as follows:
            // Both a and b are odd.  Assume a > b; then gcd(a - b, b) = gcd(a, b).
            // But in gcd(a - b, b), a - b is even and b is odd, so we can divide out powers of two.

            // We bend over backwards to avoid branching, adapting a technique from
            // http://graphics.stanford.edu/~seander/bithacks.html#IntegerMinOrMax

            long delta = a - b; // can't overflow, since a and b are nonnegative

            long minDeltaOrZero = delta & (delta >> (Long.SIZE - 1));
            // equivalent to Math.min(delta, 0)

            a = delta - minDeltaOrZero - minDeltaOrZero; // sets a to Math.abs(a - b)
            // a is now nonnegative and even

            b += minDeltaOrZero; // sets b to min(old a, b)
            a >>= Long.numberOfTrailingZeros(a); // divide out all 2s, since 2 doesn't divide b
        }
        return a << Math.min(aTwos, bTwos);
        }
    }

    @JsonValue
    public Object toJson()
    {
        if ( this.isWhole && this.notBig() )
            return new Long( this .num );
        else
            return toString;    
    }

    @Override
    public String toString()
    {
        return toString;    
    }

    private static String toString(BigRational that)
    {
        return (that.bigNum == null) 
                ? that.den == 1
                ? Long.toString(that.num)
                        : that.num + "/" + that.den
                        : that.bigDen .equals( BigInteger.ONE )
                        ? that.bigNum.toString()
                                : that.bigNum + "/" + that.bigDen;
    }

    /**
     * @return absolute value of this
     */
    public BigRational abs()
    {
        return signum == -1 ? negate() : this;
    }

    /**
     * @param that
     * @return 
     * ( this < that ) ? -1 :
     * ( this == that ) ? 0 : 1; // this > that 
     */
    @Override
    public int compareTo( BigRational that )
    {
        if(this.notBig() && that.notBig()) {
            boolean[] overflow = { false };
            // cross multiply and compare but fall through to big if we generate an overflow
            Long a = multiplyAndCheck(this.num, that.den, overflow);
            Long b = multiplyAndCheck(that.num, this.den, overflow);
            if(!overflow[0]) {
                return a .compareTo( b );
            }
        }
        // one or both is big or the long math generated an overflow 
        // but a big fraction may still be less than a long fraction
        // so use getNumerator() and getDenominator() to make them both BigIntegers
        // cross multiply and compare
        BigInteger a = this.getNumerator().multiply(that.getDenominator());
        BigInteger b = that.getNumerator().multiply(this.getDenominator());
        return a.compareTo(b);
    }

    /**
     * Supports equality test for BigRational, String and Number classes
     * @param that
     */
    @Override
    public boolean equals( Object that )
    {
        if ( that == this ) return true;
        if ( that == null ) return false;
        Class<?> thatClass = that .getClass();
        if (thatClass == this .getClass() || 
                thatClass == String.class ||
                Number.class.isAssignableFrom(thatClass)) {
            return this.toString.equals(that.toString());
        }
        return false;
    }

    // hashCode consistent with equals() and compareTo()
    @Override
    public int hashCode()
    {
        if(hashCode == null) {
            hashCode = this .toString .hashCode();
        }
        return hashCode;
    }

    public boolean isPositive() { return signum() == 1; }
    public boolean isNegative() { return signum() == -1; }
    @Override
    public boolean isZero()     { return isZero; }
    @Override
    public boolean isOne()      { return this.isOne; }
    public boolean isWhole()    { return this.isWhole; }
    @Override
    public boolean isBig()      { return bigNum != null; }
    @Override
    public boolean notBig()     { return bigNum == null; }

    public int signum()     { return signum; }

    /**
     * 
     * @param a
     * @param b
     * @return the lesser of a and b
     * @throws NullPointerException if either parameter is null;    
     */
    public static BigRational min(BigRational a, BigRational b) {
        return a.compareTo(b) == -1 ? a : b;
    }

    /**
     * 
     * @param a
     * @param b
     * @return the greater of a and b
     * @throws NullPointerException if either parameter is null;    
     */
    public static BigRational max(BigRational a, BigRational b) {
        return a.compareTo(b) == 1 ? a : b;
    }

    /**
     * 
     * @param args An array of BigRationals to be evaluated
     * @return the minimum value in the args arrray
     * @throws NullPointerException if any element of the array is null;
     * @throws IndexOutOfBoundsException if the array is empty. 
     * Use {@link #min( Collection<BigRational> collection )} to return null instead of throwing an exception.
     */
    @SafeVarargs
    @SuppressWarnings( "varargs" ) // This annotation is ignored (not supported) by Eclipse, but is used by NetBeans
    public static BigRational min(BigRational... args) {
        BigRational result = args[0];
        for(int i = 1; i < args.length; i++) {
            result = min(result, args[i]);
        }
        return result;
    }

    /**
     * 
     * @param args An array of BigRationals to be evaluated
     * @return the maximum value in the args arrray
     * @throws NullPointerException if any element of the array is null;
     * @throws IndexOutOfBoundsException if the array is empty. 
     * Use {@link #max( Collection<BigRational> collection )} to return null instead of throwing an exception.
     */
    @SafeVarargs
    @SuppressWarnings( "varargs" ) // This annotation is ignored (not supported) by Eclipse, but is used by NetBeans
    public static BigRational max(BigRational... args) {
        BigRational result = args[0];
        for(int i = 1; i < args.length; i++) {
            result = max(result, args[i]);
        }
        return result;
    }

    /**
     * 
     * @param collection
     * @return the minimum value in the collection, or null if the collection is empty
     * @throws NullPointerException if any item in the collection is null;
     */
    public static BigRational min(Collection<BigRational> collection) {
        return collection.isEmpty() ? null : min(collection.toArray(new BigRational[collection.size()]));
    }

    /**
     * 
     * @param collection
     * @return the maximum value in the collection, or null if the collection is empty
     * @throws NullPointerException if any item in the collection is null;
     */
    public static BigRational max(Collection<BigRational> collection) {
        return collection.isEmpty() ? null : max(collection.toArray(new BigRational[collection.size()]));
    }

    /**
     * 
     * @param x long value to be added
     * @param y long value to be added
     * @param overflow boolean[1] in which an overflow indicator may be returned to the caller.
     * @return x + y
     * <br>
     * If the operation results in an overflow or a sum of Long.MIN_VALUE then the returned value is undefined 
     * and overflow[0] will be set to true, otherwise, overflow is unchanged.
     * Specifically, overflow[0] is not cleared or modified at all unless an overflow occurs.
     * 
     * This gives the caller the option of performing several operations 
     * before subsequently checking if any overflowed occurred, ignoring which specific values generated the oveflow.
     * Alternatively, the caller could check each individual iteration for overflow to determine 
     * which specific pair(s) of numbers generated the overflow and respond immediately.
     *  
     * @throws NullPointerException if overflow is null
     * @throws OutOfBoundsException if overflow.length == 0
     */
    static long addAndCheck(long x, long y, boolean[] overflow) {
        long r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        // or if r == Long.MIN_VALUE
        if ((((x ^ r) & (y ^ r)) < 0) || r == Long.MIN_VALUE) {
            overflow[0] = true;
        }
        return r;
    }

    /**
     * 
     * @param x long value to be multiplied
     * @param y long value to be multiplied
     * @param overflow boolean[1] in which an overflow indicator may be returned to the caller.
     * @return x * y
     * <br>
     * If the operation results in an overflow then the returned value is undefined 
     * and overflow[0] will be set to true, otherwise, overflow is unchanged.
     * Specifically, overflow[0] is not cleared or modified at all unless an overflow occurs.
     * 
     * This gives the caller the option of performing several operations 
     * before subsequently checking if any overflowed occurred, ignoring which specific values generated the oveflow.
     * Alternatively, the caller could check each individual iteration for overflow to determine 
     * which specific pair(s) of numbers generated the overflow and respond immediately.
     *  
     * @throws NullPointerException if overflow is null
     * @throws OutOfBoundsException if overflow.length == 0
     */
    static long multiplyAndCheck(long x, long y, boolean[] overflow) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        // Note that Math.abs(Long.MIN_VALUE) returns Long.MIN_VALUE, 
        // the most negative representable long value, which is still negative.
        if (((ax | ay) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            if (((y != 0) && (r / y != x)) ||
                    (x == Long.MIN_VALUE && y == -1)) {
                overflow[0] = true;
            }
        }
        return r;
    }

    /**
     * 
     * @param n int value to be multiplied
     * @return this * n
     */
    public BigRational times( int n )
    {
        return n == 1 ? this : n == 0 ? ZERO : canMultiplyInteger
                ? new BigRational( num * n )
                        : this.times( new BigRational( n ) );
    }

    /**
     * @param that
     * @return this * that
     */
    @Override
    public BigRational times( BigRational that )
    {
        // multiplication IS commutative
        if ( this.isOne() ) {
            return that;
        }
        if ( that.isOne() ) {
            return this;
        }
        if ( this.isZero() || that.isZero() ) {
            return ZERO;
        }
        if( this.notBig() && that.notBig() ) {
            // cross multiply but switch to big if we generate an overflow
            boolean[] overflow = { false };
            long n = multiplyAndCheck(this.num, that.num, overflow);
            if(!overflow[0]) {
                long d = multiplyAndCheck(this.den, that.den, overflow);
                if(!overflow[0]) {
                    return new BigRational( n, d );
                }
            }
        }
        // one or both is big or else the long math overflowed, but the big may still be less than the long after we do the math and reduce
        // so use getNumerator() and getDenominator() to make them both BigIntegers
        // then cross multiply and reduce in the c'tor
        BigInteger n = this.getNumerator().multiply(that.getNumerator());
        BigInteger d = this.getDenominator().multiply(that.getDenominator());
        return new BigRational( n, d );
    }

    /**
     * 
     * @param n int value to be added
     * @return this + n
     */
    public BigRational plus( int n )
    {
        return n == 0 
                ? this 
                        : canAddInteger
                        ? new BigRational( num + n )
                                : this.plus( new BigRational( n ) );
    }

    /**
     * @param that
     * @return this + that
     */
    @Override
    public BigRational plus( BigRational that )
    {
        // addition IS commutative
        if (this.isZero()) {
            return that;
        }
        if (that.isZero()) {
            return this;
        }

        if( this.notBig() && that.notBig() ) {
            // cross multiply longs but fall through to big if we generate an overflow
            boolean[] overflow = { false };
            if(this.den == that.den) {
                // just add numerators and use their common denominator
                long n = addAndCheck(this.num, that.num, overflow);
                if(!overflow[0]) {
                    return new BigRational( n, this.den );
                }
            } else {
                // different denominators
                long n1 = multiplyAndCheck(this.num, that.den, overflow);
                long n2 = multiplyAndCheck(that.num, this.den, overflow);
                long n = addAndCheck(n1, n2, overflow);
                long d = multiplyAndCheck(this.den, that.den, overflow);
                if(!overflow[0]) {
                    return new BigRational( n, d );
                }
            }
        }

        // one or both is big or the long math generated an overflow so use getNumerator() and getDenominator() to make them both BigIntegers
        if (this.getDenominator().equals(that.getDenominator())) {
            // just add numerators and use their common denominator
            BigInteger n1 = this.getNumerator();
            BigInteger n2 = that.getNumerator();
            return new BigRational(n1.add(n2), this.getDenominator());
        }
        // different denominators
        BigInteger d1 = this.getDenominator();
        BigInteger d2 = that.getDenominator();
        BigInteger d = d1.multiply(d2);
        BigInteger n1 = this.getNumerator().multiply(d2);
        BigInteger n2 = that.getNumerator().multiply(d1);
        BigInteger n = n1.add(n2);
        return new BigRational(n, d);
    }

    /**
     * @return numerator as BigInteger even if it can be held in a long.
     */
    @Override
    public BigInteger getNumerator()
    {
        return bigNum == null 
                ? BigInteger.valueOf(num) 
                        : bigNum;
    }

    /**
     * @return denominator as BigInteger even if it can be held in a long.
     */
    @Override
    public BigInteger getDenominator()
    {
        return bigDen == null 
                ? BigInteger.valueOf(den) 
                        : bigDen;
    }

    /**
     * @return this * -1
     */
    @Override
    public BigRational negate()
    {
        return this.isZero()
                ? ZERO
                        : this.notBig()
                        ? new BigRational( -num, den, null, null )
                                : new BigRational( 0, 0, bigNum.negate(), bigDen );
    }

    /**
     * 
     * @param n int value to be subtracted
     * @return this - n
     */
    public BigRational minus( int n )
    {
        return n == 0 
                ? this 
                        : canAddInteger
                        ? new BigRational( num - n )
                                : this.plus( new BigRational( -n ) );
    }

    /**
     * @param that
     * @return this - that
     */
    @Override
    public BigRational minus( BigRational that )
    {
        return this.plus(that.negate());
    }

    /**
     * @return 1 / this
     */
    @Override
    public BigRational reciprocal()
    {
        return this.isOne()
                ? ONE
                        : this.isBig()
                        ? new BigRational( bigDen, bigNum )
                                : new BigRational( den, num );
    }

    /**
     * 
     * @param d long value by which this is to be divided
     * @return this / d
     * Note that {@code d} is a long, not an int as in {@code plus(int n)}, {@code minus(int n)} and {@code times(int n)}.
     */
    public BigRational dividedBy( long d )
    {
        // no need to check here if d == 0 
        // since the constructor will throw the exception
        return d == 1 
                ? this 
                        : this.times( new BigRational( 1, d ) );
    }

    /**
     * @param that
     * @return this / that
     */
    @Override
    public BigRational dividedBy( BigRational that )
    {
        // division IS NOT commutative
        return that.isOne() 
                ? this 
                        : this.times(that.reciprocal());
    }

    @Override
    public double evaluate()
    {
        if(doubleValue == null) {
            doubleValue = this.isBig()
                    ? toBigDecimal(this).doubleValue()
                            : num / (double) den; // casting of num to double is unnecessary
        }
        return doubleValue;
    }

    static BigDecimal toBigDecimal(BigRational that) {
        // Using num / (double) den or bigNum.doubleValue() / bigDen.doubleValue() 
        // potentially introduces rounding errors before the division, 
        // so let BigDecimal do the heavy lifting for dividing BigInteger values
        // with less rounding, then we can use BigDecimal.doubleValue() as needed.
        return new BigDecimal(that.getNumerator())
                .divide(new BigDecimal(that.getDenominator()), MathContext.DECIMAL64);
    }

}
