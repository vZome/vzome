

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class SegmentJoiningPoints extends Segment
{
    // parameters
    private final Point mStart, mEnd;

    public SegmentJoiningPoints( Point p1, Point p2 )
    {
        super( p1 .field );
        mStart = p1;
        mEnd = p2;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( mStart .isImpossible() || mEnd .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector startV = mStart .getLocation();
        AlgebraicVector endV = mEnd .getLocation();
        // if both 4d, keep the offset 4d, but never mix 4d and 3d when computing offset
        if ( startV .dimension() == 3 || endV .dimension() == 3 ) {
            startV = startV .projectTo3d( true );
            endV = endV .projectTo3d( true );
        }
        AlgebraicVector offset = endV .minus( startV );
        return setStateVariables( startV, offset, false );
    }
}
