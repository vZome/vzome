
package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;

public class F4Group extends B4Group
{
    final AlgebraicVector [] ROOTS = new AlgebraicVector[4];

    private final AlgebraicVector[] WEIGHTS = new AlgebraicVector[4];
    
    public final AlgebraicMatrix A; // = new GoldenNumberMatrix(
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF, GoldenNumber.HALF, GoldenNumber.HALF.neg() ),
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF, GoldenNumber.HALF.neg(), GoldenNumber.HALF ),
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF.neg(), GoldenNumber.HALF, GoldenNumber.HALF ),
//                new GoldenNumberVector( GoldenNumber.HALF, GoldenNumber.HALF.neg(), GoldenNumber.HALF.neg(), GoldenNumber.HALF.neg() ) );

    public F4Group( AlgebraicField field )
    {
        super( field );

        AlgebraicNumber one     = field .createRational( 1 );
        AlgebraicNumber two     = field .createRational( 2 );
        AlgebraicNumber three   = field .createRational( 3 );
        AlgebraicNumber four    = field .createRational( 4 );
        AlgebraicNumber neg_one = field .createRational( -1 );
        AlgebraicNumber neg_two = field .createRational( -2 );

        ROOTS[ 0 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 2, -2, 0, 0 );
        ROOTS[ 0 ] .setComponent( AlgebraicVector.X4, two );
        ROOTS[ 0 ] .setComponent( AlgebraicVector.Y4, neg_two );
        ROOTS[ 1 ] = field .basisVector( 4, AlgebraicVector.Y4 ); // ( 0, 2, -2, 0 );
        ROOTS[ 1 ] .setComponent( AlgebraicVector.Y4, two );
        ROOTS[ 1 ] .setComponent( AlgebraicVector.Z4, neg_two );
        ROOTS[ 2 ] = field .basisVector( 4, AlgebraicVector.Z4 ); // ( 0, 0, 2, 0 );
        ROOTS[ 2 ] .setComponent( AlgebraicVector.Z4, two );
        ROOTS[ 3 ] = field .basisVector( 4, AlgebraicVector.W4 ); // ( -1, -1, -1, 1 );
        ROOTS[ 3 ] .setComponent( AlgebraicVector.X4, neg_one );
        ROOTS[ 3 ] .setComponent( AlgebraicVector.Y4, neg_one );
        ROOTS[ 3 ] .setComponent( AlgebraicVector.Z4, neg_one );

        WEIGHTS[ 0 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 2, 0, 0, 2 );
        WEIGHTS[ 0 ] .setComponent( AlgebraicVector.X4, two );
        WEIGHTS[ 0 ] .setComponent( AlgebraicVector.W4, two );
        WEIGHTS[ 1 ] = field .basisVector( 4, AlgebraicVector.Y4 ); // ( 2, 2, 0, 4 );
        WEIGHTS[ 1 ] .setComponent( AlgebraicVector.X4, two );
        WEIGHTS[ 1 ] .setComponent( AlgebraicVector.Y4, two );
        WEIGHTS[ 1 ] .setComponent( AlgebraicVector.W4, four );
        WEIGHTS[ 2 ] = field .basisVector( 4, AlgebraicVector.X4 ); // ( 1, 1, 1, 3 );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.Y4, one );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.Z4, one );
        WEIGHTS[ 2 ] .setComponent( AlgebraicVector.W4, three );
        WEIGHTS[ 3 ] = field .basisVector( 4, AlgebraicVector.W4 ); // ( 0, 0, 0, 2 );
        WEIGHTS[ 3 ] .setComponent( AlgebraicVector.W4, two );        

        if ( field .scale4dRoots() ) {
            AlgebraicNumber scale = field .createPower( 1 );
            ROOTS[ 2 ] = ROOTS[ 2 ] .scale( scale );
            WEIGHTS[ 2 ] = WEIGHTS[ 2 ] .scale( scale );
        }
        
    
        /*
         *  1/2  1/2  1/2  1/2
         *  1/2  1/2 -1/2 -1/2
         *  1/2 -1/2  1/2 -1/2
         * -1/2  1/2  1/2 -1/2
         */
        AlgebraicNumber half = field .createRational( 1, 2 );
        AlgebraicNumber neg_half = field .createRational( -1, 2 );
        AlgebraicVector col1 = field .basisVector( 4, AlgebraicVector.X4 );
        col1 .setComponent( AlgebraicVector.X4, half );
        col1 .setComponent( AlgebraicVector.Y4, half );
        col1 .setComponent( AlgebraicVector.Z4, half );
        col1 .setComponent( AlgebraicVector.W4, neg_half );
        AlgebraicVector col2 = field .basisVector( 4, AlgebraicVector.X4 );
        col2 .setComponent( AlgebraicVector.X4, half );
        col2 .setComponent( AlgebraicVector.Y4, half );
        col2 .setComponent( AlgebraicVector.Z4, neg_half );
        col2 .setComponent( AlgebraicVector.W4, half );
        AlgebraicVector col3 = field .basisVector( 4, AlgebraicVector.X4 );
        col3 .setComponent( AlgebraicVector.X4, half );
        col3 .setComponent( AlgebraicVector.Y4, neg_half );
        col3 .setComponent( AlgebraicVector.Z4, half );
        col3 .setComponent( AlgebraicVector.W4, half );
        AlgebraicVector col4 = field .basisVector( 4, AlgebraicVector.X4 );
        col4 .setComponent( AlgebraicVector.X4, half );
        col4 .setComponent( AlgebraicVector.Y4, neg_half );
        col4 .setComponent( AlgebraicVector.Z4, neg_half );
        col4 .setComponent( AlgebraicVector.W4, neg_half );
        A = new AlgebraicMatrix( col1, col2, col3, col4 );
    }
    
    @Override
    public int getOrder()
    {
        return 3 * super .getOrder();
    }

    @Override
    public AlgebraicVector groupAction( AlgebraicVector model, int element )
    {
        int b4Order = super .getOrder();
        int aPower = element / b4Order;
        int b4Element = element % b4Order;
        
        switch ( aPower ) {

        case 0:
            return super .groupAction( model, b4Element );

        case 1:
            return super .groupAction( A .timesColumn( model ), b4Element );

        case 2:
            return super .groupAction( A .timesColumn( A .timesColumn( model ) ), b4Element );

        default:
            break;
        }
        return null;
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
}
