package com.vzome.core.algebra;

import static com.vzome.core.generic.Utilities.getSourceCodeLine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vzome.core.editor.Application;
import com.vzome.core.generic.Utilities;
import com.vzome.fields.sqrtphi.SqrtPhiField;

/**
 * @author David Hall
 */
public class AlgebraicFieldTest {
    // Some fields intentionally have the same hashcode and their equals() returns true,
    // so don't use a hash based collection here since we want to test both kinds of field.
    // e.g PolygonField(5) and PentagonField,
    // PolygonField(4) and RootTwoField,
    // PolygonField(6) and RootThreeField
    // or PolygonField(7) and HeptagonField
    private final static List<AlgebraicField> TEST_FIELDS = new ArrayList<>();
    
    static {
        TEST_FIELDS.add (new PentagonField());
        TEST_FIELDS.add (new RootTwoField());
        TEST_FIELDS.add (new RootThreeField());
        TEST_FIELDS.add (new HeptagonField());
        TEST_FIELDS.add (new SqrtPhiField( AlgebraicNumberImpl.FACTORY ));
        TEST_FIELDS.add (new SnubDodecField( AlgebraicNumberImpl.FACTORY ));
        TEST_FIELDS.add (new SnubCubeField( AlgebraicNumberImpl.FACTORY ));
        TEST_FIELDS.add( new PlasticNumberField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new PlasticPhiField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new SuperGoldenField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add( new EdPeggField( AlgebraicNumberImpl.FACTORY ) );
        TEST_FIELDS.add (new PolygonField( 4, AlgebraicNumberImpl.FACTORY ));
        TEST_FIELDS.add (new PolygonField( 6, AlgebraicNumberImpl.FACTORY ));
        TEST_FIELDS.add (new PolygonField( 7, AlgebraicNumberImpl.FACTORY ));
        for(int i = 5; i <= 60; i += 5) {
            TEST_FIELDS.add (new PolygonField(i, AlgebraicNumberImpl.FACTORY ));
        }
    }
    
    @Test
    public void testApplicationDocumentKinds()
    {
        // This test ensures that all supported document kinds are included in this test suite and vise versa.
        // AlgebraicFieldTest and FieldApplicationTest have similar but not identical tests.
        // Note that this test will need to be tweaked when we add parameterized fields like PolygonField and SqrtField.
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        Application app = new Application(false, null, null);
        for(AlgebraicField field: TEST_FIELDS) {
            String testFieldName = field.getName();
            assertNotNull("Application should contain test field " + testFieldName, app.getDocumentKind(testFieldName));
        }
        assertEquals("Application should contain an alias for dodecagon", "rootThree", app.getDocumentKind("dodecagon").getField().getName());
        for(String fieldName: app.getFieldNames()) {
            switch(fieldName) {
            case "dodecagon":
                // rootThree has an alias
                fieldName = "rootThree";
                break;
            }
            boolean found = false;
            for(AlgebraicField testField: TEST_FIELDS) {
                String testName = testField.getName();
                if(testName.equals(fieldName)) {
                    found = true;
                    break;
                }
            }
            assertTrue("TEST_FIELDS should contain " + fieldName, found);
        }
    }    
    
    @Test
    public void testEquality() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        AlgebraicField[] fields = TEST_FIELDS.toArray( new AlgebraicField[TEST_FIELDS.size()] );
        for(int j = 0; j < fields.length; j++) {
            for(int k = 0; k < fields.length; k++) {
                String msg = fields[j].getName() + " vs " + fields[k].getName();
                if(fields[j] instanceof PolygonField ^ fields[k] instanceof PolygonField) {
                    System.out.println ("TODO: Test equility of " + msg);
                    continue; // so test will pass until we get a better test suite
                }
                // TODO: This approach won't work when we include PolygonFields or SqrtFields in TEST_FIELDS
                // Specifically, we need to test the equalities and inequalities described in AlgebraicField.equals()
                boolean same = (j == k);
                assertEquals( msg + " : equality", same, fields[j].equals(fields[k]) );
                assertEquals( msg+ " : hashcode", same, fields[j].hashCode() == fields[k].hashCode() );
            }
        }
    }
    
