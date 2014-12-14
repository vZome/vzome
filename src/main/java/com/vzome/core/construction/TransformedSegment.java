

package com.vzome.core.construction;



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
        int[] /*AlgebraicVector*/ loc = mTransform .transform( mPrototype .getStart() );
        int[] /*AlgebraicVector*/ end = mTransform .transform( mPrototype .getEnd() );
        return setStateVariables( loc, field .subtract( end, loc ), false );
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
