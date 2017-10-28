

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class PointReflection extends Transformation
{
    // parameters
    private final Point mCenter;

    /**
     * @param prototype
     */
    public PointReflection( Point center )
    {
        super( center .field );
        mCenter = center;
        mapParamsToState();
    }
    
    @Override
    protected final boolean mapParamsToState()
    {
        if ( mCenter .isImpossible() )
            setStateVariables( null, null, true );
        AlgebraicVector loc = mCenter .getLocation() .projectTo3d( true );
        return setStateVariables( null, loc, false );
    }
    
    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .minus( mOffset );
        arg = mOffset .minus( arg );
        return arg;
    }
}
