
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedPolygon;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class TransformSelection extends ChangeConstructions
{
    protected final Transformation transform;
    
    public TransformSelection( Selection selection, RealizedModel realized, Transformation transform )
    {
        super( selection, realized, false );
        this .transform = transform;
    }
    
    public void perform()
    {
        List inputs = new ArrayList();
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            inputs .add( man );
        }
        
        redo();  // get the unselects out of the way, if anything needs to be re-selected
        
        // now apply it to the input objects
        for ( Iterator all = inputs .iterator(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
            if ( m .getRenderedObject() == null )
                continue;
            Construction c = (Construction) m .getConstructions() .next();
            Construction result = null;
            if ( c instanceof Point ) {
                result = new TransformedPoint( transform, (Point) c );
            } else if ( c instanceof Segment ) {
                result = new TransformedSegment( transform, (Segment) c );
            } else if ( c instanceof Polygon ) {
                result = new TransformedPolygon( transform, (Polygon) c );
            } else {
                // TODO handle other constructions 
            }
            select( manifestConstruction( result ), true );
        }
        redo();
    }
    
    protected String getXmlElementName()
    {
        return "TransformSelection";
    }
}
