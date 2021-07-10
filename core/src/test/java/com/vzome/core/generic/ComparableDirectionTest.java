package com.vzome.core.generic;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author David Hall
 * Tests the Direction implementation of Comparable interface.
 */
public class ComparableDirectionTest extends ComparableTest<Direction> {
	private final Direction[] testValues; 
	private final Direction[] diffValues; 
	private final Direction[] ordered3Values;

	public ComparableDirectionTest() {
        Symmetry symmetry = new IcosahedralSymmetry( new PentagonField() );
		ArrayList<Direction> list = new ArrayList<>();
        Direction[] o3V = null;
        for(String dirName : symmetry.getDirectionNames()) {
            list.add(symmetry.getDirection(dirName));
            if(list.size() == 3) {
                o3V = list.toArray(new Direction[list.size()]);
            }
        }
		testValues = list.toArray(new Direction[list.size()]);

		list.clear();
        symmetry = new OctahedralSymmetry( new HeptagonField() );
        for(String dirName : symmetry.getDirectionNames()) {
            list.add(symmetry.getDirection(dirName));
        }
        diffValues = list.toArray(new Direction[list.size()]);
		
		ordered3Values = o3V;
        assertNotNull(ordered3Values);
        // shifting the order should make this test fail.
//        Direction tempDir = ordered3Values[0];
//        ordered3Values[0] = ordered3Values[1];
//        ordered3Values[1] = ordered3Values[2];
//        ordered3Values[2] = tempDir;
	}

	@Override
	protected Direction[] getTestValues() {
		return testValues;
	}

	@Override
	protected Direction[] getDiffValues() {
		return diffValues;
	}

	@Override
	protected Direction[] get3OrderedValues() {
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
