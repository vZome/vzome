package com.vzome.core.algebra;

import static com.vzome.core.algebra.BigRational.Gcd.LOOKUP_SIZE;
import static com.vzome.core.generic.Utilities.thisSourceCodeLine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

import org.junit.Test;

public class GcdBenchmarks {

	@Test
	public void testGcdLookupSize() {
		int lSize = BigRational.Gcd.LOOKUP_SIZE;
		assertEquals("BigRational.Gcd.lookupSize must be a power of two.", lSize & -lSize, lSize);
		assertTrue("BigRational.Gcd.lookupSize must greater than 1", lSize > 1);
		assertTrue("BigRational.Gcd.lookupSize not be greater than than 256", lSize <= 256);
		assertEquals("Be sure that the highest cached values are returned as positive numbers.", 
			BigRational.Gcd.LOOKUP_SIZE, BigRational.Gcd.gcd(0, BigRational.Gcd.LOOKUP_SIZE));
		
	}

	@Test
	public void verifyUncachedImplementations() {
		for(int j = 0; j < BigRational.Gcd.LOOKUP_SIZE; j++) {
			BigInteger jj = BigInteger.valueOf(j);
			for(int k = 0; k < BigRational.Gcd.LOOKUP_SIZE; k++) {
				String msg = j + " " + k + ": ";
				BigInteger kk = BigInteger.valueOf(k);
				long expected = jj.gcd(kk).longValueExact();
				assertTrue(msg, expected >= 0);

				// These methods don't accept negative values
				assertEquals(msg, expected, cachedGcd(j, k)); 
				assertEquals(msg, expected, cachedGcd(k, j));  // swap args
				assertEquals(msg, expected, BigRational.Gcd.calculateGcd(j, k));
				assertEquals(msg, expected, BigRational.Gcd.gcd(k, j)); // swap args
				
				// These methods accept negative and positive values
				for(int sign = 1; sign >= -1; sign -= 2) {
					msg = (sign * j) + " " + k + ": ";
					assertEquals(msg, expected, swingGcd(sign * j, k));
					assertEquals(msg, expected, vZomeOriginalGcd(sign * j, k));
					assertEquals(msg, expected, apacheGcd(sign * j, k));
					assertEquals(msg, expected, googleGcd(sign * j, k));
				}
			}
		}
	}
	
	@Test
	public void verifyCachedImplementations() {
		int span = 4; // test this many values above and below the cache size
		for(int j = BigRational.Gcd.LOOKUP_SIZE-span; j < BigRational.Gcd.LOOKUP_SIZE+span; j++) {
			BigInteger jj = BigInteger.valueOf(j);
			for(int k = BigRational.Gcd.LOOKUP_SIZE-span; k < BigRational.Gcd.LOOKUP_SIZE+span; k++) {
				String msg = j + " " + k + ": ";
				BigInteger kk = BigInteger.valueOf(k);
				long expected = jj.gcd(kk).longValueExact();
				assertTrue(msg, expected >= 0);
				for(int sign = 1; sign >= -1; sign -= 2) {
					msg = (sign * j) + " " + k + ": ";
					assertEquals(msg, expected, swingGcdCached(sign * j, k));
					assertEquals(msg, expected, vZomeOriginalGcdCached(sign * j, k));
					assertEquals(msg, expected, apacheGcdCached(sign * j, k));
					assertEquals(msg, expected, googleGcdCached(sign * j, k));
					assertEquals(msg, expected, BigRational.Gcd.gcd(sign * j, k));
					assertEquals(msg, expected, BigRational.Gcd.gcd(sign * k, j)); // swap args
				}
			}
		}
	}
	
