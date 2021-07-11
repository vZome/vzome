package com.vzome.core.algebra;

public interface BigRationalFactory
{
    BigRational zero();

    BigRational one();

    BigRational createBigRational( long numerator, long denominator );

    BigRational createBigRational( String fraction );
}
