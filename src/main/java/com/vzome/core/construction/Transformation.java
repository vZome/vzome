

package com.vzome.core.construction;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;

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
    private int[][] mTransform;
    protected int[] /*AlgebraicVector*/ mOffset; // this lets us avoid doing matrix arithmetic for the offsets
    
    protected Transformation( AlgebraicField field )
    {
        super( field );
    }
    
    protected boolean setStateVariables( int[][] transform, int[] /*AlgebraicVector*/ offset, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( Arrays .equals( transform, mTransform )
        && Arrays .equals( offset, mOffset )
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
    
    public int[] /*AlgebraicVector*/ transform( int[] /*AlgebraicVector*/ arg )
    {
        arg = field .subtract( arg, mOffset );
        arg = field .transform( mTransform, arg );
        arg = field .add( arg, mOffset );
        return arg;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "transformation" );
        return result;
    }
}
