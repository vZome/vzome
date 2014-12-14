//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RationalMatrices;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;

/**
 * @author Scott Vorthmann
 * 
 */
public class OctahedralSymmetry extends AbstractSymmetry
{
    private final static int ORDER = 24;

    public final Permutation IDENTITY = new Permutation( this, null );
    
    public OctahedralSymmetry( AlgebraicField field, String frameColor, String defaultStyle )
    {
        super( ORDER, field, frameColor, defaultStyle );
        tetrahedralSubgroup = closure( new int[] { 0, 2, 4 } );
    }
    
    protected void createInitialPermutations()
    {
        mOrientations[0] = IDENTITY;
        // first, compute the rotation around the X axis (blue)
        int[] map = new int[ORDER];
        for ( int i = 0; i < 6; i++ )
            for ( int j = 0; j < 4; j++ )
                map[i * 4 + j] = i * 4 + ( ( j + 1 ) % 4 );
        mOrientations[1] = new Permutation( this, map );

        // now, the rotation around the first octant... yellow axis
        map = new int[ORDER];
        int[][] cycles = new int[][] { { 0, 4, 8 }, { 1, 11, 17 }, { 2, 16, 22 }, { 3, 21, 5 }, { 6, 20, 14 }, { 7, 13, 9 },
                { 10, 12, 18 }, { 19, 15, 23 } };
        for ( int i = 0; i < cycles.length; i++ )
            for ( int j = 0; j < cycles[i].length; j++ )
                map[cycles[i][j]] = cycles[i][( j + 1 ) % 3];
        mOrientations[4] = new Permutation( this, map );

        // finally, a rotation around a green axis...
        map = new int[ORDER];
        cycles = new int[][] { { 0, 5 }, { 1, 8 }, { 4, 9 }, { 15, 20 }, { 12, 19 }, { 16, 23 }, { 2, 17 }, { 13, 10 },
                { 21, 6 }, { 22, 3 }, { 7, 14 }, { 11, 18 } };
        for ( int i = 0; i < cycles.length; i++ )
            for ( int j = 0; j < cycles[i].length; j++ )
                map[cycles[i][j]] = cycles[i][( j + 1 ) % 2];
        mOrientations[5] = new Permutation( this, map );
    }

    protected void createOtherOrbits()
    {
        if ( mField instanceof PentagonField )
        {
            createZoneOrbit( "yellow", 0, 4, new int[] { 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 }, true, false, mField
                    .createPower( - 1 ) );

            createZoneOrbit( "green", 1, 8, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1 }, true, true, mField
                    .createRational( new int[] { 2, 1 } ) );

            createZoneOrbit( "lavender", 0, NO_ROTATION, new int[] { 2, 1, - 1, 1, 0, 1, 1, 1, 2, 1, - 1, 1 } );

            createZoneOrbit( "olive", 0, NO_ROTATION, new int[] { 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, - 1, 1 } );

            createZoneOrbit( "maroon", 0, NO_ROTATION, new int[] { - 1, 1, 1, 1, 3, 1, - 1, 1, 1, 1, - 1, 1 } );

            createZoneOrbit( "brown", 0, NO_ROTATION, new int[] { - 1, 1, 1, 1, - 1, 1, 1, 1, - 2, 1, 2, 1 } );

            createZoneOrbit( "red", 0, NO_ROTATION, new int[] { 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1 } );

            createZoneOrbit( "purple", 0, NO_ROTATION, new int[] { 1, 1, 1, 1, 0, 1, 0, 1, - 1, 1, 0, 1 }, false, false, mField
                    .createPower( - 1 ) );

            createZoneOrbit( "black", 0, NO_ROTATION, new int[] { 1, 2, 0, 1, 0, 1, 1, 2, - 1, 2, 1, 2 }, false, false, mField
                    .createRational( new int[] { 2, 1 } ) );

            createZoneOrbit( "turquoise", 0, NO_ROTATION, new int[] { 1, 1, 2, 1, 3, 1, 4, 1, 3, 1, 4, 1 } );
        }
        else if ( mField instanceof RootTwoField )
        {
            createZoneOrbit( "yellow", 0, 4, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1 }, true );

