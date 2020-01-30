package com.vzome.core.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.algebra.SnubDodecField;

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
        list.add(field.createRational( 1 ) );
        list.add(field.createRational( 2 ) );
        list.add(field.createRational( 3 ) );
        list.add(field.createRational( 4,2 ) );
        list.add(field.createRational( 5,2 ) );
		testValues = list.toArray(new AlgebraicNumber[list.size()]);

		list.clear();
        list.add(field.createRational( -1 ) );
        list.add(field.createRational( -2 ) );
        list.add(field.createRational( -3 ) );
        list.add(field.createRational( -4 ) );
        diffValues = list.toArray(new AlgebraicNumber[list.size()]);
		
		list.clear();
        list.add(field.createRational( -11 ) );
        list.add(field.createRational( 3 ) );
        list.add(field.createRational( 57 ) );
        ordered3Values = list.toArray(new AlgebraicNumber[list.size()]);
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
		AlgebraicNumber o1 = new RootTwoField().createRational( 1 );
		AlgebraicNumber o2 = new RootThreeField().createRational( 1 );
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

    @Test
    public void testComparableMatchesEvaluate() {
        AlgebraicField[] fields = {
            new PentagonField(),
            new RootTwoField(),
            new RootThreeField(),
            new HeptagonField(),
            new SnubDodecField(),
        };

        int passed = 0;
        for(AlgebraicField field: fields ) {
            // The original implementation of AlgebraicNumber.compareTo() was not consistent with AlgebraicNumber.evaluate().compareTo()
            // AlgebraicNumber.compareTo() should order values sequentially corresponding to their position on a number line.
            // This test specifically targets a particular condition where the original implementation sorted them incorrectly.
            // The obscure condition occured when:
            //  1) The unit factor of n1 is greater than the unit factor of n2
            //  2) The evaluated value of n1 is less than the evaluated value of n2
            // This test case verifies the corrected implementation.
            //
            // I was surprised that none of the other unit tests or regression tests failed
            // when I corrected the implementation of AlgebraicVector.compareTo().
            // Specifically, all of them passed that require sorting vectors,
            // such as ExporterTest which has a lot of hard coded values as expected results.

            int units1 = 4;
            int units2 = 1;
            int irrat1 = -4;
            int irrat2 = 0;

            assertTrue(units1 > units2);
            assertTrue(units1 > 1);
            assertTrue(irrat1 < irrat2);
            assertTrue(irrat1 < 0);
            assertTrue(irrat2 >= 0);

            AlgebraicNumber n1 = field.createAlgebraicNumber(units1, irrat1, 1, 0); // e.g. (4-4*phi) is negative
            AlgebraicNumber n2 = field.createAlgebraicNumber(units2, irrat2, 1, 0); // e.g.  1        is positive

            assertNotEquals(n1, n2);

            Double d1 = n1.evaluate();
            Double d2 = n2.evaluate();

            assertTrue(d1 < 0d);
            assertTrue(d2 > 0d);

            int nResult = n1.compareTo(n2);
            int dResult = d1.compareTo(d2);

            assertEquals(nResult, dResult);
            assertTrue(dResult < 0);
            passed ++;
        }
        // be sure we actually tested all of the fields
        assertEquals(fields.length, passed);
        assertTrue(passed > 0);
    }
}
