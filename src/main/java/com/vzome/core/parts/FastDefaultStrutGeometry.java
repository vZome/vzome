
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.parts;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.Symmetry.SpecialOrbit;

/**
 * @author Scott Vorthmann
 *
 */
public class FastDefaultStrutGeometry implements StrutGeometry
{
    private final AlgebraicVector b2_vector, r2_vector, y2_vector;
    
    private final Axis mAxis;
    
    public FastDefaultStrutGeometry( Direction dir )
    {
        Symmetry symm = dir .getSymmetry();
        mAxis = dir .getAxis( Symmetry.PLUS, 0 );
        Direction chiral = symm .getSpecialOrbit( SpecialOrbit.BLACK );
        Axis canonical = mAxis;
        if ( chiral != null )
            canonical = chiral .getAxis( mAxis .normal() .toRealVector() );
        int orientation = canonical .getOrientation();
        int sense = canonical .getSense();
        b2_vector = symm .getSpecialOrbit( SpecialOrbit.BLUE ) .getAxis( sense, orientation ) .normal() .scale( symm .getField() .createPower( -2 ) );
        AlgebraicVector v2 = symm .getSpecialOrbit( SpecialOrbit.RED ) .getAxis( sense, orientation ) .normal() .scale( symm .getField() .createPower( -3 ) );
        AlgebraicVector v3 = symm .getSpecialOrbit( SpecialOrbit.YELLOW ) .getAxis( sense, orientation ) .normal() .scale( symm .getField() .createPower( -4 ) );
        
        // just swapping any two of our three vertices will invert everything
        if ( sense == Symmetry.PLUS ) {
            r2_vector = v2;
            y2_vector = v3;
        } else {
            r2_vector = v3;
            y2_vector = v2;
        }
    }
    
    /* (non-Javadoc)
     * @see com.vzome.core.parts.StrutGeometry#getStrutPolyhedron(com.vzome.core.math.AlgebraicNumber)
     */
    public Polyhedron getStrutPolyhedron( AlgebraicNumber length )
    {
        AlgebraicField field = mAxis .getDirection() .getSymmetry() .getField();
        Polyhedron poly = new Polyhedron( field );
        //
        //   Strut antiprism, seen from one end (r1,b1,y1), the far side of the nearer ball.
        //   This is the PLUS orientation.  MINUS uses the mirror image (essentially).
        //
        //           r1 
        //            |\ 
        //            | \
        //         y2-|--\-b2
        //           \|   \|
        //            |    \
        //            |\   |\
        //           b1------y1
        //               \ |
        //                \|
        //                 r2
        //
        AlgebraicVector strutVector = mAxis .normal() .scale( length );
        AlgebraicVector b1_vector = b2_vector .negate() .plus( strutVector );
        AlgebraicVector r1_vector = r2_vector .negate() .plus( strutVector );
        AlgebraicVector y1_vector = y2_vector .negate() .plus( strutVector );

        poly .addVertex( r1_vector ); Integer r1 = 0;
        poly .addVertex( y1_vector ); Integer y1 = 1;
        poly .addVertex( b1_vector ); Integer b1 = 2;
        poly .addVertex( r2_vector ); Integer r2 = 3;
        poly .addVertex( y2_vector ); Integer y2 = 4;
        poly .addVertex( b2_vector ); Integer b2 = 5;

        Polyhedron.Face face = poly .newFace();
        face .add(r1);
        face .add(y1);
        face .add(b2);
        poly .addFace( face );
        face = poly .newFace();
        face .add(b1);
        face .add(r1);
        face .add(y2);
        poly .addFace( face );
        face = poly .newFace();
        face .add(y1);
        face .add(b1);
        face .add(r2);
        poly .addFace( face );
        face = poly .newFace();
        face .add(r2);
        face .add(b2);
        face .add(y1);
        poly .addFace( face );
        face = poly .newFace();
        face .add(b2);
        face .add(y2);
        face .add(r1);
        poly .addFace( face );
        face = poly .newFace();
        face .add(y2);
        face .add(r2);
        face .add(b1);
        poly .addFace( face );
        // end caps
        face = poly .newFace();
        face .add(r1);
        face .add(b1);
        face .add(y1);
        poly .addFace( face );
        face = poly .newFace();
        face .add(r2);
        face .add(y2);
        face .add(b2);
        poly .addFace( face );
        
        return poly;
    }

}
