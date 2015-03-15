
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class JoinPoints extends ChangeConstructions
{
    public JoinPoints( Selection selection, RealizedModel realized, boolean groupInSelection, boolean closedLoop )
    {
        super( selection, realized, groupInSelection );
        this .closedLoop = closedLoop;
    }

    protected void getXmlAttributes( Element element )
    {
        if ( ! closedLoop )
            element .setAttribute( "closedLoop", "false" );
    }

    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        String attr = xml .getAttribute( "closedLoop" );
        if ( attr != null && ! attr.isEmpty() )
            closedLoop = Boolean .valueOf( attr ) .booleanValue();
    }

    public void perform() throws Failure
    {
        Point firstPoint = null, lastPoint = null;
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Connector )
            {
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
                if ( lastPoint != null )
                {
                    Segment segment = new SegmentJoiningPoints( lastPoint, nextPoint );
                    addConstruction( segment );
                    select( manifestConstruction( segment ) );
                }
                else
                    firstPoint = nextPoint;
                lastPoint = nextPoint;
            }
        }
        if ( closedLoop && lastPoint != null && firstPoint != null && lastPoint != firstPoint )
        {
            Segment segment = new SegmentJoiningPoints( lastPoint, firstPoint );
            addConstruction( segment );
            select( manifestConstruction( segment ) );
        }

        redo();
    }

    private boolean closedLoop = true;
    
    protected String getXmlElementName()
    {
        return "JoinPoints";
    }
}
