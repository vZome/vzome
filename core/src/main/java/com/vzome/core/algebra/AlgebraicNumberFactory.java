package com.vzome.core.algebra;

public interface AlgebraicNumberFactory
{
    BigRational zero();

    BigRational one();

    BigRational createBigRational( long numerator, long denominator );

    AlgebraicNumber createAlgebraicNumber( AlgebraicField field, int[] numerators, int divisor );

    AlgebraicNumber createAlgebraicNumberFromTD( AlgebraicField field, int[] trailingDivisorForm );

    AlgebraicNumber createAlgebraicNumberFromPairs( AlgebraicField field, int[] pairs );

    AlgebraicNumber createRational( AlgebraicField field, long numerator, long denominator );
    
    boolean isPrime( int n );
    
    int nextPrime( int n );
}