	/**
	 * This method compares the execution time for various gcd algorthms with and without caching.
	 * It will typically take 2 minutes or longer to execute when reps is set to 250000000.
	 * Since doesn't actually test any code behavior, it's not normally enabled as a unit test.
	 * Uncomment the @Test annotation to enable it. 
	 */
//	@Test
	public void testGcdBenchmarks() { 
		int reps = 250000000;
		int range = BigRational.Gcd.LOOKUP_SIZE;

		showBenchmarkSettings(BigRational.Gcd.LOOKUP_SIZE, range, reps);
		benchmark( "Direct cache lookup", thisSourceCodeLine(), GcdBenchmarks::cachedGcd, range, reps);
		benchmark( "BigRational gcd", thisSourceCodeLine(), BigRational.Gcd::gcd, range, reps);
		benchmark( "No-op (test overhead)", thisSourceCodeLine(), GcdBenchmarks::noop, range, reps);
		
		range *= 2; // double the range so that only 1 out of 4 will use cache
		showBenchmarkSettings(null, range, reps);
		benchmark( "Swing gcd (recursive)", thisSourceCodeLine(), GcdBenchmarks::swingGcd, range, reps);
		benchmark( "Original vZome gcd (iterative)", thisSourceCodeLine(), GcdBenchmarks::vZomeOriginalGcd, range, reps);
		benchmark( "Apache Commons gcd", thisSourceCodeLine(), GcdBenchmarks::apacheGcd, range, reps);
		benchmark( "Google Commons gcd", thisSourceCodeLine(), GcdBenchmarks::googleGcd, range, reps);
		benchmark( "No-op (test overhead)", thisSourceCodeLine(), GcdBenchmarks::noop, range, reps);
		
		showBenchmarkSettings(BigRational.Gcd.LOOKUP_SIZE, range, reps);
		benchmark( "Cached Swing gcd (recursive)", thisSourceCodeLine(), GcdBenchmarks::swingGcdCached , range, reps);
		benchmark( "Cached Original vZome gcd (iterative)", thisSourceCodeLine(), GcdBenchmarks::vZomeOriginalGcdCached , range, reps);
		benchmark( "Cached Apache Commons gcd", thisSourceCodeLine(), GcdBenchmarks::apacheGcdCached , range, reps);
		benchmark( "Cached Google Commons gcd", thisSourceCodeLine(), GcdBenchmarks::googleGcdCached , range, reps);
		benchmark( "BigRational gcd", thisSourceCodeLine(), BigRational.Gcd::gcd, range, reps);
		benchmark( "No-op (test overhead)", thisSourceCodeLine(), GcdBenchmarks::noop, range, reps);
		
	}

	private static void showBenchmarkSettings(Integer cacheSize, int range, int reps) {
		System.out.println( "GCD benchmark settings:" );
		System.out.println( " reps  = " + reps );
		System.out.println( " range = 0 to " + range );
		System.out.println( " cache = " + (cacheSize == null ? "UNUSED" : cacheSize) );
	}
	
	private final static void benchmark(String name, String src, BinaryOperator<Long> op, int range, int reps) {
		System.out.printf("%1$40s %2$25s ", name, src);
		long qty = 0;
		boolean more = true;
		long start = System.nanoTime();
		while(more) {
			for(long j = 0; more && j < range; j++) {
				for(long k = 0; more && k < range; k++) {
					op.apply(j, k);
					qty++;
					if(qty == reps) {
						more = false;
					}
				}
			}
		}
		long duration = System.nanoTime() - start;
		double seconds = duration / 1000000000D;
		System.out.format("%12.6f seconds%n", seconds); // use format instead of printf to align decimal points in the output
	}
	
	private final static long noop(long a, long b) {
		return 0L;
	}
    
	private final static long vZomeOriginalGcdCached( long j, long k )
    {
		return cachedGcd(j,  k, GcdBenchmarks::vZomeOriginalGcd);
    }
	
	private final static long swingGcdCached( long j, long k )
    {
		return cachedGcd(j,  k, GcdBenchmarks::swingGcd);
    }
	
	private final static long apacheGcdCached( long j, long k )
    {
		return cachedGcd(j,  k, GcdBenchmarks::apacheGcd);
    }
	
	private final static long googleGcdCached( long j, long k )
    {
		return cachedGcd(j,  k, GcdBenchmarks::googleGcd);
    }
	
