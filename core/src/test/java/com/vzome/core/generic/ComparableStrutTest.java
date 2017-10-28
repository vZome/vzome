package com.vzome.core.generic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.model.Strut;
import java.util.ArrayList;
import org.junit.Test;

/**
 * @author David Hall Tests the Strut implementation of Comparable interface.
 */
public class ComparableStrutTest extends ComparableTest<Strut> {

	private final Strut[] testValues;
	private final Strut[] diffValues;
	private final Strut[] ordered3Values;

	public ComparableStrutTest() {
        AlgebraicField field = new PentagonField();
		ArrayList<Strut> list = new ArrayList<>();
        AlgebraicNumber s = field .createRational( 7 );
        AlgebraicNumber t = field .createRational( 6 );
        AlgebraicNumber u = field .createRational( 5 );
        AlgebraicNumber v = field .createRational( 4 );
        AlgebraicNumber w = field .createRational( 3 );
        AlgebraicNumber x = field .createRational( 2 );
        AlgebraicNumber ONE  = field .createRational( 1 );
        AlgebraicNumber ZERO = field .createRational( 0 );
        
        AlgebraicVector ORIGIN = new AlgebraicVector(ZERO, ZERO, ZERO);
        AlgebraicVector PLUS_X = new AlgebraicVector(ONE,  ZERO, ZERO);
        AlgebraicVector PLUS_Y = new AlgebraicVector(ZERO, ONE,  ZERO);
        AlgebraicVector PLUS_Z = new AlgebraicVector(ZERO, ZERO, ONE );
       
        AlgebraicVector a = new AlgebraicVector(w, x, ONE);
        AlgebraicVector b = new AlgebraicVector(ZERO, w, x);
        AlgebraicVector c = new AlgebraicVector(ONE, ZERO, w);
        AlgebraicVector d = new AlgebraicVector(x, ONE, ZERO);
		
        AlgebraicVector e = new AlgebraicVector(s, t, u);
        AlgebraicVector f = new AlgebraicVector(v, s, t);
        AlgebraicVector g = new AlgebraicVector(u, v, s);
        AlgebraicVector h = new AlgebraicVector(t, u, v);
        AlgebraicVector i = new AlgebraicVector(t, u, u);
		
        Strut sa = new Strut( ORIGIN, a );
        Strut sb = new Strut( ORIGIN, b );
        Strut sc = new Strut( ORIGIN, c );
        Strut sd = new Strut( ORIGIN, d );
		
        Strut se = new Strut( e, ORIGIN );
        Strut sf = new Strut( f, ORIGIN );
        Strut sg = new Strut( g, ORIGIN );
        Strut sh = new Strut( h, ORIGIN );
        Strut si = new Strut( i, ORIGIN );
        
        Strut X_AXIS = new Strut( ORIGIN, PLUS_X );
        Strut Y_AXIS = new Strut( ORIGIN, PLUS_Y );
        Strut Z_AXIS = new Strut( ORIGIN, PLUS_Z );
        
        list .add( sa );
        list .add( sb );
        list .add( sc );
        list .add( sd );
		testValues = list.toArray(new Strut[list.size()]);

		list.clear();
        list .add( se );
        list .add( sf );
        list .add( sg );
        list .add( sh );
        list .add( si );
		diffValues = list.toArray(new Strut[list.size()]);

		list.clear();
        // we have defined that canonically sorted axes are sorted by X then Y then Z
        list .add( X_AXIS );
        list .add( Y_AXIS );
        list .add( Z_AXIS );
		ordered3Values = list.toArray(new Strut[list.size()]);
	}

	@Override
	protected Strut[] getTestValues() {
		return testValues;
	}

	@Override
	protected Strut[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected Strut[] get3OrderedValues() {
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
