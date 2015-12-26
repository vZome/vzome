
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentCrossProduct;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class CrossProduct extends ChangeConstructions
{
    public void perform() throws Command.Failure
    {
        Point p1 = null, p2 = null;
        Segment s1 = null;
        boolean success = false;
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Connector )
            {
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
                if ( p1 == null )
                    p1 = nextPoint;
                else if ( s1 == null )
                {
                	p2 = nextPoint;
                    s1 = new SegmentJoiningPoints( p1, nextPoint );
                    addConstruction( s1 );
                }
                else 
                {
                    Segment segment = new SegmentJoiningPoints( p2, nextPoint );
                    addConstruction( segment );
                    segment = new SegmentCrossProduct( s1, segment );
                    addConstruction( segment );
                    select( manifestConstruction( segment ) );
                    Point endpt = new SegmentEndPoint( segment );
                    addConstruction( endpt );
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

    public CrossProduct( Selection selection, RealizedModel realized, ModelRoot root, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
        
    protected String getXmlElementName()
    {
        return "CrossProduct";
    }
}
