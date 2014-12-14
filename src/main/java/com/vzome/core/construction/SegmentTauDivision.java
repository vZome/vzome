

package com.vzome.core.construction;



/**
 * @author Scott Vorthmann
 */
public class SegmentTauDivision extends Point
{
    private Segment mSegment;

    /**
     * @param loc
     */
    public SegmentTauDivision( Segment seg )
    {
        super( seg .field );
        mSegment = seg;
        shrink = field .createPower( -1 );
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
    {}
    
    final int[] shrink;

    protected boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariable( null, true );
        int[] /*AlgebraicVector*/ loc = mSegment .getStart();
        int[] off = field .scaleVector( mSegment .getOffset(), shrink );
        loc = field .add( loc, off );
        return setStateVariable( loc, false );
    }
    
}
