package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class AlgebraicMatrixTest {
    
    static final double zeroDelta = 0.0D;

    @Test
    public void testDeterminant1D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( -17 ) );
        AlgebraicMatrix m = new AlgebraicMatrix( new AlgebraicVector[]{ c0 } );
//        print(m);
        
        assertTrue("require a square matrix", m.isSquare());
        assertEquals("require 1D", 1, m.getMatrix().length);
        assertEquals("properly dimensioned vectors", c0.dimension(), m.getMatrix().length);
        
        AlgebraicNumber d = m.determinant(); // calculate it
        assertEquals( "calculated determinant", -17D, d.evaluate(), zeroDelta);
        
        assertEquals( "The determinant of a square matrix equals the determinant of its transpose.", d, m.transpose().determinant());
    }
   
    @Test
    public void testDeterminant2D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( 1 ), field.createRational( 2 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.createRational( 3 ), field.createRational( 4 ) );
        AlgebraicMatrix m = new AlgebraicMatrix( c0, c1 );
//        print(m);
        
        assertTrue("require a square matrix", m.isSquare());
        assertEquals("require 2D", 2, m.getMatrix().length);
        assertEquals("properly dimensioned vectors", c0.dimension(), m.getMatrix().length);
        
        AlgebraicNumber d = m.determinant(); // calculate it
        assertEquals( "calculated determinant", -2D, d.evaluate(), zeroDelta);
        
        assertEquals( "The determinant of a square matrix equals the determinant of its transpose.", d, m.transpose().determinant());
    }
   
    @Test
    public void testDeterminant3D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( 1 ), field.createRational( 2 ), field.createRational( 3 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.createRational( 3 ), field.createRational( 2 ), field.createRational( 1 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.createRational( 2 ), field.createRational( 1 ), field.createRational( 3 ) );
        AlgebraicMatrix m = new AlgebraicMatrix( c0, c1, c2 );
//        print(m);
        
        assertTrue("require a square matrix", m.isSquare());
        assertEquals("require 3D", 3, m.getMatrix().length);
        assertEquals("properly dimensioned vectors", c0.dimension(), m.getMatrix().length);
        
        AlgebraicNumber d = m.determinant(); // calculate it
        assertEquals( "calculated determinant", -12D, d.evaluate(), zeroDelta);
        
        assertEquals( "The determinant of a square matrix equals the determinant of its transpose.", d, m.transpose().determinant());
    }
   
    @Test
    public void testDeterminant4D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( -6 ), field.createRational( 0 ), field.createRational( -20 ), field.createRational(  9 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.createRational( -7 ), field.createRational( 1 ), field.createRational(  21 ), field.createRational( 16 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.createRational( -8 ), field.createRational( 2 ), field.createRational( -22 ), field.createRational( 25 ) );
        AlgebraicVector c3 = new AlgebraicVector( field.createRational( -9 ), field.createRational( 4 ), field.createRational(  23 ), field.createRational( 36 ) );
        AlgebraicMatrix m = new AlgebraicMatrix( c0, c1, c2, c3 );
//        print(m);
        
        assertTrue("require a square matrix", m.isSquare());
        assertEquals("require 4D", 4, m.getMatrix().length);
        assertEquals("properly dimensioned vectors", c0.dimension(), m.getMatrix().length);
        
        AlgebraicNumber d = m.determinant(); // calculate it
        assertEquals( "calculated determinant", -1240D, d.evaluate(), zeroDelta);
        
        assertEquals( "The determinant of a square matrix equals the determinant of its transpose.", d, m.transpose().determinant());
    }
   
    @Test
    public void testDeterminant5D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( -6 ), field.createRational( 0 ), field.createRational( -20 ), field.createRational(  9 ), field.createRational(  2 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.createRational( -7 ), field.createRational( 1 ), field.createRational(  21 ), field.createRational( 16 ), field.createRational(  7 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.createRational( -8 ), field.createRational( 2 ), field.createRational( -22 ), field.createRational( 25 ), field.createRational( 13 ) );
        AlgebraicVector c3 = new AlgebraicVector( field.createRational( -9 ), field.createRational( 4 ), field.createRational(  23 ), field.createRational( 36 ), field.createRational(-11 ) );
        AlgebraicVector c4 = new AlgebraicVector( field.createRational(-10 ), field.createRational( 5 ), field.createRational(  24 ), field.createRational( 49 ), field.createRational( 42 ) );
        AlgebraicMatrix m = new AlgebraicMatrix( new AlgebraicVector[]{ c0, c1, c2, c3, c4 } );
//        print(m);
        
        assertTrue("require a square matrix", m.isSquare());
        assertEquals("require 5D", 5, m.getMatrix().length);
        assertEquals("properly dimensioned vectors", c0.dimension(), m.getMatrix().length);
        
        AlgebraicNumber d = m.determinant(); // calculate it
        assertEquals( "calculated determinant", 27940D, d.evaluate(), zeroDelta);
        
        assertEquals( "The determinant of a square matrix equals the determinant of its transpose.", d, m.transpose().determinant());
    }
   
    @Test
    public void testNonSquareMatrix()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( 0 ), field.createRational( 10 ), field.createRational( 20 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.createRational( 1 ), field.createRational( 11 ), field.createRational( 21 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.createRational( 2 ), field.createRational( 12 ), field.createRational( 22 ) );
        AlgebraicVector c3 = new AlgebraicVector( field.createRational( 3 ), field.createRational( 13 ), field.createRational( 23 ) );
        AlgebraicVector c4 = new AlgebraicVector( field.createRational( 4 ), field.createRational( 14 ), field.createRational( 24 ) );

        final int nRows = 3;
        assertEquals("vector rows", nRows, c0.dimension());
        assertEquals("vector rows", nRows, c1.dimension());
        assertEquals("vector rows", nRows, c2.dimension());
        assertEquals("vector rows", nRows, c3.dimension());
        assertEquals("vector rows", nRows, c4.dimension());
        
        final AlgebraicMatrix m = new AlgebraicMatrix( new AlgebraicVector[]{ c0, c1, c2, c3, c4 } );
//        print(m);
        
        assertFalse("require a non-square matrix", m.isSquare());
        
        AlgebraicNumber[][] matrix = m.getMatrix();
        final int nCols = 5;
        assertEquals("matrix columns", nRows, matrix.length);
        assertEquals("matrix rows", nCols, matrix[0].length);
        assertEquals("matrix rows", nCols, matrix[1].length);
        assertEquals("matrix rows", nCols, matrix[2].length);
        
        try {
            @SuppressWarnings("unused")
            AlgebraicMatrix i = m.inverse(); // illegal attempt to invert a non-square matrix
            fail("can't invert a non-square matrix");
        } catch(IllegalArgumentException ex) {
            assertEquals("matrix is not square", ex.getMessage());
        }
        
        try {
            @SuppressWarnings("unused")
            AlgebraicNumber d = m.determinant(); // illegal attempt to calculate the determinant of a non-square matrix
            fail("can't calculate the determinant of a non-square matrix");
        } catch(IllegalArgumentException ex) {
            assertEquals("matrix is not square", ex.getMessage());
        }
        
        try {
            @SuppressWarnings("unused")
            AlgebraicNumber t = m.trace(); // illegal attempt to calculate the trace of a non-square matrix
            fail("can't calculate the trace of a non-square matrix");
        } catch(IllegalArgumentException ex) {
            assertEquals("matrix is not square", ex.getMessage());
        }
        
        final AlgebraicMatrix t = m.transpose();
        final AlgebraicMatrix mt = m.times(t);
        assertTrue("Multiplying a non-square matrix by its transpose results in a symmetric square matrix", mt.isSquare());
        assertEquals("A 3x5 matrix times a 5x3 matrix generates a 3x3 matrix", nRows, mt.getMatrix().length);
        
        // reverse the order of the multiplication
        final AlgebraicMatrix tm = t.times(m);
        assertTrue("Multiplying a non-square matrix by its transpose results in a symmetric square matrix", tm.isSquare());
        assertEquals("A 5x3 matrix times a 3x5 matrix generates a 5x5 matrix", nCols, tm.getMatrix().length);
        
        assertNotEquals(mt, tm);
        
//        print(m);
//        print(t);
//        print(mt);
//        print(tm);
    }
    
