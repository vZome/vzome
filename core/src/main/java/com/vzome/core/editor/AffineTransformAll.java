
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


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
import com.vzome.core.model.Strut;

public class AffineTransformAll extends ChangeManifestations
{
    private Point center;
    
    public AffineTransformAll( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.center = editor .getCenterPoint();
    }
    
    @Override
    public void perform() throws Failure
    {
        Segment s1 = null, s2 = null, s3 = null;
        for (Manifestation man : mSelection) {
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
        for (Manifestation m : mManifestations) {
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
            select( manifestConstruction( result ) );
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "AffineTransformAll";
    }
}
