
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalNumbers;

public class QuaternionProjection implements Projection
{
    protected final com.vzome.core.algebra.Quaternion mRightQuat, mLeftQuat;
    
    private final AlgebraicField field;
    
    /**
     * @param field
     * @param leftQuat
     * @param rightQuat
     */
    public QuaternionProjection( AlgebraicField field, int[] leftQuat, int[] rightQuat )
    {
        if ( rightQuat == null )
            mRightQuat = null;
        else
        {
            if ( rightQuat .length / 2 == field .getOrder() * 3 )
                rightQuat = field .inflateTo4d( rightQuat );
            mRightQuat = new com.vzome.core.algebra.Quaternion( field, rightQuat );
        }

        if ( leftQuat == null )
            mLeftQuat = null;
        else
        {
            if ( leftQuat .length / 2 == field .getOrder() * 3 )
                leftQuat = field .inflateTo4d( leftQuat );
            mLeftQuat = new com.vzome.core.algebra.Quaternion( field, leftQuat );
        }
        this.field = field;
    }

    public int[] projectImage( int[] source, boolean wFirst )
    {
        if ( mRightQuat != null ) // the correct projection, drop first coordinate
        {
            if ( mLeftQuat != null )
            {
                source = mLeftQuat .rightMultiply( source ); // s = l * s
                System .out .println( "left mult: " + RationalNumbers .toString( source ) );
            }
            source = mRightQuat .leftMultiply( source );     // s = s * r
        }
        else
        {
            source = mLeftQuat .rightMultiply( source ); // s = l * s
        }
//        StringBuffer buf = new StringBuffer();
//        field .getVectorExpression( buf, source, AlgebraicField.VEF_FORMAT );
//        System .out .println( buf );
        return field .projectTo3d( source, wFirst );
    }

}