    @Test
    public void testConvertGoldenNumberPairs() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        boolean testedPolygon10 = false;
        for(AlgebraicField field : TEST_FIELDS) {
            testedPolygon10 |= field.getName().equals("polygon10");
            testConvertGoldenNumberPairs(field);
        }
        // be sure we don't remove the special polygon10 code path from the final tests case. 
        assertTrue("Skipped PolygonField(10) test case", testedPolygon10);
    }
    
    static final double PHI_VALUE = (Math.sqrt(5.0) + 1.0) / 2.0;
 // empirically found that PHI_DELTA is good up to at least 600-gon
    static final double PHI_DELTA = 0.0000000000001d;
    
    private void testConvertGoldenNumberPairs(AlgebraicField field) {
        AlgebraicNumber golden = field.getGoldenRatio();
        if(golden != null) {
            String msg = field.getName() + " field";
            System.out.println("testing " + msg);
            
            AlgebraicNumber one = field.one();
            AlgebraicNumber golden1 = golden.plus(one);
            AlgebraicNumber golden2 = golden1.plus(one);
            
            assertTrue(msg, one.isOne());
            AlgebraicNumber two = one.plus(one);
            assertFalse(msg, two.isOne());
            
            assertEquals(msg, golden.evaluate(), PHI_VALUE, PHI_DELTA);
            
            // convertGoldenNumberPairs() is currently used in two places, 
            // namely, createVector() and parseVefNumber().
            // This test should exercise both code paths
            
            // test createVector()
            AlgebraicVector v = field.createVector(new int[][] {{0,1,0,1}});
            assertTrue(msg, v.getComponent(0).isZero());
            
            v = field.createVector(new int[][] {{1,1,0,1}});
            assertTrue(msg, v.getComponent(0).isOne());
            
            v = field.createVector(new int[][] {{0,1,1,1}});
            assertEquals(msg, golden, v.getComponent(0));
            
            v = field.createVector(new int[][] {{1,1,1,1}});
            assertEquals(msg, golden1, v.getComponent(0));
            
            v = field.createVector(new int[][] {{2,1,1,1}});
            assertEquals(msg, golden2, v.getComponent(0));
            
            // verify that integer overflow is detected
            // This overflow can only occur in the 10-gon field 
            // with very large positive or negative integers.
//            try {
//                v = field.createVector(
//                    new int[][] { {
//                        Integer.MIN_VALUE+2,Integer.MAX_VALUE-3, 
//                        Integer.MAX_VALUE-5,Integer.MAX_VALUE-7
//                    } } );
//                if(field.getName().equals("polygon10")) {
//                    fail("Expected an exception for " + field.getName());
//                }
//            } catch (ArithmeticException e) {
//                if(!field.getName().equals("polygon10")) {
//                    System.out.println(e.getMessage());
//                    fail("Expected NO exception for " + field.getName());
//                }
//            }
            
            // test parseVefNumber()
            AlgebraicNumber num = field.parseVefNumber("(0,0)", false);
            assertTrue(msg, num.isZero());
            
            num = field.parseVefNumber("(0,1)", false);
            assertTrue(msg, num.isOne());
            
            num = field.parseVefNumber("(1,0)", false);
            assertEquals(msg, golden, num);

            num = field.parseVefNumber("(1,1)", false);
            assertEquals(msg, golden1, num);

            num = field.parseVefNumber("(1,2)", false);
            assertEquals(msg, golden2, num);
            
            num = field .createAlgebraicNumberFromTD( new int[]{ 0, 0, 1 } );
            assertTrue( msg, num.isZero() );
            
            num = field .createAlgebraicNumberFromTD( new int[]{ 1, 0, 1 } );
            assertTrue( msg, num.isOne() );
            
            num = field .createAlgebraicNumberFromTD( new int[]{ 0, 1, 1 } );
            assertEquals( msg, golden, num );
            
            num = field .createAlgebraicNumberFromTD( new int[]{ 1, 1, 1 } );
            assertEquals( msg, golden1, num );
            
            num = field .createAlgebraicNumberFromTD( new int[]{ 2, 1, 1 } );
            assertEquals( msg, golden2, num );
        }
    }
        
    @Test
    public void testGoldenRatio() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        List<AlgebraicField> goldenFields = new ArrayList<>();
        goldenFields.add(new PentagonField());
        goldenFields.add(new SnubDodecField( AlgebraicNumberImpl.FACTORY ));
        goldenFields.add(new SnubDodecahedronField( AlgebraicNumberImpl.FACTORY ));
        goldenFields.add(new SqrtPhiField( AlgebraicNumberImpl.FACTORY ));
        goldenFields.add( new PlasticPhiField( AlgebraicNumberImpl.FACTORY ) );
        
        for(AlgebraicField field : goldenFields) {
            String fieldName = field.getName();
            AlgebraicNumber golden = field.getGoldenRatio();
            assertNotNull(fieldName, golden);
            assertEquals(fieldName, PentagonField.PHI_VALUE, golden.evaluate(), PHI_DELTA);
            System.out.println(fieldName + ": golden ratio\t= " + golden.toString());
        }

        // make sure we test some golden and some non-golden fields
        int nNull = 0;
        int nGold = 0;
        for(AlgebraicField field : TEST_FIELDS) {
            if(! (field instanceof PolygonField)) {
                assertEquals(field.getName(), goldenFields.contains(field), field.getGoldenRatio() != null);
            }
            if(field.getGoldenRatio() == null) {
                nNull ++;
            } else {
                nGold++;
            }
        }
        assertTrue(nNull > 0);
        assertTrue(nGold > 0);
    }
    
    @Test
    public void testOrder() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        int pass = 0;
        for(AlgebraicField field : TEST_FIELDS) {
            assertTrue(field.getOrder() >= 2);
            pass++;
        }
        assertEquals(TEST_FIELDS.size(), pass);
    }

	@Test
	public void testReciprocal()
	{
	    System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
		for( AlgebraicField field : TEST_FIELDS ) {
			try {
				field .zero() .reciprocal() .evaluate();
				fail( "Zero divide should throw an exception" );
			} catch ( RuntimeException re ) {
				assertEquals( "Denominator is zero", re .getMessage() );
			}
		}
	}
	
    @Test
    public void testGaussJordanReduction()
    {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        int[][] matrix = {
            // first column is not a pivot column.
            // rows == columns
            // rank == 1
            //  both rows are identical 
            //  so result should have row 0 unchanged 
            // and all 0's in row 1
            {  0,  1 },
            {  0,  1 },
        };
        int[][] expected = {
            {  0,  1, },
            {  0,  0, },
        };
        verifyGaussJordanReduction(matrix, matrix, expected, 1);

        matrix = new int[][] {
            {  0,  2,  0,  0, },
            {  0,  0,  0,  2, },
            {  2,  0,  0,  0, },
            {  0,  0,  2,  0, },
            {  1,  2, -2,  1, },
        };
        expected = new int[][] {
            {  1,  0,  0,  0, },
            {  0,  1,  0,  0, },
            {  0,  0,  1,  0, },
            {  0,  0,  0,  1, },
            {  0,  0,  0,  0, },
        };
        verifyGaussJordanReduction(matrix, matrix, expected, 4);
    
        matrix = new int[][] {
            // intermediate column is not a pivot column.
            // rows < columns
            // rank == 2
            {  0,  0,  0,  0,  0,  0, },
            {  0,  1,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0, },
        };
        expected = new int[][] {
            {  0,  1,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0, },
            {  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  0, },
        };
        verifyGaussJordanReduction(matrix, matrix, expected, 2);

        matrix = new int[][] {
            // intermediate row is not a pivot row.
            // rows > columns
            // rank == 2
            {  0,  0,  0,  0, },
            {  0,  0,  1,  0, },
            {  0,  0,  0,  0, },
            {  0,  0,  0,  0, },
            {  0,  1,  0,  0, },
            {  0,  0,  0,  0, },
        };
        expected = new int[][] {
            {  0,  1,  0,  0, },
            {  0,  0,  1,  0, },
            {  0,  0,  0,  0, },
            {  0,  0,  0,  0, },
            {  0,  0,  0,  0, },
            {  0,  0,  0,  0, },
        };
        verifyGaussJordanReduction(matrix, matrix, expected, 2);

        matrix = new int[][] {
            {  1,  4,  6,  7, },
            { -3, -7, -8, -6, },
            {  2, 12, 17, 32, },
        };
        expected = new int[][] {
            {  1,  0,  0, -9, },
            {  0,  1,  0,  7, },
            {  0,  0,  1, -2, },
        };
        verifyGaussJordanReduction(matrix, matrix, expected, 3);
    }
    
    @Test
    public void testGaussJordanReductionAdjoined()
    {
        int[][] matrix = {
            {  1,  4,  6, },
            { -3, -7, -8, },
            {  2, 12, 17, },
        };
        int[][] adjoined = {
            {  7, },
            { -6, },
            { 32, },
        };
        int[][] expected = {
            { -9, },
            {  7, },
            { -2, },
        };
        verifyGaussJordanReduction(matrix, adjoined, expected, matrix[0].length);
    
        matrix = new int[][] {
            // n   m   l   k   j   i   h     // 30 = 5 * 3 * 2
            {  1,  0,  0,  0,  0,  0,  0, }, // 10 = 5 * 2
            {  0,  1,  0,  0,  0,  0,  0, },
            {  0,  0,  1,  0,  0,  0,  0, },
            {  0,  0,  0,  1,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0, -1, },

            {  1,  0,  0,  0,  0,  0, -2, }, // 6 = 3 * 2
            {  0,  1,  0,  0,  0, -1,  0, },
            {  0,  0,  1,  0, -1,  0,  0, }, 
        };
        adjoined = new int[][] {
            // g   f   e   d   c   b   a   1     // 30 = 5 * 3 * 2
            {  0,  0,  0,  2,  0,  0,  0,  0, }, // 10 = 5 * 2
            {  0,  0,  1,  0,  1,  0,  0,  0, },
            {  0,  1,  0,  0,  0,  1,  0,  0, },
            {  1,  0,  0,  0,  0,  0,  1,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  1, },

            {  0,  0,  0,  0,  0, -2,  0,  0, }, // 6 = 3 * 2
            {  1,  0,  0,  0, -1,  0, -1,  0, },
            {  0,  1,  0, -1,  0,  0,  0, -1, }, 
        };
        expected  = new int[][] {
            // g   f   e   d   c   b   a   1     // 30 = 5 * 3 * 2
            {  0,  0,  0,  2,  0,  0,  0,  0, },
            {  0,  0,  1,  0,  1,  0,  0,  0, },
            {  0,  1,  0,  0,  0,  1,  0,  0, },
            {  1,  0,  0,  0,  0,  0,  1,  0, },
            {  0,  0,  0,  1,  0,  1,  0,  1, },
            { -1,  0,  1,  0,  2,  0,  1,  0, },
            {  0,  0,  0,  1,  0,  1,  0,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  0, },
        };
        verifyGaussJordanReduction(matrix, adjoined, expected, matrix[0].length);
 
        matrix = new int[][] {
            // p   o   n   m   l     // 35 = 7 * 5
            {  1,  0,  0,  0,  0, }, // 7
            {  0,  1,  0,  0,  0, },
            {  0,  0,  1,  0, -1, },

            {  1,  0,  0,  0, -1, }, // 5
            {  0,  1,  0, -1,  0, },
        };
        adjoined = new int[][] {
            // k   j   i   h   g   f   e   d   c   b   a   1     // 35 = 7 * 5
            {  0,  1,  1,  0,  0,  0,  0,  0, -1, -1,  0,  0, }, // 7
            {  1,  0,  0,  1,  0,  0,  0, -1,  0,  0, -1,  0, },
            {  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0, -1, },

            {  1,  0,  0,  0, -1, -1,  0,  0,  0,  1,  1,  0, }, // 5
            {  0,  1,  0, -1,  0,  0, -1,  0,  1,  0,  0,  1, },
        };
        expected = new int[][] {
            // k   j   i   h   g   f   e   d   c   b   a   1     // 35 = 7 * 5
            {  0,  1,  1,  0,  0,  0,  0,  0, -1, -1,  0,  0, },
            {  1,  0,  0,  1,  0,  0,  0, -1,  0,  0, -1,  0, },
            { -1,  1,  1,  0,  2,  1, -1,  0, -1, -2, -1, -1, },
            {  1, -1,  0,  2,  0,  0,  1, -1, -1,  0, -1, -1, },
            { -1,  1,  1,  0,  1,  1,  0,  0, -1, -2, -1,  0, },
        };
        verifyGaussJordanReduction(matrix, adjoined, expected, matrix[0].length);

        matrix = new int[][] {
            // C   B   A   z   y   x   w   v   u   t   s   r   q   p     // 60 = 5 * 3 * 2 * 2
            {  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, }, // 20 = 5 * 2 * 2
            {  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0, -1, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0, -1,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0, -1,  0,  0, },
            
            {  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -2,  0, }, // 12 = 3 * 2 * 2
            {  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  0, -1, },
            {  0,  0,  1,  0,  0,  0,  0,  0,  0,  0, -1,  0,  0,  0, },
            {  0,  0,  0,  1,  0,  0,  0,  0,  0, -1,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0,  0,  0, -1,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0, },
        };
        adjoined = new int[][] {
            // o   n   m   l   k   j   i   h   g   f   e   d   c   b   a   1     // 60 = 5 * 3 * 2 * 2
            {  0,  0,  0,  0,  0,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0, }, // 20 = 5 * 2 * 2
            {  0,  0,  0,  0,  0,  1,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  1,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0, },
            {  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0, },
            {  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, },

            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -2,  0,  0,  0,  0,  0, }, // 12 = 3 * 2 * 2
            {  0,  0,  0,  0,  0,  0,  0,  0,  0, -1,  0, -1,  0,  0,  0,  0, },
            {  1,  0,  0,  0,  0,  0,  0,  0, -1,  0,  0,  0, -1,  0,  0,  0, },
            {  0,  1,  0,  0,  0,  0,  0, -1,  0,  0,  0,  0,  0, -1,  0,  0, },
            {  0,  0,  1,  0,  0,  0, -1,  0,  0,  0,  0,  0,  0,  0, -1,  0, },
            {  0,  0,  0,  1,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1, },
        };
        expected = new int[][] {
            // o   n   m   l   k   j   i   h   g   f   e   d   c   b   a   1     // 60 = 5 * 3 * 2 * 2
            {  0,  0,  0,  0,  0,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  1,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  1,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0, },
            {  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0, },
            {  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0, },
            {  0,  1,  0, -1,  0,  1,  0,  0,  0,  0,  0,  1,  0,  0,  0,  1, },
            {  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  1,  0,  0,  0,  1,  0, },
            {  0, -1,  0,  1,  0,  0,  0,  1,  0,  1,  0,  0,  0,  1,  0,  0, },
            { -1,  0,  0,  0,  1,  0,  0,  0,  2,  0,  0,  0,  1,  0,  0,  0, },
            {  0, -1,  0,  1,  0,  0,  0,  1,  0,  1,  0,  0,  0,  1,  0, -1, },
            {  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  1,  0,  0,  0,  0,  0, },
            {  0,  1,  0, -1,  0,  1,  0,  0,  0,  0,  0,  1,  0, -1,  0,  1, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
            {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, },
        };
        verifyGaussJordanReduction(matrix, adjoined, expected, matrix[0].length);
    }
    
    private static void verifyGaussJordanReduction(int[][] intMatrix, int[][] intAdjoined, int[][] intExpected, int expectedRank) {
        final BigRational[][] matrix = BigRationalImpl.newMatrix(intMatrix);
        final BigRational[][] adjoined = BigRationalImpl.newMatrix(intAdjoined);
        final BigRational[][] expected = BigRationalImpl.newMatrix(intExpected);
                
        System.out.println("-----------------------------------------------------");
        System.out.println(getSourceCodeLine(2));
        System.out.println("Initial Augmented Matrices:");
        showMatrices(matrix, adjoined);
        int rank = Fields.gaussJordanReduction(matrix, adjoined);
        System.out.println("Rank = " + rank);
        System.out.println("Adjoined matrix reduced:");
        showMatrix(adjoined);
        
        assertEquals(expectedRank, rank);
        assertEquals(expected.length, matrix.length);
        assertEquals(expected.length, adjoined.length);
        
        for(int r = 0; r < adjoined.length; r++ ) {
            String msg = "adjoined[" + r + "].length";
            assertEquals(msg, expected[r].length, adjoined[r].length);
        }
        for(int r = 0; r < adjoined.length; r++ ) {
            for(int c = 0; c < adjoined[0].length; c++ ) {
                String msg = "adjoined[" + r + "][" + c + "]";
                assertEquals(msg, expected[r][c], adjoined[r][c]);
            }
        }

        if(rank < expected.length) {
            for(int r = rank; r < adjoined.length; r++ ) {
                for(int c = 0; c < adjoined[0].length; c++ ) {
                    String msg = "adjoined[" + r + "][" + c + "]";
                    assertTrue(msg, adjoined[r][c].isZero());
                }
            }
        }
    }
    
    private static void showMatrix(BigRational[][] matrix) {
        StringBuffer buf = new StringBuffer();
        buf.append("{\n");
        for(int r = 0; r < matrix.length; r++) {
            buf.append("    { ");
            for(int c = 0; c < matrix[r].length; c++) {
                buf.append(paddedString(matrix[r][c]));
                buf.append(", ");
            }
            buf.append("},\n");
        }
        buf.append("}");
        System.out.println(buf.toString());
    }

    private static void showMatrices(BigRational[][] matrix, BigRational[][] adjoined) {
        StringBuffer buf = new StringBuffer();
        final String delim = "  ";
        for(int r = 0; r < matrix.length; r++) {
            buf.append(" ");
            for(int c = 0; c < matrix[r].length; c++) {
                buf.append(nzPaddedString(matrix[r][c]));
                buf.append(delim);
            }
            buf.deleteCharAt(buf.length()-1);
            buf.append("|");
            for(int c = 0; c < adjoined[r].length; c++) {
                buf.append(nzPaddedString(adjoined[r][c]));
                buf.append(delim);
            }
          buf.append("\n");
        }
        System.out.println(buf.toString());
    }
    
    private static String paddedString(BigRational b) {
        return (b.isNegative() ? "" : " ") + b.toString();
    }

    private static String nzPaddedString(BigRational b) {
     // replace zero values with underscores to make non-zero values stand out
        return b.isZero() ? " _" : paddedString(b); 
    }

}
