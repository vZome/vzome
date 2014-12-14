

package com.vzome.core.construction;



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

    public void attach()
    {
        mSegment .addDerivative( this );
    }
    
    public void detach()
    {
        mSegment .removeDerivative( this );
    }
    
    public void accept( Visitor v )
    {
        v .visitSegmentMidpoint( this );
    }

    protected boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariable( null, true );
        int[] /**/ half = field .createRational( new int[]{ 1,2 } );
        int[] /*AlgebraicVector*/ loc = mSegment .getStart();
        loc = field .add( loc, field .scaleVector( mSegment .getOffset(), half ) );
        return setStateVariable( loc, false );
    }
    
}
