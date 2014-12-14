

package com.vzome.core.construction;

import com.vzome.core.algebra.Bivector3dHomogeneous;
import com.vzome.core.algebra.Trivector3dHomogeneous;
import com.vzome.core.algebra.Vector3dHomogeneous;


public class LinePlaneIntersectionPoint extends Point
{
    // parameters
    private final Plane mPlane;
    private final Line mLine;

    public LinePlaneIntersectionPoint( Plane plane, Line line )
    {
        super( line .field );
        mPlane = plane;
        mLine = line;
        mapParamsToState();
    }

    public void attach()
    {
        mPlane .addDerivative( this );
        mLine .addDerivative( this );
    }
    
    public void detach()
    {
        mPlane .removeDerivative( this );
        mLine .removeDerivative( this );
    }

    /**
     * From Vince, GA4CG, p. 196.
     * 
     * @author Scott Vorthmann
     */
    protected boolean mapParamsToState_usingGA()
    {
        if ( mPlane .isImpossible() || mLine .isImpossible() )
            return setStateVariable( null, true );
                
        Trivector3dHomogeneous plane = mPlane .getHomogeneous();
        Bivector3dHomogeneous line = mLine .getHomogeneous();
        
        Vector3dHomogeneous intersection = plane .dual() .dot( line );
        // TODO find out why this does not work as well as the original below
        if ( ! intersection .exists() )
            return setStateVariable( null, true );
        
        return setStateVariable( intersection .getVector(), false );
    }
    
    /**
     * from http://astronomy.swin.edu.au/~pbourke/geometry/planeline/:
     *
     *
     The equation of a plane (points P are on the plane with normal N and point P3 on the plane) can be written as

    N dot (P - P3) = 0

     The equation of the line (points P on the line passing through points P1 and P2) can be written as

    P = P1 + u (P2 - P1)

     The intersection of these two occurs when

    N dot (P1 + u (P2 - P1)) = N dot P3

     Solving for u gives
     
          u = ( N dot (P3-P1) ) / ( N dot (P2-P1) )
          
     If the denominator is zero, the line is parallel to the plane.
     
     * @author Scott Vorthmann
     */
    protected boolean mapParamsToState()
    {
        if ( mPlane .isImpossible() || mLine .isImpossible() )
            return setStateVariable( null, true );
        int[] /*AlgebraicVector*/ p1 = mLine .getStart();
        int[] /*AlgebraicVector*/ p1p2 = mLine .getDirection();
        int[] /*AlgebraicVector*/ n = mPlane .getNormal();
        int[] /*AlgebraicVector*/ p3 = mPlane .getBase();
        
        int[] /*AlgebraicNumber*/ denom = field .dot( n, p1p2 );
        if ( field .isZero( denom ) )
            return setStateVariable( null, true );

        // TODO
        int[] /*AlgebraicVector*/ p1p3 = field .subtract( p3, p1 );
        int[] /*AlgebraicNumber*/ numerator = field .dot( n, p1p3 );
        int[] /*AlgebraicNumber*/ u = field .divide( numerator, denom );
        int[] /*AlgebraicVector*/ p = field .add( p1, field .scaleVector( p1p2, u ) );
        
        return setStateVariable( p, false );
    }

}
