
package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonVertex;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
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
                Segment s = (Segment) man .getFirstConstruction();
                SegmentEndPoint start = new SegmentEndPoint( s, true );
                manifestConstruction( start );
                SegmentEndPoint end = new SegmentEndPoint( s, false );
                select( manifestConstruction( end ) );
            }
            else if ( man instanceof Panel )
            {
                Polygon polygon = (Polygon) ((Panel) man) .getFirstConstruction();
                for (int i = 0; i < polygon.getVertexCount(); i++) {
                    PolygonVertex v = new PolygonVertex( polygon, i );
                    select( manifestConstruction( v ) );
                }
            }
        }
        redo();
    }
    
    public ShowVertices( EditorModel editor )
    {
        super( editor );
    }

    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }

}
