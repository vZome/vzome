package com.vzome.core.generic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.model.Connector;
import java.util.ArrayList;
import org.junit.Test;

/**
 * @author David Hall Tests the Connector implementation of Comparable interface.
 */
public class ComparableConnectorTest extends ComparableTest<Connector> {

	private final Connector[] testValues;
	private final Connector[] diffValues;
	private final Connector[] ordered3Values;

	public ComparableConnectorTest() {
        AlgebraicField field = new PentagonField();
		ArrayList<Connector> list = new ArrayList<>();
        AlgebraicNumber s = field .createRational( 7 );
        AlgebraicNumber t = field .createRational( 6 );
        AlgebraicNumber u = field .createRational( 5 );
        AlgebraicNumber v = field .createRational( 4 );
        AlgebraicNumber w = field .createRational( 3 );
        AlgebraicNumber x = field .createRational( 2 );
        AlgebraicNumber y = field .createRational( 1 );
        AlgebraicNumber z = field .createRational( 0 );
		
        Connector a = new Connector( new AlgebraicVector(w, x, y) );
        Connector b = new Connector( new AlgebraicVector(z, w, x) );
        Connector c = new Connector( new AlgebraicVector(y, z, w) );
        Connector d = new Connector( new AlgebraicVector(x, y, z) );
		
        Connector e = new Connector( new AlgebraicVector(s, t, u) );
        Connector f = new Connector( new AlgebraicVector(v, s, t) );
        Connector g = new Connector( new AlgebraicVector(u, v, s) );
        Connector h = new Connector( new AlgebraicVector(t, u, v) );
        Connector i = new Connector( new AlgebraicVector(t, u, u) );
        list .add( a );
        list .add( b );
        list .add( c );
        list .add( d );
		testValues = list.toArray(new Connector[list.size()]);

		list.clear();
        list .add( e );
        list .add( f );
        list .add( g );
        list .add( h );
		diffValues = list.toArray(new Connector[list.size()]);

		list.clear();
        list .add( b );
        list .add( h );
        list .add( i );
		ordered3Values = list.toArray(new Connector[list.size()]);
	}

	@Override
	protected Connector[] getTestValues() {
		return testValues;
	}

	@Override
	protected Connector[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected Connector[] get3OrderedValues() {
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
