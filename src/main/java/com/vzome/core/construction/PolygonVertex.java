

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

    protected boolean mapParamsToState()
    {
        if (  polygon .isImpossible() )
            return setStateVariable( null, true );
        AlgebraicVector[] vertices = polygon .getVertices();
        AlgebraicVector loc = vertices[ this .index ];
        return setStateVariable( loc, false );
    }
}
