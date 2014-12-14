
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import com.vzome.core.algebra.RationalNumbers;

import junit.framework.TestCase;

public class RationalNumbersTest extends TestCase
{
    public void testGCD()
    {
        assertEquals( 34, RationalNumbers.gcd( 40902, 24140 ) );
        assertEquals( RationalNumbers.gcd( 24140, 40902 ), RationalNumbers.gcd( 40902, 24140 ) );
        assertEquals( RationalNumbers.gcd( 40902, -24140 ), RationalNumbers.gcd( -40902, 24140 ) );
        assertEquals( 34, RationalNumbers.gcd( -40902, -24140 ) );
    }
    
    static final int[] sum1 = { 7, 66, 17, 12, 67, 44 };
    
    static final int[] sum2 = { 17, 120, -27, 70, -41, 168 };
    
    static final int[] result = { 0, 1 };
    
    public void testAddition()
    {
        RationalNumbers.add( sum1, 0, sum1, 1, result, 0 );
        assertTrue( RationalNumbers.equals( result, 0, sum1, 2 ) );
        
        RationalNumbers.add( sum2, 0, sum2, 1, result, 0 );
        assertTrue( RationalNumbers.equals( result, 0, sum2, 2 ) );
    }
    
    public void testSubtraction()
    {
        RationalNumbers.subtract( sum1, 2, sum1, 1, result, 0 );
        assertTrue( RationalNumbers.equals( result, 0, sum1, 0 ) );

        RationalNumbers.subtract( sum2, 2, sum2, 1, result, 0 );
        assertTrue( RationalNumbers.equals( result, 0, sum2, 0 ) );
    }
    
    public void testZeroDivide()
    {
        try {
            int[] data = new int[]{ 12, 5, 0, 1, 0, 0 };
            RationalNumbers .divide( data, 0, data, 1, data, 2 );
            fail( "should have seen ArithmeticException" );
        } catch ( ArithmeticException e ) {
            // OK, pass the test
        }
    }
}
