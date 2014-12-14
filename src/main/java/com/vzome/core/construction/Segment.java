

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;


/**
 * @author Scott Vorthmann
 */
public abstract class Segment extends Construction
{
    // state variables
	private int[] /*AlgebraicVector*/ mStart, mOffset;
    
    private transient int[] /*AlgebraicVector*/ mEnd;
    
    protected Segment( AlgebraicField field )
    {
        super( field );
    }
    
    protected final boolean setStateVariables( int[] /*AlgebraicVector*/ start, int[] /*AlgebraicVector*/ offset, boolean impossible )
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
    
    public int[] /*AlgebraicVector*/ getStart()
    {
        return mStart;
    }
    
    public int[] /*AlgebraicVector*/ getEnd()
    {
        if ( mEnd == null )
            mEnd = field .add( mStart, mOffset );
        return mEnd;
    }
    
    public int[] /*AlgebraicVector*/ getOffset()
    {
        return mOffset;
    }

    
    public abstract Point getStartPoint();
    
    public abstract Point getEndPoint();
    
//    public GoldenNumber getLength()
//    {
//        Axis axis = getAxis();
//        if ( axis == null )
//            return null;
//        else
//            return mOffset .div( axis .normal() );
//    }

    public void accept( Visitor v )
    {
        v .visitSegment( this );
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "segment" );
        result .setAttribute( "start", RationalVectors .toString( mStart ) );
        result .setAttribute( "end", RationalVectors .toString( getEnd() ) );
        return result;
    }


}
