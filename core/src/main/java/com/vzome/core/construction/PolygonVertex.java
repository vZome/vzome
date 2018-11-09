

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicVector;


/**
 * @author Scott Vorthmann
 */
public class PolygonVertex extends Point
{
    private final Polygon polygon;
    
    private final int index;

    public PolygonVertex( Polygon polygon, int index )
    {
        super( polygon .field );
        this .polygon = polygon;
        this .index = index;
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState()
    {
        if ( polygon .isImpossible() ) {
            return setStateVariable( null, true );
        }
        AlgebraicVector loc = polygon .getVertex( this .index );
        return setStateVariable( loc, false );
    }
}
