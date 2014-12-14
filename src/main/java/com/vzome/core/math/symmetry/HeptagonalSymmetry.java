//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.Heptagon6Field;
import com.vzome.core.algebra.RationalVectors;

/**
 * @author Scott Vorthmann
 * 
 */
public class HeptagonalSymmetry extends AbstractSymmetry
{
    private final static int ORDER = 7;

    public final Permutation IDENTITY = new Permutation( this, null );

    public HeptagonalSymmetry( AlgebraicField field, String defaultStyle )
    {
        super( ORDER, field, "blue", defaultStyle );
    }

    protected void createInitialPermutations()
    {
        mOrientations[0] = IDENTITY;
        // compute the rotation around the Z axis
        int[] map = new int[ORDER];
        for ( int i = 0; i < ORDER; i++ )
            map[i] = ( i + 1 ) % ORDER;
        mOrientations[1] = new Permutation( this, map );
    }

    protected void createFrameOrbit( String frameColor )
    {
        int[] xAxis = new int[]
            {
                2, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
                0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
                0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1
            };
        Direction dir = new Direction( frameColor, this, 0, NO_ROTATION, xAxis, true );
        mDirectionMap.put( dir.getName(), dir );
        mDirectionList.add( dir );
        orbitSet.add( dir );

        dir.createAxis( 0, NO_ROTATION, xAxis );

        int[] two = mField .createRational( new int[]{ 2,1 } );
        int[] x1 = mField .subtract( Heptagon6Field.ONE, Heptagon6Field.SIGMA );
        int[] x2 = mField .subtract( Heptagon6Field.RHO, Heptagon6Field.SIGMA );
        int[] x3 = mField .negate( Heptagon6Field.RHO );
        int[] twoMu = mField .multiply( two, Heptagon6Field.MU );
        int[] twoMuRho = mField .multiply( twoMu, Heptagon6Field.RHO );
        int[] twoMuSigma = mField .multiply( twoMu, Heptagon6Field.SIGMA );
        
        int[] omega1 = mField .origin( 3 );
        mField .setVectorComponent( omega1, RationalVectors.X, x1 );
        mField .setVectorComponent( omega1, RationalVectors.Y, twoMuRho );
        dir.createAxis( 1, NO_ROTATION, omega1 );

        int[] omega2 = mField .origin( 3 );
        mField .setVectorComponent( omega1, RationalVectors.X, x2 );
        mField .setVectorComponent( omega1, RationalVectors.Y, twoMuSigma );
        dir.createAxis( 2, NO_ROTATION, omega2 );

        int[] omega3 = mField .origin( 3 );
        mField .setVectorComponent( omega1, RationalVectors.X, x3 );
        mField .setVectorComponent( omega1, RationalVectors.Y, twoMu );
        dir.createAxis( 3, NO_ROTATION, omega3 );

        int[] omega4 = mField .origin( 3 );
        mField .setVectorComponent( omega1, RationalVectors.X, x3 );
        mField .setVectorComponent( omega1, RationalVectors.Y, mField .negate( twoMu ) );
        dir.createAxis( 4, NO_ROTATION, omega4 );

        int[] omega5 = mField .origin( 3 );
        mField .setVectorComponent( omega1, RationalVectors.X, x2 );
        mField .setVectorComponent( omega1, RationalVectors.Y, mField .negate( twoMuSigma ) );
        dir.createAxis( 5, NO_ROTATION, omega5 );

        int[] omega6 = mField .origin( 3 );
        mField .setVectorComponent( omega1, RationalVectors.X, x1 );
        mField .setVectorComponent( omega1, RationalVectors.Y, mField .negate( twoMuRho ) );
        dir.createAxis( 6, NO_ROTATION, omega6 );

        // TODO put in the mu-based rotation matrices
        
        int[] zAxis = new int[] { 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1 };
        for ( int p = 0; p < ORDER; p++ ) {
            int x = mOrientations[p].mapIndex( 0 );
            int y = mOrientations[p].mapIndex( 3 );
            mMatrices[p] = mField.createMatrix( new int[][]
                {
                        dir.getAxis( PLUS, x ).normal(), dir.getAxis( PLUS, y ).normal(), zAxis
                } );

            // now check the result
            Axis axis = dir.getAxis( PLUS, p );
            int[] norm = mField.transform( mMatrices[p], xAxis );
            // I don't know why this has to be left-multiplication
            // here, but not for OctahedralSymmetry
            if ( ! Arrays.equals( norm, axis.normal() ) )
                throw new IllegalStateException( "matrix wrong: " + p );
        }

//        createZoneOrbit( "green", 0, NO_ROTATION, new int[] { 1, 1, 1, 2, 1, 2, 0, 1, 0, 1, 0, 1 }, true );
    }
    
    protected void createOtherOrbits(){}

    public String getName()
    {
        return "heptagonal";
    }

    public int[] subgroup( String name )
    {
        return null;
    }
}
