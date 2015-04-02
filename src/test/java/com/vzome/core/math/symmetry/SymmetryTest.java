
//(c) Copyright 2015, Scott Vorthmann.

package com.vzome.core.math.symmetry;

import java.util.Arrays;

import com.vzome.core.algebra.PentagonField;

import junit.framework.TestCase;

public class SymmetryTest extends TestCase
{
    public void testAxisIncidence()
    {
        IcosahedralSymmetry symm = new IcosahedralSymmetry( new PentagonField(), null );
        
        assertTrue( Arrays.equals( new int[]{ 22, 50, 19 }, symm .getIncidentOrientations( 10 ) ) );
        assertTrue( Arrays.equals( new int[]{ 20, 52, 17 }, symm .getIncidentOrientations( 44 ) ) );
    }
    
    public void testAxisCorrection()
    {
        IcosahedralSymmetry symm = new IcosahedralSymmetry( new PentagonField(), null );

        Direction orbit = symm .getDirection( "cinnamon" );
        Axis correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 16 );
        assertEquals( 2, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 41 );
        assertEquals( 50, correctedAxis .getOrientation() );
        assertEquals( Symmetry.MINUS, correctedAxis.getSense() );
        
        orbit = symm .getDirection( "sand" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 16 );
        assertEquals( 2, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 41 );
        assertEquals( 50, correctedAxis .getOrientation() );
        assertEquals( Symmetry.MINUS, correctedAxis.getSense() );
        
        orbit = symm .getDirection( "black" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 16 );
        assertEquals( 16, correctedAxis .getOrientation() );
        assertEquals( Symmetry.MINUS, correctedAxis.getSense() );
        
        orbit = symm .getDirection( "brown" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 43 );
        assertEquals( 43, correctedAxis .getOrientation() );
        assertEquals( Symmetry.MINUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "turquoise" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 7 );
        assertEquals( 25, correctedAxis .getOrientation() );
        assertEquals( Symmetry.MINUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "coral" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 8 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "navy" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 6 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 41 );
        assertEquals( 35, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "olive" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 9 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 34 );
        assertEquals( 40, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "rose" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 46 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 35 );
        assertEquals( 57, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "maroon" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 49 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 48 );
        assertEquals( 32, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "lavender" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 13 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 55 );
        assertEquals( 50, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "apple" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 22 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 2 );
        assertEquals( 28, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );

        orbit = symm .getDirection( "spruce" );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.MINUS, 47 );
        assertEquals( 0, correctedAxis .getOrientation() );
        assertEquals( Symmetry.PLUS, correctedAxis.getSense() );
        correctedAxis = orbit .getCanonicalAxis( Symmetry.PLUS, 7 );
        assertEquals( 39, correctedAxis .getOrientation() );
        assertEquals( Symmetry.MINUS, correctedAxis.getSense() );
    }
}
