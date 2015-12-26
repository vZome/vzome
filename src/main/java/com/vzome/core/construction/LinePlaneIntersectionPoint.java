

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
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
        AlgebraicVector p1 = mLine .getStart();
        AlgebraicVector p1p2 = mLine .getDirection();
        AlgebraicVector n = mPlane .getNormal();
        AlgebraicVector p3 = mPlane .getBase();
        
        AlgebraicNumber denom = n .dot( p1p2 );
        if ( denom .isZero() )
            return setStateVariable( null, true );

        // TODO
        AlgebraicVector p1p3 = p3 .minus( p1 );
        AlgebraicNumber numerator = n .dot( p1p3 );
        AlgebraicNumber u = numerator .dividedBy( denom );
        AlgebraicVector p = p1 .plus( p1p2 .scale( u ) );
        
        return setStateVariable( p, false );
    }

}
