

package com.vzome.core.construction;




/**
 * @author Scott Vorthmann
 */
public class CentroidPoint extends Point
{
    private final Point[] mPoints;

    public CentroidPoint( Point[] points )
    {
        super( points[ 0 ] .field );
        mPoints = points;
        mapParamsToState();
    }

    public void attach()
    {
        for ( int i = 0; i < mPoints .length; i++ )
            mPoints[ i ] .addDerivative( this );
    }

    public void detach()
    {
        for ( int i = 0; i < mPoints .length; i++ )
            mPoints[ i ] .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
        // TODO implement impossibility
//        if ( mStart .isImpossible() || mEnd .isImpossible() )
//            return setStateVariables( null, null, true );

        /*AlgebraicVector*/ int[] centroid = mPoints[0] .getLocation();
        int num = 1;
        for ( int i = 1; i < mPoints .length; i++ ) {
            centroid = field .add( centroid,  mPoints[i] .getLocation() );
            num++;
        }
        centroid = field .scaleVector( centroid, field .createRational( new int[]{ 1, num } ) );
        
        return setStateVariable( centroid, false );
    }

}
