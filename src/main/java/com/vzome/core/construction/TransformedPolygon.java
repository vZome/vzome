

package com.vzome.core.construction;


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

    public void attach()
    {
        mTransform .addDerivative( this );
        mPrototype .addDerivative( this );
    }
    
    public void detach()
    {
        mTransform .removeDerivative( this );
        mPrototype .removeDerivative( this );
    }


    protected boolean mapParamsToState()
    {
        // TODO implement impossibility
//      if ( mStart .isImpossible() || mEnd .isImpossible() )
//          return setStateVariables( null, null, true );
        int[] /*AlgebraicVector*/ [] protoLocs = mPrototype .getVertices();
        int[] /*AlgebraicVector*/ [] locs = new int[ protoLocs .length ][];
        for ( int i = 0; i < locs .length; i++ )
            locs[ i ] = mTransform .transform( protoLocs[ i ] );
        return setStateVariable( locs, false );
    }
}
