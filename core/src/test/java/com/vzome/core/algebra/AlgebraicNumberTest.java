
package com.vzome.core.algebra;

import static com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT;
import static com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT;
import static com.vzome.core.algebra.AlgebraicField.VEF_FORMAT;
import static com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vzome.fields.sqrtphi.SqrtPhiField;

public class AlgebraicNumberTest
{
    @Test
    public void testZeroAndOne()
    {
        AlgebraicField field = new PentagonField();
        {
            AlgebraicNumber zero = field.zero();
            assertTrue(zero.isZero());
            assertFalse(zero.isOne());
        }
        {
            AlgebraicNumber one = field.one();
            assertFalse(one.isZero());
            assertTrue(one.isOne());
        }
        {
            AlgebraicNumber phi = field.createAlgebraicNumber(new int[]{0, 1});
            assertFalse(phi.isZero());
            assertFalse(phi.isOne());
        }
    }

    @Test
    public void testZeroPower()
    {
        final AlgebraicField[] fields = {
        	new PentagonField(),
            new RootTwoField(),
            new RootThreeField(),
            new HeptagonField(),
            new SnubDodecField( BigRationalImpl.FACTORY )
        };
        for(AlgebraicField field : fields ) {
            AlgebraicNumber one = field.createPower(0); // anything to the zero power...
            assertEquals(one, field.createRational(1)); // ...equals exactly one
            assertTrue(one.isOne());
        }
    }

    @Test
    public void testAlternativeConstructions() {
        AlgebraicField field = new PentagonField();
        int ones = -7, irrat = 3, denom = 5;

        int scalePower = 0;
        int power = 1;
        assertNotEquals(scalePower, power);

        // DJH: I was confused as to why these two ways of creating an AlgebraicNumber took different values for power.
        // I discovered that scalePower has an unexpected subtly different meaning than power and they expect different values.
        // I renamed the parameter and added this test case, mainly as a reminder of the difference between the two methods.
        // It also serves to highlight various display formats.

        AlgebraicNumber n0 = field.createAlgebraicNumber(ones, irrat, denom, scalePower);
        AlgebraicNumber n1 = field.createRational(ones, denom).plus( field.createPower(power).times( field.createRational(irrat, denom) ) );

        // Note that we also have these other methods available with their own syntactical subtleties
        // field.createAlgebraicNumber( int[] factors )
        // field.createAlgebraicNumber( BigRational[] factors )

        assertEquals(n0, n1);

        assertEquals(n1.toString(), n1.toString(DEFAULT_FORMAT));
        assertEquals("-7/5 +3/5\u03C6", n1.toString(DEFAULT_FORMAT));
        assertEquals("-7/5 +3/5*phi", n1.toString(EXPRESSION_FORMAT));
        assertEquals("-7/5 3/5", n1.toString(ZOMIC_FORMAT));
        assertEquals("(3/5,-7/5)", n1.toString(VEF_FORMAT)); // irrational is listed first in VEF format
    }

    @Test
    public void testTrailingDenominatorConstruction()
    {
        AlgebraicField field = new PentagonField();
        int ones = 7, irrat = 5, denom = 5;

        AlgebraicNumber n0 = field.createAlgebraicNumber( ones, irrat, denom, 0 );
        AlgebraicNumber n1 = field.createAlgebraicNumberFromTD( new int[] { ones, irrat, denom } );

        assertEquals( n0, n1 );

        assertEquals( n0 .toString(), n1 .toString( DEFAULT_FORMAT ) );
        assertEquals("(1,7/5)", n1.toString(VEF_FORMAT)); // irrational is listed first in VEF format
        assertArrayEquals( new int[] { ones, irrat, denom }, n0 .toTrailingDivisor() );
    }
    
