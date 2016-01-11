package com.vzome.core.generic;

import static java.lang.Integer.signum;
import static junit.framework.TestCase.*;

/**
 * @author David Hall
 */
public abstract class ComparableTest<T extends Comparable<T>>
{
	protected abstract T[] getTestValues();
	protected abstract T[] getDiffValues();
	protected abstract T[] get3OrderedValues();
	
	public void testComparableToEquivalent() { 
		for(T e : getTestValues()) {
			verifyComparableToEquivalent(e, e); 
		}
	}
	protected void verifyComparableToEquivalent(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
		int expected = 0;
		int result = o1.compareTo(o2);
		assertEquals(expected, signum(result));
	}

	public void testComparableToDifferent() 
	{ 
		for(T j : getTestValues()) {
			for(T k : getDiffValues()) {
				verifyComparableToDifferent(j, k); 
			}
		}
	}
	protected void verifyComparableToDifferent(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
		int result = o1.compareTo(o2);
		checkForMinMaxInt(result);
		assertTrue(0 != signum(result));
	}

	/**
	 * From Java Comparable<T> documentation:
	 * It is strongly recommended, but not strictly required that (x.compareTo(y)==0) == (x.equals(y)). 
	 * Generally speaking, any class that implements the Comparable interface and violates this condition should clearly indicate this fact. 
	 * The recommended language is "Note: this class has a natural ordering that is inconsistent with equals."
	 */
	public void testComparableToVsEquals() { 
		for(T j : getTestValues()) {
			for(T k : getTestValues()) {
				verifyComparableToVsEquals(j, k); 
			}
		}
	}
	protected void verifyComparableToVsEquals(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
		assertTrue( (o1.compareTo(o2)==0) == (o1.equals(o2)) );
		assertTrue( (o2.compareTo(o1)==0) == (o2.equals(o1)) );
	}
	
	/**
	 * From Java Comparable<T> documentation:
	 * The implementor must also ensure that the relation is transitive: 
	 * (x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0.
	 */
	public void testTransitiveComparison() { 
		T[] values = get3OrderedValues();
		assertNotNull(values);
		assertTrue(values.length == 3);
		T x = values[0];
		T y = values[1];
		T z = values[2];
		verifyTransitiveComparison(x, y, z);
		verifyTransitiveComparison(z, y, x);
	}
	protected void verifyTransitiveComparison(T x, T y, T z) {
		assertNotNull(x);
		assertNotNull(y);
		assertNotNull(z);
		int x_y = x.compareTo(y);
		int y_z = y.compareTo(z);
		int x_z = x.compareTo(z);
		checkForMinMaxInt(x_y);
		checkForMinMaxInt(y_z);
		checkForMinMaxInt(x_z);
		
		if((x_y > 0) && (y_z > 0)) {
			assertTrue(x_z > 0);
		}
		else if((x_y < 0) && (y_z < 0)) {
			assertTrue(x_z < 0);
		}
		else {
			String msg = "Unable to verify Transitive property for the given objects: " 
					+ x.toString() + ", "
					+ y.toString() + ", "
					+ z.toString() + ". "
					+ "Y must be 'between' X and Z.";
			assertTrue(msg, false);
		}
	}

	public void testComparableToNull() {
		for(T e : getTestValues()) {
			verifyComparableToNull(e);
		}
	}
	protected void verifyComparableToNull(T obj) {
		assertNotNull(obj);
		try {
			obj.compareTo(null);
			String msg = "Expected NullPointerException was NOT thrown.";
			assertTrue(msg, false);
		}
		catch(NullPointerException ex) {
			// this is what the spec requires, so ignore the exception.
			assertNotNull(ex);
		}
	}
	
	public void testComparableToSelf() { 
		for(T e : getTestValues()) {
			verifyComparableToSelf(e);
		}
	}
	protected void verifyComparableToSelf(T obj) {
		assertNotNull(obj);
		int expected = 0;
		int result = obj.compareTo(obj);
		checkForMinMaxInt(result);
		assertEquals(expected, signum(result));
	}

	/**
	 * While technically valid, comparisons that return Integer.MIN_VALUE or Integer.MAX_VALUE 
	 * can lead to subtle bugs. (e.g. Integer.MIN_VALUE * -1 == Integer.MIN_VALUE)
	 * This test simply warns if that is encountered.
	 */
	private void checkForMinMaxInt(int result) {
		assertTrue(Integer.MIN_VALUE * -1 == Integer.MIN_VALUE);
		
		String name = "";
		switch(result) {
			case Integer.MIN_VALUE:
				name = "Integer.MIN_VALUE";
				break;
			case Integer.MAX_VALUE:
				name = "Integer.MAX_VALUE";
				break;
			default:
				return;
		}
		String msg = "Warning: " + name + " was returned from compareTo(" + getClass().getSimpleName() + ").";
		System.out.println(msg);
	}
	
}
