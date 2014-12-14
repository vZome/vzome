

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.algebra.RationalVectors;


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

    public void attach()
    {
        mPrototype .addDerivative( this );
    }
    
    public void detach()
    {
        mPrototype .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
        if ( mPrototype .isImpossible() )
            return setStateVariable( null, true );
        
        AlgebraicField field = mPrototype .getField();
        int[] loc = field .origin( 4 );
        int order = field .getOrder();
        
        // adding a W-value to the starting point makes it work when it is part of an
        //  un-squashed object in the center of the projection
//        int[] wValue = field .createPower( 9 );
//        for ( int i = 0; i < wValue.length/2; i ++ )
//             RationalVectors .copy( wValue, i, loc, i );
        
        int[] loc3d = mPrototype .getLocation();
        for ( int i = 0; i < loc3d.length/2; i ++ )
            RationalVectors .copy( loc3d, i, loc, i+order );
        loc = mRightQuaternion .leftMultiply( loc );
        loc = mLeftQuaternion .rightMultiply( loc );
        loc = field .projectTo3d( loc, true );
        return setStateVariable( loc, false );
    }

}
