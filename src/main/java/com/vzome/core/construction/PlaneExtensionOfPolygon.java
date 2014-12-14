

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

    public void attach()
    {
        mPolygon .addDerivative( this );
    }
    
    public void detach()
    {
        mPolygon .removeDerivative( this );
    }

//    public Axis getAxis()
//    {
//        return mSegment .getAxis();
//    }

    protected boolean mapParamsToState()
    {
        if ( mPolygon .isImpossible() )
            return setStateVariables( null, null, true );
        int[] /*AlgebraicVector*/[] vs = mPolygon .getVertices();
        int[] v1 = field .subtract( vs[1], vs[0] );
        int[] v2 = field .subtract( vs[2], vs[0] );
        return setStateVariables( mPolygon .getVertices() [0],
                field .cross( v1, v2 ), false );
    }

	public Trivector3dHomogeneous getHomogeneous()
	{
        int[] /*AlgebraicVector*/[] vs = mPolygon .getVertices();
		Vector3dHomogeneous v1 = new Vector3dHomogeneous( vs[ 0 ], this .getField() );
		Vector3dHomogeneous v2 = new Vector3dHomogeneous( vs[ 1 ], this .getField() );
		Vector3dHomogeneous v3 = new Vector3dHomogeneous( vs[ 2 ], this .getField() );
		return v1 .outer( v2 ) .outer( v3 );
	}
}
