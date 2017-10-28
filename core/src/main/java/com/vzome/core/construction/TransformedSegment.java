

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

    @Override
    protected final boolean mapParamsToState()
    {
        if ( mTransform .isImpossible() || mPrototype .isImpossible() )
            return setStateVariables( null, null, true );
        AlgebraicVector loc = mTransform .transform( mPrototype .getStart() .projectTo3d( true ) );
        AlgebraicVector end = mTransform .transform( mPrototype .getEnd() .projectTo3d( true ) );
        return setStateVariables( loc, end .minus( loc ), false );
    }
}
