

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class SegmentEndPoint extends Point
{
    private Segment mSegment;
    private boolean start = false;

    /**
     * @param loc
     */
    public SegmentEndPoint( Segment seg )
    {
        this( seg, false );
    }

    public SegmentEndPoint( Segment seg, boolean start )
    {
        super( seg .field );
        mSegment = seg;
        this .start = start;
        mapParamsToState();
    }

    protected boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariable( null, true );
        AlgebraicVector loc = this .start? mSegment .getStart() : mSegment .getEnd();
        return setStateVariable( loc, false );
    }
    
}
