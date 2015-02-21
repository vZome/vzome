

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;

/**
 * @author Scott Vorthmann
 */
public abstract class Transformation extends Construction
{
    public static class Identity extends Transformation
    {
        public int[] transform( int[] arg )
        {
            return arg;
        }

        public Identity( AlgebraicField field )
        {
            super( field );
        }

        public void attach(){}

        public void detach(){}

        protected boolean mapParamsToState()
        {
            return true;
        }
    }

    public boolean isVisible()
    {
        return false;
    }    
    
    // state variables
    private AlgebraicMatrix mTransform;
    protected AlgebraicVector mOffset; // this lets us avoid doing matrix arithmetic for the offsets
    
    protected Transformation( AlgebraicField field )
    {
        super( field );
    }
    
    protected boolean setStateVariables( AlgebraicMatrix transform, AlgebraicVector offset, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( transform != null && transform .equals( mTransform )
        && offset .equals( mOffset )
        && ! isImpossible() )
            return false;
        mTransform = transform;
        mOffset = offset;
        setImpossible( false );
        return true;
    }
    
    public void accept( Visitor v )
    {
        v .visitTransformation( this );
    }
    
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .minus( mOffset );
        arg = mTransform .timesColumn( arg );
        arg = arg .plus( mOffset );
        return arg;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "transformation" );
        return result;
    }
}
