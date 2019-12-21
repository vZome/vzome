

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;


/**
 * @author Scott Vorthmann
 */
public abstract class Segment extends Construction
{
    // state variables
	private AlgebraicVector mStart, mOffset;
    
    private transient AlgebraicVector mEnd;
    
    protected Segment( AlgebraicField field )
    {
        super( field );
    }
    
    @Override
    public boolean is3d()
    {
        return mStart .dimension() == 3 && mOffset .dimension() == 3;
    }

    protected final boolean setStateVariables( AlgebraicVector start, AlgebraicVector offset, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        if ( offset .equals( mOffset )
        && ! isImpossible()
        &&   start .equals( mStart ) )
            return false;
        mOffset = offset;
        mStart = start;
        mEnd = null;
        setImpossible( false );
        return true;
    }
    
    public AlgebraicVector getStart()
    {
        return mStart;
    }
    
    public AlgebraicVector getEnd()
    {
        if ( mEnd == null )
            mEnd = mStart .plus( mOffset );
        return mEnd;
    }
    
    public AlgebraicVector getOffset()
    {
        return mOffset;
    }

    public AlgebraicVector getCentroid()
    {
        return AlgebraicVectors.getCentroid(new AlgebraicVector[] { mStart, mEnd });
    }

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "segment" );
        result .setAttribute( "start", mStart .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
        result .setAttribute( "end", getEnd() .getVectorExpression( AlgebraicField .ZOMIC_FORMAT ) );
        return result;
    }

    @Override
    public String toString()
    {
        return "segment from " + mStart + " to " + getEnd();
    }
}
