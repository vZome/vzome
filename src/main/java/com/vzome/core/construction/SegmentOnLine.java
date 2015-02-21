

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class SegmentOnLine extends Segment
{
    private Line mLine;
    
    // this is an attribute
    private AlgebraicNumber mLength;
    
    
    public SegmentOnLine( Line l3, AlgebraicNumber len )
    {
        super( l3 .field );
        mLine = l3;
        mLength = len;
        mapParamsToState();
    }

    public void attach()
    {
        mLine .addDerivative( this );
    }
    
    public void detach()
    {
        mLine .removeDerivative( this );
    }
//
//    public Axis getAxis()
//    {
//        return mLine .getAxis();
//    }
//
//    public GoldenVector getUnitVector()
//    {
//        return mLine .getUnitVector();
//    }

    protected boolean mapParamsToState()
    {
        if ( mLine .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector offset = getOffset() .scale( mLength );
        return setStateVariables( mLine .getStart(), offset, false );
    }


	public Point getStartPoint()
	{
		return null; // TODO ask mLine
	}


	public Point getEndPoint()
	{
		return null; // TODO check derivatives
	}

}
