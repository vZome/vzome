

package com.vzome.core.construction;

import com.vzome.core.algebra.Quaternion;


/**
 * @author Scott Vorthmann
 */
public class SegmentRotated4D extends Segment
{
    private final Quaternion mLeftQuaternion, mRightQuaternion;
    
    private final Segment mPrototype;

    public SegmentRotated4D( Quaternion leftQuaternion, Quaternion rightQuaternion, Segment prototype )
    {
        super( prototype .field );
        mLeftQuaternion = leftQuaternion;
        mRightQuaternion = rightQuaternion;
        mPrototype = prototype;
        mapParamsToState();
    }

    public void attach()
    {
        mPrototype .addDerivative( this );
    }
    
    public void detach()
    {
        mPrototype .removeDerivative( this );
    }

    /**
     *
     */

    protected boolean mapParamsToState()
    {
        if (  mPrototype .isImpossible() )
            return setStateVariables( null, null, true );
        int[] /*AlgebraicVector*/ loc = mPrototype .getStart();
        loc = mPrototype .getField() .inflateTo4d( loc );
        loc = mRightQuaternion .leftMultiply( loc );
        loc = mLeftQuaternion .rightMultiply( loc );
        loc = field .projectTo3d( loc, true );
        int[] /*AlgebraicVector*/ end = mPrototype .getEnd();
        end = mPrototype .getField() .inflateTo4d( end );
        end = mRightQuaternion .leftMultiply( end );
        end = mLeftQuaternion .rightMultiply( end );
        end = field .projectTo3d( end, true );
        return setStateVariables( loc, field .subtract( end, loc ), false );
    }

    
	public Point getStartPoint()
	{
		return mPrototype .getStartPoint();
	}


	public Point getEndPoint()
	{
        return mPrototype .getEndPoint();
	}


}
