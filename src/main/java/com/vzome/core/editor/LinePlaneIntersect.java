
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineExtensionOfSegment;
import com.vzome.core.construction.LinePlaneIntersectionPoint;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class LinePlaneIntersect extends ChangeManifestations
{
    public LinePlaneIntersect( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
    
    @Override
    public void perform()
    {
        Polygon panel = null;
        Segment segment = null;
        Point p0 = null, p1 = null, p2 = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( ( man instanceof Connector ) && ( p2 == null ) )
            {
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
                if ( p0 == null )
                    p0 = nextPoint;
                else if ( p1 == null )
                    p1 = nextPoint;
                else if ( p2 == null )
                    p2 = nextPoint;
            }
            else if ( ( man instanceof Strut ) && ( segment == null ) )
            {
                segment = (Segment) ((Strut) man) .getConstructions() .next();
            }
            else if ( ( man instanceof Panel ) && panel == null )
            {
                panel = (Polygon) ((Panel) man) .getConstructions() .next();
            }
        }
        if ( p2 != null && panel == null )
        {
            // three points rather than a panel
            Point[] points = new Point[]{ p0, p1, p2 };
            panel = new PolygonFromVertices( points );
        }
        if ( segment != null && panel != null )
        {
            Plane plane = new PlaneExtensionOfPolygon( panel );
            Line line = new LineExtensionOfSegment( segment );
            Point point = new LinePlaneIntersectionPoint( plane, line );

            if ( ! point .isImpossible() ) {
                select( manifestConstruction( point ) );
            }
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "LinePlaneIntersect";
    }
}
