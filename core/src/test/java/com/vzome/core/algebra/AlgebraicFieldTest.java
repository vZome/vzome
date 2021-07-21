package com.vzome.core.algebra;

import static com.vzome.core.generic.Utilities.getSourceCodeLine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.vzome.core.editor.Application;
import com.vzome.core.generic.Utilities;
import com.vzome.fields.sqrtphi.SqrtPhiField;

/**
 * @author David Hall
 */
public class AlgebraicFieldTest {
    // LinkedHashSet preserves insertion order to ensure that fields are tested in a predictable sequence
    private final static Set<AlgebraicField> fields = new LinkedHashSet<>();
    
    static {
        fields.add (new PentagonField());
        fields.add (new RootTwoField());
        fields.add (new RootThreeField());
        fields.add (new HeptagonField());
        fields.add (new SqrtPhiField( AlgebraicNumberImpl.FACTORY ));
        fields.add (new SnubDodecField( AlgebraicNumberImpl.FACTORY ));
        fields.add (new SnubCubeField( AlgebraicNumberImpl.FACTORY ));
        fields.add( new PlasticNumberField( AlgebraicNumberImpl.FACTORY ) );
        fields.add( new PlasticPhiField( AlgebraicNumberImpl.FACTORY ) );
        fields.add( new SuperGoldenField( AlgebraicNumberImpl.FACTORY ) );
        fields.add( new EdPeggField( AlgebraicNumberImpl.FACTORY ) );
    }
    
    @Test
    public void testApplicationDocumentKinds()
    {
        // This test ensures that all supported document kinds are included in this test suite and vise versa.
        // AlgebraicFieldTest and FieldApplicationTest have similar but not identical tests.
        // Note that this test will need to be tweaked when we add parameterized fields like PolygonField and SqrtField.
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        Application app = new Application(false, null, null);
        for(AlgebraicField field: fields) {
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
            for(AlgebraicField testField: fields) {
                String testName = testField.getName();
                if(testName.equals(fieldName)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Test fields should contain " + fieldName, found);
        }
    }    
    
    @Test
    public void testEquality() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        AlgebraicField[] f = fields.toArray( new AlgebraicField[fields.size()] );
        for(int j = 0; j < f.length; j++) {
            for(int k = 0; k < f.length; k++) {
                // TODO: This approach won't work when we include parameterized fields in fields
                // Specifically, we need to test the equalities and inequalities described in AlgebraicField.equals()
                boolean same = (j == k);
                assertEquals( same, f[j].equals(f[k]) );
                assertEquals( same, f[j].hashCode() == f[k].hashCode() );
            }
        }
    }
        
    @Test
    public void testGoldenRatio() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        List<AlgebraicField> goldenFields = new ArrayList<>();
        goldenFields.add(new PentagonField());
        goldenFields.add(new SnubDodecField( AlgebraicNumberImpl.FACTORY ));
        goldenFields.add(new SqrtPhiField( AlgebraicNumberImpl.FACTORY ));
        goldenFields.add( new PlasticPhiField( AlgebraicNumberImpl.FACTORY ) );
        
        for(AlgebraicField field : goldenFields) {
            String fieldName = field.getName();
            AlgebraicNumber golden = field.getGoldenRatio();
            assertNotNull(fieldName, golden);
            assertEquals(fieldName, PentagonField.PHI_VALUE, golden.evaluate(), 0.00000000000001d);
            System.out.println(fieldName + ": golden ratio\t= " + golden.toString());
        }

        // make sure we test some golden and some non-golden fields
        int nNull = 0;
        int nGold = 0;
        for(AlgebraicField field : fields) {
            assertEquals(field.getName(), goldenFields.contains(field), field.getGoldenRatio() != null);
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
        for(AlgebraicField field : fields) {
            assertTrue(field.getOrder() >= 2);
            pass++;
        }
        assertEquals(fields.size(), pass);
    }

	@Test
	public void testReciprocal()
	{
	    System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
		for( AlgebraicField field : fields ) {
			try {
				field .zero() .reciprocal() .evaluate();
				fail( "Zero divide should throw an exception" );
			} catch ( RuntimeException re ) {
				assertEquals( "Denominator is zero", re .getMessage() );
			}
		}
	}
	
	@Test
	public void testDefineMultiplier() {
	    System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
	    int pass = 0;
	    for(AlgebraicField field : fields) {
            System.out.println(field.getName());
            final int mults = field.getNumMultipliers();
            final int irrats = field.getNumIrrationals();
            assertTrue(mults <= irrats);
            for(int i = 1; i <= mults; i++) {
                StringBuffer buf = new StringBuffer();
                field.defineMultiplier(buf, i);
                final String declaration = buf.toString();
                
                switch(field.getName()) {
                case "golden":
                    assertEquals(declaration, "phi = ( 1 + sqrt(5) ) / 2");
                    break;
                    
                case "rootTwo":
                case "rootThree":
                    assertTrue(declaration.isEmpty());
                    break;
                    
                default:
                    assertFalse(declaration.isEmpty());
                    break;                        
                }
                if(!declaration.isEmpty()) {
                    // allow uppercase names too for PlasticNumberField
                    if(!declaration.matches("[A-Za-z]+ = .*")) {
                        String msg = "Expected alphanumeric variable name but found: " + declaration + ". " 
                                + field.getName() 
                                + ".getNumMultipliers() should probably be returning less than " + i;
                        fail(msg);
                    }
                }
                System.out.println("\t" + declaration);
            }
	        pass++;
	    }
	    assertTrue("Did we test any?", pass > 0);
        assertEquals(fields.size(), pass);
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
