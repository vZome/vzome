

package com.vzome.core.construction;

/**
 * @author Scott Vorthmann
 */
public class PlaneFromNormalSegment extends Plane
{
    private final Segment mNormal;
    private final Point mIntersection;

    public PlaneFromNormalSegment( Point intersection, Segment normal )
    {
        super( intersection .field );
        mNormal = normal;
        mIntersection = intersection;
        mapParamsToState();
    }
    
    protected boolean mapParamsToState()
    {
        if ( mNormal .isImpossible() || mIntersection .isImpossible() )
            return setStateVariables( null, null, true );
        return setStateVariables( mIntersection .getLocation(), mNormal .getOffset(), false );
    }
}
