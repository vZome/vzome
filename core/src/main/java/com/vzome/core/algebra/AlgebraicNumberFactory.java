package com.vzome.core.algebra;

/**
 * @author vorth
 * 
 * This interface exists so that the AlgegraicField and AlgebraicNumber interfaces do not
 * need to depend on BigRational.  We need this because we have two kinds of AlgebraicField
 * implementations in Javascript: ones that use BigRational and are transpiled from Java, and
 * ones that are reimplemented in native Javascript using BigInt in trailing-denominator format.
 * 
 * For the transpiled field implementations, we have implementations of AlgebraicNumberFactory,
 * AlgebraicNumber (JavaAlgebraicNumber), and BigRational reimplemented in Javascript.
 * 
 * For the native Javascript field implementations, we have a *different* implementation of
 * AlgebraicNumber (JsAlgebraicNumber) that does not use a factory at all.
 * 
 * We also have the Java implementation of this interface, AlgebraicNumberImpl.FACTORY.
 */

public interface AlgebraicNumberFactory
{
    BigRational zero();

    BigRational one();

    BigRational createBigRational( long numerator, long denominator );

    BigRational parseBigRational( String str );

    AlgebraicNumber createAlgebraicNumber( AlgebraicField field, int[] numerators, int divisor );

    AlgebraicNumber createAlgebraicNumberFromTD( AlgebraicField field, BigRational[] trailingDivisorForm );

    AlgebraicNumber createAlgebraicNumberFromPairs( AlgebraicField field, long[] pairs );

    AlgebraicNumber createAlgebraicNumberFromBRs( AlgebraicField field, BigRational[] pairs );

    AlgebraicNumber createRational( AlgebraicField field, long numerator, long denominator );
    
    boolean isPrime( int n );
    
    int nextPrime( int n );
}