    private final static long vZomeOriginalGcd( long j, long k )
    {
        j = Math.abs( j );
        k = Math.abs( k );
        while ( k != 0 )
        {
            long r = j % k;
            j = k;
            k = r;
        }
        return j;
    }
    
    /*
     * Ensures that both arguments are positive before calling swingGcdRecursive to do the work 
     */
    public final static long swingGcd(long a, long b) {
    	return swingGcdRecursive(Math.abs(a), Math.abs(b));
    }
    
    /**
     * 
     * @param j
     * @param k
     * @return gcd of j & k using same recursive algorithm as javax.swing.table.DefaultTableModel.gcd()
     */
    final static long swingGcdRecursive( long j, long k )
    {
    	return k==0 ? j : swingGcdRecursive(k, j % k);
    }

    
    // With a few minor tweaks, the code below is copied from Apache Commons 
	// http://grepcode.com/file/repo1.maven.org/maven2/org.apache.commons/commons-math3/3.0/org/apache/commons/math3/util/ArithmeticUtils.java#ArithmeticUtils.gcd%28long%2Clong%29
    /**
     * <p>
     * Gets the greatest common divisor of the absolute value of two numbers,
     * using the "binary gcd" method which avoids division and modulo
     * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
     * Stein (1961).
     * </p>
     * Special cases:
     * <ul>
     * <li>The invocations
     * {@code gcd(Long.MIN_VALUE, Long.MIN_VALUE)},
     * {@code gcd(Long.MIN_VALUE, 0L)} and
     * {@code gcd(0L, Long.MIN_VALUE)} throw an
     * {@code ArithmeticException}, because the result would be 2^63, which
     * is too large for a long value.</li>
     * <li>The result of {@code gcd(x, x)}, {@code gcd(0L, x)} and
     * {@code gcd(x, 0L)} is the absolute value of {@code x}, except
     * for the special cases above.
     * <li>The invocation {@code gcd(0L, 0L)} is the only one which returns
     * {@code 0L}.</li>
     * </ul>
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor, never negative.
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative {@code long} value.
     * @since 2.1
     */
    public final static long apacheGcd(final long p, final long q) {
        long u = p;
        long v = q;
        if ((u == 0) || (v == 0)) {
            if ((u == Long.MIN_VALUE) || (v == Long.MIN_VALUE)){
                throw new ArithmeticException("GCD_OVERFLOW_64_BITS: " + p + ", " + q);
            }
            return Math.abs(u) + Math.abs(v);
        }
        // keep u and v negative, as negative integers range down to
        // -2^63, while positive numbers can only be as large as 2^63-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */
        if (u > 0) {
            u = -u;
        } // make u negative
        if (v > 0) {
            v = -v;
        } // make v negative
        // B1. [Find power of 2]
        int k = 0;
        while ((u & 1) == 0 && (v & 1) == 0 && k < 63) { // while u and v are
                                                            // both even...
            u /= 2;
            v /= 2;
            k++; // cast out twos.
        }
        if (k == 63) {
        	throw new ArithmeticException("GCD_OVERFLOW_64_Bits: " + p + ", " + q); // P and Q are both Long.MIN_VALUE
        }
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        long t = ((u & 1) == 1) ? v : -(u / 2)/* B3 */;
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while ((t & 1) == 0) { // while t is even..
                t /= 2; // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t;
            } else {
                v = t;
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2;
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0);
        return -u * (1L << k); // gcd is u*2^k
    }	    
    
    // With a few minor tweaks, the code below is copied from Google Commons 
    // copied from http://grepcode.com/file/repo1.maven.org/maven2/com.google.guava/guava/19.0-rc1/com/google/common/math/LongMath.java#LongMath.gcd%28long%2Clong%29
    /**
     * Returns the greatest common divisor of {@code a, b}. Returns {@code 0} if
     * {@code a == 0 && b == 0}.
     *
     * @throws IllegalArgumentException if {@code a < 0} or {@code b < 0}
     */
    public final static long googleGcd(long a, long b) {
    	return googleGcdAlgorithm(Math.abs(a), Math.abs(b));
    }
    
