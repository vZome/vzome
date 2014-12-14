

package com.vzome.core.construction;



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
        int[] /*AlgebraicVector*/ loc = mCenter .getLocation();
        return setStateVariables( null, loc, false );
    }
    
    public int[] /*AlgebraicVector*/ transform( int[] /*AlgebraicVector*/ arg )
    {
        arg = field .subtract( arg, mOffset );
        arg = field .subtract( mOffset, arg );
        return arg;
    }
    

    public void accept( Visitor v )
    {
        v .visitPointReflection( this );
    }

}
