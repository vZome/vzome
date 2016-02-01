

package com.vzome.core.construction;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;


/**
 * @author Scott Vorthmann
 */
public class SegmentRotated4D extends Segment
{
    private final Quaternion mLeftQuaternion, mRightQuaternion;
    
    private final Segment mPrototype;

    private static final Logger logger = Logger .getLogger( "com.vzome.core.4d" );

    public SegmentRotated4D( Quaternion leftQuaternion, Quaternion rightQuaternion, Segment prototype )
    {
        super( prototype .field );
        mLeftQuaternion = leftQuaternion;
        mRightQuaternion = rightQuaternion;
        mPrototype = prototype;
        mapParamsToState();
    }

    protected boolean mapParamsToState()
    {
        if (  mPrototype .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector loc = mPrototype .getStart();
        loc = loc .inflateTo4d( true );
        loc = mRightQuaternion .leftMultiply( loc );
        loc = mLeftQuaternion .rightMultiply( loc );
//        loc = field .projectTo3d( loc, true );
        AlgebraicVector end = mPrototype .getEnd();
        end = end .inflateTo4d( true );
        end = mRightQuaternion .leftMultiply( end );
        end = mLeftQuaternion .rightMultiply( end );
//        end = field .projectTo3d( end, true );


        if ( logger .isLoggable( Level .FINER ) ) {
            logger .finer( "------------------- SegmentRotated4D" );
            logger .finer( "left:    " + mLeftQuaternion .toString() );
            logger .finer( "right:   " + mRightQuaternion .toString() );
            logger .finer( "start: " + mPrototype .getStart() .getVectorExpression( AlgebraicField.EXPRESSION_FORMAT ) );
            logger .finer( "end:   " + mPrototype .getEnd() .getVectorExpression( AlgebraicField.EXPRESSION_FORMAT ) );
            logger .finer( "new start: " + loc .getVectorExpression( AlgebraicField.EXPRESSION_FORMAT ) );
            logger .finer( "new end:   " + end .getVectorExpression( AlgebraicField.EXPRESSION_FORMAT ) );
        }
        
        return setStateVariables( loc, end .minus( loc ), false );
    }
}
