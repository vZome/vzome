

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class PlaneReflection extends Transformation
{
    // parameters
    private final Plane mMirror;
    private final AlgebraicVector mNormal, mBase;
    private final AlgebraicNumber mNormDotReciprocal;

    /**
     * @param prototype
     */
    public PlaneReflection( Plane mirror )
    {
        super( mirror .field );
        mMirror = mirror;
        mNormal = mirror .getNormal();
        mBase = mirror .getBase();
        mNormDotReciprocal = mNormal .dot( mNormal ) .reciprocal(); // do the matrix inversion just once
        
        mapParamsToState();
    }
    
    @Override
    protected final boolean mapParamsToState()
    {
        if ( mMirror .isImpossible() )
            setStateVariables( null, null, true );
        
        AlgebraicVector loc = mMirror .getBase();
        
        return setStateVariables( null /*or field identity*/, loc, false );
    }

    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .minus( mBase );
        // x' = x - 2 ((x.y)/(y.y)) y
        AlgebraicNumber xy = arg .dot( mNormal );
        xy = xy .times( field .createRational( 2 ) );
        arg = arg .minus( mNormal .scale( xy .times( mNormDotReciprocal ) ) );
        return arg .plus( mBase );
    }

}
