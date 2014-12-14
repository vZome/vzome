//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.Heptagon6Field;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.algebra.RationalNumbers;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.algebra.SnubDodecField;

import junit.framework.TestCase;

public class AlgebraicNumberTest extends TestCase
{
    public void notestSnubDodecField()
    {
        AlgebraicField field = new SnubDodecField( new PentagonField() );

        int[] tau_xi2_inv =
            {
               -12, 1, 8, 1, 2, 1, -1, 1, 6, 1, -4, 1
            };

        writeNumber( "tau_xi2_inv", tau_xi2_inv, field );
        
        int[] result = field .multiply( SnubDodecField.TAU, SnubDodecField .ONE );
        assertTrue( Arrays .equals( result, SnubDodecField.TAU ) );

        result = field .multiply( SnubDodecField.ONE, SnubDodecField .TAU );
        assertTrue( Arrays .equals( result, SnubDodecField.TAU ) );

        result = field .multiply( SnubDodecField.TAU, SnubDodecField .TAU );
        assertTrue( Arrays .equals( result, field .add( SnubDodecField.TAU, SnubDodecField .ONE ) ) );

        writeNumber( "1/tau", SnubDodecField.TAU_INV, field );

        writeNumber( "tau", SnubDodecField.TAU, field );

        writeNumber( "1/xi", SnubDodecField.XI_INV, field );

        writeNumber( "xi", SnubDodecField.XI, field );

        writeNumber( "alpha", SnubDodecField.ALPHA, field );

        writeNumber( "beta", SnubDodecField.BETA, field );

        result = field.multiply( SnubDodecField.TAU, SnubDodecField.ALPHA );
        writeNumber( "alpha*tau", result, field );

        result = field.multiply( SnubDodecField.TAU_INV, SnubDodecField.ALPHA );
        writeNumber( "alpha/tau", result, field );

        result = field.multiply( SnubDodecField.TAU, SnubDodecField.BETA );
        writeNumber( "beta*tau", result, field );

        result = field.multiply( SnubDodecField.TAU_INV, SnubDodecField.BETA );
        writeNumber( "beta/tau", result, field );

        result = field.multiply( SnubDodecField.XI_INV, SnubDodecField.XI );
        writeNumber( "xi * 1/xi", result, field );
        assertTrue( Arrays .equals( result, SnubDodecField.ONE ) );
        
        result = field.multiply( SnubDodecField.XI_INV, SnubDodecField.TAU_INV );
        writeNumber( "1 / tau*xi", result, field );
        
        result = field.multiply( result, SnubDodecField.XI_INV );
        writeNumber( "1 / tau*xi^2", result, field );
        
        assertTrue( Arrays.equals( result, tau_xi2_inv ) );
        
        result = field.divide( SnubDodecField.XI, SnubDodecField.XI );
        writeNumber( "xi / xi", result, field );
        assertTrue( Arrays .equals( result, SnubDodecField.ONE ) );
        
        int[][] repr = new int[6][12];
        field.createRepresentation( SnubDodecField.XI, 0, repr, 0, 0 );
        int[][] inverse = field.invert( repr );
        result = field.transform( inverse, SnubDodecField.ONE );
        assertTrue( Arrays.equals( SnubDodecField.XI_INV, result ) );

    }

    private void writeNumber( String name, int[] number, AlgebraicField field )
    {
        StringBuffer buf = new StringBuffer( name + " is " );
        field .getNumberExpression( buf, number, 0, AlgebraicField.VEF_FORMAT );
        System.out.println( buf );
        System.out.println();
    }
    
    public void testHeptagonField()
    {
        AlgebraicField field = new HeptagonField();

        int[] one =
            {
                    1, 1, 0, 1, 0, 1
            };
        int[] rho =
            {
                    0, 1, 1, 1, 0, 1
            };
        int[] sigma =
            {
                    0, 1, 0, 1, 1, 1
            };
        int[] sigma_5 =
            {
                    6, 1, 11, 1, 14, 1
            };

        writeNumber( "sigma_5", sigma_5, field );

        assertTrue( Arrays.equals( field.add( rho, sigma ), field.multiply( rho, sigma ) ) );
        assertTrue( Arrays.equals( field.add( one, sigma ), field.multiply( rho, rho ) ) );
        assertTrue( Arrays.equals( field.add( rho, field.add( one, sigma ) ), field.multiply( sigma, sigma ) ) );
        assertTrue( Arrays.equals( field.multiply( sigma, field.multiply( sigma, field.multiply( sigma, field.multiply( sigma,
                sigma ) ) ) ), sigma_5 ) );
    }

