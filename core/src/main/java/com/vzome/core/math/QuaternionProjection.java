
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.commands.XmlSaveFormat;

public class QuaternionProjection implements Projection
{
    private final Quaternion[][] quaternions = new Quaternion[2][2];

    private final AlgebraicField field;

    /**
     * @param field
     * @param leftQuat
     * @param rightQuat
     */
    public QuaternionProjection( AlgebraicField field, AlgebraicVector leftQuat, AlgebraicVector rightQuat )
    {
        this.field = field;
        this.setQuaternion(leftQuat, LEFT);
        this.setQuaternion(rightQuat, RIGHT);
    }

    @Override
    public AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst )
    {
        AlgebraicVector result = source;
        Quaternion leftQuat = getQuaternion( LEFT, wFirst );
        Quaternion rightQuat = getQuaternion( RIGHT, wFirst );
        if ( rightQuat != null ) // the correct projection, drop first coordinate
        {
            if ( leftQuat != null )
            {
                result = leftQuat .rightMultiply( result ); // s = l * s
                System .out .println( "left mult: " + result .toString() );
            }
            result = rightQuat .leftMultiply( result );     // s = s * r
        }
        else
        {
            result = leftQuat .rightMultiply( result ); // s = l * s
        }
        //        StringBuilder buf = new StringBuilder();
        //        field .getVectorExpression( buf, source, AlgebraicField.VEF_FORMAT );
        //        System .out .println( buf );
        return field .projectTo3d( result, wFirst );
    }

    private static final int LEFT = 0, RIGHT = 1;
    private static final int WFIRST = 0, WLAST = 1;

    private void setQuaternion(AlgebraicVector quatVector, int hand) {
        quaternions[hand][WFIRST] = quatVector == null ? null : new Quaternion( field, quatVector .inflateTo4d( true ) );
        quaternions[hand][WLAST]  = quatVector == null ? null : new Quaternion( field, quatVector .inflateTo4d( false ) );
    }

    private Quaternion getQuaternion(int hand, boolean wFirst) {
        return quaternions[hand][ wFirst ? WFIRST : WLAST ];
    }

    // attribute name here is "quaternion" instead of "rightQuaternion" in order to support legacy VEF
    private static final String RIGHT_QUATERNION_ATTRIBUTENAME = "quaternion";
    private static final String LEFT_QUATERNION_ATTRIBUTENAME = "leftQuaternion";

    @Override
    public void getXmlAttributes( Element element )
    {
        Quaternion leftQuat = getQuaternion( LEFT, true );
        Quaternion rightQuat = getQuaternion( RIGHT, true );
        if ( leftQuat != null ) {
            DomUtils .addAttribute( element, LEFT_QUATERNION_ATTRIBUTENAME, leftQuat.getVector() .toParsableString() );
        }
        if ( rightQuat != null ) {
            DomUtils .addAttribute( element, RIGHT_QUATERNION_ATTRIBUTENAME, rightQuat.getVector() .toParsableString() );
        }
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        this.setQuaternion(format .parseRationalVector( xml, LEFT_QUATERNION_ATTRIBUTENAME ), LEFT );
        this.setQuaternion(format .parseRationalVector( xml,RIGHT_QUATERNION_ATTRIBUTENAME ), RIGHT);
    }

    @Override
    public String getProjectionName() {
        return "Quaternion";
    }
}