    public final static long googleGcdAlgorithm(long a, long b) {
//      checkNonNegative("a", a);
//      checkNonNegative("b", b);
      if (a == 0) {
        // 0 % b == 0, so b divides a, but the converse doesn't hold.
        // BigInteger.gcd is consistent with this decision.
        return b;
      } else if (b == 0) {
        return a; // similar logic
      }
      /*
       * Uses the binary GCD algorithm; see http://en.wikipedia.org/wiki/Binary_GCD_algorithm.
       * This is >60% faster than the Euclidean algorithm in benchmarks.
       */
      int aTwos = Long.numberOfTrailingZeros(a);
      a >>= aTwos; // divide out all 2s
      int bTwos = Long.numberOfTrailingZeros(b);
      b >>= bTwos; // divide out all 2s
      while (a != b) { // both a, b are odd
        // The key to the binary GCD algorithm is as follows:
        // Both a and b are odd.  Assume a > b; then gcd(a - b, b) = gcd(a, b).
        // But in gcd(a - b, b), a - b is even and b is odd, so we can divide out powers of two.

        // We bend over backwards to avoid branching, adapting a technique from
        // http://graphics.stanford.edu/~seander/bithacks.html#IntegerMinOrMax

        long delta = a - b; // can't overflow, since a and b are nonnegative

        long minDeltaOrZero = delta & (delta >> (Long.SIZE - 1));
        // equivalent to Math.min(delta, 0)

        a = delta - minDeltaOrZero - minDeltaOrZero; // sets a to Math.abs(a - b)
        // a is now nonnegative and even

        b += minDeltaOrZero; // sets b to min(old a, b)
        a >>= Long.numberOfTrailingZeros(a); // divide out all 2s, since 2 doesn't divide b
      }
      return a << Math.min(aTwos, bTwos);
    }
    
    static final byte[][] LOOKUP_TABLE = BigRational.Gcd.calculateLookupTable(LOOKUP_SIZE);
    /**
     * 
     * @param j
     * @param k
     * @return the cached gcd value with no range or error checking
     */
    public final static long cachedGcd(long j, long k)
    {
    	return (long) 0x00FF & LOOKUP_TABLE[(int)j] [(int)k];
    }

    public final static long cachedGcd(long j, long k, BinaryOperator<Long> gcd)
    {
    	// since this is intended to be a relative performance test of the algorithms,
    	// there's no overhead added by checking to ensure that neither j nor k are negative
    	return(((j|k) & BigRational.Gcd.LOOKUP_MASK) == 0L)
    			? cachedGcd(j, k)
    			: gcd.apply(j, k);
    }

//	// I have disabled this test for now, along with the code that it is supposed to test in BigRational.Gcd.gcd().
//	// See the comments there explaining the reasons.  
//	@Test
//	public void testGcdExtremes() {
//		// The invocations gcd(Long.MIN_VALUE, Long.MIN_VALUE), gcd(Long.MIN_VALUE, 0L) and gcd(0L, Long.MIN_VALUE) should throw an ArithmeticException, 
//		// because the result would be 2^63, which is too large for a long value.
//		
//        String failMsg = "Exception should have been thrown";
//        int tries = 0;
//        int catches = 0;
//        try {
//        	tries++;
//    		System.out.println(gcd(Long.MIN_VALUE, Long.MIN_VALUE));
//            fail(failMsg);
//        } catch (Exception ex) {
//            catches++;
//        }
//        try {
//        	tries++;
//    		System.out.println(gcd(Long.MIN_VALUE, 0L));
//            fail(failMsg);
//        } catch (Exception ex) {
//            catches++;
//        }
//        try {
//        	tries++;
//    		System.out.println(gcd(0L, Long.MIN_VALUE));
//            fail(failMsg);
//        } catch (Exception ex) {
//            catches++;
//        }
//        assertEquals(3, tries);
//        assertEquals(catches, tries);
//	}

}