    @Test
    public void testOperatorOverloads() {
        AlgebraicField field = new PentagonField();
        AlgebraicNumber[] numbers = new AlgebraicNumber[] {
                field.zero(),
                field.one(),
                field.createRational(-1),
                field.createRational(42),
                field.createRational(22, 7),
                field.getUnitTerm(1),
                field.createPower(2),
                field.createPower(-2)
        };

        final int denominator = 5;
        for(int numerator = -3; numerator <= 3; numerator++) {
//            BigRational br = new BigRational(numerator, denominator);
            for(AlgebraicNumber n : numbers) {
                // first, test with fractions as numerator and denominator args
                AlgebraicNumber r = field.createRational(numerator, denominator);
                assertEquals("add rat", n. plus(r), n. plus(numerator, denominator));
                assertEquals("sub rat", n.minus(r), n.minus(numerator, denominator));
                assertEquals("mul rat", n.times(r), n.times(numerator, denominator));
                try {
                    assertEquals("div rat", n.dividedBy(r), n.dividedBy(numerator, denominator));
                    assertNotEquals("Expected no divide by zero exception.", 0, numerator);
                } catch( IllegalArgumentException ex) {
                    assertEquals("Expected divide by zero exception.", 0, numerator);
                }
                // then with BigRational
//                r = field.createRational(br);
//                assertEquals("add big", n. plus(r), n. plus(br));
//                assertEquals("sub big", n.minus(r), n.minus(br));
//                assertEquals("mul big", n.times(r), n.times(br));
//                try {
//                    assertEquals("div big", n.dividedBy(r), n.dividedBy(br));
//                    assertNotEquals("Expected no divide by zero exception.", 0, numerator);
//                } catch( IllegalArgumentException ex) {
//                    assertEquals("Expected divide by zero exception.", 0, numerator);
//                }
                // and again with integers
                r = field.createRational(numerator);
                assertEquals("add int", n. plus(r), n. plus(numerator));
                assertEquals("sub int", n.minus(r), n.minus(numerator));
                assertEquals("mul int", n.times(r), n.timesInt(numerator));
                try {
                    assertEquals("div int", n.dividedBy(r), n.dividedBy(numerator));
                    assertNotEquals("Expected no divide by zero exception.", 0, numerator);
                } catch( IllegalArgumentException ex) {
                    assertEquals("Expected divide by zero exception.", 0, numerator);
                }
            }
        }
    }
    
    @Test
    public void testCreateAlgebraicNumber() {
        AlgebraicField field = new SqrtPhiField( BigRationalImpl.FACTORY ); // just because it has order > 2
        
        int[] nums = new int[] {0, 2, -2, 42}; 
        for(int den = -3; den <= 3; den++) {
            if(den == 0) {
                try {
                    field.createAlgebraicNumber(nums, den);
                    fail("Expected divide by zero exception.");
                } catch( IllegalArgumentException ex) {
                    // ignore the expected divide by zero
                }
            } else {
                // createAlgebraicNumber interprets its int[] arg as numerators.
                // In contrast, createVector interprets its int[] arg as numerator denominator pairs.
                // This test simply verifies that the optional denominator arg to createAlgebraicNumber
                // is correctly applied to the numerators. 
                AlgebraicNumber n1 = field.createAlgebraicNumber(nums, den);
                if(den == 1) {
                    assertEquals(n1.toString(), n1, field.createAlgebraicNumber(nums));
                }
                int[] fractions = new int[] {nums[0], den, nums[1], den, nums[2], den, nums[3], den};
                AlgebraicNumber n2 = field.createVector(new int[][] {fractions}).getComponent(0);
                assertEquals(n1.toString(), n1, n2);
            }
        }
    }

