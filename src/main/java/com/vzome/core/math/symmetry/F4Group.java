
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalMatrices;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.algebra.RootTwoField;

public class F4Group extends B4Group
{
    final int[] /*AlgebraicVector*/ [] ROOTS = new int[4] /*AlgebraicVector*/[];

    private final int[] /*AlgebraicVector*/[] WEIGHTS = new int[4] /*AlgebraicVector*/[];
    
    public final int[][] A; // = new GoldenNumberMatrix(
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF, GoldenNumber.HALF, GoldenNumber.HALF.neg() ),
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF, GoldenNumber.HALF.neg(), GoldenNumber.HALF ),
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF.neg(), GoldenNumber.HALF, GoldenNumber.HALF ),
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF.neg(), GoldenNumber.HALF.neg(), GoldenNumber.HALF.neg() ) );

    public F4Group( AlgebraicField field )
    {
        super( field );

        int[] /*AlgebraicNumber*/ one = field .createRational( new int[]{ 1, 1 } );
        int[] /*AlgebraicNumber*/ two = field .createRational( new int[]{ 2, 1 } );
        int[] /*AlgebraicNumber*/ three = field .createRational( new int[]{ 3, 1 } );
        int[] /*AlgebraicNumber*/ four = field .createRational( new int[]{ 4, 1 } );
        int[] /*AlgebraicNumber*/ neg_one = field .createRational( new int[]{ -1, 1 } );
        int[] /*AlgebraicNumber*/ neg_two = field .createRational( new int[]{ -2, 1 } );

        ROOTS[ 0 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 2, -2, 0, 0 );
        field .setVectorComponent( ROOTS[ 0 ], RationalVectors.X4, two );
        field .setVectorComponent( ROOTS[ 0 ], RationalVectors.Y4, neg_two );
        ROOTS[ 1 ] = field .basisVector( 4, RationalVectors.Y4 ); // ( 0, 2, -2, 0 );
        field .setVectorComponent( ROOTS[ 1 ], RationalVectors.Y4, two );
        field .setVectorComponent( ROOTS[ 1 ], RationalVectors.Z4, neg_two );
        ROOTS[ 2 ] = field .basisVector( 4, RationalVectors.Z4 ); // ( 0, 0, 2, 0 );
        field .setVectorComponent( ROOTS[ 2 ], RationalVectors.Z4, two );
        ROOTS[ 3 ] = field .basisVector( 4, RationalVectors.W4 ); // ( -1, -1, -1, 1 );
        field .setVectorComponent( ROOTS[ 3 ], RationalVectors.X4, neg_one );
        field .setVectorComponent( ROOTS[ 3 ], RationalVectors.Y4, neg_one );
        field .setVectorComponent( ROOTS[ 3 ], RationalVectors.Z4, neg_one );

        WEIGHTS[ 0 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 2, 0, 0, 2 );
        field .setVectorComponent( WEIGHTS[ 0 ], RationalVectors.X4, two );
        field .setVectorComponent( WEIGHTS[ 0 ], RationalVectors.W4, two );
        WEIGHTS[ 1 ] = field .basisVector( 4, RationalVectors.Y4 ); // ( 2, 2, 0, 4 );
        field .setVectorComponent( WEIGHTS[ 1 ], RationalVectors.X4, two );
        field .setVectorComponent( WEIGHTS[ 1 ], RationalVectors.Y4, two );
        field .setVectorComponent( WEIGHTS[ 1 ], RationalVectors.W4, four );
        WEIGHTS[ 2 ] = field .basisVector( 4, RationalVectors.X4 ); // ( 1, 1, 1, 3 );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.Y4, one );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.Z4, one );
        field .setVectorComponent( WEIGHTS[ 2 ], RationalVectors.W4, three );
        WEIGHTS[ 3 ] = field .basisVector( 4, RationalVectors.W4 ); // ( 0, 0, 0, 2 );
        field .setVectorComponent( WEIGHTS[ 3 ], RationalVectors.W4, two );        

        if ( field instanceof RootTwoField ) {
            int[] scale = field .createPower( 1 );
            ROOTS[ 2 ] = RationalMatrices .scaleVector( field, ROOTS[ 2 ], scale );
            WEIGHTS[ 2 ] = RationalMatrices .scaleVector( field, WEIGHTS[ 2 ], scale );
        }
        
    
        /*
         *  1/2  1/2  1/2  1/2
         *  1/2  1/2 -1/2 -1/2
         *  1/2 -1/2  1/2 -1/2
         * -1/2  1/2  1/2 -1/2
         */
        int[] /*AlgebraicNumber*/ half = field .createRational( new int[]{ 1, 2 } );
        int[] /*AlgebraicNumber*/ neg_half = field .createRational( new int[]{ -1, 2 } );
        int[] col1 = field .basisVector( 4, RationalVectors.X4 );
        field .setVectorComponent( col1, RationalVectors.X4, half );
        field .setVectorComponent( col1, RationalVectors.Y4, half );
        field .setVectorComponent( col1, RationalVectors.Z4, half );
        field .setVectorComponent( col1, RationalVectors.W4, neg_half );
        int[] col2 = field .basisVector( 4, RationalVectors.X4 );
        field .setVectorComponent( col2, RationalVectors.X4, half );
        field .setVectorComponent( col2, RationalVectors.Y4, half );
        field .setVectorComponent( col2, RationalVectors.Z4, neg_half );
        field .setVectorComponent( col2, RationalVectors.W4, half );
        int[] col3 = field .basisVector( 4, RationalVectors.X4 );
        field .setVectorComponent( col3, RationalVectors.X4, half );
        field .setVectorComponent( col3, RationalVectors.Y4, neg_half );
        field .setVectorComponent( col3, RationalVectors.Z4, half );
        field .setVectorComponent( col3, RationalVectors.W4, half );
        int[] col4 = field .basisVector( 4, RationalVectors.X4 );
        field .setVectorComponent( col4, RationalVectors.X4, half );
        field .setVectorComponent( col4, RationalVectors.Y4, neg_half );
        field .setVectorComponent( col4, RationalVectors.Z4, neg_half );
        field .setVectorComponent( col4, RationalVectors.W4, neg_half );
        A = field .createMatrix( new int[][]{ col1, col2, col3, col4 } );
    }
    
    public int getOrder()
    {
        return 3 * super .getOrder();
    }

    public int[] /*AlgebraicVector*/ groupAction( int[] /*AlgebraicVector*/ model, int element )
    {
        int b4Order = super .getOrder();
        int aPower = element / b4Order;
        int b4Element = element % b4Order;
        AlgebraicField field = getField();

        switch ( aPower ) {

        case 0:
            return super .groupAction( model, b4Element );

        case 1:
            return super .groupAction( field .transform( A, model ), b4Element );

        case 2:
            return super .groupAction( field .transform( A, field .transform( A, model ) ), b4Element );

        default:
            break;
        }
        return null;
    }

    public int[] /*AlgebraicVector*/ getWeight( int i )
    {
        return WEIGHTS[ i ];
    }

    public int[] /*AlgebraicVector*/ getSimpleRoot( int i )
    {
        return ROOTS[ i ];
    }
}