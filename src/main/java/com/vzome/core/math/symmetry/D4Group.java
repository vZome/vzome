
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;

public class D4Group implements CoxeterGroup
{
    private final AlgebraicField field;
    
    protected final int[] /*AlgebraicVector*/[] ROOTS = new int[4] /*AlgebraicVector*/ [];

    protected final int[] /*AlgebraicVector*/[] WEIGHTS = new int[4] /*AlgebraicVector*/ [];

    private static final int[][] D4_PERMS = new int[][]
    {
        // evens
        {0,1,2,3},
        {2,3,0,1},
        {1,0,3,2},
        {3,2,1,0},

        {2,1,3,0},
        {3,0,2,1},
        {1,2,0,3},
        {0,3,1,2},

        {0,2,3,1},
        {3,1,0,2},
        {2,0,1,3},
        {1,3,2,0},

        // odds
        {1,0,2,3},
        {2,3,1,0},
        {0,1,3,2},
        {3,2,0,1},

        {0,2,1,3},
        {1,3,0,2},
        {2,0,3,1},
        {3,1,2,0},

        {2,1,0,3},
        {0,3,2,1},
        {1,2,3,0},
        {3,0,1,2},
    };

    public D4Group( AlgebraicField field )
    {
        this.field = field;
        int[] /*AlgebraicNumber*/ neg_one = field .createRational( new int[]{ -1, 1 } );
        
        ROOTS[ 0 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 0, 1, -1, 0 );
        field .setVectorComponent( ROOTS[ 0 ], RationalVectors.Y4, neg_one );
        ROOTS[ 1 ] = field .basisVector( 4, RationalVectors.Y4 ); // ( 0, 0, 1, -1 );
        field .setVectorComponent( ROOTS[ 1 ], RationalVectors.Z4, neg_one );
        ROOTS[ 2 ] = field .basisVector( 4, RationalVectors.Z4 ); // ( -1, 0, 0, 1 );
        field .setVectorComponent( ROOTS[ 2 ], RationalVectors.W4, neg_one );
        ROOTS[ 3 ] = field .basisVector( 4, RationalVectors.Z4 ); // ( 1, 0, 0, 1 );
        field .setVectorComponent( ROOTS[ 3 ], RationalVectors.W4, field .createPower( 0 ) );

        int[] y = field .basisVector( 4, RationalVectors.Y4 );
        int[] /*AlgebraicNumber*/ half = field .createRational( new int[]{ 1, 2 } );
        int[] /*AlgebraicNumber*/ neg_half = field .createRational( new int[]{ -1, 2 } );
        WEIGHTS[ 0 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 0, 1, 0, 0 );
        WEIGHTS[ 1 ] = field .add( WEIGHTS[ 0 ], y ); // ( 0, 1, 1, 0 );
        WEIGHTS[ 2 ] = field .basisVector( 4, RationalVectors.X4 ); // ( -1/2, 1/2, 1/2, 1/2 );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.X4, half );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.Y4, half );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.Z4, half );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.W4, neg_half );
        WEIGHTS[ 3 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 1/2, 1/2, 1/2, 1/2 );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.X4, half );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.Y4, half );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.Z4, half );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.W4, half );
    }
    
    public int getOrder()
    {
        return 24*8;
    }

    public int[] /*AlgebraicVector*/ groupAction( int[] /*AlgebraicVector*/ model, int element )
    {
        // all perms, even sign changes
        int[] /**/ result = field .basisVector( 4, RationalVectors.X4 );
        int perm = element / 8;
        int signs = element % 8;  // only three bits!
        boolean even = true;
        for ( int c = 0; c < 4; c++ )
        {
            int[] source = field .getVectorComponent( model, ( D4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( c == 3 && !even ) // fourth bit is implied, to come out even
            {
                source = field .negate( source );
            }
            else if ( signs%2 != 0 )
            {
                even = ! even;
                source = field .negate( source );
            }
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
        // even perms, even sign changes
        int[] /**/ result = field .basisVector( 4, RationalVectors.X4 );
        int perm = element / 8;
        if ( perm >= 12 ) // an odd permutation
            return null;
        int signs = element % 8;  // only three bits!
        boolean even = true;
        for ( int c = 0; c < 4; c++ )
        {
            int[] source = field .getVectorComponent( model, ( D4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( c == 3 && !even ) // fourth bit is implied, to come out even
            {
                source = field .negate( source );
            }
            else if ( signs%2 != 0 )
            {
                even = ! even;
                source = field .negate( source );
            }
            field .setVectorComponent( result, (c+1)%4, source );
            signs = signs >> 1;
        }
        return result;
    }
}