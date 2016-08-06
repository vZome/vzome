
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.commands.Command;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentCrossProduct;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SparseCrossProduct extends ChangeManifestations
{
    @Override
    public void perform() throws Command.Failure
    {
        Point p1 = null, p2 = null;
        Segment s1 = null;
        boolean success = false;
        for (Manifestation man : mSelection) {
            if ( man instanceof Connector )
            {
            	// Only unselect connectors, leaving struts and panels selected.
            	//  This is designed for testing Selection.reselect(), to preserve
            	//  selection order after undo.
                unselect( man );
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
                if ( p1 == null )
                    p1 = nextPoint;
                else if ( s1 == null )
                {
                    p2 = nextPoint;
                    s1 = new SegmentJoiningPoints( p1, nextPoint );
                }
                else 
                {
                    Segment segment = new SegmentJoiningPoints( p2, nextPoint );
                    segment = new SegmentCrossProduct( s1, segment );
                    select( manifestConstruction( segment ) );
                    Point endpt = new SegmentEndPoint( segment );
                    manifestConstruction( endpt );
                    success = true;
                    break;
                }
            }
        }
        if ( ! success )
            throw new Command.Failure( "cross-product requires three selected vertices" );
        
        redo();
    }

    public SparseCrossProduct( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
        
    @Override
    protected String getXmlElementName()
    {
        return "SparseCrossProduct";
    }
}
