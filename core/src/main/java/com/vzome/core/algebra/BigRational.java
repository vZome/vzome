package com.vzome.core.algebra;

import java.math.BigInteger;

/**
 * Immutable Abstract Data Type for arbitrarily large rational numbers. 
 */
public interface BigRational extends Fields.RationalElement<BigInteger,BigRational>
{
    public boolean isNegative();    
}
