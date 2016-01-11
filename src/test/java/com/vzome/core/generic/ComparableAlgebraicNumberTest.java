package com.vzome.core.generic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import java.util.ArrayList;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * @author David Hall
 * Tests the AlgebraicNumber implementation of Comparable interface.
 */
public class ComparableAlgebraicNumberTest extends ComparableTest<AlgebraicNumber> {
	private final AlgebraicNumber[] testValues; 
	private final AlgebraicNumber[] diffValues; 
	private final AlgebraicNumber[] ordered3Values;

	public ComparableAlgebraicNumberTest() {
		AlgebraicField field = new PentagonField();
		ArrayList<AlgebraicNumber> list = new ArrayList<>();
        list.add(field.createRational( new int[]{ 0,1 } ));
        list.add(field.createRational( new int[]{ 1,1 } ));
        list.add(field.createRational( new int[]{ 2,1 } ));
        list.add(field.createRational( new int[]{ 3,1 } ));
        list.add(field.createRational( new int[]{ 5,2 } ));
        list.add(field.createRational( new int[]{ 5,2 } ));
		testValues = list.toArray(new AlgebraicNumber[]{});

		list.clear();
		list.add(field.createRational( new int[]{ -1,1 } ));
        list.add(field.createRational( new int[]{ -2,1 } ));
        list.add(field.createRational( new int[]{ -3,1 } ));
        list.add(field.createRational( new int[]{ -4,1 } ));
        diffValues = list.toArray(new AlgebraicNumber[]{});
		
		list.clear();
        list.add(field.createRational( new int[]{ 1,2 } ));
        list.add(field.createRational( new int[]{ 3,2 } ));
        list.add(field.createRational( new int[]{ 5,2 } ));
        ordered3Values = list.toArray(new AlgebraicNumber[]{});
	}

	@Override
	protected AlgebraicNumber[] getTestValues() {
		return testValues;
	}

	@Override
	protected AlgebraicNumber[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected AlgebraicNumber[] get3OrderedValues() {
		return ordered3Values;
	}

	@Test
	public void testMismatchedFieldThrowsException() { 
		AlgebraicNumber o1 = new RootTwoField().createRational( new int[]{ 0,1 } );
		AlgebraicNumber o2 = new RootThreeField().createRational( new int[]{ 0,1 } );
		assertNotNull(o1);
		assertNotNull(o2);
		try {
			o1.compareTo(o2);
			String msg = "Expected IllegalStateException was NOT thrown.";
			assertTrue(msg, false);
		}
		catch(IllegalStateException ex) {
			// this is what the spec requires, so ignore the exception.
			assertNotNull(ex);
		}
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
