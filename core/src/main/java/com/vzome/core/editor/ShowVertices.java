
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonVertex;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;


public class ShowVertices extends ChangeManifestations
{
    public static final String NAME = "ShowVertices";

    @Override
	public void perform() throws Failure
    {
        for (Manifestation man : mSelection) {
            unselect( man );
            // TODO: Should we unselect all of the balls and then redo before entering this loop?
            // If any balls on selected struts or panels are pre-selected, then they become deselected.
            // TODO: They should only be deselected if they are not on any selected panels or struts,
            // but how does this affect backward compatability?
            if ( man instanceof Strut )
            {
                Segment s = (Segment) man .getConstructions() .next();
                SegmentEndPoint start = new SegmentEndPoint( s, true );
                manifestConstruction( start );
                SegmentEndPoint end = new SegmentEndPoint( s, false );
                select( manifestConstruction( end ) );
            }
            else if ( man instanceof Panel )
            {
                Polygon polygon = (Polygon) ((Panel) man) .getConstructions() .next();
                for (int i = 0; i < polygon.getVertexCount(); i++) {
                    PolygonVertex v = new PolygonVertex( polygon, i );
                    select( manifestConstruction( v ) );
                }
            }
        }
        redo();
    }
    
    public ShowVertices( Selection selection, RealizedModel realized )
    {
        super( selection, realized, false );
    }

    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }

}
