package com.vzome.core.generic;

import org.junit.Test;

/**
 * @author David Hall
 * This class is used to verify the behavior of the generic ComparableTest class 
 * using the String class since it has known predictable behaviors.
 */
public class ComparableStringTest extends ComparableTest<String> {
	private final String[] testValues = new String[] {"", "-17", "  ", "Hello", "foo", "zebra", ".", " with whitespace, \n newline, and\t tab."};
	private final String[] diffValues = new String[] {"...", "42", " ", "world", "bar", "penguin", "?", "\r"};
	private final String[] ordered3Values = new String[] {"Ax", "By", "Cz"};
	
	@Override
	protected String[] getTestValues() {
		return testValues;
	}

	@Override
	protected String[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected String[] get3OrderedValues() {
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
