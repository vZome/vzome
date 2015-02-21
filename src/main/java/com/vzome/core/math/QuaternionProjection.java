
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;

public class QuaternionProjection implements Projection
{
    protected final com.vzome.core.algebra.Quaternion mRightQuat, mLeftQuat;
    
    private final AlgebraicField field;
    
    /**
     * @param field
     * @param leftQuat
     * @param rightQuat
     */
    public QuaternionProjection( AlgebraicField field, AlgebraicVector leftQuat, AlgebraicVector rightQuat )
    {
        if ( rightQuat == null )
            mRightQuat = null;
        else
        {
            if ( rightQuat .dimension() == field .getOrder() * 3 )
                rightQuat = rightQuat .inflateTo4d( true );
            mRightQuat = new com.vzome.core.algebra.Quaternion( field, rightQuat );
        }

        if ( leftQuat == null )
            mLeftQuat = null;
        else
        {
            if ( leftQuat .dimension() == field .getOrder() * 3 )
                leftQuat = leftQuat .inflateTo4d( true );
            mLeftQuat = new com.vzome.core.algebra.Quaternion( field, leftQuat );
        }
        this.field = field;
    }

    public AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst )
    {
        if ( mRightQuat != null ) // the correct projection, drop first coordinate
        {
            if ( mLeftQuat != null )
            {
                source = mLeftQuat .rightMultiply( source ); // s = l * s
                System .out .println( "left mult: " + source .toString() );
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
