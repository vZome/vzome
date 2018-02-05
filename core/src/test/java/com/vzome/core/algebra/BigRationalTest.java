package com.vzome.core.algebra;

import static com.vzome.core.generic.Utilities.compareDoubles;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * @author David Hall
 */
public class BigRationalTest {
	
    @Test
    public void testToString() {
        Long num = 2L;
        Long den = 3L;
        BigRational r = new BigRational(num, den);
        String expected = num.toString() + "/" + den.toString();
        assertEquals(expected, r.toString());
        num = -2L;
        r = new BigRational(num, den);
        expected = num.toString() + "/" + den.toString();
        assertEquals(expected, r.toString());
        num = 0L;
        r = new BigRational(num, den);
        expected = "0";
        assertEquals(expected, r.toString());
    }

    @Test
    public void testAbs() {
        BigRational r = new BigRational("-2/3");
        assertEquals(-1, r.signum());
        assertNotEquals(r.signum(), r.abs().signum());
        assertEquals(1, r.abs().signum());
        r = BigRational.ZERO;
        assertEquals(r, r.abs());
        assertEquals(r.signum(), r.abs().signum());
    }

    @Test
    public void testMinMax() {
        BigRational j = new BigRational("2/3");
        assertEquals(j, BigRational.min(j, j.reciprocal()));
        assertEquals(j.reciprocal(), BigRational.max(j, j.reciprocal()));

        BigRational k = new BigRational("3/4");
        
        assertEquals(j, BigRational.min(j, k));
        assertEquals(k, BigRational.max(j, k));

        // both are commutative
        assertEquals(BigRational.min(j, k), BigRational.min(k, j));
        assertEquals(BigRational.max(j, k), BigRational.max(k, j));

        // test min and max of more than 2 items
        BigRational n = new BigRational(-42);
        
        assertEquals(n, BigRational.min(j, k, n));
        assertEquals(k, BigRational.max(j, k, n));
        
        // test duplicates
        assertEquals(n, BigRational.min(j, k, n, n, n, j, j, k, k));
        assertEquals(k, BigRational.max(j, k, n, n, n, j, j, k, k));
        
        List<BigRational> list = new ArrayList<>();
        // empty list returnd null
        assertNull(BigRational.min(list));
        assertNull(BigRational.max(list));
        // single element in list is returned
        list.add(j);
        assertEquals(j, BigRational.min(list));
        assertEquals(j, BigRational.max(list));
        // duplicates are OK
        list.add(j);
        assertEquals(j, BigRational.min(list));
        assertEquals(j, BigRational.max(list));
        // more...
        list.add(k);
        assertEquals(j, BigRational.min(list));
        assertEquals(k, BigRational.max(list));
        // more...
        list.add(n);
        assertEquals(n, BigRational.min(list));
        assertEquals(k, BigRational.max(list));
}

    @Test
    public void testCompareToBig() {
        BigInteger num = new BigInteger(Long.MAX_VALUE + "2");
        BigInteger den = new BigInteger(Long.MAX_VALUE + "3");
        assertNotEquals(num, den);

        BigRational r1 = new BigRational(num.negate(), den);
        BigRational r2 = new BigRational(num, den.negate());
        assertEquals(0, r1.compareTo(r2));

        r1 = new BigRational(num, den);
        r2 = new BigRational(num.negate(), den);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
    }

    @Test
    public void testCompareToLong() {
        Long num = 2L;
        Long den = 3L;
        assertNotEquals(num, den);

        BigRational r1 = new BigRational(-num, den);
        BigRational r2 = new BigRational(num, -den);
        assertEquals(0, r1.compareTo(r2));

        r1 = new BigRational(num, den);
        r2 = new BigRational(-num, den);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
    }
    
