

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Trivector3dHomogeneous;
import com.vzome.core.algebra.Vector3dHomogeneous;



/**
 * @author Scott Vorthmann
 */
public class PlaneExtensionOfPolygon extends Plane
{
    private final Polygon mPolygon;
    
    public PlaneExtensionOfPolygon( Polygon polygon )
    {
        super( polygon .field );
        mPolygon = polygon;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( mPolygon .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector[] vs = mPolygon .getVertices();
        AlgebraicVector v1 = vs[1] .minus( vs[0] );
        AlgebraicVector v2 = vs[2] .minus( vs[0] );
        return setStateVariables( mPolygon .getVertices() [0], v1 .cross( v2 ), false );
    }

    @Override
	public Trivector3dHomogeneous getHomogeneous()
	{
        AlgebraicVector[] vs = mPolygon .getVertices();
		Vector3dHomogeneous v1 = new Vector3dHomogeneous( vs[ 0 ], this .getField() );
		Vector3dHomogeneous v2 = new Vector3dHomogeneous( vs[ 1 ], this .getField() );
		Vector3dHomogeneous v3 = new Vector3dHomogeneous( vs[ 2 ], this .getField() );
		return v1 .outer( v2 ) .outer( v3 );
	}
}
