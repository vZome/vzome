

package com.vzome.core.construction;

import java.util.Arrays;


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

    public void attach()
    {
        line1 .addDerivative( this );
        line2 .addDerivative( this );
    }
    
    public void detach()
    {
        line1 .removeDerivative( this );
        line2 .removeDerivative( this );
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
//        int[] /*AlgebraicVector*/ o1 = line1 .getStart();
//        int[] /*AlgebraicVector*/ d1 = line1 .getDirection();
//        int[] /*AlgebraicVector*/ o2 = line2 .getStart();
//        int[] /*AlgebraicVector*/ d2 = line2 .getDirection();
//        int[] /*AlgebraicVector*/ o2_o1 = o2 .minus( o1 );
//        
//        int[] /*AlgebraicVector*/ d1xd2 = d1 .cross( d2 );
//        
//        IntegralNumber denom = d1xd2 .dot( d1xd2 );
//        if ( denom .isZero() )
//            return setStateVariable( null, true );
//
//        IntegralNumberField field = (IntegralNumberField) o1 .getFactory();
//        int[] /*AlgebraicVector*/ w = field .getAxis( GoldenVector .W_AXIS );
//        GoldenMatrix m = field .createGoldenMatrix( o2_o1, d2, d1xd2, w );
//        IntegralNumber t1 = m .determinant() .div( denom );
//        m = field .createGoldenMatrix( o2_o1, d1, d1xd2, w );
//        IntegralNumber t2 = m .determinant() .div( denom );
//
//        int[] /*AlgebraicVector*/ p1 = o1 .plus( d1 .times( t1 ) );
//        int[] /*AlgebraicVector*/ p2 = o2 .plus( d2 .times( t2 ) );
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
        int[] /*AlgebraicVector*/ p1 = line1 .getStart();
        int[] /*AlgebraicVector*/ p21 = line1 .getDirection();
        int[] /*AlgebraicVector*/ p3 = line2 .getStart();
        int[] /*AlgebraicVector*/ p43 = line2 .getDirection();
        
        // check the degenerate cases, avoid the expensive and ill-conditioned computation
        if ( Arrays .equals( p1, p3 ) )
            return setStateVariable( p1, false );
        int[] p2 = field .add( p1, p21 );
        if ( Arrays .equals( p2, p3 ) )
            return setStateVariable( p2, false );
        int[] p4 = field .add( p3, p43 );
        if ( Arrays .equals( p1, p4 ) )
            return setStateVariable( p1, false );
        if ( Arrays .equals( p2, p4 ) )
            return setStateVariable( p2, false );
        
        int[] /*AlgebraicVector*/ p13 = field .subtract( p1, p3 );

        int[] /*AlgebraicNumber*/ d1343 = field .dot( p13, p43 );
        int[] /*AlgebraicNumber*/ d4321 = field .dot( p43, p21 );
        int[] /*AlgebraicNumber*/ d1321 = field .dot( p13, p21 );
        int[] /*AlgebraicNumber*/ d4343 = field .dot( p43, p43 );
        int[] /*AlgebraicNumber*/ d2121 = field .dot( p21, p21 );
        
        int[] /*AlgebraicNumber*/ denom = field .subtract( field .multiply( d2121, d4343 ), field .multiply( d4321, d4321 ) );
        if ( field .isZero( denom ) )
            return setStateVariable( null, true );

        int[] /*AlgebraicNumber*/ numer = field .subtract( field .multiply( d1343, d4321 ), field .multiply( d1321, d4343 ) );
        
        int[] /*AlgebraicNumber*/ mua = field .divide( numer, denom );
        int[] /*AlgebraicNumber*/ mub = field .divide( field .add( d1343, field .multiply( d4321, mua ) ), d4343 );

        int[] /*AlgebraicVector*/ pa = field .add( p1, field .scaleVector( p21, mua ) );
        int[] /*AlgebraicVector*/ pb = field .add( p3, field .scaleVector( p43, mub ) );

        if ( ! Arrays .equals( pa, pb ) )
            return setStateVariable( null, true );
        
        return setStateVariable( pb, false );
    }

    // now, a version using Geometric Algebra
//    protected boolean mapParamsToStateGA()
//    {
//        int[] /*AlgebraicVector*/ p1 = line1 .getStart();
//        int[] /*AlgebraicVector*/ p21 = line1 .getDirection();
//        int[] /*AlgebraicVector*/ p3 = line2 .getStart();
//        int[] /*AlgebraicVector*/ p43 = line2 .getDirection();
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
