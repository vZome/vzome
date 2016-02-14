package com.vzome.core.generic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import java.util.ArrayList;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * @author David Hall Tests the AlgebraicVector implementation of Comparable interface.
 */
public class ComparableAlgebraicVectorTest extends ComparableTest<AlgebraicVector> {

	private final AlgebraicVector[] testValues;
	private final AlgebraicVector[] diffValues;
	private final AlgebraicVector[] ordered3Values;

	public ComparableAlgebraicVectorTest() {
        AlgebraicField field = new PentagonField();
		ArrayList<AlgebraicVector> list = new ArrayList<>();
        AlgebraicNumber s = field .createRational( 7 );
        AlgebraicNumber t = field .createRational( 6 );
        AlgebraicNumber u = field .createRational( 5 );
        AlgebraicNumber v = field .createRational( 4 );
        AlgebraicNumber w = field .createRational( 3 );
        AlgebraicNumber x = field .createRational( 2 );
        AlgebraicNumber y = field .createRational( 1 );
        AlgebraicNumber z = field .createRational( 0 );
		
        AlgebraicVector a = new AlgebraicVector(w, x, y);
        AlgebraicVector b = new AlgebraicVector(z, w, x);
        AlgebraicVector c = new AlgebraicVector(y, z, w);
        AlgebraicVector d = new AlgebraicVector(x, y, z);
		
        AlgebraicVector e = new AlgebraicVector(s, t, u);
        AlgebraicVector f = new AlgebraicVector(v, s, t);
        AlgebraicVector g = new AlgebraicVector(u, v, s);
        AlgebraicVector h = new AlgebraicVector(t, u, v);
        AlgebraicVector i = new AlgebraicVector(t, u, u);
        list .add( a );
        list .add( b );
        list .add( c );
        list .add( d );
		testValues = list.toArray(new AlgebraicVector[list.size()]);

		list.clear();
        list .add( e );
        list .add( f );
        list .add( g );
        list .add( h );
		diffValues = list.toArray(new AlgebraicVector[list.size()]);

		list.clear();
        list .add( b );
        list .add( h );
        list .add( i );
		ordered3Values = list.toArray(new AlgebraicVector[list.size()]);
	}

	@Override
	protected AlgebraicVector[] getTestValues() {
		return testValues;
	}

	@Override
	protected AlgebraicVector[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected AlgebraicVector[] get3OrderedValues() {
		return ordered3Values;
	}

	@Test
	public void testMismatchedFieldThrowsException() {
		AlgebraicField field = new RootTwoField();
        AlgebraicNumber u = field .createRational( 2 );
        AlgebraicNumber v = field .createRational( 1 );
        AlgebraicNumber w = field .createRational( 0 );
        AlgebraicVector o1 = new AlgebraicVector(u, v, w);
		
		field = new RootThreeField();
        AlgebraicNumber x = field .createRational( 2 );
        AlgebraicNumber y = field .createRational( 1 );
        AlgebraicNumber z = field .createRational( 0 );
        AlgebraicVector o2 = new AlgebraicVector(x, y, z);

		try {
			o1.compareTo(o2);
			String msg = "Expected IllegalStateException was NOT thrown.";
			assertTrue(msg, false);
		} catch (IllegalStateException ex) {
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
