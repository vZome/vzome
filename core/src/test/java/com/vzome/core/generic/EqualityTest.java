package com.vzome.core.generic;

import static org.junit.Assert.assertNotNull;

/**
 * @author David Hall
 */
public abstract class EqualityTest<T extends Object>
{
	public void verifyEquals(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
		// TODO:
	}
	
	public void verifyHashCode(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
		// TODO:
	}
}
