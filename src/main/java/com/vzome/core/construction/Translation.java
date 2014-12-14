

package com.vzome.core.construction;



public class Translation extends Transformation
{
    private int[] /*AlgebraicVector*/ mOffset;
    private ModelRoot mRoot;

    public Translation( int[] /*AlgebraicVector*/ offset, ModelRoot root )
    {
        super( root .field );
        mOffset = offset;
        mRoot = root;        
    }

    public int[] /*AlgebraicVector*/ transform( int[] /*AlgebraicVector*/ arg )
    {
        arg = field .add( arg, mOffset );
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
