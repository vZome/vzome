
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.construction;

public class PolygonFromVertices extends Polygon
{
    private final Point[] mVertices;

    public PolygonFromVertices( Point[] vertices )
    {
        super( vertices.length == 0 ? null : vertices[ 0 ] .field );
        mVertices = vertices;
        mapParamsToState();
    }

    public void attach()
    {
        for ( int i = 0; i < mVertices .length; i++ )
            mVertices[ i ] .addDerivative( this );
    }

    public void detach()
    {
        for ( int i = 0; i < mVertices .length; i++ )
            mVertices[ i ] .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
        // TODO implement impossibility
//        if ( mStart .isImpossible() || mEnd .isImpossible() )
//            return setStateVariables( null, null, true );
        int[][] locs = new int[ mVertices .length ][];
        for ( int i = 0; i < mVertices .length; i++ )
            locs[ i ] = mVertices[ i ] .getLocation();
        return setStateVariable( locs, false );
    }
}
