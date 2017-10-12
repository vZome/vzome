
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SymmetryTransformation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedPolygon;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.math.symmetry.DodecagonalSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class DodecagonSymmetry extends ChangeManifestations
{
    private final Point center;
    
    private final Symmetry symmetry;
    
    public DodecagonSymmetry( Selection selection, RealizedModel realized, Point center, Symmetry symmetry, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
        this .center = center;
        this .symmetry = new DodecagonalSymmetry( symmetry .getField(), null );
    }
    
    @Override
    public void perform()
    {
        Transformation transform = new SymmetryTransformation( symmetry, 1, center );
        
        for (Manifestation man : mSelection) {
            Construction c = man .getConstructions() .next();

            for ( int i = 0; i < 11; i++ )
            {
                if ( c instanceof Point ) {
                    c = new TransformedPoint( transform, (Point) c );
                } else if ( c instanceof Segment ) {
                    c = new TransformedSegment( transform, (Segment) c );
                } else if ( c instanceof Polygon ) {
                    c = new TransformedPolygon( transform, (Polygon) c );
                } else {
                    // TODO handle other constructions 
                }
                if ( c == null )
                    continue;
                select( manifestConstruction( c ) );
            }
        }
        redo();
    }
    
    @Override
    protected String getXmlElementName()
    {
        return "DodecagonSymmetry";
    }
}
