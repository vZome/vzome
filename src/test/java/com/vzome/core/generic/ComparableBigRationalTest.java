package com.vzome.core.generic;

import com.vzome.core.algebra.BigRational;
import java.util.ArrayList;
import org.junit.Test;

/**
 * @author David Hall
 * Tests the BigRational implementation of Comparable interface.
 */
public class ComparableBigRationalTest extends ComparableTest<BigRational> {
	private final BigRational[] testValues;
	private final BigRational[] diffValues; // = new Integer[] {-63, 12, 37};
	private final BigRational[] ordered3Values; // = new Integer[] {-123, 456, 789};

	public ComparableBigRationalTest() {
		ArrayList<BigRational> temp = new ArrayList<>();
        temp.add(new BigRational( Integer.MIN_VALUE ));
        temp.add(new BigRational( Integer.MAX_VALUE ));
        temp.add(new BigRational( -31 ));
        temp.add(new BigRational( 2 ));
        temp.add(new BigRational( 0 ));
        temp.add(new BigRational( 1 ));
        temp.add(new BigRational( -1 ));
		testValues = temp.toArray(new BigRational[]{});

		temp.clear();
        temp.add(new BigRational( -63, 2 ));
        temp.add(new BigRational( 12, 7));
        temp.add(new BigRational( 37, 4));
        temp.add(new BigRational( 1234, 11));
        temp.add(new BigRational( -999 , 5));
		diffValues = temp.toArray(new BigRational[]{});

		temp.clear();
		temp.add(new BigRational( -123 ));
		temp.add(new BigRational( 456 ));
		temp.add(new BigRational( 789 ));
		ordered3Values = temp.toArray(new BigRational[]{});
	}

	@Override
	protected BigRational[] getTestValues() {
		return testValues;
	}

	@Override
	protected BigRational[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected BigRational[] get3OrderedValues() {
		return ordered3Values;
	}

	@Test
	@Override
	public void testComparableToEquivalent() { 
		super.testComparableToEquivalent();
	}

	@Test
	@Override
	public void testComparableToSelf() {
		super.testComparableToSelf();
	}

	@Test
	@Override
	public void testComparableToNull() {
		super.testComparableToNull();
	}

	@Test
	@Override
	public void testTransitiveComparison() {
		super.testTransitiveComparison();
	}

	@Test
	@Override
	public void testComparableToVsEquals() {
		super.testComparableToVsEquals();
	}

	@Test
	@Override
	public void testComparableToDifferent() {
		super.testComparableToDifferent();
	}
}
