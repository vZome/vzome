

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

        @Override
        protected boolean mapParamsToState()
        {
            return true;
        }
    }

    @Override
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
    
	@Override
	public boolean equals( Object that )
	{
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (!(that instanceof Transformation)) {
			return false;
		}
		Transformation other = (Transformation) that;
		if (mOffset == null) {
			if (other.mOffset != null) {
				return false;
			}
		} else if (!mOffset.equals(other.mOffset)) {
			return false;
		}
		if (mTransform == null) {
			if (other.mTransform != null) {
				return false;
			}
		} else if (!mTransform.equals(other.mTransform)) {
			return false;
		}
		return true;
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

    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .minus( mOffset );
        arg = mTransform .timesColumn( arg );
        arg = arg .plus( mOffset );
        return arg;
    }

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "transformation" );
        return result;
    }
}
