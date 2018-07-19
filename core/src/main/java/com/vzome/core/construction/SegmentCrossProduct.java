

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;



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
        if ( seg1 .isImpossible() || seg2 .isImpossible() ) {
            return setStateVariables( null, null, true );
        }
        AlgebraicVector v2 = AlgebraicVectors.getNormal(seg1 .getOffset(), seg2 .getOffset()) 
                .scale( field .createPower( -4 ) ) 
                .scale( field .createRational( 1, 2 ) );
        return setStateVariables( seg1 .getEnd(), v2, false );
    }
}
