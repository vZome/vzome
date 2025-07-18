
package com.vzome.core.algebra;

/**
 * 
 * Immutable representation of an Algebraic Number
 *
 */
public interface AlgebraicNumber extends Fields.Element<AlgebraicNumber>, Comparable<AlgebraicNumber>
{    
    // for JSON serialization
    public static class Views {
        public interface TrailingDivisor {}
        public interface Rational {}
        public interface Real {}
    }

    public boolean greaterThan(AlgebraicNumber other);

    public boolean lessThan(AlgebraicNumber other);

    public boolean greaterThanOrEqualTo(AlgebraicNumber other);

    public boolean lessThanOrEqualTo(AlgebraicNumber other);

    public AlgebraicField getField();
    
    /**
     * 
     * @param n is the value to be added
     * @return this + n
     */
    public AlgebraicNumber plusInt( int n );

    /**
     * 
     * @param num is the numerator of the rational value to be added
     * @param den is the denominator of the rational value to be added
     * @return this + (num / den)
     */
    public AlgebraicNumber plusRational( int num, int den );

    /**
     * 
     * @param that is the value to be added
     * @return this + n
     */
    @Override
    public AlgebraicNumber plus( AlgebraicNumber that );

    /**
     * 
     * @param n is the value to be multiplied
     * @return this * n
     */
    @Override
    public AlgebraicNumber timesInt( int n );

    /**
     * 
     * @param num is the numerator of the rational value to be multiplied
     * @param den is the denominator of the rational value to be multiplied
     * @return this * (num / den)
     */
    public AlgebraicNumber timesRational( int num, int den );

    @Override
    public AlgebraicNumber times( AlgebraicNumber that );

    /**
     * 
     * @param n is the value to be subtracted
     * @return this - n
     */
    public AlgebraicNumber minusInt( int n );

    /**
     * 
     * @param num is the numerator of the rational value to be subtracted
     * @param den is the denominator of the rational value to be subtracted
     * @return this - (num / den)
     */
    public AlgebraicNumber minusRational( int num, int den );

    /**
     * 
     * @param that is the value to be subtracted
     * @return this - n
     */
    @Override
    public AlgebraicNumber minus( AlgebraicNumber that );

    /**
     * 
     * @param divisor
     * @return this / divisor
     */
    public AlgebraicNumber dividedByInt( int divisor );

    /**
     * 
     * @param num is the numerator of the divisor
     * @param den is the denominator of the divisor
     * @return this / (num / den)
     */
    public AlgebraicNumber dividedByRational( int num, int den );

    public AlgebraicNumber dividedBy( AlgebraicNumber that );

    @Override
    public double evaluate();


    // isRational() is not currently used enough 
    // to warrant caching it in a private field like isZero and isOne
    // so just calculate it
    public boolean isRational();

    @Override
    public boolean isZero();
    @Override
    public boolean isOne();
    
    public int signum();
    
    @Override
    public AlgebraicNumber negate();

    @Override
    public AlgebraicNumber reciprocal();

    /**
     * 
     * @param buf
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
     * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
     * {@code ZOMIC_FORMAT      // 4 3}<br>
     * {@code VEF_FORMAT        // (3,4)}<br>
     * {@code MATHML_FORMAT     // Use getMathML()}
     * {@code MATH_FORMAT       // Originally used in JavaScript parts panel, not in Java}
     */
    public void getNumberExpression( StringBuffer buf, int format );

    /**
     * 
     * @param format must be one of the following values.
     * The result is formatted as follows:
     * <br>
     * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
     * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
     * {@code ZOMIC_FORMAT      // 4 3}<br>
     * {@code VEF_FORMAT        // (3,4)}
     * {@code MATHML_FORMAT     // Use getMathML()}
     * {@code MATH_FORMAT       // Originally used in JavaScript parts panel, not in Java}
     * 
     * @return 
     */
    public String toString( int format );
    
    public int[] toTrailingDivisor();
}
