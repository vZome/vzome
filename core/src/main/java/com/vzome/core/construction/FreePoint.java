

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;

/**
 * @author Scott Vorthmann
 */
public class FreePoint extends Point
{
    /**
     * @param loc
     */
    public FreePoint( AlgebraicVector loc )
    {
        super( loc .getField() );
        // the usual pattern is to call mapParamsToState()
        setStateVariable( loc, false );
    }

    @Override
    protected boolean mapParamsToState()
    {
        // This won't get called unless there was a state change.  See setLocationAttribute above.
        return true;
    }
    
//    public static Construction load( Element elem, Map<String, Construction> index )
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
