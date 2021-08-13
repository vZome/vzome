package com.vzome.core.math.symmetry;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicNumberImpl;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.algebra.PolygonFieldTest;
import com.vzome.core.generic.Utilities;
import com.vzome.core.math.RealVector;
import com.vzome.fields.heptagon.HeptagonalAntiprismSymmetry;

/**
 * @author David Hall
 */
public class AntiprismSymmetryTest {
    public static final int MAX_SIDES = PolygonFieldTest.MAX_SIDES;
    
    private static final AntiprismSymmetry getAntiprismSymmetry(int nSides) {
        return new AntiprismSymmetry(new PolygonField(nSides, AlgebraicNumberImpl.FACTORY )).createStandardOrbits("blue");
    }

    @Test
    public void testPermutations() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        AntiprismSymmetry symm = getAntiprismSymmetry(7);

        Permutation perm7 = symm.getPermutation(3);
        assertEquals(7, perm7.getOrder());
        assertEquals(0, perm7.mapIndex(4));
        assertEquals(8, perm7.mapIndex(12));
        assertEquals(3, symm.getMapping(11, 7));

        Permutation perm2 = symm.getPermutation(9);
        assertEquals(2, perm2.getOrder());
        assertEquals(4, perm2.mapIndex(12));
        assertEquals(10, perm2.mapIndex(6));
        assertEquals(9, symm.getMapping(3, 13));

