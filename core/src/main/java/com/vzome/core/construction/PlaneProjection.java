

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class PlaneProjection extends Transformation
{
    // parameters
    private final Plane projectionPlane;
    private final AlgebraicVector projectionVector;

    /**
     * @param prototype
     */
    public PlaneProjection( Plane projectionPlane, Line projectionLine )
    {
        super( projectionPlane .field );
        this.projectionPlane = projectionPlane;
        if ( projectionLine == null )
            this .projectionVector = projectionPlane .getNormal();
        else
            this .projectionVector = projectionLine .getDirection();
        
        mapParamsToState();
    }
    
    @Override
    protected final boolean mapParamsToState()
    {
        if ( projectionPlane .isImpossible() )
            setStateVariables( null, null, true );
        
        AlgebraicVector loc = projectionPlane .getBase();
        
        return setStateVariables( null /*or field identity*/, loc, false );
    }

    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        Line line = new LineFromPointAndVector( arg, this .projectionVector );
        Point point = new LinePlaneIntersectionPoint( this .projectionPlane, line );
        return point .getLocation();
    }

}
