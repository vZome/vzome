

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;



public class Translation extends Transformation
{
    private AlgebraicVector mOffset;
    private ModelRoot mRoot;

    public Translation( AlgebraicVector offset, ModelRoot root )
    {
        super( root .field );
        mOffset = offset;
        mRoot = root;        
    }

    public AlgebraicVector transform( AlgebraicVector arg )
    {
        arg = arg .plus( mOffset );
        return arg;
    }

    public void attach()
    {
        mRoot .addDerivative( this );
    }
    
    public void detach()
    {
        mRoot .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
//        AlgebraicField factory = (AlgebraicField) mOffset .getFactory();
        return setStateVariables( null, null, /*factory .identity(), factory .origin(),*/ false );
    }

    public void accept( Visitor v )
    {
        v .visitTranslation( this );
    }

}
