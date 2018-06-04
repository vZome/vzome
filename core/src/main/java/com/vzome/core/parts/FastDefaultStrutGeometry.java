
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.parts;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 * 
 * Since every field allows octahedral symmetry, we want the richest triangular
 * tiling of that symmetry, which means we want the fundamental triangles of
 * the reflection group, by definition.  Rather than rely on any symmetry orbits,
 * we can simply hard-code the vertices of a disdyakis dodecahedron, all
 * reflections and permutations of (8,0,0), (4,4,4), and (5,5,0).
 * (This is a simple approximation with integers.)
 * These vertices correspond to blue-like, yellow-like, and green-like axes,
 * in the usual Zome coloring.
 *
 */
public class FastDefaultStrutGeometry implements StrutGeometry
{
    private AlgebraicVector g2_vector, b2_vector, y2_vector;
    
    private final Axis mAxis;
    
    public FastDefaultStrutGeometry( Direction dir )
    {
        mAxis = dir .getAxis( Symmetry.PLUS, 0 );
        AlgebraicVector v = mAxis .normal();
        double x = v .getComponent( AlgebraicVector.X ) .evaluate();
        double y = v .getComponent( AlgebraicVector.Y ) .evaluate();
        double z = v .getComponent( AlgebraicVector.Z ) .evaluate();

        // first, find the octant
        boolean evenParity = true;
        boolean xNeg = x < 0d;
        if ( xNeg ) {
            x = -x;
            evenParity = ! evenParity;
        }
        boolean yNeg = y < 0d;
        if ( yNeg ) {
            y = -y;
            evenParity = ! evenParity;
        }
        boolean zNeg = z < 0d;
        if ( zNeg ) {
            z = -z;
            evenParity = ! evenParity;
        }
        
        // now, find the corner of the octant
        int first, second, third;
        boolean firstNeg, secondNeg, thirdNeg;
        if ( x >= y ) {
            if ( y >= z ) {
                first  = AlgebraicVector.X; firstNeg  = xNeg;
                second = AlgebraicVector.Y; secondNeg = yNeg;
                third  = AlgebraicVector.Z; thirdNeg  = zNeg;
            } else if ( x >= z ) {
                first  = AlgebraicVector.X; firstNeg  = xNeg;
                second = AlgebraicVector.Z; secondNeg = zNeg;
                third  = AlgebraicVector.Y; thirdNeg  = yNeg;
                evenParity = ! evenParity;
            } else {
                first  = AlgebraicVector.Z; firstNeg  = zNeg;
                second = AlgebraicVector.X; secondNeg = xNeg;
                third  = AlgebraicVector.Y; thirdNeg  = yNeg;
            }
        } else {
            if ( x >= z ) {
                first  = AlgebraicVector.Y; firstNeg  = yNeg;
                second = AlgebraicVector.X; secondNeg = xNeg;
                third  = AlgebraicVector.Z; thirdNeg  = zNeg;
                evenParity = ! evenParity;
            } else if ( y >= z ) {
                first  = AlgebraicVector.Y; firstNeg  = yNeg;
                second = AlgebraicVector.Z; secondNeg = zNeg;
                third  = AlgebraicVector.X; thirdNeg  = xNeg;
            } else {
                first  = AlgebraicVector.Z; firstNeg  = zNeg;
                second = AlgebraicVector.Y; secondNeg = yNeg;
                third  = AlgebraicVector.X; thirdNeg  = xNeg;
                evenParity = ! evenParity;
            }
        }
//        System .out .println( "---------------------------------------------" );
//        System .out .printf( "  direction is %s%n", this .mAxis );
//        System .out .printf( "  direction normal is %s%n", v );
//        System .out .printf( "    first, second, third = %d %d %d%n", first, second, third );
//        System .out .printf( "    firstNeg, secondNeg, thirdNeg = %b %b %b%n", firstNeg, secondNeg, thirdNeg );
//        System .out .printf( "    even parity = %b%n", evenParity );
        
        AlgebraicField field = v .getField();
        AlgebraicNumber eight = field .createRational( 8, 10 );
        b2_vector = field .origin( 3 );
        b2_vector .setComponent( first, firstNeg? eight.negate() : eight );
        
        AlgebraicNumber five = field .createRational( 5, 10 );
        g2_vector = field .origin( 3 );
        g2_vector .setComponent( first,  firstNeg?  five.negate() : five );
        g2_vector .setComponent( second, secondNeg? five.negate() : five );
        
        AlgebraicNumber four = field .createRational( 4, 10 );
        y2_vector = field .origin( 3 );
        y2_vector .setComponent( first,  firstNeg?  four.negate() : four );
        y2_vector .setComponent( second, secondNeg? four.negate() : four );
        y2_vector .setComponent( third,  thirdNeg?  four.negate() : four );
        
        if ( ! evenParity ) {
            // swap y2 and g2... any swap would do
            AlgebraicVector swap = y2_vector;
            y2_vector = g2_vector;
            g2_vector = swap;            
        }
    }
    
    /* (non-Javadoc)
     * @see com.vzome.core.parts.StrutGeometry#getStrutPolyhedron(com.vzome.core.math.AlgebraicNumber)
     */
    @Override
    public Polyhedron getStrutPolyhedron( AlgebraicNumber length )
    {
        AlgebraicField field = mAxis .getDirection() .getSymmetry() .getField();
        Polyhedron poly = new Polyhedron( field );
        //
        //   Strut antiprism, seen from one end (b1,g1,y1), the far side of the nearer ball.
        //   This is the even parity orientation.
        //
        //           b1 
        //            |\ 
        //            | \
        //         y2-|--\-g2
        //           \|   \|
        //            |    \
        //            |\   |\
        //           g1------y1
        //               \ |
        //                \|
        //                 b2
        //
        AlgebraicVector strutVector = mAxis .normal() .scale( length );
        AlgebraicVector g1_vector = g2_vector .negate() .plus( strutVector );
        AlgebraicVector r1_vector = b2_vector .negate() .plus( strutVector );
        AlgebraicVector y1_vector = y2_vector .negate() .plus( strutVector );

        poly .addVertex( r1_vector ); Integer b1 = 0;
        poly .addVertex( y1_vector ); Integer y1 = 1;
        poly .addVertex( g1_vector ); Integer g1 = 2;
        poly .addVertex( b2_vector ); Integer b2 = 3;
        poly .addVertex( y2_vector ); Integer y2 = 4;
        poly .addVertex( g2_vector ); Integer g2 = 5;

        Polyhedron.Face face = poly .newFace();
        face .add(b1);
        face .add(y1);
        face .add(g2);
        poly .addFace( face );
        face = poly .newFace();
        face .add(g1);
        face .add(b1);
        face .add(y2);
        poly .addFace( face );
        face = poly .newFace();
        face .add(y1);
        face .add(g1);
        face .add(b2);
        poly .addFace( face );
        face = poly .newFace();
        face .add(b2);
        face .add(g2);
        face .add(y1);
        poly .addFace( face );
        face = poly .newFace();
        face .add(g2);
        face .add(y2);
        face .add(b1);
        poly .addFace( face );
        face = poly .newFace();
        face .add(y2);
        face .add(b2);
        face .add(g1);
        poly .addFace( face );
        // end caps
        face = poly .newFace();
        face .add(b1);
        face .add(g1);
        face .add(y1);
        poly .addFace( face );
        face = poly .newFace();
        face .add(b2);
        face .add(y2);
        face .add(g2);
        poly .addFace( face );
        
        return poly;
    }

}
