
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.algebra.RootTwoField;

public class B4Group implements CoxeterGroup
{
    private final AlgebraicField field;
    
    protected final int[] /*AlgebraicVector*/[] ROOTS = new int[4] /*AlgebraicVector*/ [];

    protected final int[] /*AlgebraicVector*/[] WEIGHTS = new int[4] /*AlgebraicVector*/ [];

    private static final int[][] B4_PERMS = new int[][]{
        {0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,2,1}, {0,3,1,2},
        {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,2,0}, {1,3,0,2},
        {2,1,0,3}, {2,1,3,0}, {2,0,1,3}, {2,0,3,1}, {2,3,0,1}, {2,3,1,0},
        {3,1,2,0}, {3,1,0,2}, {3,2,1,0}, {3,2,0,1}, {3,0,2,1}, {3,0,1,2} };

    public B4Group( AlgebraicField field )
    {
        this.field = field;
        int[] /*AlgebraicNumber*/ neg_one = field .createRational( new int[]{ -1, 1 } );
        
        /* in vZome 2.1 source:
         *                                       X      Y       Z      W
        ROOTS[ 0 ] = field .createGoldenVector( one, one.neg(), zero, zero, one );
        ROOTS[ 1 ] = field .createGoldenVector( zero, one, one.neg(), zero, one );
        ROOTS[ 2 ] = field .createGoldenVector( zero, zero, one, one.neg(), one );
        ROOTS[ 3 ] = field .createGoldenVector( zero, zero, zero, one, one );
         */
        ROOTS[ 0 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 0, 1, -1, 0 );
        field .setVectorComponent( ROOTS[ 0 ], RationalVectors.Y4, neg_one );
        ROOTS[ 1 ] = field .basisVector( 4, RationalVectors.Y4 ); // ( 0, 0, 1, -1 );
        field .setVectorComponent( ROOTS[ 1 ], RationalVectors.Z4, neg_one );
        ROOTS[ 2 ] = field .basisVector( 4, RationalVectors.Z4 ); // ( -1, 0, 0, 1 );
        field .setVectorComponent( ROOTS[ 2 ], RationalVectors.W4, neg_one );
        ROOTS[ 3 ] = field .basisVector( 4, RationalVectors.W4 ); // ( 1, 0, 0, 0 );

        /* in vZome 2.1 source:
         *                                         X    Y     Z      W
        WEIGHTS[ 0 ] = field .createGoldenVector( one, zero, zero, zero, one );
        WEIGHTS[ 1 ] = field .createGoldenVector( one, one, zero, zero, one );
        WEIGHTS[ 2 ] = field .createGoldenVector( one, one, one, zero, one );
        WEIGHTS[ 3 ] = field .createGoldenVector( half, half, half, half, one );
         */
        int[] y = field .basisVector( 4, RationalVectors.Y4 );
        int[] z = field .basisVector( 4, RationalVectors.Z4 );
        WEIGHTS[ 0 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 0, 1, 0, 0 );
        WEIGHTS[ 1 ] = field .add( WEIGHTS[ 0 ], y ); // ( 0, 1, 1, 0 );
        WEIGHTS[ 2 ] = field .add( WEIGHTS[ 1 ], z ); // ( 0, 1, 1, 1 );
        WEIGHTS[ 3 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 1/2, 1/2, 1/2, 1/2 );
        int[] /*AlgebraicNumber*/ half = field .createRational( new int[]{ 1, 2 } );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.X4, half );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.Y4, half );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.Z4, half );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.W4, half );
        

        if ( field instanceof RootTwoField ) {
            int[] scale = field .createPower( 1 );
            ROOTS[ 3 ] = field .scaleVector( ROOTS[ 3 ], scale );
            WEIGHTS[ 3 ] = field .scaleVector( WEIGHTS[ 3 ], scale );
        }
    }
    
    public int getOrder()
    {
        return 24*16;
    }

    public int[] /*AlgebraicVector*/ groupAction( int[] /*AlgebraicVector*/ model, int element )
    {
        
        /* from vZome 2.1:
         * 
        IntegralNumber coords[] = new IntegralNumber[ 4 ];
        for ( int c = 0; c < 4; c++ ) {
            IntegralNumber coord = null;
            switch ( B4_PERMS[ perm ][ c ] ) {
            case 0:
                coord = model .getX();
                break;
            case 1:
                coord = model .getY();
                break;
            case 2:
                coord = model .getZ();
                break;
            case 3:
                coord = model .getW();
                break;
            default:
                break;
            }
            if ( signs%2 == 0 )
                coords[ c ] = coord;
            else
                coords[ c ] = coord .neg();
        }
        return model .getFactory() .createGoldenVector( coords[0], coords[1], coords[2], coords[3], model .getFactory() .one() );
         */
        int[] /**/ result = field .basisVector( 4, RationalVectors.X4 );
        int perm = element / 16;
        int signs = element % 16;
        for ( int c = 0; c < 4; c++ )
        {
            int[] source = field .getVectorComponent( model, ( B4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( signs%2 != 0 )
                source = field .negate( source );
            field .setVectorComponent( result, (c+1)%4, source );
            signs = signs >> 1;
        }
        return result;
    }

    public int[] /*AlgebraicVector*/ getOrigin()
    {
        return this.field .origin( 4 );
    }

    public int[] /*AlgebraicVector*/ getWeight( int i )
    {
        return WEIGHTS[ i ];
    }

    public int[] /*AlgebraicVector*/ getSimpleRoot( int i )
    {
        return ROOTS[ i ];
    }

    public AlgebraicField getField()
    {
        return field;
    }

    public int[] chiralSubgroupAction( int[] model, int element )
    {
        int[] /**/ result = field .basisVector( 4, RationalVectors.X4 );
        int perm = element / 16;
        int signs = element % 16;
        boolean even = true;
        for ( int c = 0; c < 4; c++ )
        {
            int[] source = field .getVectorComponent( model, ( B4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( signs%2 != 0 )
            {
                even = !even;
                source = field .negate( source );
            }
            field .setVectorComponent( result, (c+1)%4, source );
            signs = signs >> 1;
        }
        return even? result : null;
    }
}