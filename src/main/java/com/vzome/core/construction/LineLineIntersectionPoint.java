

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;


public class LineLineIntersectionPoint extends Point
{
    // parameters
    private final Line line1, line2;

    public LineLineIntersectionPoint( Line line1, Line line2 )
    {
        super( line1 .field );
        this.line1 = line1;
        this.line2 = line2;
        mapParamsToState();
    }

//    /**
//     * from http://www.realtimerendering.com/int/
//     *
//     *
//     Ray/ray: (after Goldman, Graphics Gems; see his article for the derivation)
//     
//     Define each ray by an origin o and a normalized (unit vector) direction d. The two lines are then
//
//L1(t1) = o1 + d1*t1
//L2(t2) = o2 + d2*t2
//
//The solution is:
//t1 = Determinant{(o2-o1),d2,d1 X d2} / ||d1 X d2||^2
//
//and
//
//t2 = Determinant{(o2-o1),d1,d1 X d2} / ||d1 X d2||^2
//
//If the lines are parallel, the denominator ||d1 X d2||^2 is 0.
//
//If the lines do not intersect, t1 and t2 mark the points of closest approach on each line.
//     
//     * @author Scott Vorthmann
//     */
//    protected boolean mapParamsToStateBuggy()
//    {
//        if ( line1 .isImpossible() || line2 .isImpossible() )
//            return setStateVariable( null, true );
//        
//        AlgebraicVector o1 = line1 .getStart();
//        AlgebraicVector d1 = line1 .getDirection();
//        AlgebraicVector o2 = line2 .getStart();
//        AlgebraicVector d2 = line2 .getDirection();
//        AlgebraicVector o2_o1 = o2 .minus( o1 );
//        
//        AlgebraicVector d1xd2 = d1 .cross( d2 );
//        
//        IntegralNumber denom = d1xd2 .dot( d1xd2 );
//        if ( denom .isZero() )
//            return setStateVariable( null, true );
//
//        IntegralNumberField field = (IntegralNumberField) o1 .getFactory();
//        AlgebraicVector w = field .getAxis( GoldenVector .W_AXIS );
//        GoldenMatrix m = field .createGoldenMatrix( o2_o1, d2, d1xd2, w );
//        IntegralNumber t1 = m .determinant() .div( denom );
//        m = field .createGoldenMatrix( o2_o1, d1, d1xd2, w );
//        IntegralNumber t2 = m .determinant() .div( denom );
//
//        AlgebraicVector p1 = o1 .plus( d1 .times( t1 ) );
//        AlgebraicVector p2 = o2 .plus( d2 .times( t2 ) );
//        if ( ! p1 .equals( p2 ) )
//            return setStateVariable( null, true );
//        
//        return setStateVariable( p1, false );
//    }

