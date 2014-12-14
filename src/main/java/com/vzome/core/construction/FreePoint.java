

package com.vzome.core.construction;

/**
 * @author Scott Vorthmann
 */
public class FreePoint extends Point
{
    private ModelRoot mRoot;
    
    /**
     * @param loc
     */
    public FreePoint( int[] /*AlgebraicVector*/ loc, ModelRoot root )
    {
        super( root.field );
        // the usual pattern is to call mapParamsToState()
        setStateVariable( loc, false );
        mRoot = root;
    }

    public void attach()
    {
        mRoot .addDerivative( this );
    }
    
    public void detach()
    {
        mRoot .removeDerivative( this );
    }

    /**
     * A public setter for FreePoint's location attribute,
     * distinguished (in concept) from Point's state variable.
     * @param loc
     */
    public void setLocationAttribute( int[] /*AlgebraicVector*/ loc )
    {
        // this is the only Construction where we "preempt" paramOrAttrChanged(),
        //   because the location attribute is stored as the state variable
        if ( setStateVariable( loc, false ) )
            paramOrAttrChanged();
    }
    
    public void accept( Visitor v )
    {
        v .visitFreePoint( this );
    }
    
    protected boolean mapParamsToState()
    {
        // This won't get called unless there was a state change.  See setLocationAttribute above.
        return true;
    }
    
//    public static Construction load( Element elem, Map index )
//    {
//        String idStr = elem .getAttributeValue( "id" );
//        int id = Integer .parseInt( idStr );
//        Element loc  = elem .getFirstChildElement( "location" );
//        GoldenVector location = GoldenVector .load( loc );
//        ModelRoot root = (ModelRoot) index .get( "root" );
//        FreePoint result = new FreePoint( location, root );
//        result .mId = id;
//        index .put( idStr, result );
//        return result;
//    }
}
