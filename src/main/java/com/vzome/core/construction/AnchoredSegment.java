

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.symmetry.Axis;

/**
 * @author Scott Vorthmann
 */
public class AnchoredSegment extends Segment
{
    // parameters
    private Point mAnchor;
    
    // attributes
	public Axis mAxis;
	public int[] /*AlgebraicNumber*/ mLength;

    /**
     * @param step
     * @param start
     */
    public AnchoredSegment( Axis axis, int[] /*AlgebraicNumber*/ length, Point start )
    {
        super( start .field );
        mAnchor = start;
        mAxis = axis;
        mLength = length;
        mapParamsToState();
    }

    public void attach()
    {
        mAnchor .addDerivative( this );
    }
    
    public void detach()
    {
        mAnchor .removeDerivative( this );
    }
    
    protected boolean mapParamsToState()
    {
        if ( mAnchor .isImpossible() || field .isOrigin( mLength ) )
            return setStateVariables( null, null, true );
        int[] /*AlgebraicVector*/ gv = mAnchor .getLocation();
        AlgebraicField field = mAxis .getDirection() .getSymmetry() .getField();
        int[] /*AlgebraicVector*/ offset = field .scaleVector( mAxis .normal(), mLength );
        return setStateVariables( gv, offset, false );
    }

    public void accept( Visitor v )
    {
        v .visitAnchoredSegment( this );
    }

    /**
     * @param step
     */
    public void setAxis( Axis axis )
    {
        mAxis = axis;
        paramOrAttrChanged();
    }
    
    public void setLength( int[] /*AlgebraicNumber*/ len )
    {
        mLength = len;
        paramOrAttrChanged();
    }

    public Axis getAxis()
    {
        return mAxis;
    }
    
    public int[] /*AlgebraicNumber*/ getLength()
    {
        return mLength;
    }

    public int[] /*AlgebraicVector*/ getUnitVector()
    {
        return mAxis .normal();
    }

    
	public Point getStartPoint()
	{
		return mAnchor;
	}


	public Point getEndPoint()
	{
		Construction[] derivs = getDerivatives();
		for ( int i = 0; i < derivs .length; i++ )
			if ( derivs[i] instanceof SegmentEndPoint )
				return (Point) derivs[i];
		return null;
	}

}
