

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class TransformedPolygon extends Polygon
{
    private final Transformation mTransform;
    
    private final Polygon mPrototype;

    public TransformedPolygon( Transformation transform, Polygon prototype )
    {
        super( prototype .field );
        mTransform = transform;
        mPrototype = prototype;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        // TODO implement impossibility
//      if ( mStart .isImpossible() || mEnd .isImpossible() )
//          return setStateVariables( null, null, true );
        AlgebraicVector [] vertices = new AlgebraicVector[ mPrototype .getVertexCount() ];
        for ( int i = 0; i < vertices .length; i++ ) {
            vertices[ i ] = mTransform .transform( mPrototype .getVertex( i ) );
        }
        return setStateVariable( vertices, false );
    }
}
