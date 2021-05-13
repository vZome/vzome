
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.math;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.xml.DomUtils;

public class PerspectiveProjection implements Projection
{
    private final AlgebraicField field;
    
    private AlgebraicNumber cameraDist;
    
    public PerspectiveProjection( AlgebraicField field, AlgebraicNumber cameraDist )
    {
        super();
        this .field = field;
        this .cameraDist = cameraDist;
    }
    
    private AlgebraicNumber minDenom;
    private double minDenomValue;

    @Override
    public AlgebraicVector projectImage( AlgebraicVector source, boolean wFirst )
    {
        /*
         * from my WebGL vertex shader:
         * 
            float denom = cameraDist - position .w;
            denom = max( denom, 0.0001 );
            position3d .x = position .x / denom;
            position3d .y = position .y / denom;
            position3d .z = position .z / denom;
            position3d .w = 1.0;
         */
        AlgebraicVector result = this .field .origin( 4 );
        AlgebraicNumber w = source .getComponent( 0 );
        AlgebraicNumber denom = cameraDist .minus( w );
        if ( minDenom == null )
        {
            minDenom = field .createPower( -5 );
            minDenomValue = minDenom .evaluate();
        }
        double denomValue = denom .evaluate();
        if ( denomValue < minDenomValue )
        {
            denom = minDenom;
        }
        AlgebraicNumber numerator = denom .reciprocal(); // do the matrix inversion once
        
        result .setComponent( 0, field .one());
        result .setComponent( 1, source .getComponent( 1 ) .times( numerator ) );
        result .setComponent( 2, source .getComponent( 2 ) .times( numerator ) );
        result .setComponent( 3, source .getComponent( 3 ) .times( numerator ) );
        return result;
    }

    @Override
    public void getXmlAttributes(Element element) {
        if (cameraDist != null) {
            DomUtils .addAttribute( element, "cameraDist", cameraDist .toString( AlgebraicField .ZOMIC_FORMAT ) );
        }
    }

    @Override
    public void setXmlAttributes( Element xml )
    {
        String nums = xml .getAttribute( "cameraDist" );
        if ( nums == null || nums .isEmpty() )
            return;
        cameraDist = this .field .parseNumber( nums );
    }

    @Override
    public String getProjectionName() {
        return "Perspective";
    }

}
