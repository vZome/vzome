

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
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
	public AlgebraicNumber mLength;

    /**
     * @param step
     * @param start
     */
    public AnchoredSegment( Axis axis, AlgebraicNumber length, Point start )
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
        if ( mAnchor .isImpossible() || mLength .isZero() )
            return setStateVariables( null, null, true );
        AlgebraicVector gv = mAnchor .getLocation() .projectTo3d( true );
        AlgebraicVector offset = mAxis .normal() .scale( mLength );
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
    
    public void setLength( AlgebraicNumber len )
    {
        mLength = len;
        paramOrAttrChanged();
    }

    public Axis getAxis()
    {
        return mAxis;
    }
    
    public AlgebraicNumber getLength()
    {
        return mLength;
    }

    public AlgebraicVector getUnitVector()
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
