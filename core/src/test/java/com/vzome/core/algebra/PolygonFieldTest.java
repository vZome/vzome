package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vzome.core.generic.Utilities;

public class PolygonFieldTest {

    // MAX_SIDES is somewhat arbitrary, though it should be at least larger than 
    // the product of 3 distinct primes (e.g. 2 * 3 * 5 = 30).
    // The PolygonField multiplication tensor uses a 3D array of (nSides/2)^3 shorts.
    // It can also bog down computationally if nSides is too big.
    // MAX_SIDES is the max number of polygon sides tested by many of the unit tests.
    public static final int MAX_SIDES = 64;
    
    @Test()
    public void testMIN_SIDES() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        String msg = "Expected an IllegalArgumentException for a parameter less than MIN_SIDES.";
        boolean caught = false;
        try {
            new PolygonField(PolygonField.MIN_SIDES - 1);
            fail(msg);
        }
        catch(IllegalArgumentException ex) {
            caught = true;
        }
        assertTrue(caught);
        
        caught = false;
        try {
            PolygonField.getNormalizerMatrix(PolygonField.MIN_SIDES - 1);
            fail(msg);
        }
        catch(IllegalArgumentException ex) {
            caught = true;
        }
        assertTrue(caught);
    }

    @Test
    public void testHighOrderPolygonFieldConstructor() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        long t0 = System.currentTimeMillis();
        PolygonField field = new PolygonField(2*3*5*7);
        long t1 = System.currentTimeMillis();
        assertNotNull(field);
        System.out.println(field.getName() + " is order " + field.getOrder() + ".\nIts constructor takes " + (t1-t0) + " msec.");
    }

    @Test
    public void testEulerTotient() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        System.out.println("nSides:\tnDiags\t= order + nSecondary. (nPrimary = order)");
        for(int nSides = PolygonField.MIN_SIDES; nSides > 0; nSides++) { // loop until n wraps past Integer.MAX_VALUE to become negative.
            int nDiags = PolygonField.diagonalCount(nSides);
            int nPrimary = PolygonField.primaryDiagonalCount(nSides);
            int nSecondary = PolygonField.secondaryDiagonalCount(nSides);
            assertEquals(nDiags, nSides/2);
            assertTrue(nPrimary <= nDiags);
            assertEquals(nPrimary, PolygonField.getOrder(nSides));;
            System.out.println(nSides + ":\t" + nDiags + "\t= " + nPrimary + "\t+ " + nSecondary);
            if(nSides == MAX_SIDES) {
                System.out.println("... skip ahead closer to " + Integer.MAX_VALUE + " ...");
                nSides = Integer.MAX_VALUE - 20;
            }
        }
    }
    
    @Test
    public void testGetUnitDiagonal() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(int nSides = PolygonField.MIN_SIDES; nSides <= MAX_SIDES; nSides++ ) {
            int nSecondary = PolygonField.secondaryDiagonalCount(nSides);
            if(PolygonField.isPowerOfTwo(nSides) || PolygonField.isPrime(nSides)) {
                assertEquals(0, nSecondary);
                continue;
            }
            PolygonField field = new PolygonField(nSides);
            System.out.println(field.getName());
            assertEquals(nSides, field.polygonSides().intValue());
            for(int i = field.getOrder(); i < field.diagonalCount(); i++) {
                System.out.print("  " + (i == 0 ? "1" : field.getIrrational(i)) + "\t= ");
                AlgebraicNumber n = field.getUnitDiagonal(i);
                System.out.println(n);
                BigRational sum = BigRational.ZERO;
                for(BigRational term : ((AlgebraicNumberImpl) n).getFactors()) {
                    if(!term.isZero()) {
                        sum = sum.plus(term.abs());
                        if(sum.compareTo(BigRational.ONE) == 1) {
                            // we don't care about the actual sum, just that it's greater than 1.
                            break;
                        }
                    }
                }
                assertTrue(sum.compareTo(BigRational.ONE) == 1);
            }
        }       
    }
    
    @Test
    public void printNormalizerMatrix() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(int nSides = PolygonField.MIN_SIDES; nSides <= MAX_SIDES; nSides++ ) {
            int nDiags = PolygonField.diagonalCount(nSides);
            int nSecondary = PolygonField.secondaryDiagonalCount(nSides);
            System.out.print("\n" + nSides + "-gon: " + nDiags + " diagonals");
            short[][] result = PolygonField.getNormalizerMatrix(nSides);
            if(nSecondary == 0) {
                System.out.println();
                assertNull(result);
            } else {
                System.out.println(" - " + nSecondary + " secondary (dependant) diagonal" + (nSecondary == 1 ? "" : "s") + ":");
                assertNotNull(result);
                assertEquals(nDiags, result.length + result[0].length);
                printNormalizerMatrix(result);
            }
        }
    }

    public static void printNormalizerMatrix(short[][] m2) {
        if(m2 == null || m2.length == 0) {
            return;
        }
        int len = m2[0].length;
        System.out.println("{");
        System.out.print("    // " + ParameterizedFields.getCanonicalLabel(0));
        for(int i = 1; i < len; i++) {
            System.out.print("  " + ParameterizedFields.getCanonicalLabel(i));
        }
        System.out.println();
        int i = len;
        for(short[] a1 : m2) {
            System.out.print("    { ");
            for(short n : a1) {
                if(n >= 0) {
                    System.out.print(" "); 
                }
                System.out.print(n + ",");
            }
            System.out.print(" },    //  " + ParameterizedFields.getCanonicalLabel(i) + " = ");
            int j = 0; 
            for(short n : a1) {
                System.out.print(" "); 
                if(n > 0) {
                    System.out.print("+"); 
                }
                if(n != 0) {
                    System.out.print(n + ParameterizedFields.getCanonicalLabel(j)); 
                } else {
                    System.out.print("   ");
                }
                System.out.print(" "); 
                j++;
            }
            System.out.println("|");
            i++;
        }
        System.out.println("};");
    }
    
}
