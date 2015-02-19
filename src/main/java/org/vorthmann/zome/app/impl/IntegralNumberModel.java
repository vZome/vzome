
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import org.vorthmann.ui.DefaultController;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;

public class IntegralNumberModel extends DefaultController
{
    protected int mScaleModel, mTausModel, mOnesModel, mDivisorModel;

    public IntegralNumberModel()
    {
        setStandardLength( 3 );
    }
    
    public IntegralNumberModel( Element length )
    {
        mScaleModel = Integer .parseInt( length .getAttribute( "scale" ) );
        mTausModel = Integer .parseInt( length .getAttribute( "taus" ) );
        mOnesModel = Integer .parseInt( length .getAttribute( "ones" ) );
        mDivisorModel = Integer .parseInt( length .getAttribute( "divisor" ) );
    }

    public void setStandardLength( int scale )
    {
        mScaleModel = scale;
        mTausModel = 0;
        mOnesModel = 1;
        mDivisorModel = 1;
    }

    public AlgebraicNumber getValue( AlgebraicField field )
    {
        return field .createAlgebraicNumber( mOnesModel, mTausModel, mDivisorModel, mScaleModel );
    }

    public void adjustScale( int amt )
    {
        int oldScale = mScaleModel;
        mScaleModel += amt;
        properties() .firePropertyChange( "scale", oldScale, mScaleModel );  // this will result in a redundant setProperty()
    }
    
    public int getScale()
    {
        return mScaleModel;
    }
    

    public String getProperty( String propName )
    {
        if ( "scale" .equals( propName ) )
            return Integer .toString( mScaleModel );
        else if ( "taus" .equals( propName ) )
            return Integer .toString( mTausModel );
        else if ( "ones" .equals( propName ) )
            return Integer .toString( mOnesModel );
        else if ( "divisor" .equals( propName ) )
            return Integer .toString( mDivisorModel );
        else
            return super .getProperty( propName );
    }

    public void setProperty( String propName, Object value )
    {
        if ( "scale" .equals( propName ) )
            mScaleModel = Integer .parseInt( (String) value );
        else if ( "taus" .equals( propName ) )
            mTausModel = Integer .parseInt( (String) value );
        else if ( "ones" .equals( propName ) )
            mOnesModel = Integer .parseInt( (String) value );
        else if ( "divisor" .equals( propName ) )
            mDivisorModel = Integer .parseInt( (String) value );
    }

    public void getXml( Element elem )
    {
        elem .setAttribute( "scale", Integer.toString( mScaleModel ) );
        elem .setAttribute( "taus", Integer.toString( mTausModel ) );
        elem .setAttribute( "ones", Integer.toString( mOnesModel ) );
        elem .setAttribute( "divisor", Integer.toString( mDivisorModel ) );
    }
}