        Permutation prod = perm2.compose(perm7);
        assertEquals(2, prod.getOrder());
        assertEquals(10, prod.mapIndex(3));
    }
    
    @Test
    public void testComputeOrbitDots() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(int nSides = PolygonField.MIN_SIDES; nSides <= MAX_SIDES; nSides++) {
            AntiprismSymmetry symm = getAntiprismSymmetry(nSides);
            String msg = symm.getName() + nSides;
            symm.computeOrbitDots(); // this is necessary to apply the blue dot hack
            
            Direction dir = symm.getSpecialOrbit(SpecialOrbit.BLUE);
            assertEquals(msg, "blue", dir.getName());
            assertEquals(msg, 0d, dir.getDotX());
            assertEquals(msg, 1d, dir.getDotY());
            
            dir = symm.getSpecialOrbit(SpecialOrbit.RED);
            assertEquals(msg, "red", dir.getName());
            assertEquals(msg, 1d, dir.getDotX());
            assertEquals(msg, 0d, dir.getDotY());

            dir = symm.getSpecialOrbit(SpecialOrbit.YELLOW);
            assertEquals(msg, symm.getField().isEven() ? "green" : "blue", dir.getName());
        }
    }

    @Test
    public void testOrientations() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        AntiprismSymmetry symm = getAntiprismSymmetry(7);

        AlgebraicMatrix m2 = symm.getMatrix(2);
        AlgebraicMatrix m4 = symm.getMatrix(4);
        AlgebraicMatrix m6 = symm.getMatrix(6);

        printMatrix(m2);
        printMatrix(m4);
        printMatrix(m6);

        assertEquals(m2, m6.times(m6.times(m4)));
    }

    private void printMatrix(AlgebraicMatrix m ) {
        System.out.print(m);
        System.out.print("\t= ");
        AlgebraicNumber[][] a2 = m.getMatrix();
        System.out.print("[ ");
        for(AlgebraicNumber[] a1 : a2) {
            System.out.print("[");
            for(AlgebraicNumber a : a1) {
                System.out.print(a.evaluate() + ", ");
            }
            System.out.print("],");
        }
        System.out.print("]");
        System.out.println();
    }

    @Test
    public void testGetAxisUncorrected() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        AntiprismSymmetry symm = getAntiprismSymmetry(7);

        RealVector v1 = new RealVector(0.1, 0.1, 3.0);
        RealVector v2 = new RealVector(0.1, 0.1, -3.0);
        RealVector v3 = new RealVector(0.1, -0.1, 3.0);
        RealVector v4 = new RealVector(0.1, -0.1, -3.0);
        RealVector v5 = new RealVector(-0.1, 0.1, 3.0);
        RealVector v6 = new RealVector(-0.1, 0.1, -3.0);
        RealVector v7 = new RealVector(-0.1, -0.1, 3.0);
        RealVector v8 = new RealVector(-0.1, -0.1, -3.0);

        Direction redOrbit = symm.getDirection("red");

        Axis axis = redOrbit.getAxis(v1);
        Axis expected = redOrbit.getAxis(Axis.PLUS, 1); // these numbers are pretty arbitrary, for red...
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v2);
        expected = redOrbit.getAxis(Axis.PLUS, 7); // since there are really only "up" and "down"
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v3);
        expected = redOrbit.getAxis(Axis.MINUS, 11);
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v4);
        expected = redOrbit.getAxis(Axis.PLUS, 13);
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v5);
        expected = redOrbit.getAxis(Axis.PLUS, 3);
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v6);
        expected = redOrbit.getAxis(Axis.PLUS, 8);
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v7);
        expected = redOrbit.getAxis(Axis.MINUS, 8);
        assertEquals(expected, axis);

        axis = redOrbit.getAxis(v8);
        expected = redOrbit.getAxis(Axis.PLUS, 9);
        assertEquals(expected, axis);
    }

    @Test
    public void testGetAxisCorrected() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        AntiprismSymmetry symm = getAntiprismSymmetry(7);

        RealVector v1 = new RealVector(0.5, 0.1, 0.1);
        RealVector v2 = new RealVector(0.5, 0.1, -0.1);
        RealVector v3 = new RealVector(0.5, -0.1, 0.1);
        RealVector v4 = new RealVector(0.5, -0.1, -0.1);
        RealVector v5 = new RealVector(-0.5, 0.1, 0.1);
        RealVector v6 = new RealVector(-0.5, 0.1, -0.1);
        RealVector v7 = new RealVector(-0.5, -0.1, 0.1);
        RealVector v8 = new RealVector(-0.5, -0.1, -0.1);

        Direction blueOrbit = symm.getDirection("blue");

        Axis axis = blueOrbit.getAxis(v1);
        Axis expected = blueOrbit.getAxis(Axis.PLUS, 0, true);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v2);
        expected = blueOrbit.getAxis(Axis.MINUS, 0, true);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v3);
        expected = blueOrbit.getAxis(Axis.MINUS, 7, true);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v4);
        expected = blueOrbit.getAxis(Axis.PLUS, 7, true);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v5);
        expected = blueOrbit.getAxis(Axis.PLUS, 7, false);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v6);
        expected = blueOrbit.getAxis(Axis.MINUS, 7, false);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v7);
        expected = blueOrbit.getAxis(Axis.MINUS, 0, false);
        assertEquals(expected, axis);

        axis = blueOrbit.getAxis(v8);
        expected = blueOrbit.getAxis(Axis.PLUS, 0, false);
        assertEquals(expected, axis);
    }

    @Test
    public void testGetRotationMatrix() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(int parity = 0; parity <=1; parity++ ) {
//            System.out.println("\n" + (parity == 0 ? "Even" : "Odd") + "...");
            for (int nSides = PolygonField.MIN_SIDES + parity; nSides <= MAX_SIDES; nSides+=2) {
//                System.out.println();
                AntiprismSymmetry symm = getAntiprismSymmetry(nSides);
                PolygonField field = symm.getField();
                AlgebraicMatrix rotationMatrix = symm.getRotationMatrix();
//                System.out.println("nSides = " + nSides + " ");
//                System.out.println("rotationMatrix = " + rotationMatrix);
                final AlgebraicVector posX = field.basisVector(3, AlgebraicVector.X);
                final AlgebraicVector posY = field.basisVector(3, AlgebraicVector.Y);
                final AlgebraicVector negX = field.basisVector(3, AlgebraicVector.X).negate();
                final AlgebraicVector negY = field.basisVector(3, AlgebraicVector.Y).negate();
                AlgebraicVector v = posX;
                int isX = 0;
                int isY = 0;
                int i = 0;
                for (i = 0; i < nSides; i++) {
//                    System.out.print(i + "\t: " + v);
                    v = rotationMatrix.timesColumn(v);
//                    System.out.println("\t\t rotates to: " + v);
                    if (i % nSides == nSides - 1) {
                        assertEquals(posX, v);
                    } else {
                        assertNotEquals(posX, v);
                    }
                    if (v.equals(posX) || v.equals(negX)) {
                        isX++;
                    } else if (v.equals(posY) || v.equals(negY)) {
                        isY++;
                    }
                }
//                System.out.println(i++ + "\t:\t\t back to the X axis: " + v);
//                System.out.println();
                assertEquals(posX, v);
                if (field.isOdd()) {
                    assertEquals(1, isX);
                    assertEquals(1, isY);
                } else {
                    assertEquals(2, isX);
                    assertEquals((nSides % 4 == 0) ? 2 : 0, isY);
                }
            }
        }
    }
    
    @Test
    public void compareHeptagonRotationMatrices() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        HeptagonalAntiprismSymmetry hSymm = new HeptagonalAntiprismSymmetry(
                new HeptagonField(), "blue", true )
                .createStandardOrbits("blue");
        AntiprismSymmetry aSymm = getAntiprismSymmetry(7);
        for(int i = 0; i < 14; i++) {
            AlgebraicMatrix hMatrix = hSymm.getMatrix(i);
            AlgebraicMatrix aMatrix = aSymm.getMatrix(i);
            assertEquals("@ " + i, hMatrix, aMatrix);
        }
    }

    @Test
    public void testEmbedInR3() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        final RealVector[] expected = { new RealVector(1, 0, 0), new RealVector(0, 1, 0), new RealVector(0, 0, 1) };
        for (int nSides = PolygonField.MIN_SIDES; nSides <= MAX_SIDES; nSides++) {
            AntiprismSymmetry symm = getAntiprismSymmetry(nSides);
            PolygonField field = symm.getField();
            int dims = 3;
            double lastX = 0.5d;
            double lastY = 0.5d;
            for (int axis = AlgebraicVector.X; axis <= AlgebraicVector.Z; axis++) {
                AlgebraicVector v = field.basisVector(dims, axis);
                RealVector result = symm.embedInR3(v);
                // System.out.println(v + " ---> " + result);
                if (axis == AlgebraicVector.Y && field.isOdd()) {
                    assertNotEquals(expected[axis].x, result.x);
                    assertNotEquals(expected[axis].y, result.y);
                    assertEquals(expected[axis].z, result.z);
                    assertTrue(lastX > result.x);
                    assertTrue(lastY < result.y);
                    lastX = result.x;
                    lastY = result.y;
                } else {
                    assertEquals(expected[axis], result);
                }
            }
        }
    }

    @Test
    public void testHeptagonEmbedInR3() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        HeptagonField hField = new HeptagonField();
        HeptagonalAntiprismSymmetry hSymm = new HeptagonalAntiprismSymmetry(hField, "blue", true).createStandardOrbits("blue");
        AntiprismSymmetry apSymm = getAntiprismSymmetry(7);
        PolygonField pField = apSymm.getField();
        int dims = 3;
        for (int axis = AlgebraicVector.X; axis <= AlgebraicVector.Z; axis++) {
            AlgebraicVector hv = hField.basisVector(dims, axis);
            AlgebraicVector pv = pField.basisVector(dims, axis);
            RealVector hResult = hSymm.embedInR3(hv);
            RealVector pResult = apSymm.embedInR3(pv);
            if (axis == AlgebraicVector.Y) {
                // Math differs in the last decimal position for this one case...
                // but that's close enough since one is calculated with sin() and the other with
                // cos().
                assertEquals(hResult.x, pResult.x, 0.00000000000000006d);
                assertEquals(hResult.y, pResult.y);
                assertEquals(hResult.z, pResult.z);
            } else {
                assertEquals(hResult, pResult);
            }
        }
    }

}
