
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicVector;
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

	public void perform() throws Failure
    {
        for ( Iterator<Manifestation> mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = mans .next();
            unselect( man );
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
                AlgebraicVector[] vertices = polygon .getVertices();
                for (int i = 0; i < vertices.length; i++) {
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

    protected String getXmlElementName()
    {
        return NAME;
    }

}
