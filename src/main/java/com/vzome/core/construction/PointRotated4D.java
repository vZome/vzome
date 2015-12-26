

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;


/**
 * @author Scott Vorthmann
 */
public class PointRotated4D extends Point
{
    private final Quaternion mLeftQuaternion, mRightQuaternion;
    
    private final Point mPrototype;

    public PointRotated4D( Quaternion leftQuaternion, Quaternion rightQuaternion, Point prototype )
    {
        super( prototype .field );
        mLeftQuaternion = leftQuaternion;
        mRightQuaternion = rightQuaternion;
        mPrototype = prototype;
        mapParamsToState();
    }

    protected boolean mapParamsToState()
    {
        if ( mPrototype .isImpossible() )
            return setStateVariable( null, true );
        
        AlgebraicField field = mPrototype .getField();
        AlgebraicVector loc = field .origin( 4 );
        
        // adding a W-value to the starting point makes it work when it is part of an
        //  un-squashed object in the center of the projection
//        int[] wValue = field .createPower( 9 );
//        for ( int i = 0; i < wValue.length/2; i ++ )
//             RationalVectors .copy( wValue, i, loc, i );
        
        AlgebraicVector loc3d = mPrototype .getLocation();
        loc = loc3d .inflateTo4d( true );
        loc = mRightQuaternion .leftMultiply( loc );
        loc = mLeftQuaternion .rightMultiply( loc );
//        loc = field .projectTo3d( loc, true );
        return setStateVariable( loc, false );
    }

}
