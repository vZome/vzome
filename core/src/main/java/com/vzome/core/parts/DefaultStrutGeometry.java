
package com.vzome.core.parts;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 *
 */
public class DefaultStrutGeometry implements StrutGeometry
{
    private final AlgebraicVector[] mCorners = new AlgebraicVector[4];
    
    private final Axis mAxis;
    
//    private static <T> Iterable<T>
//    getIterableFromIterator(Iterator<T> iterator)
//    {
//        return new Iterable<T>() {
//            @Override
//            public Iterator<T> iterator()
//            {
//                return iterator;
//            }
//        };
//    }

    public DefaultStrutGeometry( Direction dir )
    {
        AlgebraicField field = dir .getSymmetry() .getField();
        mAxis = dir .getAxis( Symmetry.PLUS, 0 );
        final AlgebraicVector normal = mAxis .normal();
        AlgebraicVector unitNormal = unitVector( field, normal );

        // now find the direction most orthogonal to unitNormal, call it x
        Symmetry symm = dir .getSymmetry();
        AlgebraicVector x = unitVector( field, field .basisVector( 3, AlgebraicVector.Z ) );  // just a default
        double minDot = 99d;
        for ( Direction orbit : symm .getDirections() )
            for ( Axis ortho : orbit ) {
                AlgebraicNumber dot = ortho .normal() .dot( unitNormal );
                double dotVal = Math .abs( dot .evaluate() );
                if ( dotVal < minDot ) {
                    minDot = dotVal;
                    x = unitVector( field, ortho .normal() );
                }
            }
        AlgebraicVector y = AlgebraicVectors.getNormal( x, unitNormal );
        AlgebraicNumber scaleFactor = field .createAlgebraicNumber( 1, 0, 2, -2 );
        x = x .scale( scaleFactor );
        y = y .scale( scaleFactor );
        mCorners[0] = x .plus( y );
        mCorners[1] = y .minus( x );
        mCorners[2] = mCorners[ 0 ] .negate();
        mCorners[3] = mCorners[ 1 ] .negate();
    }
    
    private static AlgebraicVector unitVector( AlgebraicField field, AlgebraicVector vector )
    {
        AlgebraicVector unitNormal = vector;
        AlgebraicNumber fieldLen = vector .dot( vector );
        double length = Math .sqrt( fieldLen .evaluate());
        int len = (int) Math .floor( length );
        if ( len >= 1 )
        {
            AlgebraicNumber fraction = field .createRational( 1, len );
            unitNormal = vector .scale( fraction );
        }
        return unitNormal;
    }

    /* (non-Javadoc)
     * @see com.vzome.core.parts.StrutGeometry#getStrutPolyhedron(com.vzome.core.math.AlgebraicNumber)
     */
    @Override
    public Polyhedron getStrutPolyhedron( AlgebraicNumber length )
    {
        AlgebraicField field = mAxis .getDirection() .getSymmetry() .getField();
        Polyhedron poly = new Polyhedron( field );
        if ( mCorners[0] == null )
            return null;
        //
        //               3 ------<----------2
        //              / \                / \
        //             /   \              /   \
        //            5  -  \  -  -  -   4     \
        //             \     1 ------->-------- 0
        //              \   /              \   /
        //               \ /                \ /
        //                7 ---------------- 6
        //
        AlgebraicVector strutVector = mAxis .normal() .scale( length );
        for ( int i = 0; i < 4; i++ ) {
            poly .addVertex( mCorners[i] );
            poly .addVertex( mCorners[i] .plus( strutVector ) );
        }
        // two loops so normal computation works in addFace

        // strut sides
        for ( int i = 0; i < 4; i++ ) {
            Polyhedron.Face face = poly .newFace();
            int j = i*2 + 1;
            face .add(j);
            --j;
            face .add(j);
            j = (j+2) % 8;
            face .add(j);
            ++j;
            face .add(j);
            poly .addFace( face );
        }
        
        // end caps
//        Polyhedron.Face face = poly .newFace();
//        face .add( new Integer( 1 ) );
//        face .add( new Integer( 3 ) );
//        face .add( new Integer( 5 ) );
//        face .add( new Integer( 7 ) );
//        poly .addFace( face );
//        face = poly .newFace();
//        face .add( new Integer( 6 ) );
//        face .add( new Integer( 4 ) );
//        face .add( new Integer( 2 ) );
//        face .add( new Integer( 0 ) );
//        poly .addFace( face );
        return poly;
    }

}
