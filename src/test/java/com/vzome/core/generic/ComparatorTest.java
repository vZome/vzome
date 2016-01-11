package com.vzome.core.generic;

import java.util.Comparator;
import static junit.framework.TestCase.*;

/**
 * @author David Hall
 */
public abstract class ComparatorTest<T extends Comparator<T>>
{

	public void verifyCompareAnther(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
	}

	public void verifyCompareVsEquals(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
	}

	public void verifyTransitiveComparison(T o1, T o2) {
		assertNotNull(o1);
		assertNotNull(o2);
	}

	public void verifyCompareNull(T obj) {
		assertNotNull(obj);
	}
	
	public void verifyCompareSelf(T obj) {
		assertNotNull(obj);
	}

}
