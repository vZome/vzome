

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class TransformedSegment extends Segment
{
    private final Transformation mTransform;
    
    private final Segment mPrototype;

    public TransformedSegment( Transformation transform, Segment prototype )
    {
        super( prototype .field );
        mTransform = transform;
        mPrototype = prototype;
        mapParamsToState();
    }

    public void attach()
    {
        mTransform .addDerivative( this );
        mPrototype .addDerivative( this );
    }
    
    public void detach()
    {
        mTransform .removeDerivative( this );
        mPrototype .removeDerivative( this );
    }

    /**
     *
     */

    protected boolean mapParamsToState()
    {
        if ( mTransform .isImpossible() || mPrototype .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector loc = mTransform .transform( mPrototype .getStart() );
        AlgebraicVector end = mTransform .transform( mPrototype .getEnd() );
        return setStateVariables( loc, end .minus( loc ), false );
    }

    
	public Point getStartPoint()
	{
		return mPrototype .getStartPoint();  // TODO is this right?
	}


	public Point getEndPoint()
	{
        return mPrototype .getEndPoint();  // TODO is this right?
	}


}
