

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class SegmentMidpoint extends Point
{
    private Segment mSegment;

    /**
     * @param loc
     */
    public SegmentMidpoint( Segment seg )
    {
        super( seg .field );
        mSegment = seg;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariable( null, true );
        AlgebraicNumber half = field .createRational( 1, 2 );
        AlgebraicVector loc = mSegment .getStart();
        loc = loc .plus( mSegment .getOffset() .scale( half ) );
        return setStateVariable( loc, false );
    }
    
}
