

package com.vzome.core.construction;



/**
 * @author Scott Vorthmann
 */
public class SegmentCrossProduct extends Segment
{
    // parameters
    private Segment seg1, seg2;
    
    public SegmentCrossProduct( Segment s1, Segment s2 )
    {
        super( s1 .field );
        seg1 = s1;
        seg2 = s2;
        mapParamsToState();
    }

    public void attach()
    {
        seg1 .addDerivative( this );
        seg2 .addDerivative( this );
    }
    
    public void detach()
    {
        seg1 .removeDerivative( this );
        seg2 .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
        if ( seg1 .isImpossible() || seg2 .isImpossible() )
            return setStateVariables( null, null, true );
        int[] v1 = seg1 .getOffset();
        int[] v2 = seg2 .getOffset();
        v2 = field .cross( v1, v2 );
        v2 = field .negate( v2 );
        v2 = field .scaleVector( v2, field .createPower( -4 ) );
        v2 = field .scaleVector( v2, field .createRational( new int[]{ 1,2 } ) );
        return setStateVariables( seg1 .getEnd(), v2, false );
    }

    public Point getEndPoint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Point getStartPoint()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
