
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.construction;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.DomUtils;


public abstract class Polygon extends Construction
{
    @Override
	public String toString() {
		return "polygon " + Arrays.toString(mVertices);
	}


	private AlgebraicVector[] mVertices;
    
    public Polygon( AlgebraicField field )
    {
        super( field );
    }

    protected boolean setStateVariable( AlgebraicVector[] vertices, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        // TODO implement change control
//        if ( offset .equals( mOffset )
//        && ! isImpossible()
//        &&   start .equals( mStart ) )
//            return false;
        mVertices = vertices;
        setImpossible( false );
        return true;
    }
    
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "polygon" );
        this .getXml( result, "vertex" );
        return result;
    }

    public void getXml( Element result, String vertexChildName )
    {
        for (AlgebraicVector vertex : mVertices) {
            Element child = result .getOwnerDocument() .createElement( vertexChildName );
            DomUtils.addAttribute(child, "at", vertex.getVectorExpression(AlgebraicField.ZOMIC_FORMAT));
            result .appendChild( child );
        }
    }

    
    public AlgebraicVector[] getVertices()
    {
        return mVertices .clone();
    }
}
