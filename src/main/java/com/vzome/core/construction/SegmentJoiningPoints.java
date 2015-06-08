

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class SegmentJoiningPoints extends Segment
{
    // parameters
    private Point mStart, mEnd;
    
    public SegmentJoiningPoints( Point p1, Point p2 )
    {
        super( p1 .field );
        mStart = p1;
        mEnd = p2;
        mapParamsToState();
    }

    public void attach()
    {
        mStart .addDerivative( this );
        mEnd .addDerivative( this );
    }
    
    public void detach()
    {
        mStart .removeDerivative( this );
        mEnd .removeDerivative( this );
    }
//
//    public Axis getAxis()
//    {
//        return IcosahedralSymmetry .INSTANCE .getAxis( getOffset() );
//    }
//
//
//    public GoldenVector getUnitVector()
//    {
//        return getAxis() .normal();
//    }


    protected boolean mapParamsToState()
    {
        if ( mStart .isImpossible() || mEnd .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector startV = mStart .getLocation();
        AlgebraicVector endV = mEnd .getLocation();
        // if both 4d, keep the offset 4d, but never mix 4d and 3d when computing offset
        if ( startV .dimension() == 3 || endV .dimension() == 3 ) {
        	startV = startV .projectTo3d( true );
        	endV = endV .projectTo3d( true );
        }
        AlgebraicVector offset = endV .minus( startV );
        return setStateVariables( startV, offset, false );
    }


	public Point getStartPoint()
	{
		return mStart;
	}


	public Point getEndPoint()
	{
		return mEnd;
	}
    
//    public static Construction load( Element elem, Map index )
//    {
//        String myIdStr = elem .getAttributeValue( "id" );
//        int id = Integer .parseInt( myIdStr );
//
//        String idStr = elem .getAttributeValue( "start" );
//        Point start = (Point) index .get( idStr );
//        idStr = elem .getAttributeValue( "end" );
//        Point end = (Point) index .get( idStr );
//        SegmentJoiningPoints result = new SegmentJoiningPoints( start, end );
//        
//        result .mId = id;
//        index .put( myIdStr, result );
//        return result;
//    }

}
