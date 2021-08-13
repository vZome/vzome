
package com.vzome.core.edits;


import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineFromPointAndVector;
import com.vzome.core.construction.LinePlaneIntersectionPoint;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneFromPointAndNormal;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class LinePlaneIntersect extends ChangeManifestations
{
    public LinePlaneIntersect( EditorModel editor )
    {
        super( editor );
    }
    
    @Override
    protected boolean groupingAware()
    {
        return true;
    }
    
    @Override
    public void perform()
    {
        Panel panel = null;
        Strut strut = null;
        Point p0 = null, p1 = null, p2 = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( ( man instanceof Connector ) && ( p2 == null ) )
            {
                Point nextPoint = (Point) ((Connector) man) .getFirstConstruction();
                if ( p0 == null )
                    p0 = nextPoint;
                else if ( p1 == null )
                    p1 = nextPoint;
                else if ( p2 == null )
                    p2 = nextPoint;
            }
            else if ( ( man instanceof Strut ) && ( strut == null ) )
            {
                strut = ((Strut) man);
            }
            else if ( ( man instanceof Panel ) && panel == null )
            {
                panel = ((Panel) man);
            }
        }
        Point point = null;
        Plane plane = null;
        Line line = new LineFromPointAndVector( strut .getLocation(), strut .getZoneVector() );
        if ( p2 != null && panel == null )
        {
            // three points rather than a panel
            Point[] points = new Point[]{ p0, p1, p2 };
            Polygon polygon = new PolygonFromVertices( points );
            plane = new PlaneExtensionOfPolygon( polygon );
        }
        else if ( strut != null && panel != null )
        {
            plane = new PlaneFromPointAndNormal( panel .getFirstVertex(), panel .getZoneVector() );
        }
        if ( plane != null && ! plane .isImpossible() ) {
        	point = new LinePlaneIntersectionPoint( plane, line );
        	if ( ! point .isImpossible() )
        		select( manifestConstruction( point ) );
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "LinePlaneIntersect";
    }
}
