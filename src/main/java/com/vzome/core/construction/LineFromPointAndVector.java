

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;

/**
 * @author Scott Vorthmann
 */
public class LineFromPointAndVector extends Line
{
	private final AlgebraicVector point;
	private final AlgebraicVector direction;
    
    public LineFromPointAndVector( AlgebraicVector point, AlgebraicVector direction )
    {
        super( point .getField() );
		this .point = point;
		this .direction = direction;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( direction .isOrigin() )
            return setStateVariables( null, null, true );
        return setStateVariables( this .point, this .direction, false );
    }
}
