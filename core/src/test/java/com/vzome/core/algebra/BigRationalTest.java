package com.vzome.core.algebra;

import static com.vzome.core.generic.Utilities.compareDoubles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        BigRational r = new BigRationalImpl(num, den);
        String expected = num.toString() + "/" + den.toString();
        assertEquals(expected, r.toString());
        num = -2L;
        r = new BigRationalImpl(num, den);
        expected = num.toString() + "/" + den.toString();
        assertEquals(expected, r.toString());
        num = 0L;
        r = new BigRationalImpl(num, den);
        expected = "0";
        assertEquals(expected, r.toString());
    }

    @Test
    public void testAbs() {
        BigRationalImpl r = new BigRationalImpl("-2/3");
        assertEquals(-1, r.signum());
        assertNotEquals(r.signum(), r.abs().signum());
        assertEquals(1, r.abs().signum());
        r = BigRationalImpl.ZERO;
        assertEquals(r, r.abs());
        assertEquals(r.signum(), r.abs().signum());
    }

    @Test
    public void testMinMax() {
        BigRationalImpl j = new BigRationalImpl("2/3");
        assertEquals(j, BigRationalImpl.min(j, j.reciprocal()));
        assertEquals(j.reciprocal(), BigRationalImpl.max(j, j.reciprocal()));

        BigRationalImpl k = new BigRationalImpl("3/4");
        
        assertEquals(j, BigRationalImpl.min(j, k));
        assertEquals(k, BigRationalImpl.max(j, k));

        // both are commutative
        assertEquals(BigRationalImpl.min(j, k), BigRationalImpl.min(k, j));
        assertEquals(BigRationalImpl.max(j, k), BigRationalImpl.max(k, j));

        // test min and max of more than 2 items
        BigRationalImpl n = new BigRationalImpl(-42);
        
        assertEquals(n, BigRationalImpl.min(j, k, n));
        assertEquals(k, BigRationalImpl.max(j, k, n));
        
        // test duplicates
        assertEquals(n, BigRationalImpl.min(j, k, n, n, n, j, j, k, k));
        assertEquals(k, BigRationalImpl.max(j, k, n, n, n, j, j, k, k));
        
        List<BigRationalImpl> list = new ArrayList<>();
        // empty list returnd null
        assertNull(BigRationalImpl.min(list));
        assertNull(BigRationalImpl.max(list));
        // single element in list is returned
        list.add(j);
        assertEquals(j, BigRationalImpl.min(list));
        assertEquals(j, BigRationalImpl.max(list));
        // duplicates are OK
        list.add(j);
        assertEquals(j, BigRationalImpl.min(list));
        assertEquals(j, BigRationalImpl.max(list));
        // more...
        list.add(k);
        assertEquals(j, BigRationalImpl.min(list));
        assertEquals(k, BigRationalImpl.max(list));
        // more...
        list.add(n);
        assertEquals(n, BigRationalImpl.min(list));
        assertEquals(k, BigRationalImpl.max(list));
}

    @Test
    public void testCompareToBig() {
        BigInteger num = new BigInteger(Long.MAX_VALUE + "2");
        BigInteger den = new BigInteger(Long.MAX_VALUE + "3");
        assertNotEquals(num, den);

        BigRationalImpl r1 = new BigRationalImpl(num.negate(), den);
        BigRationalImpl r2 = new BigRationalImpl(num, den.negate());
        assertEquals(0, r1.compareTo(r2));

        r1 = new BigRationalImpl(num, den);
        r2 = new BigRationalImpl(num.negate(), den);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
    }

    @Test
    public void testCompareToLong() {
        Long num = 2L;
        Long den = 3L;
        assertNotEquals(num, den);

        BigRationalImpl r1 = new BigRationalImpl(-num, den);
        BigRationalImpl r2 = new BigRationalImpl(num, -den);
        assertEquals(0, r1.compareTo(r2));

        r1 = new BigRationalImpl(num, den);
        r2 = new BigRationalImpl(-num, den);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));
    }
    
    @Test
    public void testCompareToMixed() {
        Long j = 2L;
        BigInteger k = new BigInteger(Long.MAX_VALUE + "3");
        assertNotEquals(j, k);

        BigRationalImpl r1 = new BigRationalImpl(-j, k);
        BigRationalImpl r2 = new BigRationalImpl(j, k.negate());
        assertEquals(0, r1.compareTo(r2));

        r1 = new BigRationalImpl(j, k);
        r2 = new BigRationalImpl(-j, k);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));

        // swap order to test other c'tor
        r1 = new BigRationalImpl(k, j);
        r2 = new BigRationalImpl(k.negate(), j);
        assertEquals(1, r1.compareTo(r2));
        assertEquals(-1, r2.compareTo(r1));

    }

    @Test
    public void testIsZero() {
        BigRationalImpl r = new BigRationalImpl(0);
        assertTrue(r.isZero());
        r = new BigRationalImpl(1);
        assertFalse(r.isZero());
        r = new BigRationalImpl(-1);
        assertFalse(r.isZero());
    }

    @Test
    public void testIsPositive() {
        BigRationalImpl r = new BigRationalImpl(1);
        assertTrue(r.isPositive());
        r = new BigRationalImpl(0);
        assertFalse(r.isPositive());
        r = new BigRationalImpl(-1);
        assertFalse(r.isPositive());
    }

    @Test
    public void testIsNegative() {
        BigRationalImpl r = new BigRationalImpl(-1);
        assertTrue(r.isNegative());
        r = new BigRationalImpl(0);
        assertFalse(r.isNegative());
        r = new BigRationalImpl(1);
        assertFalse(r.isNegative());
    }

    @Test
    public void testIsOne() {
        BigRationalImpl r = new BigRationalImpl(1);
        assertTrue(r.isOne());
        r = new BigRationalImpl(0);
        assertFalse(r.isOne());
        r = new BigRationalImpl(-1);
        assertFalse(r.isOne());
    }

    @Test
    public void testIsWhole() {
        assertTrue(BigRationalImpl.ONE.isWhole());
        assertTrue(BigRationalImpl.ZERO.isWhole());
        assertTrue(new BigRationalImpl(-37).isWhole());
        assertTrue(new BigRationalImpl(242, 1).isWhole());
        assertFalse(new BigRationalImpl(1, 17).isWhole());
        BigRationalImpl r = new BigRationalImpl(2, 3);
        assertFalse(r.isWhole());
        assertFalse(r.reciprocal().isWhole());
        r = new BigRationalImpl(Long.MIN_VALUE);
        assertTrue(r.isWhole());
        r = new BigRationalImpl(Long.MAX_VALUE);
        assertTrue(r.isWhole());
    }

    @Test
    public void testIsBig() {
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MAX_VALUE + "");
        assertFalse(r.isBig());
        r = new BigRationalImpl(Long.MIN_VALUE + "/2");
        assertFalse(r.isBig());
        r = new BigRationalImpl(Long.MIN_VALUE + "/" + Long.MIN_VALUE);
        assertFalse(r.isBig());
        assertTrue(r.isOne());
        // big
        r = new BigRationalImpl(Long.MIN_VALUE + "");
        assertTrue(r.isBig());
        r = new BigRationalImpl(Long.MAX_VALUE + "0");
        assertTrue(r.isBig());
        r = new BigRationalImpl(Long.MIN_VALUE + "0");
        assertTrue(r.isBig());
    }

    @Test
    public void testNotBig() {
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MAX_VALUE);
        assertTrue(r.notBig());
        assertTrue(r.fitsInLong());
        
        r = new BigRationalImpl(Long.MIN_VALUE-1);
        assertTrue(r.notBig());
        assertTrue(r.fitsInLong());
        
        // reciprocals
        r = new BigRationalImpl(1, Long.MAX_VALUE);
        assertTrue(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRationalImpl(1, Long.MIN_VALUE-1);
        assertTrue(r.notBig());
        assertFalse(r.fitsInLong());
        
        // big
        r = new BigRationalImpl(Long.MIN_VALUE);
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
                
        r = new BigRationalImpl(Long.MAX_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRationalImpl(Long.MIN_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        // reciprocals
        r = new BigRationalImpl(1, Long.MIN_VALUE);
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRationalImpl("1/" + Long.MAX_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
        
        r = new BigRationalImpl("1/" + Long.MIN_VALUE + "0");
        assertFalse(r.notBig());
        assertFalse(r.fitsInLong());
    }

    @Test
    public void testNotBigExtremes() {
        // not big
        BigRationalImpl
        r = new BigRationalImpl(Long.MAX_VALUE, Long.MAX_VALUE);
        assertTrue(r.notBig());
        assertTrue(r.isOne());
        r = new BigRationalImpl(Long.MIN_VALUE, Long.MIN_VALUE);
        assertTrue(r.notBig());
        assertTrue(r.isOne());
    }

    @SuppressWarnings("unlikely-arg-type")
	@Test
    public void testEquals() {
        // test various code paths
        assertFalse(BigRationalImpl.ZERO.equals(null));
        // new implementation can test equality with a String or a Number
        assertTrue(BigRationalImpl.ZERO.equals(0));
        assertTrue(BigRationalImpl.ZERO.equals("0"));
        assertFalse(BigRationalImpl.ZERO.equals("Zero"));
        assertFalse(BigRationalImpl.ZERO.equals(this)); // could be any invalid type
        assertTrue(BigRationalImpl.ZERO.equals(BigInteger.ZERO));
        assertTrue(BigRationalImpl.ZERO.equals(BigRationalImpl.ZERO));
        assertTrue(BigRationalImpl.ZERO.equals(new BigRationalImpl(0)));
        assertTrue(BigRationalImpl.ONE.equals(new BigRationalImpl(1)));
        assertFalse(BigRationalImpl.ONE.equals(new BigRationalImpl(-1)));
        assertFalse(new BigRationalImpl(Long.MIN_VALUE,19).equals(new BigRationalImpl(Long.MIN_VALUE, -19)));	 // BigRationalImpl initially failed
    }

    @Test
    public void testHashCode() {
        // Hash codes are used by HashSets
        Set<BigRationalImpl> set1 = new HashSet<>();  
        Set<BigRationalImpl> set2 = new HashSet<>(); // set 2 is just here to exercice the cashed hashcode
        int qty = 0;
        for(int n = -50; n <= 50; n++) {
            if(n != 0) {
                BigRationalImpl b = new BigRationalImpl(n);
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
        double delta = 0.0D;
        assertEquals(Long.MIN_VALUE, new BigRationalImpl(Long.MIN_VALUE).evaluate(), delta); // big
        assertEquals(17d, new BigRationalImpl(17L).evaluate(), delta); // not big
        assertEquals(0.25d, new BigRationalImpl("-9/-36").evaluate(), delta); // not big
        assertEquals(0.001d, new BigRationalImpl(Long.MIN_VALUE + "/" + Long.MIN_VALUE+"000").evaluate(), delta); // big
        
        // last digit of n is chosen so it can't be reduced (e.g. to 2/3)
        String n = "222222222222222222222222222222222222222222222222220"; 
        String d = "333333333333333333333333333333333333333333333333333";
        String f = n + "/" + d;
        BigRationalImpl r = new BigRationalImpl(f);
        assertEquals(f, r.toString());

        BigDecimal bigDecimal = BigRationalImpl.toBigDecimal(r);
        double expected = bigDecimal.doubleValue();

        // Some alternative methods for calculating the double value of this fraction
        // may have greater rounding error than bigDecimal
        // even if r is not big, (e.g. 2/3) 
        // and especially when the decimal part is greater than 1/2,
        // then r.evaluate() may introduce 
        // minimal rounding error in the last digit.
        delta = 0.0000000000001;
        
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
        BigRationalImpl n = new BigRationalImpl(Long.MIN_VALUE);
    	n = n.plus(2);
        assertTrue(n.isWhole());
    	assertEquals(Long.toString(Long.MIN_VALUE + 2), n.toString());
    	
        n = BigRationalImpl.ZERO.plus(3);
        assertEquals("3", n.toString());

        n = n.plus(-13);
        assertEquals("-10", n.toString());

        n = new BigRationalImpl(Integer.MAX_VALUE);
    	n = n.plus(Integer.MAX_VALUE);
        assertFalse(n.isBig());
    	assertEquals(Long.toString(Integer.MAX_VALUE * 2L), n.toString());
    	
        n = BigRationalImpl.ZERO.plus(7);
        assertEquals("7", n.toString());

        n = n.plus(0);
        assertEquals("7", n.toString());

        n = n.plus(-17);
        assertEquals("-10", n.toString());
    }
    
    @Test
    public void testMinusInteger() {
        BigRationalImpl n = new BigRationalImpl(Long.MAX_VALUE);
    	n = n.minus(2);
        assertTrue(n.isWhole());
    	assertEquals(Long.toString(Long.MAX_VALUE - 2), n.toString());
    	
        n = BigRationalImpl.ZERO.minus(3);
        assertEquals("-3", n.toString());

        n = n.minus(-13);
        assertEquals("10", n.toString());

        n = BigRationalImpl.ZERO.minus(5);
        assertEquals("-5", n.toString());

        n = n.minus(0);
        assertEquals("-5", n.toString());

        n = n.minus(-15);
        assertEquals("10", n.toString());
    }
    
    @Test
    public void testTimesInteger() {
        BigRationalImpl n = new BigRationalImpl(Long.MAX_VALUE);
    	n = n.timesInt(2);
        assertTrue(n.isBig());
    	assertEquals(BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)).toString(), n.toString());
    	
    	n = new BigRationalImpl(Integer.MAX_VALUE);
    	n = n.timesInt(2);
    	assertEquals(Long.toString(Integer.MAX_VALUE * 2L), n.toString());
    	
    	n = new BigRationalImpl(37);
    	n = n.timesInt(1);
    	assertEquals("37", n.toString());
    	
    	n = n.timesInt(0);
    	assertEquals("0", n.toString());
    	
    	n = new BigRationalImpl(43);
    	n = n.timesInt(7);
    	assertEquals(Long.toString(43*7), n.toString());
    	
        n = BigRationalImpl.ZERO.timesInt(6);
        assertEquals("0", n.toString());

        n = new BigRationalImpl(Integer.MAX_VALUE);
    	n = n.timesInt(Integer.MAX_VALUE);
        assertFalse(n.isBig());
    	assertEquals(Long.toString((long)Integer.MAX_VALUE * (long)Integer.MAX_VALUE), n.toString());

    	n = new BigRationalImpl(Integer.MIN_VALUE);
    	n = n.timesInt(Integer.MIN_VALUE);
        assertFalse(n.isBig());
    	assertEquals(n.toString(), Long.toString((long)Integer.MIN_VALUE * (long)Integer.MIN_VALUE));
    }

    @Test
    public void testDividedByLong() {
        BigRationalImpl n = new BigRationalImpl(Long.MIN_VALUE);
    	n = n.dividedBy(2);
        assertTrue(n.isWhole());
    	assertEquals(Long.toString(Long.MIN_VALUE / 2L), n.toString());
    	
    	n = n.dividedBy(3);
        assertFalse(n.isWhole());
    	assertEquals(Long.toString(Long.MIN_VALUE / 2L) + "/3", n.toString());
    	
        n = BigRationalImpl.ONE.dividedBy(1);
        assertEquals("1", n.toString());

        n = BigRationalImpl.ONE.dividedBy(-2);
        assertEquals("-1/2", n.toString());

        n = BigRationalImpl.ZERO.dividedBy(3);
        assertEquals("0", n.toString());

        n = BigRationalImpl.ONE.dividedBy(4);
        assertEquals("1/4", n.toString());

        n = new BigRationalImpl(Integer.MAX_VALUE);
    	n = n.dividedBy(Integer.MAX_VALUE);
    	assertEquals("1", n.toString());
    }    

    @Test
    public void testTimes() {
        {
            BigRationalImpl neg = new BigRationalImpl(-1L);
            BigRationalImpl min = new BigRationalImpl(Long.MIN_VALUE);
            assertFalse(neg.isBig());
            assertTrue(min.isBig());	 		// BigRational initially failed
            assertTrue(neg.isNegative());
            assertTrue(min.isNegative());
            BigRationalImpl result = (BigRationalImpl) min.times(neg);
            assertTrue(result.isBig());	 		// BigRationalImpl initially failed
            assertTrue(result.isPositive());	// BigRationalImpl initially failed
            result = (BigRationalImpl) result.times(neg);
            assertTrue(result.isBig());			// BigRationalImpl initially failed
            assertTrue(result.isNegative());
        }
        assertEquals("11/5", new BigRationalImpl("7/5").times(new BigRationalImpl("11/7")).toString()); // 7's cancel
        // tests 4 different code paths
        assertTrue(new BigRationalImpl("2").times(BigRationalImpl.ZERO).isZero());
        assertTrue(BigRationalImpl.ZERO.times(new BigRationalImpl("2")).isZero());
        assertTrue(BigRationalImpl.ZERO.times(BigRationalImpl.ONE).isZero());
        assertTrue(BigRationalImpl.ONE.times(BigRationalImpl.ZERO).isZero());
        {
            BigRationalImpl neg = new BigRationalImpl(-1L);
            assertFalse(neg.isBig());
            BigRationalImpl min = new BigRationalImpl(Long.MIN_VALUE);
            assertTrue(min.isBig());				 // BigRationalImpl initially failed
            min = new BigRationalImpl(Long.MIN_VALUE+1);
            assertTrue(min.notBig());
            BigRationalImpl result = (BigRationalImpl) min.times(neg);
            assertTrue(result.notBig());
            result = (BigRationalImpl) result.times(neg);
            assertFalse(result.isBig());
        }
    }

    @Test
    public void testTimesOne() {
        BigRationalImpl r1 = new BigRationalImpl(1);
        BigRationalImpl r2 = new BigRationalImpl(2);
        assertFalse(r1.equals(r2));
        // test two different code paths in BigRationalImpl.times()
        assertEquals(r1.times(r2), r2.times(r1));
    }

    @Test
    public void testGetNumerator() {
        Long num = 2L;
        Long den = 3L;
        BigRationalImpl r = new BigRationalImpl(num, den);
        BigInteger expNum = new BigInteger(num.toString());
        BigInteger result = r.getNumerator();
        assertEquals(expNum, result);
    }

    @Test
    public void testGetDenominator() {
        Long num = 2L;
        Long den = 3L;
        BigRationalImpl r = new BigRationalImpl(num, den);
        BigInteger expDen = new BigInteger(den.toString());
        BigInteger result = r.getDenominator();
        assertEquals(expDen, result);
    }

    @Test
    public void testOperatorOverloads() {
        BigRationalImpl[] numbers = new BigRationalImpl[] {
                BigRationalImpl.ZERO,
                BigRationalImpl.ONE,
                new BigRationalImpl(-1),
                new BigRationalImpl(42),
                new BigRationalImpl(22, 7)
        };

        final int denominator = 5;
        for(int numerator = -3; numerator <= 3; numerator++) {
            for(BigRationalImpl n : numbers) {
                // first, test with fractions as numerator and denominator args
                BigRationalImpl r = new BigRationalImpl(numerator, denominator);
                assertEquals("add rat", n. plus(r), n. plus(numerator, denominator));
                assertEquals("sub rat", n.minus(r), n.minus(numerator, denominator));
                assertEquals("mul rat", n.times(r), n.timesRational(numerator, denominator));
                try {
                    assertEquals("div rat", n.dividedBy(r), n.dividedBy(numerator, denominator));
                    assertNotEquals("Expected no divide by zero exception.", 0, numerator);
                } catch( IllegalArgumentException ex) {
                    assertEquals("Expected divide by zero exception.", 0, numerator);
                }
                // and again with just integers
                r = new BigRationalImpl(numerator);
                assertEquals("add int", n. plus(r), n. plus(numerator));
                assertEquals("sub int", n.minus(r), n.minus(numerator));
                assertEquals("mul int", n.times(r), n.timesInt(numerator));
                try {
                    assertEquals("div int", n.dividedBy(r), n.dividedBy(numerator));
                    assertNotEquals("Expected no divide by zero exception.", 0, numerator);
                } catch( IllegalArgumentException ex) {
                    assertEquals("Expected divide by zero exception.", 0, numerator);
                }
            }
        }
    }

    @Test
    public void testPlus() {
    	// test various code optimization paths
        assertEquals("0", BigRationalImpl.ZERO.plus(BigRationalImpl.ZERO).toString());
        assertEquals("1", BigRationalImpl.ZERO.plus(BigRationalImpl.ONE).toString());
        assertEquals("1", BigRationalImpl.ONE.plus(BigRationalImpl.ZERO).toString());
        assertEquals("2", BigRationalImpl.ONE.plus(BigRationalImpl.ONE).toString());
    }
    
    @Test
    public void testNegate() {
        assertEquals(BigRationalImpl.ZERO.negate(), BigRationalImpl.ZERO);
        for(long n = 1; n <=10; n++) {
            BigRationalImpl pos = new BigRationalImpl(n);
            BigRationalImpl neg = new BigRationalImpl(-n);
            assertFalse(pos.equals(neg));
            assertEquals(pos.negate(), neg);
            assertEquals(neg.negate(), pos);
        }
        BigInteger big = new BigInteger(Long.toString(Long.MAX_VALUE) + "123567");
        BigRationalImpl pos = new BigRationalImpl(big);
        BigRationalImpl neg = new BigRationalImpl(big.negate());
        assertTrue(pos.isBig());
        assertTrue(neg.isBig());
        assertFalse(pos.equals(neg));
        assertEquals(pos.negate(), neg);
        assertEquals(neg.negate(), pos);
    }

    @Test
    public void testMinus() {
        // identities
        assertEquals("0", BigRationalImpl.ZERO.minus(BigRationalImpl.ZERO).toString());
        assertEquals("-1", BigRationalImpl.ZERO.minus(BigRationalImpl.ONE).toString());
        assertEquals("1", BigRationalImpl.ONE.minus(BigRationalImpl.ZERO).toString());
        assertEquals("0", BigRationalImpl.ONE.minus(BigRationalImpl.ONE).toString());

        // two not-big values subtract to make a negative big value
        BigRationalImpl br = new BigRationalImpl(Long.MIN_VALUE + 1 + "");
        assertTrue(br.notBig());
        assertTrue(br.isNegative());
        br = (BigRationalImpl) br.minus(BigRationalImpl.ONE);
        assertTrue(br.isBig());		 // BigRationalImpl initially failed
        assertTrue(br.isNegative());

        // big
        BigInteger big = new BigInteger(Long.MAX_VALUE + "7");
        BigRationalImpl pos = new BigRationalImpl(big);
        BigRationalImpl neg = new BigRationalImpl(big.negate());
        assertTrue(pos.isBig());
        assertTrue(neg.isBig());
        assertFalse(pos.equals(neg));
        assertEquals(pos.negate(), neg);
        assertEquals(neg.negate(), pos);
        assertEquals(neg.minus(neg).toString(), "0");
        assertEquals(pos.minus(pos).toString(), "0");
        assertTrue(((BigRationalImpl) pos.minus(neg)).isPositive());
        assertTrue(neg.minus(pos).isNegative());
        // fractions
        assertEquals("-6/7", new BigRationalImpl("5/7").minus(new BigRationalImpl("11/7")).toString());  // denominator same
        assertEquals("-6/35", new BigRationalImpl("7/5").minus(new BigRationalImpl("11/7")).toString()); // denominators mixed
        assertEquals("-999/100000000000000000000", 
                new BigRationalImpl("1/100000000000000000000")
                        .minus(new BigRationalImpl("1/100000000000000000")).toString());
    }

    @Test
    public void testNotBigReciprocal() {
        assertTrue(BigRationalImpl.ONE.reciprocal().isOne());

        BigRationalImpl rational = new BigRationalImpl("37/19");
        BigRationalImpl reciprocal = rational.reciprocal();
        assertTrue(rational.isPositive());
        assertTrue(reciprocal.isPositive());
        assertFalse(rational.isBig());
        assertFalse(reciprocal.isBig());
        assertFalse(rational.equals(reciprocal));
        assertTrue(rational.times(reciprocal).isOne());
    }

    @Test
    public void testBigReciprocal() {
        BigRationalImpl rational = new BigRationalImpl(Long.toString(Long.MAX_VALUE) + "37/19");
        BigRationalImpl reciprocal = rational.reciprocal();
        assertTrue(rational.isPositive());
        assertTrue(reciprocal.isPositive());
        assertTrue(rational.isBig());
        assertTrue(reciprocal.isBig());
        assertFalse(rational.equals(reciprocal));
        assertTrue(rational.times(reciprocal).isOne());
    }

    @Test
    public void testBigNegativeReciprocal() {
        BigRationalImpl rational = new BigRationalImpl(Long.MIN_VALUE);
        BigRationalImpl reciprocal = rational.reciprocal();
        assertTrue(rational.isNegative());
        assertTrue(reciprocal.isNegative());
        assertTrue(rational.isBig());	 // BigRationalImpl initially failed
        assertTrue(reciprocal.isBig());	 // BigRationalImpl initially failed
        assertFalse(rational.equals(reciprocal));
        assertTrue(rational.times(reciprocal).isOne());
    }

    @Test
    public void testDividedBy() {
        BigRationalImpl b0 = new BigRationalImpl(0);
        BigRationalImpl b1 = new BigRationalImpl(1);
        BigRationalImpl b2 = new BigRationalImpl(2);
        assertEquals("1/2", b1.dividedBy(b2).toString());
        assertEquals("1", b2.dividedBy(b2).toString());
        assertEquals("2", b2.dividedBy(b1).toString());
        assertEquals("0", b0.dividedBy(b1).toString());
    }

    @Test
    public void testUnderflowByOne() {
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MIN_VALUE+1);
        assertTrue(r.isNegative());
        assertTrue(r.notBig());
        // big
        r = (BigRationalImpl) r.minus(BigRationalImpl.ONE);
        assertTrue(r.isNegative());
		 assertTrue(r.isBig()); // BigRationalImpl initially failed
    }

    // This is an example of an alternate annotation to be used for verifying that exceptions are thrown
    // See https://github.com/junit-team/junit4/wiki/Exception-testing
    @Test(expected = IllegalArgumentException.class)
    public void testAltDivideByZero() {
        // specify this here instead of hard coding the parameter below
        // so that the eclipse code coverage report recognizes that the method has been executed
        long denom = 0L;
        new BigRationalImpl(1L, denom);
    }
        
    @Test
    public void testDivideByZero() {
        String expected = "Denominator is zero";
        String failMsg = "Exception should have been thrown";
        int tries = 0;
        int catches = 0;
        try {
        	tries++;
            new BigRationalImpl(1L, 0L);  // Long c'tor
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            new BigRationalImpl(BigInteger.ONE, BigInteger.ZERO); // BigInteger c'tor
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            new BigRationalImpl("1/0"); // String c'tor
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRationalImpl.ZERO.reciprocal();
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRationalImpl.ONE.dividedBy(0);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRationalImpl.ONE.dividedBy(BigRationalImpl.ZERO);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRationalImpl.ZERO.dividedBy(0);
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertEquals(expected, ex.getMessage());
            catches++;
        }
        try {
        	tries++;
            BigRationalImpl.ZERO.dividedBy(BigRationalImpl.ZERO);
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
        BigRationalImpl hr = new BigRationalImpl(-1L, -1L);  // Long c'tor
        assertTrue(hr.isOne());
        assertEquals(hr.getNumerator(), BigInteger.ONE );
        assertEquals(hr.getDenominator(), BigInteger.ONE );

        BigInteger neg1 = new BigInteger("-1");
        hr = new BigRationalImpl(neg1); // BigInteger c'tor
        assertTrue(hr.isNegative());
        assertEquals(hr.getNumerator(), BigInteger.ONE.negate() );
        assertEquals(hr.getDenominator(), BigInteger.ONE );

        hr = new BigRationalImpl(neg1, neg1); // BigInteger fractional c'tor
        assertTrue(hr.isOne());
        assertEquals(hr.getNumerator(), BigInteger.ONE );
        assertEquals(hr.getDenominator(), BigInteger.ONE );

        hr = new BigRationalImpl("-1/-1"); // String c'tor
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
            new BigRationalImpl("");
            fail(failMsg);
        } catch (NumberFormatException ex) {
            catches++;
        }
        try {
        	tries++;
            new BigRationalImpl("1/2/3");
            fail(failMsg);
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().startsWith(prefix));
            catches++;
        }
        try {
        	tries++;
            new BigRationalImpl("n/d");
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
        BigRationalImpl r = new BigRationalImpl("00000000000000000000");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRationalImpl("00000000000000000000/1");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRationalImpl("00000000000000000000/000000000000000000001");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRationalImpl("0/1");  
        assertFalse(r.isBig());
        assertTrue(r.isZero());
        r = new BigRationalImpl("-0"); // negative sign should be ignored for value of zero  
        assertFalse(r.isBig());
        assertTrue(r.isZero());

        r = new BigRationalImpl("365");
        assertEquals(r.getNumerator().longValueExact(), 365 );
        assertEquals(r.getDenominator().longValueExact(), 1 );
        assertFalse(r.isBig());

        r = new BigRationalImpl("-17");
        assertEquals(r.getNumerator().longValueExact(), -17L );
        assertEquals(r.getDenominator().longValueExact(), 1 );
        assertFalse(r.isBig());

        r = new BigRationalImpl("81/54");
        assertEquals(r.getNumerator().longValueExact(), 3 );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertFalse(r.isBig());

        // optional + sign is numerator or denominator is OK
        r = new BigRationalImpl("+81/+54");
        assertEquals(r.getNumerator().longValueExact(), 3 );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertFalse(r.isBig());

        // both numerator and denominator being negative will result in positive
        r = new BigRationalImpl("-81/-54");
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

        r = new BigRationalImpl(IMAX + "/2");
        assertEquals(r.getNumerator().longValueExact(), Integer.MAX_VALUE );
        assertEquals(r.getDenominator().longValueExact(), 2 );
        assertTrue(r.isPositive());
        assertFalse(r.isBig());

        r = new BigRationalImpl(LMAX + "/4");
        assertEquals(r.getNumerator().longValueExact(), Long.MAX_VALUE );
        assertEquals(r.getDenominator().longValueExact(), 4 );
        assertTrue(r.isPositive());
        assertFalse(r.isBig());

        r = new BigRationalImpl(LMAX + "/" + LMIN);
        assertEquals(r.getNumerator().longValueExact(), Math.negateExact(Long.MAX_VALUE) );	 // BigRationalImpl initially failed
        assertEquals(r.getDenominator(), new BigInteger(LMIN.replace("-", "")));	 // BigRationalImpl initially failed
        assertTrue(r.isNegative());	 // BigRationalImpl initially failed
        assertTrue(r.isBig());	 // BigRationalImpl initially failed

        r = new BigRationalImpl(LMIN + "/" + IMIN);
        assertEquals(r.getNumerator().longValueExact(), Long.MIN_VALUE / Integer.MIN_VALUE);
        assertEquals(r.getDenominator().longValueExact(), 1L);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());

        // these two numbers must be relatively prime
        String sNum = "55692240674506394991082821978149278838567587240332360";
        String sDen = "82146474236873358310743188506279129062594221951901256610699966876769677815441";
        r = new BigRationalImpl(sNum + "/" + sDen);
        assertEquals(r.getNumerator().toString(), sNum);
        assertEquals(r.getDenominator().toString(), sDen);
        assertTrue(r.isPositive());
        assertTrue(r.isBig());
    }

    @Test
    public void testUnderflowWithDenominatorOne() {
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MIN_VALUE+1);
        assertTrue(r.isNegative());
        assertFalse(r.isBig());
        // big
        r = (BigRationalImpl) r.minus(new BigRationalImpl(2));
        assertTrue(r.isNegative());    // BigRationalImpl initially failed
        assertTrue(r.isBig());         // BigRationalImpl initially failed
    }

    @Test
    public void testOverflowByOne() {
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MAX_VALUE);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());
        // big
        r = (BigRationalImpl) r.plus(BigRationalImpl.ONE);
        assertTrue(r.isPositive());    // BigRationalImpl initially failed
        assertTrue(r.isBig());         // BigRationalImpl initially failed
    }

    @Test
    public void testMultiplyExact() {
    	boolean[] overflow = new boolean[] { false };

    	overflow[0] = false;
    	BigRationalImpl.multiplyAndCheck(4611686018427387904L, -4, overflow);
    	assertTrue(overflow[0]);

    	overflow[0] = false;
    	BigRationalImpl.multiplyAndCheck(Long.MIN_VALUE, -2, overflow);
    	assertTrue(overflow[0]);

    	overflow[0] = false;
    	BigRationalImpl.multiplyAndCheck(Long.MIN_VALUE, -1, overflow);
    	assertTrue(overflow[0]);

    	overflow[0] = false;
    	BigRationalImpl.multiplyAndCheck(Long.MIN_VALUE, 0, overflow);
    	assertFalse(overflow[0]);

    }
    
    @Test
    public void testMultiplicationOverflowToBig() {
        BigRationalImpl b2 = new BigRationalImpl(2);
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MAX_VALUE);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());
        // big
        r = (BigRationalImpl) r.times(b2);
        assertTrue(r.isPositive());
        assertTrue(r.isBig());

        // not big
        r = new BigRationalImpl(Long.MAX_VALUE * -1);
        assertTrue(r.isNegative());
        assertFalse(r.isBig());
        // big
        r = (BigRationalImpl) r.times(b2);
        assertTrue(r.isNegative());
        assertTrue(r.isBig());
    }
    
    @Test
    public void testOverflowWithDenominatorOne() {
        // not big
        BigRationalImpl r = new BigRationalImpl(Long.MAX_VALUE);
        assertTrue(r.isPositive());
        assertFalse(r.isBig());
        // big
        r = (BigRationalImpl) r.plus(new BigRationalImpl(2));
        assertTrue(r.isPositive());    // BigRationalImpl initially failed
        assertTrue(r.isBig());         // BigRationalImpl initially failed
    }

    @Test
    public void testOverflowWithFraction() {
        // not big
        final BigRationalImpl r = new BigRationalImpl(Long.MAX_VALUE, 3);
        BigRationalImpl total = r;
        assertTrue(total.isPositive());
        assertFalse(total.isBig());
        assertEquals("9223372036854775807/3", total.toString());

        total = (BigRationalImpl) total.plus(r);
        assertTrue(total.isPositive());	 // BigRationalImpl initially failed
        assertTrue(total.isBig());	 // BigRationalImpl initially failed
        assertEquals("18446744073709551614/3", total.toString());	 // BigRationalImpl initially failed

        total = (BigRationalImpl) total.plus(new BigRationalImpl(2));
        assertTrue(total.isPositive());
        assertTrue(total.isBig());	 // BigRationalImpl initially failed
        assertEquals("18446744073709551620/3", total.toString());	 // BigRationalImpl initially failed

        total = (BigRationalImpl) total.plus(r);
        assertTrue(total.isPositive());	 // BigRationalImpl initially failed
        assertTrue(total.isBig());	 // BigRationalImpl initially failed
        assertEquals("9223372036854775809", total.toString());	 // BigRationalImpl initially failed
    }

    @Test
    public void testReduceBig() {
        // big denom with 0 num
        BigRationalImpl r = new BigRationalImpl("0/1" + Long.MAX_VALUE);
        assertTrue(r.isZero());
        assertFalse(r.isBig());
    }

    @Test
    public void testHarmonicMeans() {
        // The harmonic means of a set of numbers is the reciprocal of the sum of their reciprocals.
        // This test calculates the harmonic mean of all integers from 1 to the specified limit. 
        double last = 2.0;
        boolean big = false;
        BigRationalImpl sum = BigRationalImpl.ZERO;
        // I have run this test with a limit as high as 10,000 but it took 40 seconds to run
        // A limit of 1000 runs in well under a second 
        // and is adequate to test for the problem that initiated this test case
        final long limit = 1000;
        long i;
        for(i = 1; i <= limit; i++) {
            BigRationalImpl recip = new BigRationalImpl(1, i);
            sum = (BigRationalImpl) sum.plus(recip);
            BigRationalImpl harmonicMean = sum.reciprocal();
            double curr = harmonicMean.evaluate();
            if((!big) && harmonicMean.isBig()) {
                big = true;
                System.out.println("first big harmonic means is:\n#" + i + ":\t" + curr + "\t= " + harmonicMean);
            }
            if(i == limit)
                System.out.println("test limit:\n#" + i + ":\t" + curr + "\t= " + harmonicMean);
            // Prior to the bug fix associated with this test case, these tests would fail as indicated 
            assertTrue("positive", curr > 0.0d);            // i = 719 incorrectly reduced to 0.
            assertTrue("diminishing", last > curr);         // i = 720 remained at 0.
            assertTrue("finite", Double.isFinite(curr));    // i = 727 incorrectly reduced to NaN.
            last = curr;
        }
        assertTrue("big", big);
        assertTrue("big enough", i > 727);  // be sure we tested the original problem
    }

    @Test
    public void testBugFix1() {
        Long den = 4294967296L;
        Long n1 = 19401945L;
        Long n2 = 949299809L;

        BigRationalImpl br1 = new BigRationalImpl(n1, den);
        BigRationalImpl br2 = new BigRationalImpl(n2, den);
        
        BigRationalImpl br12 = (BigRationalImpl) br1.plus(br2);	// BigRationalImpl initially failed with an exception
        assertFalse(br12.isBig());	 		// BigRationalImpl initially failed
    }

    // BigRationalImpl initially had this test as a local method 
    // with println() in place of all of the assertEquals() 
    @Test
    public void testMain() {
        BigRationalImpl x, y, z;

        // 1/2 + 1/3 = 5/6
        x = new BigRationalImpl(1, 2);
        y = new BigRationalImpl(1, 3);
        z = (BigRationalImpl) x.plus(y);
        assertEquals("5/6", z.toString());

        // 8/9 + 1/9 = 1
        x = new BigRationalImpl(8, 9);
        y = new BigRationalImpl(1, 9);
        z = (BigRationalImpl) x.plus(y);
        assertEquals("1", z.toString());

        // 1/200000000 + 1/300000000 = 1/1200000000
        x = new BigRationalImpl(1, 200000000);
        y = new BigRationalImpl(1, 300000000);
        z = (BigRationalImpl) x.plus(y);
        assertEquals("1/120000000", z.toString());

        // 1073741789/20 + 1073741789/30 = 1073741789/12
        x = new BigRationalImpl(1073741789, 20);
        y = new BigRationalImpl(1073741789, 30);
        z = (BigRationalImpl) x.plus(y);
        assertEquals("1073741789/12", z.toString());

        //  4/17 * 17/4 = 1
        x = new BigRationalImpl(4, 17);
        y = new BigRationalImpl(17, 4);
        z = (BigRationalImpl) x.times(y);
        assertEquals("1", z.toString());

        // 3037141/3247033 * 3037547/3246599 = 841/961
        x = new BigRationalImpl(3037141, 3247033);
        y = new BigRationalImpl(3037547, 3246599);
        z = (BigRationalImpl) x.times(y);
        assertEquals("841/961", z.toString());
        
        // 1/6 - -4/-8 = -1/3
        x = new BigRationalImpl(1, 6);
        y = new BigRationalImpl(-4, -8);
        z = (BigRationalImpl) x.minus(y);
        assertEquals("-1/3", z.toString());

        // divide-by-zero
        x = new BigRationalImpl(0, 5);
//        System.out.println(x);
        assertEquals(0, ((BigRationalImpl) x.plus(x)).compareTo(x));
        try {
        	z = x.reciprocal();
        	fail("Exception should have been thrown");
        }
        catch(IllegalArgumentException ex) {
        	
        }

        // -1/200000000 + 1/300000000 = -1/600000000
        x = new BigRationalImpl(-1, 200000000);
        y = new BigRationalImpl(1, 300000000);
        z = (BigRationalImpl) x.plus(y);
        assertEquals("-1/600000000", z.toString());
    }	
}