    public void testHeptagon6Field()
    {
        AlgebraicField field = new Heptagon6Field();

        int[] one =
            {
                    1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1
            };
        int[] rho =
            {
                    0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1
            };
        int[] sigma =
            {
                    0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1
            };
        int[] sigma_5 =
            {
                    6, 1, 11, 1, 14, 1, 0, 1, 0, 1, 0, 1
            };

        writeNumber( "sigma_5", sigma_5, field );

        assertTrue( Arrays.equals( field.add( rho, sigma ), field.multiply( rho, sigma ) ) );
        assertTrue( Arrays.equals( field.add( one, sigma ), field.multiply( rho, rho ) ) );
        assertTrue( Arrays.equals( field.add( rho, field.add( one, sigma ) ), field.multiply( sigma, sigma ) ) );
        assertTrue( Arrays.equals( field.multiply( sigma, field.multiply( sigma, field.multiply( sigma, field.multiply( sigma,
                sigma ) ) ) ), sigma_5 ) );

        assertTrue( Arrays.equals( Heptagon6Field .MU_2, field .multiply( Heptagon6Field.MU, Heptagon6Field.MU ) ) );
    }

    public void testPentagonField()
    {
        AlgebraicField field = new PentagonField();

        int[] one =
            {
                    1, 1, 0, 1
            };
        int[] tau =
            {
                    0, 1, 1, 1
            };
        int[] tau_5 =
            {
                    3, 1, 5, 1
            };
        int[] tau_9 =
            {
                    21, 1, 34, 1
            };
        int[] tau_minus5 =
            {
                    - 8, 1, 5, 1
            };

        int[] result = field.multiply( tau, tau );
        assertTrue( Arrays.equals( result, field.add( tau, one ) ) );
        result = field.divide( result, tau );
        assertTrue( Arrays.equals( result, tau ) );
        assertTrue( Arrays.equals(
                field.multiply( tau, field.multiply( tau, field.multiply( tau, field.multiply( tau, tau ) ) ) ), tau_5 ) );
        assertTrue( Arrays.equals( field
                .multiply( tau_5, field.multiply( tau, field.multiply( tau, field.multiply( tau, tau ) ) ) ), tau_9 ) );
        assertTrue( Arrays.equals( field.createPower( 5 ), tau_5 ) );
        assertTrue( Arrays.equals( field.createPower( 9 ), tau_9 ) );
        assertTrue( Arrays.equals( field.createPower( - 5 ), tau_minus5 ) );
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

        int[] v1 =
            {
                    0, 1, 1, 1, 1, 1, 0, 1
            };
        int[] v2 =
            {
                    0, 1, - 5, 1, 5, 1, 0, 1
            };

        int[][] dotV1 = new int[2][8];
        field.createRepresentation( v1, 0, dotV1, 0, 0 );
        field.createRepresentation( v1, 2, dotV1, 0, 2 );

        int[] dot = field.transform( dotV1, v2 );
        assertTrue( RationalNumbers.isZero( dot, 0 ) );
    }

    public void testMatrixInverse()
    {
        AlgebraicField field = new PentagonField();

        int[] tau_5 =
            {
                    3, 1, 5, 1
            };
        int[] one =
            {
                    1, 1, 0, 1
            };
        int[] tau_minus5 =
            {
                    - 8, 1, 5, 1
            };

        int[][] repr = new int[2][4];
        field.createRepresentation( tau_5, 0, repr, 0, 0 );
        int[][] inverse = field.invert( repr );
        assertTrue( Arrays.equals( tau_minus5, field.transform( inverse, one ) ) );
    }

