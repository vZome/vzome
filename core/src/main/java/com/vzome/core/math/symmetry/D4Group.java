
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;

public class D4Group implements CoxeterGroup
{
    private final AlgebraicField field;
    
    protected final AlgebraicVector[] ROOTS = new AlgebraicVector[4];

    protected final AlgebraicVector[] WEIGHTS = new AlgebraicVector[4];

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
        AlgebraicNumber neg_one = field .createRational( -1 );
        
        ROOTS[ 0 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 0, 1, -1, 0 );
        ROOTS[ 0 ] .setComponent( AlgebraicVector.Y4, neg_one );
        ROOTS[ 1 ] = field .basisVector( 4, AlgebraicVector.Y4 ); // ( 0, 0, 1, -1 );
        ROOTS[ 1 ] .setComponent( AlgebraicVector.Z4, neg_one );
        ROOTS[ 2 ] = field .basisVector( 4, AlgebraicVector.Z4 ); // ( -1, 0, 0, 1 );
        ROOTS[ 2 ] .setComponent( AlgebraicVector.W4, neg_one );
        ROOTS[ 3 ] = field .basisVector( 4, AlgebraicVector.Z4 ); // ( 1, 0, 0, 1 );
        ROOTS[ 3 ] .setComponent( AlgebraicVector.W4, field .one() );

        AlgebraicVector y = field .basisVector( 4, AlgebraicVector.Y4 );
        AlgebraicNumber half = field .createRational( 1, 2 );
        AlgebraicNumber neg_half = field .createRational( -1, 2 );
        WEIGHTS[ 0 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 0, 1, 0, 0 );
        WEIGHTS[ 1 ] = WEIGHTS[ 0 ] .plus( y ); // ( 0, 1, 1, 0 );
        WEIGHTS[ 2 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( -1/2, 1/2, 1/2, 1/2 );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.X4, half );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.Y4, half );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.Z4, half );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.W4, neg_half );
        WEIGHTS[ 3 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 1/2, 1/2, 1/2, 1/2 );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.X4, half );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.Y4, half );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.Z4, half );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.W4, half );
    }
    
    @Override
    public int getOrder()
    {
        return 24*8;
    }

    @Override
    public AlgebraicVector groupAction( AlgebraicVector model, int element )
    {
        // all perms, even sign changes
        AlgebraicVector result = field .basisVector( 4, AlgebraicVector.X4 );
        int perm = element / 8;
        int signs = element % 8;  // only three bits!
        boolean even = true;
        for ( int c = 0; c < 4; c++ )
        {
            AlgebraicNumber source = model .getComponent( ( D4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( c == 3 && !even ) // fourth bit is implied, to come out even
            {
                source = source .negate();
            }
            else if ( signs%2 != 0 )
            {
                even = ! even;
                source = source .negate();
            }
            result .setComponent( (c+1)%4, source );
            signs = signs >> 1;
        }
        return result;
    }

    @Override
    public AlgebraicVector getOrigin()
    {
        return this.field .origin( 4 );
    }

    @Override
    public AlgebraicVector getWeight( int i )
    {
        return WEIGHTS[ i ];
    }

    @Override
    public AlgebraicVector getSimpleRoot( int i )
    {
        return ROOTS[ i ];
    }

    @Override
    public AlgebraicField getField()
    {
        return field;
    }

    @Override
    public AlgebraicVector chiralSubgroupAction( AlgebraicVector model, int element )
    {
        // even perms, even sign changes
        AlgebraicVector result = field .basisVector( 4, AlgebraicVector.X4 );
        int perm = element / 8;
        if ( perm >= 12 ) // an odd permutation
            return null;
        int signs = element % 8;  // only three bits!
        boolean even = true;
        for ( int c = 0; c < 4; c++ )
        {
            AlgebraicNumber source = model .getComponent( ( D4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( c == 3 && !even ) // fourth bit is implied, to come out even
            {
                source = source .negate();
            }
            else if ( signs%2 != 0 )
            {
                even = ! even;
                source = source .negate();
            }
            result .setComponent( (c+1)%4, source );
            signs = signs >> 1;
        }
        return result;
    }
}