    @Test
    public void testPrepareAlgebraicNumberTerms() {
        List<AlgebraicField> fields = new ArrayList<>();
        // list any fields that need to remap golden terms 
        fields.add(new SqrtPhiField( BigRationalImpl.FACTORY ));
        for(int n = 5; n <= 50; n += 5) {
//            fields.add(new PolygonField(n)); // TODO eventually
        }
//        fields.add(new SqrtField(5));  // TODO eventually
        
        // There are numerous ways to create AlgebraicNumbers with multiple terms
        // This test ensures that all of them generate the correct AlgebraicNumber 
        // when given only 2 terms from an AlgebraicNumber from the golden field 
        final PentagonField goldenField = new PentagonField();
        final int ones = -37, phis = 42; // these can have any non-zero value
        final int denom = 1, scalePower = 0; // these must be as stated
        final AlgebraicNumberImpl goldenNumber = (AlgebraicNumberImpl) goldenField.createAlgebraicNumber(ones, phis, denom, scalePower);
        System.out.println(goldenField.getName() + ": " + goldenNumber + "\n");
        final BigRational[] goldenTerms = goldenNumber .getFactors();
        final double goldenEvaluate = goldenNumber.evaluate();
        final double delta = 0.0d;
        
        for (AlgebraicField field : fields) {
            System.out.println(field.getName());
            assertNotNull(field.getGoldenRatio());
            { // Using new AlgebraicNumber( AlgebraicField field, BigRational[] newFactors )
             // this test originally failed
                AlgebraicNumber test = new AlgebraicNumberImpl( field, goldenTerms );
                System.out.println(test);
                assertEquals(field.getName(), goldenEvaluate, test.evaluate(), delta);
            }
            { // Using field.createAlgebraicNumber( int[] factors )
              // this test originally failed
                int[] terms = { ones, phis };
                AlgebraicNumber test = field.createAlgebraicNumber(terms);
                System.out.println(test);
                assertEquals(field.getName(), goldenEvaluate, test.evaluate(), delta);
            }
            { // Using field.createVector( int[][] nums )
                int[][] terms = { 
                        { ones, denom, phis, denom }, 
                        { ones, denom, phis, denom },
                        { ones, denom, phis, denom }, 
                    };
                AlgebraicVector testVector = field.createVector(terms);
                System.out.println(testVector);
                assertEquals(field.getName(), 3, testVector.dimension());
                for (AlgebraicNumber test : testVector.getComponents()) {
                    assertEquals(field.getName(), goldenEvaluate, test.evaluate(), delta);
                }
            }
            { // Using field.parseVector( String nums )
              // requires the string to have exactly the correct number of terms
              // so this test is dependent on the field we're testing
                if (field instanceof SqrtPhiField) {
                    final int dims = 4;
                    StringBuilder sb = new StringBuilder();
                    for (int d = 0; d < dims; d++) {
                        sb.append(ones).append(" 0 ").append(phis).append(" 0 ");
                    }
                    AlgebraicVector testVector = field.parseVector(sb.toString());
                    System.out.println(testVector);
                    assertEquals(field.getName(), dims, testVector.dimension());
                    for (AlgebraicNumber test : testVector.getComponents()) {
                        assertEquals(field.getName(), goldenEvaluate, test.evaluate(), delta);
                    }
                }
            }
        }
    }

    @Test
    public void testFactorsNotNull()
    {
        final AlgebraicField field = new PentagonField();
        {
            final BigRational[] inputFactors = {BigRationalImpl.ONE};
            assertEquals( inputFactors.length, 1 );

            AlgebraicNumberImpl value = new AlgebraicNumberImpl(field, inputFactors);

            BigRational[] factors = value.getFactors();
            assertEquals( factors.length, 2 );
            assertEquals( factors.length, field.getOrder() );
            // although we only provided a single element array as inputFactor,
            // the c'tor should zero-fill the array so it never contains nulls
            for(int i = 0; i < factors.length; i++) {
                assertNotNull( factors[i] );
            }
        }
        // a similar but not identical test...
        {
            final BigRational[] inputFactors = new BigRational[field.getOrder()];
            assertEquals( inputFactors.length, 2 );

            inputFactors[0] = BigRationalImpl.ONE;
            assertNotNull( inputFactors[0]);
            assertNull( inputFactors[1]);

            AlgebraicNumberImpl value = new AlgebraicNumberImpl(field, inputFactors);

            // although we provided a null element in inputFactor,
            // the c'tor should zero-fill its factors so it never contains nulls
            BigRational[] factors = value.getFactors();
            assertEquals( factors.length, 2 );
            assertEquals( factors.length, field.getOrder() );
            for(int i = 0; i < factors.length; i++) {
                assertNotNull( factors[i] );
            }
            // check for the specific values we expect
            assertEquals( factors[0], inputFactors[0] );
            assertEquals( factors[1], BigRationalImpl.ZERO );
        }
    }

    @Test
    public void testPentagonField()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicNumber one = field .one();
        AlgebraicNumber phi = field .createAlgebraicNumber( new int[]{ 0, 1 } );
        AlgebraicNumber phi_5 = field .createAlgebraicNumber( new int[]{ 3, 5 } );
        AlgebraicNumber phi_9 = field .createAlgebraicNumber( new int[]{ 21, 34 } );
        AlgebraicNumber phi_minus5 = field .createAlgebraicNumber( new int[]{ -8, 5 } );