            createZoneOrbit( "green", 1, 8, new int[] { 0, 1, 1, 2, 0, 1, 1, 2, 0, 1, 0, 1 }, true );

            createZoneOrbit( "brown", 0, NO_ROTATION, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 2, 1, 0, 1 }, true );
        }
        else if ( mField instanceof RootThreeField )
        {
            createZoneOrbit( "red", 0, NO_ROTATION, new int[] { 1, 1, 1, 2, 1, 2, 0, 1, 0, 1, 0, 1 }, true );

            createZoneOrbit( "yellow", 0, 4, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1 }, true );

            createZoneOrbit( "green", 1, 8, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1 }, true );

            createZoneOrbit( "brown", 0, NO_ROTATION, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 2, 1, 0, 1 } );
        }
        else
        {
            int[] xAxis = mField.basisVector( 3, RationalVectors.X );
            int[] yAxis = mField.basisVector( 3, RationalVectors.Y );
            int[] zAxis = mField.basisVector( 3, RationalVectors.Z );
            int[] green = mField.add( xAxis, yAxis );
            createZoneOrbit( "green", 1, 8, green, true );
            int[] yellow = mField.add( green, zAxis );
            createZoneOrbit( "yellow", 0, 4, yellow, true );
        }
    }

    protected void createFrameOrbit( String frameColor )
    {
        int[] xAxis = mField.basisVector( 3, RationalVectors.X );
        int[] yAxis = mField.basisVector( 3, RationalVectors.Y );
        int[] zAxis = mField.basisVector( 3, RationalVectors.Z );
        Direction dir;
        if ( mField instanceof PentagonField )
            dir = createZoneOrbit( frameColor, 0, 1, xAxis, true, true, mField .createRational( new int[] { 2, 1 } ) );
        else
            dir = createZoneOrbit( frameColor, 0, 1, xAxis, true );

        createBasisAxes( dir, xAxis, 0 );
        createBasisAxes( dir, mField.negate( xAxis ), 12 );
        createBasisAxes( dir, yAxis, 5 );
        createBasisAxes( dir, mField.negate( yAxis ), 7 );
        createBasisAxes( dir, zAxis, 4 );
        createBasisAxes( dir, mField.negate( zAxis ), 6 );
        for ( int p = 0; p < ORDER; p++ ) {
            int x = mOrientations[p].mapIndex( 0 );
            int y = mOrientations[p].mapIndex( 8 );
            int z = mOrientations[p].mapIndex( 4 );
            mMatrices[p] = mField.createMatrix( new int[][] { dir.getAxis( PLUS, x ).normal(), dir.getAxis( PLUS, y ).normal(),
                    dir.getAxis( PLUS, z ).normal() } );

            Axis axis = dir.getAxis( PLUS, p );
            int[] norm = RationalMatrices.transform( mMatrices[p], xAxis );
            if ( ! Arrays.equals( norm, axis.normal() ) )
                throw new IllegalStateException( "matrix wrong: " + p );
        }
    }

    private void createBasisAxes( Direction dir, int[] norm, int orientation )
    {
        for ( int i = 0; i < 4; i++ ) {
            int prototype = mOrientations[orientation].mapIndex( i );
            int rotatedPrototype = mOrientations[orientation].mapIndex( ( i + 1 ) % 4 );
            int rotation = this.getMapping( prototype, rotatedPrototype );
            dir.createAxis( prototype, rotation, norm );
        }
    }

    public String getName()
    {
        return "octahedral";
    }

    private final int[] tetrahedralSubgroup;

    public int[] subgroup( String name )
    {
        if ( TETRAHEDRAL.equals( name ) )
            return tetrahedralSubgroup;
        return null;
    }
}
