

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

    @Override
    protected final boolean mapParamsToState()
    {
        if ( mSegment .isImpossible() )
            return setStateVariables( null, null, true );
        return setStateVariables( mSegment .getStart(), mSegment .getOffset(), false );
    }

    @Override
	public Bivector3dHomogeneous getHomogeneous()
	{
		Vector3dHomogeneous v1 = new Vector3dHomogeneous( mSegment .getStart(), this .getField() );
		Vector3dHomogeneous v2 = new Vector3dHomogeneous( mSegment .getEnd(), this .getField() );
		
		return v1 .outer( v2 );
	}
}
