

package com.vzome.core.construction;

import com.vzome.core.algebra.Bivector3dHomogeneous;
import com.vzome.core.algebra.Vector3dHomogeneous;



/**
 * @author Scott Vorthmann
 */
public class LineExtensionOfSegment extends Line
{
    private final Segment mSegment;
    
    public LineExtensionOfSegment( Segment seg )
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

//    public Axis getAxis()
//    {
//        return mSegment .getAxis();
//    }

    protected boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariables( null, null, true );
        return setStateVariables( mSegment .getStart(), mSegment .getOffset(), false );
    }

	public Bivector3dHomogeneous getHomogeneous()
	{
		Vector3dHomogeneous v1 = new Vector3dHomogeneous( mSegment .getStart(), this .getField() );
		Vector3dHomogeneous v2 = new Vector3dHomogeneous( mSegment .getEnd(), this .getField() );
		
		return v1 .outer( v2 );
	}
}
