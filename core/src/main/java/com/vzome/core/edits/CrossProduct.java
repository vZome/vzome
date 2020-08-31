
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.commands.Command;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentCrossProduct;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class CrossProduct extends ChangeManifestations
{
    @Override
    public void perform() throws Command.Failure
    {
        Point p1 = null, p2 = null;
        Segment s1 = null;
        boolean success = false;
        setOrderedSelection( true );
        for (Manifestation man : mSelection) {
            if ( success )
            {
                // Since this is an ordered selection, the selection will be emptied
                //  and repopulated on undo.  This lets us repopulate.
                recordSelected( man );
            }
            else {
                unselect( man ); // Yes, unselecting struts and panels, until success with three balls.
                // This is for backward compatibility.
                if ( man instanceof Connector )
                {
                    Point nextPoint = (Point) ((Connector) man) .getFirstConstruction();
                    if ( p1 == null ) {
                        p1 = nextPoint;
                    }
                    else if ( s1 == null )
                    {
                        p2 = nextPoint;
                        s1 = new SegmentJoiningPoints( p1, nextPoint );
                    }
                    else if ( ! success )
                    {
                        Segment segment = new SegmentJoiningPoints( p2, nextPoint );
                        segment = new SegmentCrossProduct( s1, segment );
                        select( manifestConstruction( segment ) );
                        Point endpt = new SegmentEndPoint( segment );
                        manifestConstruction( endpt );
                        success = true;
                    }
                    else
                        recordSelected( man );
                }
            }
        }
        if ( ! success )
            throw new Command.Failure( "cross-product requires three selected vertices" );

        redo();
    }

    public CrossProduct( Selection selection, RealizedModel realized )
    {
        super( selection, realized );
    }

    @Override
    protected String getXmlElementName()
    {
        return "CrossProduct";
    }
}
