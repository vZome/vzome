

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



public class Translation extends Transformation
{
    private AlgebraicVector mOffset;

    public Translation( AlgebraicVector offset, ModelRoot root )
    {
        super( root .field );
        mOffset = offset;
    }

    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .plus( mOffset );
        return arg;
    }

    protected boolean mapParamsToState()
    {
//        AlgebraicField factory = (AlgebraicField) mOffset .getFactory();
        return setStateVariables( null, null, /*factory .identity(), factory .origin(),*/ false );
    }
}
