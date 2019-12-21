
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedPolygon;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class TransformSelection extends ChangeManifestations
{
    protected final Transformation transform;
    
    public TransformSelection( Selection selection, RealizedModel realized, Transformation transform )
    {
        super( selection, realized );
        this .transform = transform;
    }
    
    @Override
    public void perform()
    {
        List<Manifestation> inputs = new ArrayList<>();
        for (Manifestation man : mSelection) {
            unselect( man );
            inputs .add( man );
        }
        
        redo();  // get the unselects out of the way, if anything needs to be re-selected
        // now apply it to the input objects
        for (Manifestation m : inputs) {
            if ( m .getRenderedObject() == null )
                continue;
            Construction c = m .getConstructions() .next();
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
    
    @Override
    protected String getXmlElementName()
    {
        return "TransformSelection";
    }
}
