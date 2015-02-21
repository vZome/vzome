

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;



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
    
    final AlgebraicNumber shrink;

    protected boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariable( null, true );
        AlgebraicVector loc = mSegment .getStart();
        AlgebraicVector off = mSegment .getOffset() .scale( shrink );
        loc = loc .plus( off );
        return setStateVariable( loc, false );
    }
    
}
