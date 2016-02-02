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
	private final BigRational[] diffValues;
	private final BigRational[] ordered3Values;

	public ComparableBigRationalTest() {
		ArrayList<BigRational> list = new ArrayList<>();
        list.add(new BigRational( Integer.MIN_VALUE ));
        list.add(new BigRational( Integer.MAX_VALUE ));
        list.add(new BigRational( -31 ));
        list.add(new BigRational( 2 ));
        list.add(new BigRational( 0 ));
        list.add(new BigRational( 1 ));
        list.add(new BigRational( -1 ));
		testValues = list.toArray(new BigRational[list.size()]);

		list.clear();
        list.add(new BigRational( -63, 2 ));
        list.add(new BigRational( 12, 7));
        list.add(new BigRational( 37, 4));
        list.add(new BigRational( 1234, 11));
        list.add(new BigRational( -999 , 5));
		diffValues = list.toArray(new BigRational[list.size()]);

		list.clear();
		list.add(new BigRational( -123 ));
		list.add(new BigRational( 456 ));
		list.add(new BigRational( 789 ));
		ordered3Values = list.toArray(new BigRational[list.size()]);
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
