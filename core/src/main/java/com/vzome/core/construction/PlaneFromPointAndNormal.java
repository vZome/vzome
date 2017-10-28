

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class PlaneFromPointAndNormal extends Plane
{
    private AlgebraicVector normal;
	private AlgebraicVector point;

	public PlaneFromPointAndNormal( AlgebraicVector point, AlgebraicVector normal )
    {
        super( point .getField() );
		this.point = point;
		this.normal = normal;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( this .normal .isOrigin() )
            return setStateVariables( null, null, true );
        return setStateVariables( this .point, this .normal, false );
    }
}
