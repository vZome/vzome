
package com.vzome.core.edits;


import com.vzome.core.commands.Command;
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
    public void perform() throws Command.Failure
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
        if(strut == null) {
            // Ideally I'd throw a Failure here to notify the user of the problem 
            // but since other similar issues have always been silently ignored by this command, 
            // I don't want to introduce a different behavior when loading legacy models
            // nor do I want a different user experience when the strut is missing 
            // than when other required inputs are missing.
            // This includes issues like not enough balls or no plane selected 
            // or when too many balls, struts or panels are selected.
            // Rather than throw new Command.Failure("No strut was selected."),
            // I'll just avoid the NPE by returning early.
            // Note that this avoids the redo() below,
            // so none of the unselects from above get committed.
            // but that would happen anyway if an NPE or a Failure were thrown.
            // It makes for inconsistent error handling, but it avoids an NPE below 
            // and doesn't break any legacy models.
            return;
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