    public void testQuaternions3D()
    {
        AlgebraicField field = new PentagonField();

        int[] left3 =
            {
                    1, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1
            }; // same as below,
        // but just
        // imaginary parts
        int[] right3 =
            {
                    0, 1, 0, 1, 5, 1, 0, 1, 1, 1, 0, 1
            };
        int[] left =
            {
                    0, 1, 0, 1, 1, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1
            }; // i +
        // 2j +
        // 3k
        int[] right =
            {
                    0, 1, 0, 1, 0, 1, 0, 1, 5, 1, 0, 1, 1, 1, 0, 1
            }; // 5j
        // + k
        int[] expected =
            {
                    - 13, 1, 0, 1, - 13, 1, 0, 1, - 1, 1, 0, 1, 5, 1, 0, 1
            }; // -13
        // -
        // 13i
        // -j
        // +
        // 5k

        // = (i + 2j + 3k) * 5j + (i + 2j + 3k) * k
        // = 5ij + 10jj + 15kj + ik + 2jk + 3kk
        // = 5k - 10 - 15i - j + 2i - 3
        // = -13 - 13i -j + 5k

        Quaternion q = new Quaternion( field, left3 );
        int[] result = q.rightMultiply( right );
        assertTrue( Arrays.equals( expected, result ) );

        q = new Quaternion( field, right3 );
        result = q.leftMultiply( left );
        assertTrue( Arrays.equals( expected, result ) );
    }

    public void testQuaternions()
    {
        AlgebraicField field = new PentagonField();

        int[] left =
            {
                    1, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1, 1, 1, 0, 1
            }; // 1 +
        // 2i +
        // 3j +
        // k
        int[] right =
            {
                    2, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1
            }; // 2 +
        // j +
        // k
        int[] expected =
            {
                    - 2, 1, 0, 1, 6, 1, 0, 1, 5, 1, 0, 1, 5, 1, 0, 1
            }; // -2
        // +
        // 6i
        // +
        // 5j
        // +
        // 5k

        // = (2 + j + k) + (4i + 2ij + 2ik) + (6j + 3jj + 3jk) + (2k + kj + kk)
        // = 2 + j + k + 4i + 2k - 2j + 6j - 3 + 3i + 2k - i - 1
        // = (2-3-1) + (4+3-1)i + (1-2+6)j + (1+2+2)k
        // = -2 + 6i + 5j + 5k

        Quaternion q = new Quaternion( field, left );
        int[] result = q.rightMultiply( right );
        assertTrue( Arrays.equals( expected, result ) );

        q = new Quaternion( field, right );
        result = q.leftMultiply( left );
        assertTrue( Arrays.equals( expected, result ) );
    }

    public void testRootTwoField()
    {
        AlgebraicField field = new RootTwoField();

        int[] two =
            {
                    2, 1, 0, 1
            };
        int[] sqrt2 =
            {
                    0, 1, 1, 1
            };
        int[] eight =
            {
                    8, 1, 0, 1
            };
        int[] powerNeg5 =
            {
                    0, 1, 1, 8
            };

        assertTrue( Arrays.equals( two, field.multiply( sqrt2, sqrt2 ) ) );
        assertTrue( Arrays.equals( eight, field.multiply( two, field.multiply( sqrt2, field.multiply( sqrt2, two ) ) ) ) );
        assertTrue( Arrays.equals( powerNeg5, field.createPower( - 5 ) ) );
    }

    public void testRootThreeField()
    {
        AlgebraicField field = new RootThreeField();

        int[] three =
            {
                    3, 1, 0, 1
            };
        int[] nine =
            {
                    9, 1, 0, 1
            };
        int[] sqrt3 =
            {
                    0, 1, 1, 1
            };
        int[] powerNeg3 =
            {
                    0, 1, 1, 9
            };
        int[] r1, r2, r3;

        assertTrue( Arrays.equals( three, field.multiply( sqrt3, sqrt3 ) ) );
        r1 = field.multiply( sqrt3, three );
        r2 = field.multiply( sqrt3, r1 );
        assertTrue( Arrays.equals( nine, r2 ) );
        assertTrue( Arrays.equals( powerNeg3, field.createPower( - 3 ) ) );
    }
}