    @Test
    public void testCompareToMixed() {
        Long j = 2L;
        BigInteger k = new BigInteger(Long.MAX_VALUE + "3");
        assertNotEquals(j, k);

        BigRational r1 = new BigRational(-j, k);
        BigRational r2 = new BigRational(j, k.negate());
        assertEquals(0, r1.compareTo(r2));

        r1 = new BigRational(j, k);
        r2 = new BigRational(-j, k);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));

        // swap order to test other c'tor
        r1 = new BigRational(k, j);
        r2 = new BigRational(k.negate(), j);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));

    }

    @Test
    public void testIsZero() {
        BigRational r = new BigRational(0);
        assertTrue(r.isZero());
        r = new BigRational(1);
        assertFalse(r.isZero());
        r = new BigRational(-1);
        assertFalse(r.isZero());
    }

    @Test
    public void testIsPositive() {
        BigRational r = new BigRational(1);
        assertTrue(r.isPositive());
        r = new BigRational(0);
        assertFalse(r.isPositive());
        r = new BigRational(-1);
        assertFalse(r.isPositive());
    }

    @Test
    public void testIsNegative() {
        BigRational r = new BigRational(-1);
        assertTrue(r.isNegative());
        r = new BigRational(0);
        assertFalse(r.isNegative());
        r = new BigRational(1);
        assertFalse(r.isNegative());
    }

    @Test
    public void testIsOne() {
        BigRational r = new BigRational(1);
        assertTrue(r.isOne());
        r = new BigRational(0);
        assertFalse(r.isOne());
        r = new BigRational(-1);
        assertFalse(r.isOne());
    }

    @Test
    public void testIsWhole() {
        assertTrue(BigRational.ONE.isWhole());
        assertTrue(BigRational.ZERO.isWhole());
        assertTrue(new BigRational(-37).isWhole());
        assertTrue(new BigRational(242, 1).isWhole());
        assertFalse(new BigRational(1, 17).isWhole());
        BigRational r = new BigRational(2, 3);
        assertFalse(r.isWhole());
        assertFalse(r.reciprocal().isWhole());
        r = new BigRational(Long.MIN_VALUE);
        assertTrue(r.isWhole());
        r = new BigRational(Long.MAX_VALUE);
        assertTrue(r.isWhole());
    }

    @Test
    public void testIsBig() {
        // not big
        BigRational r = new BigRational(Long.MAX_VALUE + "");
        assertFalse(r.isBig());
        r = new BigRational(Long.MIN_VALUE + "/2");
        assertFalse(r.isBig());
        r = new BigRational(Long.MIN_VALUE + "/" + Long.MIN_VALUE);
        assertFalse(r.isBig());
        assertTrue(r.isOne());
        // big
        r = new BigRational(Long.MIN_VALUE + "");
        assertTrue(r.isBig());
        r = new BigRational(Long.MAX_VALUE + "0");
        assertTrue(r.isBig());
        r = new BigRational(Long.MIN_VALUE + "0");
        assertTrue(r.isBig());
    }

    @Test
    public void testNotBig() {
        // not big
        BigRational r = new BigRational(Long.MAX_VALUE);
        assertTrue(r.notBig());
        assertTrue(r.fitsInLong());
        
        r = new BigRational(Long.MIN_VALUE-1);
        assertTrue(r.notBig());
        assertTrue(r.fitsInLong());
        
        // reciprocals
        r = new BigRational(1, Long.MAX_VALUE);
        assertTrue(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRational(1, Long.MIN_VALUE-1);
        assertTrue(r.notBig());
        assertFalse(r.fitsInLong());
        
        // big
        r = new BigRational(Long.MIN_VALUE);
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
                
        r = new BigRational(Long.MAX_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRational(Long.MIN_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        // reciprocals
        r = new BigRational(1, Long.MIN_VALUE);
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRational("1/" + Long.MAX_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRational("1/" + Long.MIN_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
    }

    @Test
    public void testNotBigExtremes() {
        // not big
        BigRational
        r = new BigRational(Long.MAX_VALUE, Long.MAX_VALUE);
        assertTrue(r.notBig());
        assertTrue(r.isOne());
        r = new BigRational(Long.MIN_VALUE, Long.MIN_VALUE);
        assertTrue(r.notBig());
        assertTrue(r.isOne());
    }

    @SuppressWarnings("unlikely-arg-type")
	@Test
    public void testEquals() {
        // test various code paths
        assertFalse(BigRational.ZERO.equals(null));
        // new implementation can test equality with a String or a Number
        assertTrue(BigRational.ZERO.equals(0));
        assertTrue(BigRational.ZERO.equals("0"));
        assertFalse(BigRational.ZERO.equals("Zero"));
        assertFalse(BigRational.ZERO.equals(this)); // could be any invalid type
        assertTrue(BigRational.ZERO.equals(BigInteger.ZERO));
        assertTrue(BigRational.ZERO.equals(BigRational.ZERO));
        assertTrue(BigRational.ZERO.equals(new BigRational(0)));
        assertTrue(BigRational.ONE.equals(new BigRational(1)));
        assertFalse(BigRational.ONE.equals(new BigRational(-1)));
        assertFalse(new BigRational(Long.MIN_VALUE,19).equals(new BigRational(Long.MIN_VALUE, -19)));	 // BigRational initially failed
    }

    @Test
    public void testHashCode() {
        // Hash codes are used by HashSets
        Set<BigRational> set1 = new HashSet<>();  
        Set<BigRational> set2 = new HashSet<>(); // set 2 is just here to exercice the cashed hashcode
        int qty = 0;
        for(int n = -50; n <= 50; n++) {
            if(n != 0) {
                BigRational b = new BigRational(n);
                set1.add(b); // hashcode is calculated on demand and cached
                set2.add(b); // cached hashcode is used subsequently
                qty++;

                set1.add(b.reciprocal());
                if(n != 1 && n != -1) {
                    // 1 and -1 are their own reciprocals
                    // so they should not be added to the list the second time
                    // so don't count them
                    // this indicates that the hash code is the same
                    qty++;
                }
            }
        }
        assertEquals(qty, set1.size());
        assertEquals(200 - 2, qty);
    }

    @Test
    public void testEvaluate() {
        assertEquals((double) Long.MIN_VALUE, new BigRational(Long.MIN_VALUE).evaluate()); // big
        assertEquals(17d, new BigRational(17L).evaluate()); // not big
        assertEquals(0.25d, new BigRational("-9/-36").evaluate()); // not big
        assertEquals(0.001d, new BigRational(Long.MIN_VALUE + "/" + Long.MIN_VALUE+"000").evaluate()); // big
        
        // last digit of n is chosen so it can't be reduced (e.g. to 2/3)
        String n = "222222222222222222222222222222222222222222222222220"; 
        String d = "333333333333333333333333333333333333333333333333333";
        String f = n + "/" + d;
        BigRational r = new BigRational(f);
        assertEquals(f, r.toString());

        BigDecimal bigDecimal = BigRational.toBigDecimal(r);
        double expected = bigDecimal.doubleValue();

        // Some alternative methods for calculating the double value of this fraction
        // may have greater rounding error than bigDecimal
        // even if r is not big, (e.g. 2/3) 
        // and especially when the decimal part is greater than 1/2,
        // then r.evaluate() may introduce 
        // minimal rounding error in the last digit.
        double delta = 0.0000000000001;
        
        double value = r.evaluate();
        compareDoubles(r.evaluate(), value, 0d); // subsequent calls to evaluate() will return the cached value which will be unchanged.       
        compareDoubles(expected, value, delta);
                
        value = Double.parseDouble(n)/Double.parseDouble(d);
        compareDoubles(expected, value, delta);
        
        value = r.getNumerator().doubleValue() / r.getDenominator().doubleValue();
        compareDoubles(expected, value, delta);
        
        assertTrue(r.isBig());
    }
    
    @Test
    public void testPlusInteger() {
    	BigRational n = new BigRational(Long.MIN_VALUE);
    	n = n.plus(2);
        assertTrue(n.isWhole());
    	assertEquals(Long.toString(Long.MIN_VALUE + 2), n.toString());
    	
        n = BigRational.ZERO.plus(3);
        assertEquals("3", n.toString());

        n = n.plus(-13);
        assertEquals("-10", n.toString());

        n = new BigRational(Integer.MAX_VALUE);
    	n = n.plus(Integer.MAX_VALUE);
        assertFalse(n.isBig());
    	assertEquals(Long.toString(Integer.MAX_VALUE * 2L), n.toString());
    	
        n = BigRational.ZERO.plus(7);
        assertEquals("7", n.toString());

        n = n.plus(0);
        assertEquals("7", n.toString());

        n = n.plus(-17);
        assertEquals("-10", n.toString());
    }
    
    @Test
    public void testMinusInteger() {
    	BigRational n = new BigRational(Long.MAX_VALUE);
    	n = n.minus(2);
        assertTrue(n.isWhole());
    	assertEquals(Long.toString(Long.MAX_VALUE - 2), n.toString());
    	
        n = BigRational.ZERO.minus(3);
        assertEquals("-3", n.toString());

        n = n.minus(-13);
        assertEquals("10", n.toString());

        n = BigRational.ZERO.minus(5);
        assertEquals("-5", n.toString());

        n = n.minus(0);
        assertEquals("-5", n.toString());

        n = n.minus(-15);
        assertEquals("10", n.toString());
    }
    
    @Test
    public void testTimesInteger() {
    	BigRational n = new BigRational(Long.MAX_VALUE);
    	n = n.times(2);
        assertTrue(n.isBig());
    	assertEquals(BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)).toString(), n.toString());
    	
    	n = new BigRational(Integer.MAX_VALUE);
    	n = n.times(2);
    	assertEquals(Long.toString(Integer.MAX_VALUE * 2L), n.toString());
    	
    	n = new BigRational(37);
    	n = n.times(1);
    	assertEquals("37", n.toString());
    	
    	n = n.times(0);
    	assertEquals("0", n.toString());
    	
    	n = new BigRational(43);
    	n = n.times(7);
    	assertEquals(Long.toString(43*7), n.toString());
    	
        n = BigRational.ZERO.times(6);
        assertEquals("0", n.toString());

        n = new BigRational(Integer.MAX_VALUE);
    	n = n.times(Integer.MAX_VALUE);
        assertFalse(n.isBig());
    	assertEquals(Long.toString((long)Integer.MAX_VALUE * (long)Integer.MAX_VALUE), n.toString());

    	n = new BigRational(Integer.MIN_VALUE);
    	n = n.times(Integer.MIN_VALUE);
        assertFalse(n.isBig());
    	assertEquals(n.toString(), Long.toString((long)Integer.MIN_VALUE * (long)Integer.MIN_VALUE));
    }

    @Test
    public void testDividedByLong() {
    	BigRational n = new BigRational(Long.MIN_VALUE);
    	n = n.dividedBy(2);
        assertTrue(n.isWhole());
    	assertEquals(Long.toString(Long.MIN_VALUE / 2L), n.toString());
    	
    	n = n.dividedBy(3);
        assertFalse(n.isWhole());
    	assertEquals(Long.toString(Long.MIN_VALUE / 2L) + "/3", n.toString());
    	
        n = BigRational.ONE.dividedBy(1);
        assertEquals("1", n.toString());

        n = BigRational.ONE.dividedBy(-2);
        assertEquals("-1/2", n.toString());

        n = BigRational.ZERO.dividedBy(3);
        assertEquals("0", n.toString());

        n = BigRational.ONE.dividedBy(4);
        assertEquals("1/4", n.toString());

        n = new BigRational(Integer.MAX_VALUE);
    	n = n.dividedBy(Integer.MAX_VALUE);
    	assertEquals("1", n.toString());
    }    

    @Test
    public void testTimes() {
        {
            BigRational neg = new BigRational(-1L);
            BigRational min = new BigRational(Long.MIN_VALUE);
            assertFalse(neg.isBig());
            assertTrue(min.isBig());	 		// BigRational initially failed
            assertTrue(neg.isNegative());
            assertTrue(min.isNegative());
            BigRational result = min.times(neg);
            assertTrue(result.isBig());	 		// BigRational initially failed
            assertTrue(result.isPositive());	// BigRational initially failed
            result = result.times(neg);
            assertTrue(result.isBig());			// BigRational initially failed
            assertTrue(result.isNegative());
        }
        assertEquals("11/5", new BigRational("7/5").times(new BigRational("11/7")).toString()); // 7's cancel
        // tests 4 different code paths
        assertTrue(new BigRational("2").times(BigRational.ZERO).isZero());
        assertTrue(BigRational.ZERO.times(new BigRational("2")).isZero());
        assertTrue(BigRational.ZERO.times(BigRational.ONE).isZero());
        assertTrue(BigRational.ONE.times(BigRational.ZERO).isZero());
        {
            BigRational neg = new BigRational(-1L);
            assertFalse(neg.isBig());
            BigRational min = new BigRational(Long.MIN_VALUE);
            assertTrue(min.isBig());				 // BigRational initially failed
            min = new BigRational(Long.MIN_VALUE+1);
            assertTrue(min.notBig());
            BigRational result = min.times(neg);
            assertTrue(result.notBig());
            result = result.times(neg);
            assertFalse(result.isBig());
        }
    }

    @Test
    public void testTimesOne() {
        BigRational r1 = new BigRational(1);
        BigRational r2 = new BigRational(2);
        assertFalse(r1.equals(r2));
        // test two different code paths in BigRational.times()
        assertEquals(r1.times(r2), r2.times(r1));
    }

    @Test
    public void testGetNumerator() {
        Long num = 2L;
        Long den = 3L;
        BigRational r = new BigRational(num, den);
        BigInteger expNum = new BigInteger(num.toString());
        BigInteger result = r.getNumerator();
        assertEquals(expNum, result);
    }

    @Test
    public void testGetDenominator() {
        Long num = 2L;
        Long den = 3L;
        BigRational r = new BigRational(num, den);
        BigInteger expDen = new BigInteger(den.toString());
        BigInteger result = r.getDenominator();
        assertEquals(expDen, result);
    }

    @Test
    public void testPlus() {
    	// test various code optimization paths
        assertEquals("0", BigRational.ZERO.plus(BigRational.ZERO).toString());
        assertEquals("1", BigRational.ZERO.plus(BigRational.ONE).toString());
        assertEquals("1", BigRational.ONE.plus(BigRational.ZERO).toString());
        assertEquals("2", BigRational.ONE.plus(BigRational.ONE).toString());
    }
    
    @Test
    public void testNegate() {
        assertEquals(BigRational.ZERO.negate(), BigRational.ZERO);
        for(long n = 1; n <=10; n++) {
            BigRational pos = new BigRational(n);
            BigRational neg = new BigRational(-n);
            assertFalse(pos.equals(neg));
            assertEquals(pos.negate(), neg);
            assertEquals(neg.negate(), pos);
        }
        BigInteger big = new BigInteger(Long.toString(Long.MAX_VALUE) + "123567");
        BigRational pos = new BigRational(big);
        BigRational neg = new BigRational(big.negate());
        assertTrue(pos.isBig());
        assertTrue(neg.isBig());
        assertFalse(pos.equals(neg));
        assertEquals(pos.negate(), neg);
        assertEquals(neg.negate(), pos);
    }

    @Test
    public void testMinus() {
        // identities
        assertEquals("0", BigRational.ZERO.minus(BigRational.ZERO).toString());
        assertEquals("-1", BigRational.ZERO.minus(BigRational.ONE).toString());
        assertEquals("1", BigRational.ONE.minus(BigRational.ZERO).toString());
        assertEquals("0", BigRational.ONE.minus(BigRational.ONE).toString());

        // two not-big values subtract to make a negative big value
        BigRational br = new BigRational(Long.MIN_VALUE + 1 + "");
        assertTrue(br.notBig());
        assertTrue(br.isNegative());
        br = br.minus(BigRational.ONE);
        assertTrue(br.isBig());		 // BigRational initially failed
        assertTrue(br.isNegative());

        // big
        BigInteger big = new BigInteger(Long.MAX_VALUE + "7");
        BigRational pos = new BigRational(big);
        BigRational neg = new BigRational(big.negate());
        assertTrue(pos.isBig());
        assertTrue(neg.isBig());
        assertFalse(pos.equals(neg));
        assertEquals(pos.negate(), neg);
        assertEquals(neg.negate(), pos);
        assertEquals(neg.minus(neg).toString(), "0");
        assertEquals(pos.minus(pos).toString(), "0");
        assertTrue(pos.minus(neg).isPositive());
        assertTrue(neg.minus(pos).isNegative());
        // fractions
        assertEquals("-6/7", new BigRational("5/7").minus(new BigRational("11/7")).toString());  // denominator same
        assertEquals("-6/35", new BigRational("7/5").minus(new BigRational("11/7")).toString()); // denominators mixed
        assertEquals("-999/100000000000000000000", 
                new BigRational("1/100000000000000000000")
                        .minus(new BigRational("1/100000000000000000")).toString());
    }

    @Test
    public void testNotBigReciprocal() {
        assertTrue(BigRational.ONE.reciprocal().isOne());

        BigRational rational = new BigRational("37/19");
        BigRational reciprocal = rational.reciprocal();
        assertTrue(rational.isPositive());
        assertTrue(reciprocal.isPositive());
        assertFalse(rational.isBig());
        assertFalse(reciprocal.isBig());
        assertFalse(rational.equals(reciprocal));
        assertTrue(rational.times(reciprocal).isOne());
    }

    @Test
    public void testBigReciprocal() {
        BigRational rational = new BigRational(Long.toString(Long.MAX_VALUE) + "37/19");
        BigRational reciprocal = rational.reciprocal();
        assertTrue(rational.isPositive());
        assertTrue(reciprocal.isPositive());
        assertTrue(rational.isBig());
        assertTrue(reciprocal.isBig());
        assertFalse(rational.equals(reciprocal));
        assertTrue(rational.times(reciprocal).isOne());
    }

    @Test
    public void testBigNegativeReciprocal() {
        BigRational rational = new BigRational(Long.MIN_VALUE);
        BigRational reciprocal = rational.reciprocal();
        assertTrue(rational.isNegative());
        assertTrue(reciprocal.isNegative());
        assertTrue(rational.isBig());	 // BigRational initially failed
        assertTrue(reciprocal.isBig());	 // BigRational initially failed
        assertFalse(rational.equals(reciprocal));
        assertTrue(rational.times(reciprocal).isOne());
    }

    @Test
    public void testDividedBy() {
        BigRational b0 = new BigRational(0);
        BigRational b1 = new BigRational(1);
        BigRational b2 = new BigRational(2);
        assertEquals("1/2", b1.dividedBy(b2).toString());
        assertEquals("1", b2.dividedBy(b2).toString());
        assertEquals("2", b2.dividedBy(b1).toString());
        assertEquals("0", b0.dividedBy(b1).toString());
    }

    @Test
    public void testUnderflowByOne() {
        // not big
        BigRational r = new BigRational(Long.MIN_VALUE+1);
        assertTrue(r.isNegative());
        assertTrue(r.notBig());
        // big
        r = r.minus(BigRational.ONE);
        assertTrue(r.isNegative());
		 assertTrue(r.isBig()); // BigRational initially failed
    }

    // This is an example of an alternate annotation to be used for verifying that exceptions are thrown
    // See https://github.com/junit-team/junit4/wiki/Exception-testing
    @Test(expected = IllegalArgumentException.class)
    public void testAltDivideByZero() {
        // specify this here instead of hard coding the parameter below
        // so that the eclipse code coverage report recognizes that the method has been executed
        long denom = 0L;
        new BigRational(1L, denom);
    }
        
    @Test
    public void testDivideByZero() {
        String expected = "Denominator is zero";
        String failMsg = "Exception should have been thrown";
        int tries = 0;
        int catches = 0;
        try {
        	tries++;
            new BigRational(1L, 0L);  // Long c'tor
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            new BigRational(BigInteger.ONE, BigInteger.ZERO); // BigInteger c'tor
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            new BigRational("1/0"); // String c'tor
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRational.ZERO.reciprocal();
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRational.ONE.dividedBy(0);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRational.ONE.dividedBy(BigRational.ZERO);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRational.ZERO.dividedBy(0);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRational.ZERO.dividedBy(BigRational.ZERO);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        assertEquals(8, tries);
        assertEquals(catches, tries);
    }

    @Test
    public void testNegativeDenominators() {
        BigRational hr = new BigRational(-1L, -1L);  // Long c'tor
        assertTrue(hr.isOne());
        assertEquals(hr.getNumerator(), BigInteger.ONE );
        assertEquals(hr.getDenominator(), BigInteger.ONE );

        BigInteger neg1 = new BigInteger("-1");
        hr = new BigRational(neg1); // BigInteger c'tor
        assertTrue(hr.isNegative());
        assertEquals(hr.getNumerator(), BigInteger.ONE.negate() );
        assertEquals(hr.getDenominator(), BigInteger.ONE );

        hr = new BigRational(neg1, neg1); // BigInteger fractional c'tor
        assertTrue(hr.isOne());
        assertEquals(hr.getNumerator(), BigInteger.ONE );
        assertEquals(hr.getDenominator(), BigInteger.ONE );

        hr = new BigRational("-1/-1"); // String c'tor
        assertTrue(hr.isOne());
        assertEquals(hr.getNumerator(), BigInteger.ONE );
        assertEquals(hr.getDenominator(), BigInteger.ONE );
    }

    @Test
    public void testInvalidStringConstructors() {
        String prefix = "Parsing error: '";
        String failMsg = "Exception should have been thrown";
        int tries = 0;
        int catches = 0;
        try {
        	tries++;
            new BigRational("");
            fail(failMsg);
        } catch (NumberFormatException ex) {
            catches++;
        }
        try {
        	tries++;
            new BigRational("1/2/3");
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().startsWith(prefix));
            catches++;
        }
        try {
        	tries++;
            new BigRational("n/d");
            fail(failMsg);
        } catch (NumberFormatException ex) {
            catches++;
        }
        assertEquals(3, tries);
        assertEquals(catches, tries);
    }

    @Test
    public void testValidStringConstructors() {
    	// 20 characters should initially be parsed as BigIntegers but then reduced to notBig representation
        BigRational r = new BigRational("00000000000000000000");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRational("00000000000000000000/1");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRational("00000000000000000000/000000000000000000001");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRational("0/1");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRational("-0"); // negative sign should be ignored for value of zero  
        assertFalse(r.isBig());
        assertTrue(r.isZero());

        r = new BigRational("365");
        assertEquals(r.getNumerator().longValueExact(), 365 );
        assertEquals(r.getDenominator().longValueExact(), 1 );
        assertFalse(r.isBig());

        r = new BigRational("-17");
        assertEquals(r.getNumerator().longValueExact(), -17L );
        assertEquals(r.getDenominator().longValueExact(), 1 );
        assertFalse(r.isBig());

        r = new BigRational("81/54");
        assertEquals(r.getNumerator().longValueExact(), 3 );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertFalse(r.isBig());

        // optional + sign is numerator or denominator is OK
        r = new BigRational("+81/+54");
        assertEquals(r.getNumerator().longValueExact(), 3 );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertFalse(r.isBig());

        // both numerator and denominator being negative will result in positive
        r = new BigRational("-81/-54");
        assertEquals(r.getNumerator().longValueExact(), 3 );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertFalse(r.isBig());
        assertTrue(r.isPositive());

        final String IMAX = Long.toString(Integer.MAX_VALUE);
        final String IMIN = Long.toString(Integer.MIN_VALUE);
        final String LMAX = Long.toString(Long.MAX_VALUE);
        final String LMIN = Long.toString(Long.MIN_VALUE);

        assertEquals(IMAX,  "2147483647");
        assertEquals(IMIN, "-2147483648");
        assertEquals(LMAX,  "9223372036854775807");
        assertEquals(LMIN, "-9223372036854775808");

        r = new BigRational(IMAX + "/2");
        assertEquals(r.getNumerator().longValueExact(), Integer.MAX_VALUE );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertTrue(r.isPositive());
        assertFalse(r.isBig());

        r = new BigRational(LMAX + "/4");
        assertEquals(r.getNumerator().longValueExact(), Long.MAX_VALUE );
        assertEquals(r.getDenominator().longValueExact(), 4 );
        assertTrue(r.isPositive());
        assertFalse(r.isBig());

        r = new BigRational(LMAX + "/" + LMIN);
        assertEquals(r.getNumerator().longValueExact(), Math.negateExact(Long.MAX_VALUE) );	 // BigRational initially failed
        assertEquals(r.getDenominator(), new BigInteger(LMIN.replace("-", "")));	 // BigRational initially failed
        assertTrue(r.isNegative());	 // BigRational initially failed
        assertTrue(r.isBig());	 // BigRational initially failed

        r = new BigRational(LMIN + "/" + IMIN);
        assertEquals(r.getNumerator().longValueExact(), Long.MIN_VALUE / Integer.MIN_VALUE);
        assertEquals(r.getDenominator().longValueExact(), 1L);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());

        // these two numbers must be relatively prime
        String sNum = "55692240674506394991082821978149278838567587240332360";
        String sDen = "82146474236873358310743188506279129062594221951901256610699966876769677815441";
        r = new BigRational(sNum + "/" + sDen);
        assertEquals(r.getNumerator().toString(), sNum);
        assertEquals(r.getDenominator().toString(), sDen);
        assertTrue(r.isPositive());
        assertTrue(r.isBig());
    }

    @Test
    public void testUnderflowWithDenominatorOne() {
        // not big
        BigRational r = new BigRational(Long.MIN_VALUE+1);
        assertTrue(r.isNegative());
        assertFalse(r.isBig());
        // big
        r = r.minus(new BigRational(2));
        assertTrue(r.isNegative());    // BigRational initially failed
        assertTrue(r.isBig());         // BigRational initially failed
    }

    @Test
    public void testOverflowByOne() {
        // not big
        BigRational r = new BigRational(Long.MAX_VALUE);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());
        // big
        r = r.plus(BigRational.ONE);
        assertTrue(r.isPositive());    // BigRational initially failed
        assertTrue(r.isBig());         // BigRational initially failed
    }

    @Test
    public void testMultiplyExact() {
    	boolean[] overflow = new boolean[] { false };

    	overflow[0] = false;
    	BigRational.multiplyAndCheck(4611686018427387904L, -4, overflow);
    	assertTrue(overflow[0]);

    	overflow[0] = false;
    	BigRational.multiplyAndCheck(Long.MIN_VALUE, -2, overflow);
    	assertTrue(overflow[0]);

    	overflow[0] = false;
    	BigRational.multiplyAndCheck(Long.MIN_VALUE, -1, overflow);
    	assertTrue(overflow[0]);

    	overflow[0] = false;
    	BigRational.multiplyAndCheck(Long.MIN_VALUE, 0, overflow);
    	assertFalse(overflow[0]);

    }
    
    @Test
    public void testMultiplicationOverflowToBig() {
        BigRational b2 = new BigRational(2);
        // not big
        BigRational r = new BigRational(Long.MAX_VALUE);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());
        // big
        r = r.times(b2);
        assertTrue(r.isPositive());
        assertTrue(r.isBig());

        // not big
        r = new BigRational(Long.MAX_VALUE * -1);
        assertTrue(r.isNegative());
        assertFalse(r.isBig());
        // big
        r = r.times(b2);
        assertTrue(r.isNegative());
        assertTrue(r.isBig());
    }
    
    @Test
    public void testOverflowWithDenominatorOne() {
        // not big
        BigRational r = new BigRational(Long.MAX_VALUE);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());
        // big
        r = r.plus(new BigRational(2));
        assertTrue(r.isPositive());    // BigRational initially failed
        assertTrue(r.isBig());         // BigRational initially failed
    }

    @Test
    public void testOverflowWithFraction() {
        // not big
        final BigRational r = new BigRational(Long.MAX_VALUE, 3);
        BigRational total = r;
        assertTrue(total.isPositive());
        assertFalse(total.isBig());
        assertEquals("9223372036854775807/3", total.toString());

        total = total.plus(r);
        assertTrue(total.isPositive());	 // BigRational initially failed
        assertTrue(total.isBig());	 // BigRational initially failed
        assertEquals("18446744073709551614/3", total.toString());	 // BigRational initially failed

        total = total.plus(new BigRational(2));
        assertTrue(total.isPositive());
        assertTrue(total.isBig());	 // BigRational initially failed
        assertEquals("18446744073709551620/3", total.toString());	 // BigRational initially failed

        total = total.plus(r);
        assertTrue(total.isPositive());	 // BigRational initially failed
        assertTrue(total.isBig());	 // BigRational initially failed
        assertEquals("9223372036854775809", total.toString());	 // BigRational initially failed
    }

    @Test
    public void testReduceBig() {
        // big denom with 0 num
        BigRational r = new BigRational("0/1" + Long.MAX_VALUE);
        assertTrue(r.isZero());
        assertFalse(r.isBig());
    }

    @Test
    public void testBugFix1() {
        Long den = 4294967296L;
        Long n1 = 19401945L;
        Long n2 = 949299809L;

        BigRational br1 = new BigRational(n1, den);
        BigRational br2 = new BigRational(n2, den);
        
        BigRational br12 = br1.plus(br2);	// BigRational initially failed with an exception
        assertFalse(br12.isBig());	 		// BigRational initially failed
    }

    // BigRational initially had this test as a local method 
    // with println() in place of all of the assertEquals() 
    @Test
    public void testMain() {
        BigRational x, y, z;

        // 1/2 + 1/3 = 5/6
        x = new BigRational(1, 2);
        y = new BigRational(1, 3);
        z = x.plus(y);
        assertEquals("5/6", z.toString());

        // 8/9 + 1/9 = 1
        x = new BigRational(8, 9);
        y = new BigRational(1, 9);
        z = x.plus(y);
        assertEquals("1", z.toString());

        // 1/200000000 + 1/300000000 = 1/1200000000
        x = new BigRational(1, 200000000);
        y = new BigRational(1, 300000000);
        z = x.plus(y);
        assertEquals("1/120000000", z.toString());

        // 1073741789/20 + 1073741789/30 = 1073741789/12
        x = new BigRational(1073741789, 20);
        y = new BigRational(1073741789, 30);
        z = x.plus(y);
        assertEquals("1073741789/12", z.toString());

        //  4/17 * 17/4 = 1
        x = new BigRational(4, 17);
        y = new BigRational(17, 4);
        z = x.times(y);
        assertEquals("1", z.toString());

        // 3037141/3247033 * 3037547/3246599 = 841/961
        x = new BigRational(3037141, 3247033);
        y = new BigRational(3037547, 3246599);
        z = x.times(y);
        assertEquals("841/961", z.toString());
        
        // 1/6 - -4/-8 = -1/3
        x = new BigRational(1, 6);
        y = new BigRational(-4, -8);
        z = x.minus(y);
        assertEquals("-1/3", z.toString());

        // divide-by-zero
        x = new BigRational(0, 5);
//        System.out.println(x);
        assertEquals(0, x.plus(x).compareTo(x));
        try {
        	z = x.reciprocal();
        	fail("Exception should have been thrown");
        }
        catch(IllegalArgumentException ex) {
        	
        }

        // -1/200000000 + 1/300000000 = -1/600000000
        x = new BigRational(-1, 200000000);
        y = new BigRational(1, 300000000);
        z = x.plus(y);
        assertEquals("-1/600000000", z.toString());
    }	
}
