//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math;

import java.nio.IntBuffer;

import com.vzome.core.algebra.AlgebraicNumber;

public interface IntegralNumber
{
    void write( IntBuffer buf );

    IntegralNumber plus( IntegralNumber addend );

    IntegralNumber minus( IntegralNumber subtrahend );

    /**
     * multiply by the given power of phi
     * @param power
     * @return
     */
    IntegralNumber scale( int power );

    /**
     * @param multiplicand
     * @return
     */
    IntegralNumber times( IntegralNumber multiplicand );

    IntegralNumber times( int factor );

    IntegralNumber div( int divisor );

    IntegralNumber div( IntegralNumber divisor );

    IntegralNumber neg();

    /**
     * Return the value of this IntegralNumber as an unstructured real number.
     * That is, a*Tau+b is computed and returned.
     * @return double
     */
    double value();
    
    int getOnes();
    
    int getDivisor();
    
    AlgebraicNumber getAlgebraicNumber();

    IntegralNumber inverse();

    int determinant();
    
    int euclideanNorm();

    String toString();
    
    String toString( int format );
    
    int DEFAULT_FORMAT = 0; // (3phi+4)/2
    
    int EXPRESSION_FORMAT = 1; // (3*tau+4)/2

    int ZOMIC_FORMAT = 2;      // 4 3

    int VEF_FORMAT = 3;      // (3,4)

    boolean equals( Object other );

    int hashCode();

    boolean isZero();

    boolean isOne();

    /**
     * 
     * @return the zero of this number's field
     */
    IntegralNumber zero();

    /**
     * 
     * @return the multiplicative identity of this number's field
     */
    IntegralNumber one();
    
    Factory getFactory();
    
    public interface Factory
    {
        IntegralNumber createIntegralNumber( int taus, int ones, int divisor, int power );
        
        IntegralNumber parseString( String string );
        
        IntegralNumber zero();

        IntegralNumber one();
    }

    IntegralNumber conjugate();

}
