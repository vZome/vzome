

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;


/**
 * @author Scott Vorthmann
 */
public class PolygonRotated4D extends Polygon
{
    private final Quaternion mLeftQuaternion, mRightQuaternion;
    
    private final Polygon mPrototype;

    public PolygonRotated4D( Quaternion leftQuaternion, Quaternion rightQuaternion, Polygon prototype )
    {
        super( prototype .field );
        mLeftQuaternion = leftQuaternion;
        mRightQuaternion = rightQuaternion;
        mPrototype = prototype;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if (  mPrototype .isImpossible() )
            return setStateVariable( null, true );
        AlgebraicVector[] vertices = new AlgebraicVector[ mPrototype .getVertexCount() ];
        for ( int i = 0; i < vertices.length; i++ ) {
            AlgebraicVector loc = mRightQuaternion .leftMultiply( mPrototype.getVertex(i) );
            loc = mLeftQuaternion .rightMultiply( loc );
            vertices[i] = loc; // field .projectTo3d( loc, true );
        }
        return setStateVariable( vertices, false );
    }
}
