
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.ChangeOfBasis;
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
import com.vzome.core.model.Strut;

public class AffineTransformAll extends ChangeConstructions
{
    private Point center;
    
    public AffineTransformAll( Selection selection, RealizedModel realized, Point center, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
        this .center = center;
    }
    
    public void perform() throws Failure
    {
        Segment s1 = null, s2 = null, s3 = null;
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Strut )
            {
                if ( s1 == null )
                    s1 = (Segment) man .getConstructions() .next();
                else if ( s2 == null )
                    s2 = (Segment) man .getConstructions() .next();
                else if ( s3 == null )
                    s3 = (Segment) man .getConstructions() .next();
            }
        }
        if ( s3 == null || s2 == null || s1 == null )
            throw new Failure( "three struts required" );
        
        redo();  // get the unselects out of the way, if anything needs to be re-selected
        
        // now, construct the affine transformation
        Transformation transform = new ChangeOfBasis( s1, s2, s3, center, true );

        // now apply it to all objects
        for ( Iterator all = mManifestations .getAllManifestations(); all .hasNext(); ) {
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
            addConstruction( result );
            select( manifestConstruction( result ) );
        }
        redo();
    }
    
    protected String getXmlElementName()
    {
        return "AffineTransformAll";
    }
}
