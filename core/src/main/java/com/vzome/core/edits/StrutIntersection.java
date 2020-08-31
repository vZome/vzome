
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Line;
import com.vzome.core.construction.LineFromPointAndVector;
import com.vzome.core.construction.LineLineIntersectionPoint;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class StrutIntersection extends ChangeManifestations
{
    public StrutIntersection( Selection selection, RealizedModel realized )
    {
        super( selection, realized );
    }
    
    @Override
    public void perform() throws Failure
    {
        Strut s1 = null, s2 = null;
        for (Manifestation man : mSelection) {
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

        Line l1 = new LineFromPointAndVector( s1 .getLocation(), s1 .getZoneVector() );
        Line l2 = new LineFromPointAndVector( s2 .getLocation(), s2 .getZoneVector() );
        Point point = new LineLineIntersectionPoint( l1, l2 );
        
        if ( point .isImpossible() )
            throw new Failure( "lines are parallel or non-intersecting" );
        
        Manifestation ball = manifestConstruction( point );
        select( ball );
        
        redo();
    }

    @Override
    protected String getXmlElementName()
    {
        return "StrutIntersection";
    }

}
