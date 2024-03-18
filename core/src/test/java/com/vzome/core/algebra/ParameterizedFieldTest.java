package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vzome.core.generic.Utilities;
import com.vzome.fields.sqrtphi.SqrtPhiField;

/**
 * @author David Hall
 */
public class ParameterizedFieldTest {
    
    private static final List< AlgebraicField > TEST_FIELDS = new ArrayList<>();
    
    static {
        TEST_FIELDS.add( new PentagonField() );
        TEST_FIELDS.add( new RootTwoField() );
        TEST_FIELDS.add( new RootThreeField() );
        TEST_FIELDS.add( new HeptagonField() );
        TEST_FIELDS.add( new SnubDodecField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new SnubDodecahedronField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new SqrtPhiField( AlgebraicNumberImpl.FACTORY ) );
//        TEST_FIELDS.add( new SqrtField(2) );
//        TEST_FIELDS.add( new SqrtField(3) );
//        TEST_FIELDS.add( new SqrtField(6) );
        TEST_FIELDS.add( new SnubCubeField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new PlasticNumberField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new PlasticPhiField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new SuperGoldenField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new EdPeggField( AlgebraicNumberImpl.FACTORY ) );
        for(int nSides = PolygonField.MIN_SIDES; nSides <= PolygonFieldTest.MAX_SIDES; nSides++) {
            TEST_FIELDS.add( new PolygonField(nSides, AlgebraicNumberImpl.FACTORY ) );
        }
    }
    
    @Test
    public void testHaveSameInitialCoefficients() {
        SnubDodecahedronField newSdField = new SnubDodecahedronField( AlgebraicNumberImpl.FACTORY );
        SnubDodecField oldSdField = new SnubDodecField(AlgebraicNumberImpl.FACTORY); 
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(newSdField, SnubDodecField.FIELD_NAME));
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(oldSdField, SnubDodecahedronField.FIELD_NAME));

        PolygonField polyField = new PolygonField(5, AlgebraicNumberImpl.FACTORY ); 
        PentagonField pentField = new PentagonField(); 
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(polyField, PentagonField.FIELD_NAME));
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(pentField, PolygonField.FIELD_PREFIX + "5"));

        polyField = new PolygonField(7, AlgebraicNumberImpl.FACTORY ); 
        HeptagonField heptField = new HeptagonField(); 
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(polyField, HeptagonField.FIELD_NAME));
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(heptField, PolygonField.FIELD_PREFIX + "7"));
    }


    @Test
    public void testMulDivEvaluate() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(AlgebraicField field : TEST_FIELDS) {
            testMulDivEvaluate( field );    
        }
    }
    
    public static void testMulDivEvaluate(AlgebraicField field) {
        // Empirically found the minimum delta that works...
        final double delta = 0.000000000000171d; // twelve 0's between the decimal point and the first non-zero digit 
        int n = field.getOrder();
        for (int i = 0; i < n; i++) {
            AlgebraicNumber n1 = field.getUnitTerm(i);
            double d1 = n1.evaluate();
            for (int j = 0; j < n; j++) {
                AlgebraicNumber n2 = field.getUnitTerm(j);
                double d2 = n2.evaluate();
                AlgebraicNumber product = n1.times(n2);
                AlgebraicNumber quotient = n1.dividedBy(n2);
                double prod = product.evaluate();
                double quot = quotient.evaluate();
//                System.out.println(field.getName() + ": " + n1.toString() + " * " + n2.toString() + " = " + product.toString() + "\t: " + d1 + " * " + d2 + " = " + prod );
//                System.out.println(field.getName() + ": " + n1.toString() + " / " + n2.toString() + " = " + quotient.toString() + "\t: " + d1 + " / " + d2 + " = "+ quot );
                assertEquals(d1 * d2, prod, delta);
                assertEquals(d1 / d2, quot, delta);
            }
        }
    }
    
    @Test
    public void printMathTables() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(AlgebraicField field : TEST_FIELDS) {
            ParameterizedFields.printMathTables( field );
        }
    }

    @Test
    public void printNumberByName() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        String[] names = {
                "sqrt2","sqrt3","sqrt5","sqrt6","sqrt7","sqrt8","sqrt10",
                "phi","xi","rho","sigma",
                "alpha","beta","gamma","delta","epsilon",
                "theta","kappa","lambda","mu","psi"
            };
        for(String name : names) {
            System.out.println(name);
            boolean found = false;
            for(AlgebraicField field : TEST_FIELDS) {
                AlgebraicNumber n = field.getNumberByName(name);
                if(n != null) {
                    found = true;
                    System.out.print("  " + field.getName() + "\t" + n + "\t" + n.evaluate());
                    if(name.startsWith("sqrt")) {
                        String radicand = name.substring(4);
                        // verify the aliases
                        assertEquals(name, n, field.getNumberByName("root" + radicand));
                        assertEquals(name, n, field.getNumberByName("\u221A" + radicand));
                        // now square it
                        AlgebraicNumber sq = n.times(n);
                        System.out.print("^2 = \t" + sq.evaluate());
                        double expected = Double.valueOf(radicand);
                        assertEquals(name, expected, sq.evaluate(), 0.0d);
                    }
                    System.out.println();
                }
            }
            assertTrue("found a match for " + name, found);
        }
    }

    @Test
    public void testGetIrrationalName() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(AlgebraicField field : TEST_FIELDS) {
            for(int format = AlgebraicField.DEFAULT_FORMAT; format <= AlgebraicField.EXPRESSION_FORMAT; format++) {
                assertEquals(field.getName() + " 0th irrational name should be a single space", " ", field.getIrrational(0, format));
                for(int i=1; i < field.getOrder(); i++) {
                    String name = field.getIrrational(i, format);
                    AlgebraicNumber n = field.getUnitTerm(i);    
                    AlgebraicNumber q = field.getNumberByName(name);
                    assertEquals(n, q);
                }
                try {
                    int limit = field.getOrder();
                    if(field instanceof PolygonField) {
                        limit = ((PolygonField)field).polygonSides();
                    }
                    field.getIrrational(limit, format);
                    fail(field.getName() + ".getIrrational: expected an ArrayIndexOutOfBoundsException for the INDEX parameter");
                }
                catch(ArrayIndexOutOfBoundsException ex) {
                    // success
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    fail(field.getName() + ": inconsistent exception class: " + ex.getClass().getSimpleName());
                }
            }
            try {
                field.getIrrational(0, 2);
                fail(field.getName() + ".getIrrational: expected an ArrayIndexOutOfBoundsException for the FORMAT parameter");
            }
            catch(ArrayIndexOutOfBoundsException ex) {
                // success
            }
            catch(Exception ex) {
                ex.printStackTrace();
                fail(field.getName() + ": inconsistent exception class: " + ex.getClass().getSimpleName());
            }

        }
    }

    @Test
    public void printExponentTables() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(AlgebraicField field : TEST_FIELDS) {
            ParameterizedFields.printExponentTable( field, 6 );
        }
    }

    @Test
    public void printMultiplicationTensors() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(AlgebraicField field : TEST_FIELDS) {
            if(field instanceof ParameterizedField) {
                ParameterizedFields.printMultiplicationTensor( (ParameterizedField)field );    
            }
        }
    }

}
