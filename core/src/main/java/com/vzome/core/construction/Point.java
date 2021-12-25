package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public abstract class Point extends Construction
{
    private AlgebraicVector mLocation;

    protected Point( AlgebraicField field )
    {
        super( field );
    }
    
    public String getSignature()
    {
        return this .mLocation .toString();
    }

    @Override
    public boolean is3d()
    {
        return mLocation .dimension() == 3;
    }

    protected boolean setStateVariable( AlgebraicVector loc, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( loc .equals( mLocation )
        && ! isImpossible() )
            return false;
        mLocation = loc;
        setImpossible( false );
        return true;
    }
    
    public AlgebraicVector getLocation()
    {
        return mLocation;
    }

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "point" );
        result .setAttribute( "at", getLocation() .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
        return result;
    }

    @Override
    public String toString()
    {
        return "point at " + mLocation;
    }
}
