

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;
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
        AlgebraicVector loc = mPrototype .getStart();
        loc = loc .inflateTo4d( true );
        loc = mRightQuaternion .leftMultiply( loc );
        loc = mLeftQuaternion .rightMultiply( loc );
        loc = field .projectTo3d( loc, true );
        AlgebraicVector end = mPrototype .getEnd();
        end = end .inflateTo4d( true );
        end = mRightQuaternion .leftMultiply( end );
        end = mLeftQuaternion .rightMultiply( end );
        end = field .projectTo3d( end, true );
        return setStateVariables( loc, end .minus( loc ), false );
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
