package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        TEST_FIELDS.add( new SnubDodecField() );
        TEST_FIELDS.add( new SqrtPhiField() );
//        TEST_FIELDS.add( new SnubDodecahedronField() );
//        TEST_FIELDS.add( new SqrtField(2) );
//        TEST_FIELDS.add( new SqrtField(3) );
//        TEST_FIELDS.add( new SqrtField(6) );
//        TEST_FIELDS.add( new SnubCubeField() );
//        TEST_FIELDS.add( new PlasticNumberField() );
//        TEST_FIELDS.add( new SuperGoldenField() );
//        TEST_FIELDS.add( new EdPeggField() );
        for(int nSides = PolygonField.MIN_SIDES; nSides <= PolygonFieldTest.MAX_SIDES; nSides++) {
            TEST_FIELDS.add( new PolygonField(nSides) );
        }
    }
    
    @Test
    public void testHaveSameInitialCoefficients() {
        PolygonField polyField = new PolygonField(5); 
        PentagonField pentField = new PentagonField(); 
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(polyField, PentagonField.FIELD_NAME));
        assertTrue(AlgebraicFields.haveSameInitialCoefficients(pentField, PolygonField.FIELD_PREFIX + "5"));

        polyField = new PolygonField(7); 
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
            if(field instanceof ParameterizedField<?>) {
                ParameterizedFields.printMultiplicationTensor( (ParameterizedField<?>)field );    
            }
        }
    }

}
