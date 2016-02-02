//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class HeptagonSubdivision extends ChangeManifestations
{
    @Override
    public void perform() throws Command.Failure
    {
        Point p1 = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( man instanceof Connector ) {
                Point nextPoint = (Point) ( (Connector) man ).getConstructions().next();
                if ( p1 == null )
                    p1 = nextPoint;
                else {
                    Segment segment = new SegmentJoiningPoints( p1, nextPoint );
                    HeptagonField field = (HeptagonField) segment .getField();
                    AlgebraicNumber scaleFactor = field .sigmaReciprocal();
                    AlgebraicVector offset = segment .getOffset();
                    AlgebraicVector off2 = offset .scale( scaleFactor );
                    AlgebraicVector off1 = off2 .scale( scaleFactor );

                    AlgebraicVector v1 = p1.getLocation() .plus( off1 );
                    Point firstPoint = new FreePoint( v1 );
                    select( manifestConstruction( firstPoint ) );

                    AlgebraicVector v2 = v1 .plus( off2 ); // note, starting from v1
                    // not p1
                    Point secondPoint = new FreePoint( v2 );
                    select( manifestConstruction( secondPoint ) );
                    break;
                }
            }
        }

        redo();
    }

    public HeptagonSubdivision( Selection selection, RealizedModel realized, ModelRoot root, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }

    @Override
    protected String getXmlElementName()
    {
        return "HeptagonSubdivision";
    }
}
