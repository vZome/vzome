

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

    public void attach()
    {
        mCenter .addDerivative( this );
    }
    
    public void detach()
    {
        mCenter .removeDerivative( this );
    }
    
    protected boolean mapParamsToState()
    {
        if ( mCenter .isImpossible() )
            setStateVariables( null, null, true );
        AlgebraicVector loc = mCenter .getLocation();
        return setStateVariables( null, loc, false );
    }
    
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .minus( mOffset );
        arg = mOffset .minus( arg );
        return arg;
    }
    

    public void accept( Visitor v )
    {
        v .visitPointReflection( this );
    }

}
