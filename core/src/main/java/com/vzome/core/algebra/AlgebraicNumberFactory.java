package com.vzome.core.algebra;

import defs.js.BigInt;

public interface AlgebraicNumberFactory
{
    BigRational zero();

    BigRational one();

    BigRational createBigRational( long numerator, long denominator );

    AlgebraicNumber createAlgebraicNumber( AlgebraicField field, int[] numerators, int divisor );

    AlgebraicNumber createAlgebraicNumberFromTDExact( AlgebraicField field, BigInt[] trailingDivisorForm );

    AlgebraicNumber createAlgebraicNumberFromPairs( AlgebraicField field, long[] pairs );

    AlgebraicNumber createRational( AlgebraicField field, long numerator, long denominator );
    
    boolean isPrime( int n );
    
    int nextPrime( int n );
}
