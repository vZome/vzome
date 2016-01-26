
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineExtensionOfSegment;
import com.vzome.core.construction.LineLineIntersectionPoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class StrutIntersection extends ChangeManifestations
{
    public StrutIntersection( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
    
    public void perform() throws Failure
    {
        Strut s1 = null, s2 = null;
        for ( Iterator<Manifestation> mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = mans .next();
            unselect( man );
            if ( man instanceof Strut )
                if ( s1 == null )
                    s1 = (Strut) man;
                else if ( s2 == null )
                    s2 = (Strut) man;
                else
                    throw new Failure( "only two struts are allowed" );
        }
        if ( s1 == null || s2 == null )
            throw new Failure( "two struts are required" );

        Segment seg1 = (Segment) s1 .getConstructions() .next();
        Line l1 = new LineExtensionOfSegment( seg1 );
        Segment seg2 = (Segment) s2 .getConstructions() .next();
        Line l2 = new LineExtensionOfSegment( seg2 );
        Point point = new LineLineIntersectionPoint( l1, l2 );
        
        if ( point .isImpossible() )
            throw new Failure( "lines are parallel or non-intersecting" );
        
        Manifestation ball = manifestConstruction( point );
        select( ball );
        
        redo();
    }

    protected String getXmlElementName()
    {
        return "StrutIntersection";
    }

}
