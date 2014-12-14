

package com.vzome.core.construction;


/**
 * @author Scott Vorthmann
 */
public class SegmentOnLine extends Segment
{
    private Line mLine;
    
    // this is an attribute
    private int[] /*AlgebraicNumber*/ mLength;
    
    
    public SegmentOnLine( Line l3, int[] /*AlgebraicNumber*/ len )
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
        int[] offset = field .multiply( getOffset(), mLength );
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
