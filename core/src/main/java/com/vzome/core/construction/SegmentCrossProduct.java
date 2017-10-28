

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class SegmentCrossProduct extends Segment
{
    // parameters
    private final Segment seg1;
    private final Segment seg2;
    
    public SegmentCrossProduct( Segment s1, Segment s2 )
    {
        super( s1 .field );
        seg1 = s1;
        seg2 = s2;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( seg1 .isImpossible() || seg2 .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector v1 = seg1 .getOffset();
        AlgebraicVector v2 = seg2 .getOffset();
        v2 = v1 .cross( v2 );
        v2 = v2 .scale( field .createPower( -4 ) );
        v2 = v2 .scale( field .createRational( 1, 2 ) );
        return setStateVariables( seg1 .getEnd(), v2, false );
    }
}
