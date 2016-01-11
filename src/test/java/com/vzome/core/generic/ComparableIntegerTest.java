package com.vzome.core.generic;

import org.junit.Test;

/**
 * @author David Hall
 * This class is used to verify the behavior of the generic ComparableTest class 
 * using the Integer class since it has known predictable behaviors.
 */
public class ComparableIntegerTest extends ComparableTest<Integer> {
	private final Integer[] testValues = new Integer[] {Integer.MIN_VALUE, -17, -1, 0, 1, 2, 42, Integer.MAX_VALUE};
	private final Integer[] diffValues = new Integer[] {-63, 12, 37};
	private final Integer[] ordered3Values = new Integer[] {-123, 456, 789};

	@Override
	protected Integer[] getTestValues() {
		return testValues;
	}

	@Override
	protected Integer[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected Integer[] get3OrderedValues() {
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
