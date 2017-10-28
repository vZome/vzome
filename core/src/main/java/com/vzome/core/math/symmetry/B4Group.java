
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.RootTwoField;

public class B4Group implements CoxeterGroup
{
    private final AlgebraicField field;
    
    protected final AlgebraicVector[] ROOTS = new AlgebraicVector[4];

    protected final AlgebraicVector[] WEIGHTS = new AlgebraicVector[4];

    private static final int[][] B4_PERMS = new int[][]{
        {0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,2,1}, {0,3,1,2},
        {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,2,0}, {1,3,0,2},
        {2,1,0,3}, {2,1,3,0}, {2,0,1,3}, {2,0,3,1}, {2,3,0,1}, {2,3,1,0},
        {3,1,2,0}, {3,1,0,2}, {3,2,1,0}, {3,2,0,1}, {3,0,2,1}, {3,0,1,2} };

    public B4Group( AlgebraicField field )
    {
        this.field = field;
        AlgebraicNumber neg_one = field .createRational( -1 );
        
        /* in vZome 2.1 source:
         *                                       X      Y       Z      W
        ROOTS[ 0 ] = field .createGoldenVector( one, one.neg(), zero, zero, one );
        ROOTS[ 1 ] = field .createGoldenVector( zero, one, one.neg(), zero, one );
        ROOTS[ 2 ] = field .createGoldenVector( zero, zero, one, one.neg(), one );
        ROOTS[ 3 ] = field .createGoldenVector( zero, zero, zero, one, one );
         */
        ROOTS[ 0 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 0, 1, -1, 0 );
        ROOTS[ 0 ] .setComponent( AlgebraicVector.Y4, neg_one );
        ROOTS[ 1 ] = field .basisVector( 4, AlgebraicVector.Y4 ); // ( 0, 0, 1, -1 );
        ROOTS[ 1 ] .setComponent( AlgebraicVector.Z4, neg_one );
        ROOTS[ 2 ] = field .basisVector( 4, AlgebraicVector.Z4 ); // ( -1, 0, 0, 1 );
        ROOTS[ 2 ] .setComponent( AlgebraicVector.W4, neg_one );
        ROOTS[ 3 ] = field .basisVector( 4, AlgebraicVector.W4 ); // ( 1, 0, 0, 0 );

        /* in vZome 2.1 source:
         *                                         X    Y     Z      W
        WEIGHTS[ 0 ] = field .createGoldenVector( one, zero, zero, zero, one );
        WEIGHTS[ 1 ] = field .createGoldenVector( one, one, zero, zero, one );
        WEIGHTS[ 2 ] = field .createGoldenVector( one, one, one, zero, one );
        WEIGHTS[ 3 ] = field .createGoldenVector( half, half, half, half, one );
         */
        AlgebraicVector y = field .basisVector( 4, AlgebraicVector.Y4 );
        AlgebraicVector z = field .basisVector( 4, AlgebraicVector.Z4 );
        WEIGHTS[ 0 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 0, 1, 0, 0 );
        WEIGHTS[ 1 ] = WEIGHTS[ 0 ] .plus( y ); // ( 0, 1, 1, 0 );
        WEIGHTS[ 2 ] = WEIGHTS[ 1 ] .plus( z ); // ( 0, 1, 1, 1 );
        WEIGHTS[ 3 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 1/2, 1/2, 1/2, 1/2 );
        AlgebraicNumber half = field .createRational( 1, 2 );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.X4, half );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.Y4, half );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.Z4, half );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.W4, half );
        

        if ( field instanceof RootTwoField ) {
            AlgebraicNumber scale = field .createPower( 1 );
            ROOTS[ 3 ] = ROOTS[ 3 ] .scale( scale );
            WEIGHTS[ 3 ] = WEIGHTS[ 3 ] .scale( scale );
        }
    }
    
    @Override
    public int getOrder()
    {
        return 24*16;
    }

    @Override
    public AlgebraicVector groupAction( AlgebraicVector model, int element )
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
        AlgebraicVector result = field .basisVector( 4, AlgebraicVector.X4 );
        int perm = element / 16;
        int signs = element % 16;
        for ( int c = 0; c < 4; c++ )
        {
            AlgebraicNumber source = model .getComponent( ( B4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( signs%2 != 0 )
                source = source .negate();
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
        AlgebraicVector result = field .basisVector( 4, AlgebraicVector.X4 );
        int perm = element / 16;
        int signs = element % 16;
        boolean even = true;
        for ( int c = 0; c < 4; c++ )
        {
            AlgebraicNumber source = model .getComponent( ( B4_PERMS[ perm ][ c ] + 1 ) % 4 );
            if ( signs%2 != 0 )
            {
                even = !even;
                source = source .negate();
            }
            result .setComponent( (c+1)%4, source );
            signs = signs >> 1;
        }
        return even? result : null;
    }
}