//    private static void print(AlgebraicMatrix m) {
//        String s = " " + Utilities.getSourceCodeLine(2) + "\n";
//        System.out.println(m.toString()
//                .replaceAll("],", "],\n")
//                + s);
//        
//        // formatted for cut & paste into wolframalpha
////        System.out.println(m.toString()
////                .replaceAll("\\[", "{")
////                .replaceAll("]", "}")
////                .replaceAll("},  }", "} }")
////                + s); 
//        
////        ObjectMapper jsonMapper = new ObjectMapper();
////        try {
////            String jsonString = jsonMapper .writeValueAsString( m );
////            System .out .println( jsonString );
////        } catch (JsonProcessingException e) {
////            // TODO: handle exception
////            e .printStackTrace();
////        }
//    }
   
    // This test was moved from AlgebraicNumberTest to AlgebraicMatrixTest 
    @Test
    public void testChangeOfBasis()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createRational( 3 ), field.one(), field .zero() );
        AlgebraicVector c1 = new AlgebraicVector( field.one(), field.createRational( 3 ), field .zero() );
        AlgebraicVector c2 = new AlgebraicVector( field.one(), field .one(), field.createRational( 3 ) );
        AlgebraicMatrix U = new AlgebraicMatrix( c0, c1, c2 );
        
        AlgebraicVector b0 = new AlgebraicVector( field.one(), field.createRational( -3 ), field .zero() );
        AlgebraicVector b1 = new AlgebraicVector( field.createRational( -3 ), field.one(), field .zero() );
        AlgebraicVector b2 = new AlgebraicVector( field.createRational( -2 ), field .createRational( -2 ), field.createRational( 2 ) );
        AlgebraicMatrix V = new AlgebraicMatrix( b0, b1, b2 );
        
        AlgebraicMatrix r = V .times( U .inverse() );
                
        assertEquals( r .timesColumn( c0 ), b0 );
        assertEquals( r .timesColumn( c1 ), b1 );
        assertEquals( r .timesColumn( c2 ), b2 );
    }
 
    @Test
    public void testMatrixInverse()
    {
        final AlgebraicField field = new PentagonField();
        final AlgebraicMatrix identity = new AlgebraicMatrix( field, 3 );

        AlgebraicVector c0 = new AlgebraicVector( field.one(), field.zero(), field .createRational( 5 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.one(), field.createRational( 2 ), field .createRational( 5 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.one(), field.createRational( 3 ), field .one() );
        AlgebraicMatrix matrix = new AlgebraicMatrix( c0, c1, c2 );
        assertFalse(matrix.equals(identity));
        
        AlgebraicMatrix result = matrix.times(matrix.inverse());

        assertTrue(result.equals(identity));
    }

    // This test was moved from AlgebraicNumberTest to AlgebraicMatrixTest 
    @Test
    public void testMatrixTranspose()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.one(), field.zero(), field .createRational( 5 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.one(), field.createRational( 2 ), field .createRational( 5 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.one(), field.createRational( 3 ), field .one() );
        AlgebraicMatrix m = new AlgebraicMatrix( c0, c1, c2 );
        AlgebraicVector in = new AlgebraicVector( field.zero(), field.zero(), field .createRational( 8 ) );
        AlgebraicVector out = new AlgebraicVector( field.createRational( 8 ), field.createRational( 24 ), field .createRational( 8 ) );

        assertEquals( m .timesColumn( in ), out );
        assertEquals( m .transpose() .timesRow( in ), out );
    }

}