        AlgebraicNumber result = phi .times( phi );
        assertTrue( result .equals( phi .plus( one ) ) );
        result = result .dividedBy( phi );
        assertTrue( result .equals( phi ) );
        assertTrue( phi_5 .equals( phi .times( phi .times( phi .times( phi .times( phi ) ) ) ) ) );
        assertTrue( phi_9 .equals( phi_5 .times( phi .times( phi .times( phi .times( phi ) ) ) ) ) );
        assertTrue( phi_5 .equals( field.createPower( 5 ) ) );
        assertTrue( phi_9 .equals( field.createPower( 9 ) ) );
        assertTrue( phi_minus5 .equals( field.createPower( -5 ) ) );
    }

    @Test
    public void testToString()
    {
        AlgebraicField field = new PentagonField();
        AlgebraicNumber number = field .createAlgebraicNumber( 22, 15, 6, 0 );
        
        assertEquals( "11/3 +5/2\u03C6", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "11/3 +5/2*phi", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "11/3 5/2", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(5/2,11/3)", number.toString( AlgebraicField.VEF_FORMAT ) );
        
        number = field .createRational( 0 );

        assertEquals( "0", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "0", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "0 0", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(0,0)", number.toString( AlgebraicField.VEF_FORMAT ) );
        
        number = field .createAlgebraicNumber( new int[]{1, 0} );

        assertEquals( "1", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "1", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        
        number = field .createAlgebraicNumber( new int[]{0, 1} );

        assertEquals( "\u03C6", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "phi", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        
        field = new HeptagonField();
        number = field .createAlgebraicNumber( new int[]{ 6, 11, 14 } );
        
        assertEquals( "6 +11\u03C1 +14\u03C3", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "6 +11*rho +14*sigma", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "6 11 14", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(14,11,6)", number.toString( AlgebraicField.VEF_FORMAT ) );
        
        field = new SnubDodecField( BigRationalImpl.FACTORY );
        number = field .createAlgebraicNumber( new int[]{ -12, 8, 2, -1, 6, -4 } );
        
        assertEquals( "-12 +8\u03C6 +2\u03BE -\u03C6\u03BE +6\u03BE\u00B2 -4\u03C6\u03BE\u00B2", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "-12 +8*phi +2*xi -phi*xi +6*xi^2 -4*phi*xi^2", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "-12 8 2 -1 6 -4", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(-4,6,-1,2,8,-12)", number.toString( AlgebraicField.VEF_FORMAT ) );

        number = field .createAlgebraicNumber( new int[]{0, 0, 0, 0, 0, 0} );
        
        assertEquals( "0", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "0", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );

        number = field .createAlgebraicNumber( new int[]{0, 0, 1, 0, 0, 0} );
        
        assertEquals( "\u03BE", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "xi", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );

        number = field .createAlgebraicNumber( new int[]{0, 1, 0, 0, 0, 1} );
        
        assertEquals( "\u03C6 +\u03C6\u03BE\u00B2", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "phi +phi*xi^2", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
    }

    @Test
    public void testRootTwoField()
    {
        AlgebraicField field = new RootTwoField();

        AlgebraicNumber two = field .createAlgebraicNumber( new int[]{ 2, 0 } );
        AlgebraicNumber sqrt2 = field .createAlgebraicNumber( new int[]{ 0, 1 } );
        AlgebraicNumber eight = field .createAlgebraicNumber( new int[]{ 8, 0 } );
        AlgebraicNumber powerNeg5 = field .createAlgebraicNumber( 0, 1, 8, 0 );

        assertTrue( two .equals( sqrt2 .times( sqrt2 ) ) );
        assertTrue( eight .equals( two .times( sqrt2 .times( sqrt2.times( two ) ) ) ) );
        assertTrue( powerNeg5 .equals( field.createPower( -5 ) ) );
    }

    @Test
    public void testRootThreeField()
    {
        AlgebraicField field = new RootThreeField();

        AlgebraicNumber three = field .createAlgebraicNumber( 3, 0, 1, 0 );
        AlgebraicNumber nine = field .createAlgebraicNumber( 9, 0, 1, 0 );
        AlgebraicNumber sqrt3 = field .createAlgebraicNumber( 0, 1, 1, 0 );
        AlgebraicNumber powerNeg3 = field .createAlgebraicNumber( 0, 1, 9, 0 );
        AlgebraicNumber r1, r2;

        assertTrue( three.equals( sqrt3 .times( sqrt3 ) ) );
        r1 = sqrt3 .times( three );
        r2 = sqrt3 .times( r1 );
        assertTrue( nine .equals( r2 ) );
        assertTrue( powerNeg3 .equals( field.createPower( - 3 ) ) );
    }

    @Test
    public void testHeptagonField()
    {
        AlgebraicField field = new HeptagonField();

        AlgebraicNumber rho = field .createAlgebraicNumber( new int[]{ 0, 1, 0 } );
        AlgebraicNumber sigma = field .createAlgebraicNumber( new int[]{ 0, 0, 1 } );
        AlgebraicNumber sigma_4 = sigma .times( sigma ) .times( sigma ) .times( sigma );
        AlgebraicNumber sigma_5 = field .createAlgebraicNumber( new int[]{ 6, 11, 14 } );
        assertEquals( rho .plus( sigma ), rho .times( sigma ) );
        assertEquals( field .one() .plus( sigma ), rho .times( rho ) );
        assertEquals( field .one() .plus( rho ) .plus( sigma ), sigma .times( sigma ) );
        assertEquals( sigma_4 .times( sigma ), sigma_5 );
        assertEquals( sigma_4, sigma_5 .dividedBy( sigma ) );
    }

    @Test
    public void testSnubDodecField()
    {
        AlgebraicField field = new SnubDodecField( BigRationalImpl.FACTORY );

        AlgebraicNumber phi_xi2_inv = field .createAlgebraicNumber( new int[]{ -12, 8, 2, -1, 6, -4 } );

        writeNumber( "phi_xi2_inv", phi_xi2_inv, field );
        
        AlgebraicNumber phi = field .createPower( 1 );
        
        AlgebraicNumber result = phi .times( field .one() );
        assertEquals( result, phi );

        result = field .one() .times( phi );
        assertEquals( result, phi );

        result = phi .times( phi );
        assertEquals( result, phi .plus( field .one() ) );

        AlgebraicNumber phi_inv = field .createPower( 1 ) .reciprocal();
        result = phi .times( phi_inv );
        assertEquals( result, field .one() );
        
        AlgebraicNumber xi = field .createAlgebraicNumber( new int[]{ 0, 0, 1, 0, 0, 0 } );

        result = xi .dividedBy( xi );
        writeNumber( "xi / xi", result, field );
        assertEquals( result, field .one() );
        
        result = phi .times( xi ) .times( xi ) .reciprocal();
        assertEquals( result, phi_xi2_inv );
    }

    private void writeNumber( String string, AlgebraicNumber phi_xi2_inv, AlgebraicField field )
    {
    }

    // public void testBasisMatrix()
    // {
    // AlgebraicField field = HeptagonField.INSTANCE;
    //
    // int[] e1 = { 1, 0, 0, 0, 1, 0, 0, 0, 1 }; // one, rho, sigma
    // int[] e2 = { 2, 0, 0, 1, 0, 0, 0, 1, 0 }; // two, one, rho
    // int[] e3 = { 0, 1, 0, 0, 0, 1, 1, 0, 0 }; // rho, sigma, one
    //
    // int[][] matrix = { { 1, 0, 0, 2, 0, 0, 0, 1, 0 }, { 0, 1, 0, 0, 2, 0, 1,
    // 0, 1 }, { 0, 0, 1, 0, 0, 2, 0, 1, 1 },
    //
    // { 0, 1, 0, 1, 0, 0, 0, 0, 1 }, { 1, 0, 1, 0, 1, 0, 0, 1, 1 }, { 0, 1, 1,
    // 0, 0, 1, 1, 1, 1 },
    //
    // { 0, 0, 1, 0, 1, 0, 1, 0, 0 }, { 0, 1, 1, 1, 0, 1, 0, 1, 0 }, { 1, 1, 1,
    // 0, 1, 1, 0, 0, 1 } };
    //
    // int[][] result = field.createMatrix( new int[][] { e1, e2, e3 } );
    // assertEquals( result.length, matrix.length );
    // for ( int i = 0; i < matrix.length; i++ )
    // assertTrue( Arrays.equals( result[i], matrix[i] ) );
    //
    // int[] x = { 0, 0, 0, 1, 0, 0, 0, 0, 0 };
    // int[] xt = field.transform( result, x );
    // assertTrue( Arrays.equals( xt, e2 ) );
    // }

    @Test
    public void testDotProduct()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector v1 = field .createVector( new int[][]
            {
                {1,1, 0,1}, {0,1, 1,1}
            } );
        AlgebraicVector v2 = field .createVector( new int[][]
            {
                {0,1, -5,1}, {5,1, 0,1}
            } );

        AlgebraicNumber dot = v1 .dot( v2 );
        assertTrue( dot .isZero() );
    }
    
    @Test
    @SuppressWarnings("unlikely-arg-type")
	public void testEquals()
    {
        AlgebraicField field = new PentagonField();
        
        assertFalse(field.zero().equals(null));
        assertFalse(field.zero().equals(this));
    }

    @Test
    public void testDivisor()
    {
        AlgebraicField field = new HeptagonField();
        
        BigRational r01 = new BigRationalImpl(0, 1); 
        BigRational r23 = new BigRationalImpl(2, 3); 
        BigRational r45 = new BigRationalImpl(4, 5); 
        BigRational r67 = new BigRationalImpl(6, 7); 
        BigRational r89 = new BigRationalImpl(8, 9); 
        BigRational r1011 = new BigRationalImpl(10,11); 
        AlgebraicNumberImpl n = new AlgebraicNumberImpl( field, new BigRational[] { r01, r23, r45} ); 
        assertEquals( BigInteger.valueOf(3 * 5), n.getDivisor() );
        
        n = new AlgebraicNumberImpl( field, new BigRational[] { r1011, r89, r67} );
        assertEquals( BigInteger.valueOf(11 * 9 * 7), n.getDivisor() );

        n = (AlgebraicNumberImpl) field .createRational( 42 );
        assertEquals( BigInteger.ONE, n.getDivisor() );
    }

    @Test
    public void testConstructorException()
    {
        final AlgebraicField[] fields = {
        	new PentagonField(),
            new RootTwoField(),
            new RootThreeField(),
            new HeptagonField(),
            new SnubDodecField( BigRationalImpl.FACTORY ),
            new SqrtPhiField( BigRationalImpl.FACTORY ),
            new SnubCubeField( BigRationalImpl.FACTORY )
        };
        int tests = 0;
        for(AlgebraicField field : fields ) {
            int order = field.getOrder();
            int[] factors = new int[order];
            AlgebraicNumber n = field.createAlgebraicNumber(factors);
            assertTrue(n.isZero());

            factors = new int[order + 2];
            try {
                field.createAlgebraicNumber(factors);
                fail("Expected an IllegalStateException since there are too many factors.");
            }
            catch(IllegalStateException ex) {
                tests ++;
            }
        }
        assertEquals(fields.length, tests);
    }

    @Test
    public void testCompareTo()
    {
        AlgebraicField field = new PentagonField();
        BigRational r01 = new BigRationalImpl(0, 1); 
        BigRational r23 = new BigRationalImpl(2, 3); 
        BigRational r45 = new BigRationalImpl(4, 5); 
        BigRational r67 = new BigRationalImpl(6, 7); 
        AlgebraicNumber j = new AlgebraicNumberImpl( field, new BigRational[] { r01, r23 } ); 
        AlgebraicNumber k = new AlgebraicNumberImpl( field, new BigRational[] { r45, r67} );
        assertNotEquals( 0, j.compareTo(k) );
        assertNotEquals( j.compareTo(k), k.compareTo(j) );
        // Because of the rounding errors when converting to a double,
        // It's possible that the two double values are equal 
        // yet the two AlgebraicaNumbers are not.
        // TODO: Develop a test case to show this scenario
        // TODO: Consider if it's worth the overhead of using BigDecimal.compareTo() in this case
    }

    @Test
    public void testHaveSameInitialCoefficients() {
    	AlgebraicField pent = new PentagonField();
    	AlgebraicField snub = new SnubDodecField( BigRationalImpl.FACTORY );
    	
	    assertEquals(
	    	pent.createAlgebraicNumber( new int[]{2, 3} ).evaluate(),
	    	snub.createAlgebraicNumber( new int[]{2, 3} ).evaluate(),
	    	0.0D
	    );
	    
	    assertTrue(  AlgebraicFields.haveSameInitialCoefficients(pent, PentagonField.FIELD_NAME) );	    
	    assertTrue(  AlgebraicFields.haveSameInitialCoefficients(snub, PentagonField.FIELD_NAME) );	    
	    assertFalse( AlgebraicFields.haveSameInitialCoefficients(pent, SnubDodecField.FIELD_NAME) );
	    assertFalse( AlgebraicFields.haveSameInitialCoefficients(pent, RootTwoField.FIELD_NAME) );
    }
}
