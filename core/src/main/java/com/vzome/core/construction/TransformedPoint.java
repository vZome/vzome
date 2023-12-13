

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class TransformedPoint extends Point
{
    private final Transformation mTransform;
    
    private final Point mPrototype;

    public TransformedPoint( Transformation transform, Point prototype )
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
            return setStateVariable( null, true );
        AlgebraicVector loc = mTransform .transform( mPrototype .getLocation() );
        return setStateVariable( loc, loc == null );
    }

}
