package com.vzome.core.algebra;

/**
 * Immutable Abstract Data Type for arbitrarily large rational numbers. 
 */
public interface BigRational extends Fields.Element<BigRational>
{
    public boolean isNegative();    
}
