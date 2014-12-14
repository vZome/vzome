

package com.vzome.core.construction;


/**
 * @author Scott Vorthmann
 */
public class PlaneReflection extends Transformation
{
    // parameters
    private final Plane mMirror;
    private final int[] /*AlgebraicVector*/ mNormal, mBase;
    private final int[] /*AlgebraicNumber*/ mNormDot;

    /**
     * @param prototype
     */
    public PlaneReflection( Plane mirror )
    {
        super( mirror .field );
        mMirror = mirror;
        mNormal = mirror .getNormal();
        mBase = mirror .getBase();
        mNormDot = field .dot( mNormal, mNormal );
        
        mapParamsToState();
    }

    public void attach()
    {
        mMirror .addDerivative( this );
    }
    
    public void detach()
    {
        mMirror .removeDerivative( this );
    }
    
    protected boolean mapParamsToState()
    {
        if ( mMirror .isImpossible() )
            setStateVariables( null, null, true );
        
        int[] /*AlgebraicVector*/ loc = mMirror .getBase();
        
        return setStateVariables( null /*or field identity*/, loc, false );
    }
    

    public void accept( Visitor v )
    {
        v .visitPlaneReflection( this );
    }

    public int[] /*AlgebraicVector*/ transform( int[] /*AlgebraicVector*/ arg )
    {
        arg = field .subtract( arg, mBase );
        // x' = x - 2 ((x.y)/(y.y)) y
        int[] /*AlgebraicNumber*/ xy = field .dot( arg, mNormal );
        xy = field .multiply( xy, field .createRational( new int[]{ 2,1 } ) );
        arg = field .subtract( arg, field .scaleVector( mNormal, field .divide( xy, mNormDot ) ) );
        return field .add( arg, mBase );
    }

}
