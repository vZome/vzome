

package com.vzome.core.construction;

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
        if ( mPolygon .isImpossible() ) {
            return setStateVariables( null, null, true );
        }
        return setStateVariables( mPolygon .getVertex( 0 ), mPolygon.getNormal(), false );
    }

    @Override
	public Trivector3dHomogeneous getHomogeneous()
	{
		Vector3dHomogeneous v1 = new Vector3dHomogeneous( mPolygon .getVertex( 0 ), this .getField() );
		Vector3dHomogeneous v2 = new Vector3dHomogeneous( mPolygon .getVertex( 1 ), this .getField() );
		Vector3dHomogeneous v3 = new Vector3dHomogeneous( mPolygon .getVertex( 2 ), this .getField() );
		// TODO: This should be refactored to use some variant of getNormal() 
		// that doesn't depend on the first three vertices being non-collinear.
		return v1 .outer( v2 ) .outer( v3 );
	}
}
