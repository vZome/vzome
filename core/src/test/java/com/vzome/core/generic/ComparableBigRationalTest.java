package com.vzome.core.generic;

import java.util.ArrayList;

import org.junit.Test;

import com.vzome.core.algebra.BigRationalImpl;

/**
 * @author David Hall
 * Tests the BigRational implementation of Comparable interface.
 */
public class ComparableBigRationalTest extends ComparableTest<BigRationalImpl> {
	private final BigRationalImpl[] testValues;
	private final BigRationalImpl[] diffValues;
	private final BigRationalImpl[] ordered3Values;

	public ComparableBigRationalTest() {
		ArrayList<BigRationalImpl> list = new ArrayList<>();
        list.add(new BigRationalImpl( Integer.MIN_VALUE ));
        list.add(new BigRationalImpl( Integer.MAX_VALUE ));
        list.add(new BigRationalImpl( -31 ));
        list.add(new BigRationalImpl( 2 ));
        list.add(new BigRationalImpl( 0 ));
        list.add(new BigRationalImpl( 1 ));
        list.add(new BigRationalImpl( -1 ));
		testValues = list.toArray(new BigRationalImpl[list.size()]);

		list.clear();
        list.add(new BigRationalImpl( -63, 2 ));
        list.add(new BigRationalImpl( 12, 7));
        list.add(new BigRationalImpl( 37, 4));
        list.add(new BigRationalImpl( 1234, 11));
        list.add(new BigRationalImpl( -999 , 5));
		diffValues = list.toArray(new BigRationalImpl[list.size()]);

		list.clear();
		list.add(new BigRationalImpl( -123 ));
		list.add(new BigRationalImpl( 456 ));
		list.add(new BigRationalImpl( 789 ));
		ordered3Values = list.toArray(new BigRationalImpl[list.size()]);
	}

	@Override
	protected BigRationalImpl[] getTestValues() {
		return testValues;
	}

	@Override
	protected BigRationalImpl[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected BigRationalImpl[] get3OrderedValues() {
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
