//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import junit.framework.TestCase;

public class AlgebraicNumberTest extends TestCase
{
    public void testPentagonField()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicNumber one = field .one();
        AlgebraicNumber tau = field .createAlgebraicNumber( new int[]{ 0, 1 } );
        AlgebraicNumber tau_5 = field .createAlgebraicNumber( new int[]{ 3, 5 } );
        AlgebraicNumber tau_9 = field .createAlgebraicNumber( new int[]{ 21, 34 } );
        AlgebraicNumber tau_minus5 = field .createAlgebraicNumber( new int[]{ -8, 5 } );

        AlgebraicNumber result = tau .times( tau );
        assertTrue( result .equals( tau .plus( one ) ) );
        result = result .dividedBy( tau );
        assertTrue( result .equals( tau ) );
        assertTrue( tau_5 .equals( tau .times( tau .times( tau .times( tau .times( tau ) ) ) ) ) );
        assertTrue( tau_9 .equals( tau_5 .times( tau .times( tau .times( tau .times( tau ) ) ) ) ) );
        assertTrue( tau_5 .equals( field.createPower( 5 ) ) );
        assertTrue( tau_9 .equals( field.createPower( 9 ) ) );
        assertTrue( tau_minus5 .equals( field.createPower( -5 ) ) );
    }
    
    public void testToString()
    {
        AlgebraicField field = new PentagonField();
        AlgebraicNumber number = field .createAlgebraicNumber( 22, 15, 6, 0 );
        
        assertEquals( "11/3 +5/2\u03C6", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "11/3 +5/2*phi", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "11/3 5/2", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(5/2,11/3)", number.toString( AlgebraicField.VEF_FORMAT ) );
        
        number = field .createAlgebraicNumber( 0 );

        assertEquals( "0", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "0", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "0 0", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(0,0)", number.toString( AlgebraicField.VEF_FORMAT ) );
        
        number = field .createAlgebraicNumber( 1, 0 );

        assertEquals( "1", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "1", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        
        number = field .createAlgebraicNumber( 0, 1 );

        assertEquals( "\u03C6", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "phi", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        
        field = new HeptagonField();
        number = field .createAlgebraicNumber( new int[]{ 6, 11, 14 } );
        
        assertEquals( "6 +11\u03C1 +14\u03C3", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "6 +11*rho +14*sigma", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "6 11 14", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(14,11,6)", number.toString( AlgebraicField.VEF_FORMAT ) );
        
        field = new SnubDodecField( new PentagonField() );
        number = field .createAlgebraicNumber( new int[]{ -12, 8, 2, -1, 6, -4 } );
        
        assertEquals( "-12 +8\u03C6 +2\u03BE -\u03C6\u03BE +6\u03BE\u00B2 -4\u03C6\u03BE\u00B2", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "-12 +8*phi +2*xi -phi*xi +6*xi^2 -4*phi*xi^2", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
        assertEquals( "-12 8 2 -1 6 -4", number.toString( AlgebraicField.ZOMIC_FORMAT ) );
        assertEquals( "(-4,6,-1,2,8,-12)", number.toString( AlgebraicField.VEF_FORMAT ) );

        number = field .createAlgebraicNumber( 0, 0, 0, 0, 0, 0 );
        
        assertEquals( "0", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "0", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );

        number = field .createAlgebraicNumber( 0, 0, 1, 0, 0, 0 );
        
        assertEquals( "\u03BE", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "xi", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );

        number = field .createAlgebraicNumber( 0, 1, 0, 0, 0, 1 );
        
        assertEquals( "\u03C6 +\u03C6\u03BE\u00B2", number.toString( AlgebraicField.DEFAULT_FORMAT ) );
        assertEquals( "phi +phi*xi^2", number.toString( AlgebraicField.EXPRESSION_FORMAT ) );
}

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

    public void testHeptagonField()
    {
        AlgebraicField field = new HeptagonField();

        AlgebraicNumber rho = field .createAlgebraicNumber( new int[]{ 0, 1, 0 } );
        AlgebraicNumber sigma = field .createAlgebraicNumber( new int[]{ 0, 0, 1 } );
        AlgebraicNumber sigma_5 = field .createAlgebraicNumber( new int[]{ 6, 11, 14 } );

        assertEquals( rho .plus( sigma ), rho .times( sigma ) );
        assertEquals( field .one() .plus( sigma ), rho .times( rho ) );
        assertEquals( field .one() .plus( rho ) .plus( sigma ), sigma .times( sigma ) );
        assertEquals( sigma .times( sigma ) .times( sigma ) .times( sigma ) .times( sigma ), sigma_5 );
    }

    public void testSnubDodecField()
    {
        AlgebraicField field = new SnubDodecField( new PentagonField() );

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

    private void writeNumber( String string, AlgebraicNumber phi_xi2_inv,
            AlgebraicField field )
    {
        // TODO Auto-generated method stub
        
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

    public void testDotProduct()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector v1 = field .createVector( new int[]
            {
                1, 1, 0, 1, 0, 1, 1, 1
            } );
        AlgebraicVector v2 = field .createVector( new int[]
            {
                0, 1, -5, 1, 5, 1, 0, 1
            } );

        AlgebraicNumber dot = v1 .dot( v2 );
        assertTrue( dot .isZero() );
    }
    
    public void testChangeOfBasis()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.createAlgebraicNumber( 3 ), field.one(), field .zero() );
        AlgebraicVector c1 = new AlgebraicVector( field.one(), field.createAlgebraicNumber( 3 ), field .zero() );
        AlgebraicVector c2 = new AlgebraicVector( field.one(), field .one(), field.createAlgebraicNumber( 3 ) );
        AlgebraicMatrix U = new AlgebraicMatrix( c0, c1, c2 );
        
        AlgebraicVector b0 = new AlgebraicVector( field.one(), field.createAlgebraicNumber( -3 ), field .zero() );
        AlgebraicVector b1 = new AlgebraicVector( field.createAlgebraicNumber( -3 ), field.one(), field .zero() );
        AlgebraicVector b2 = new AlgebraicVector( field.createAlgebraicNumber( -2 ), field .createAlgebraicNumber( -2 ), field.createAlgebraicNumber( 2 ) );
        AlgebraicMatrix V = new AlgebraicMatrix( b0, b1, b2 );
        
        AlgebraicMatrix r = V .times( U .inverse() );
                
        assertEquals( r .timesColumn( c0 ), b0 );
        assertEquals( r .timesColumn( c1 ), b1 );
        assertEquals( r .timesColumn( c2 ), b2 );
    }

    public void testMatrixInverse()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector c0 = new AlgebraicVector( field.one(), field.zero(), field .createAlgebraicNumber( 5 ) );
        AlgebraicVector c1 = new AlgebraicVector( field.one(), field.createAlgebraicNumber( 2 ), field .createAlgebraicNumber( 5 ) );
        AlgebraicVector c2 = new AlgebraicVector( field.one(), field.createAlgebraicNumber( 3 ), field .one() );
        AlgebraicMatrix m = new AlgebraicMatrix( c0, c1, c2 );
        AlgebraicVector in = new AlgebraicVector( field.zero(), field.zero(), field .createAlgebraicNumber( 8 ) );
        AlgebraicVector out = new AlgebraicVector( field.createAlgebraicNumber( 8 ), field.createAlgebraicNumber( 24 ), field .createAlgebraicNumber( 8 ) );

        assertEquals( m .timesColumn( in ), out );
        assertEquals( m .transpose() .timesRow( in ), out );
    }

    public void testQuaternions3D()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector left3 = field .createVector( new int[]
            {
                    1, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1
            } ); // same as below, but just imaginary parts
        AlgebraicVector right3 = field .createVector( new int[]
            {
                    0, 1, 0, 1, 5, 1, 0, 1, 1, 1, 0, 1
            } );
        AlgebraicVector left = field .createVector( new int[]
            {
                    0, 1, 0, 1, 1, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1
            } ); // i + 2j + 3k
        AlgebraicVector right = field .createVector( new int[]
            {
                    0, 1, 0, 1, 0, 1, 0, 1, 5, 1, 0, 1, 1, 1, 0, 1
            } ); // 5j + k
        AlgebraicVector expected = field .createVector( new int[]
            {
                    - 13, 1, 0, 1, - 13, 1, 0, 1, - 1, 1, 0, 1, 5, 1, 0, 1
            } ); // -13 - 13i -j + 5k

        // = (i + 2j + 3k) * 5j + (i + 2j + 3k) * k
        // = 5ij + 10jj + 15kj + ik + 2jk + 3kk
        // = 5k - 10 - 15i - j + 2i - 3
        // = -13 - 13i -j + 5k

        Quaternion q = new Quaternion( field, left3 );
        AlgebraicVector result = q.rightMultiply( right );
        assertEquals( expected, result );

        q = new Quaternion( field, right3 );
        result = q.leftMultiply( left );
        assertEquals( expected, result );
    }

    public void testQuaternions()
    {
        AlgebraicField field = new PentagonField();

        AlgebraicVector left = field .createVector( new int[]
            {
                    1, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1, 1, 1, 0, 1
            } ); // 1 + 2i + 3j + k
        AlgebraicVector right = field .createVector( new int[]
            {
                    2, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1
            } ); // 2 + j + k
        AlgebraicVector expected = field .createVector( new int[]
            {
                    - 2, 1, 0, 1, 6, 1, 0, 1, 5, 1, 0, 1, 5, 1, 0, 1
            } ); // -2 + 6i + 5j + 5k

        // = (2 + j + k) + (4i + 2ij + 2ik) + (6j + 3jj + 3jk) + (2k + kj + kk)
        // = 2 + j + k + 4i + 2k - 2j + 6j - 3 + 3i + 2k - i - 1
        // = (2-3-1) + (4+3-1)i + (1-2+6)j + (1+2+2)k
        // = -2 + 6i + 5j + 5k

        Quaternion q = new Quaternion( field, left );
        AlgebraicVector result = q.rightMultiply( right );
        assertEquals( expected, result );

        q = new Quaternion( field, right );
        result = q .leftMultiply( left );
        assertEquals( expected, result );
    }
}