    //    2ND ATTEMPT, after the one above didn't work (bad results).
    //    This attempt worked most of the time, but is apparently ill-conditioned
    //    when the starting values have large denominators (like 44).
//    /**
//     * from http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline3d/:
//     * 
//       Calculate the line segment PaPb that is the shortest route between
//       two lines P1P2 and P3P4. Calculate also the values of mua and mub where
//          Pa = P1 + mua (P2 - P1)
//          Pb = P3 + mub (P4 - P3)
//       Return FALSE if no solution exists.
//    
//    int LineLineIntersect(
//       XYZ p1,XYZ p2,XYZ p3,XYZ p4,XYZ *pa,XYZ *pb,
//       double *mua, double *mub)
//    {
//       XYZ p13,p43,p21;
//       double d1343,d4321,d1321,d4343,d2121;
//       double numer,denom;
//
//       p13.x = p1.x - p3.x;
//       p13.y = p1.y - p3.y;
//       p13.z = p1.z - p3.z;
//       p43.x = p4.x - p3.x;
//       p43.y = p4.y - p3.y;
//       p43.z = p4.z - p3.z;
//       if (ABS(p43.x)  < EPS && ABS(p43.y)  < EPS && ABS(p43.z)  < EPS)
//          return(FALSE);
//       p21.x = p2.x - p1.x;
//       p21.y = p2.y - p1.y;
//       p21.z = p2.z - p1.z;
//       if (ABS(p21.x)  < EPS && ABS(p21.y)  < EPS && ABS(p21.z)  < EPS)
//          return(FALSE);
//
//       d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
//       d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
//       d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
//       d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
//       d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;
//
//       denom = d2121 * d4343 - d4321 * d4321;
//       if (ABS(denom) < EPS)
//          return(FALSE);
//       numer = d1343 * d4321 - d1321 * d4343;
//
//       *mua = numer / denom;
//       *mub = (d1343 + d4321 * (*mua)) / d4343;
//
//       pa->x = p1.x + *mua * p21.x;
//       pa->y = p1.y + *mua * p21.y;
//       pa->z = p1.z + *mua * p21.z;
//       pb->x = p3.x + *mub * p43.x;
//       pb->y = p3.y + *mub * p43.y;
//       pb->z = p3.z + *mub * p43.z;
//
//       return(TRUE);
//    }
//
//    */
    protected boolean mapParamsToState()
    {
        AlgebraicVector p1 = line1 .getStart();
        AlgebraicVector p21 = line1 .getDirection();
        AlgebraicVector p3 = line2 .getStart();
        AlgebraicVector p43 = line2 .getDirection();
        
        // check the degenerate cases, avoid the expensive and ill-conditioned computation
        if ( p1 .equals( p3 ) )
            return setStateVariable( p1, false );
        AlgebraicVector p2 = p1 .plus( p21 );
        if ( p2 .equals( p3 ) )
            return setStateVariable( p2, false );
        AlgebraicVector p4 = p3 .plus( p43 );
        if ( p1 .equals( p4 ) )
            return setStateVariable( p1, false );
        if ( p2 .equals( p4 ) )
            return setStateVariable( p2, false );
        
        AlgebraicVector p13 = p1 .minus( p3 );

        AlgebraicNumber d1343 = p13 .dot( p43 );
        AlgebraicNumber d4321 = p43 .dot( p21 );
        AlgebraicNumber d1321 = p13 .dot( p21 );
        AlgebraicNumber d4343 = p43 .dot( p43 );
        AlgebraicNumber d2121 = p21 .dot( p21 );
        
        AlgebraicNumber denom = d2121 .times( d4343 ) .minus( d4321 .times( d4321 ) );
        if ( denom .isZero() )
            return setStateVariable( null, true );

        AlgebraicNumber numer = d1343 .times( d4321 ) .minus( d1321 .times( d4343 ) );
        
        AlgebraicNumber mua = numer .dividedBy( denom );
        AlgebraicNumber mub = d1343 .plus( d4321 .times( mua ) ) .dividedBy( d4343 );

        AlgebraicVector pa = p1 .plus( p21 .scale( mua ) );
        AlgebraicVector pb = p3 .plus( p43 .scale( mub ) );

        if ( ! pa .equals( pb ) )
            return setStateVariable( null, true );
        
        return setStateVariable( pb, false );
    }

    // now, a version using Geometric Algebra
//    protected boolean mapParamsToStateGA()
//    {
//        AlgebraicVector p1 = line1 .getStart();
//        AlgebraicVector p21 = line1 .getDirection();
//        AlgebraicVector p3 = line2 .getStart();
//        AlgebraicVector p43 = line2 .getDirection();
//        
//        AlgebraicField field = line1 .getField();
//        BladeListMultivector pt1 = BladeListMultivector .newProjectivePoint3D( p1, field );
//        BladeListMultivector pt2 = BladeListMultivector .newProjectivePoint3D( field .add( p1, p21 ), field );
//        BladeListMultivector pt3 = BladeListMultivector .newProjectivePoint3D( p3, field );
//        BladeListMultivector pt4 = BladeListMultivector .newProjectivePoint3D( field .add( p3, p43 ), field );
//
//        BladeListMultivector bv12 = pt1 .wedge( pt2 );
//        BladeListMultivector bv34 = pt3 .wedge( pt4 );
//        BladeListMultivector meet = bv12 .meet( bv34 );
//        
//        int[] intersection = null;
//        
//        return setStateVariable( intersection, false );
//    }
}
