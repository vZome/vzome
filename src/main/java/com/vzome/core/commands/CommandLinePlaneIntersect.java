

package com.vzome.core.commands;

import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineExtensionOfSegment;
import com.vzome.core.construction.LinePlaneIntersectionPoint;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;

/**
 * @author Scott Vorthmann
 */
public class CommandLinePlaneIntersect extends AbstractCommand
{
    private static final Object[][] PARAM_SIGNATURE =
        new Object[][]{ { "panel", Polygon.class }, { "segment", Segment.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{};

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    
    public ConstructionList apply( ConstructionList parameters, Map attrs, ConstructionChanges effects ) throws Failure
    {
        ConstructionList result = new ConstructionList();
        if ( parameters == null || parameters .size() != 2 )
            throw new Failure( "Intersection requires a panel and a strut." );
        try {
            Polygon panel;
            Segment segment;
            Construction first = (Construction) parameters .get( 0 );
            if ( first instanceof Polygon ) {
                panel = (Polygon) first;
                segment = (Segment) parameters .get( 1 );
            } else {
                segment = (Segment) first;
                panel = (Polygon) parameters .get( 1 );
            }
            Plane plane = new PlaneExtensionOfPolygon( panel );
            Line line = new LineExtensionOfSegment( segment );
            Point point = new LinePlaneIntersectionPoint( plane, line );
            result .addConstruction( point );
            effects .constructionAdded( point );
        } catch ( ClassCastException e ) {
            throw new Failure( "Intersection requires a panel and a strut." );
        }
        return result;
    }
}
