
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentEndPoint;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.Translation;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class AffinePentagon extends ChangeManifestations
{
    @Override
    public void perform() throws Command.Failure
    {
        final String errorMsg = "Affine pentagon command requires two selected struts with a common vertex.";

        Strut strut1 = null, strut2 = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( man instanceof Strut ) {
                if ( strut1 == null ) {
                    strut1 = (Strut) man;
                } else if ( strut2 == null ) {
                    strut2 = (Strut) man;
                }
            }
        }
        redo();  // get the unselects out of the way, in case anything needs to be re-selected later

        if (strut1 == null || strut2 == null) {
            fail(errorMsg);
        }
        
        AlgebraicField field = strut1.getLocation().getField();
        // Although this now works with a SnubDodecField, 
        // I'm not going to enable it in the menu yet.
        // TODO: I plan to replace this command with a more generalized AffinePolygon command 
        // and I'll add the new command to the menu at that time 
//        if (! AlgebraicFields.haveSameInitialCoefficients(field, "golden" ) ) {
//            fail("Affine pentagon command requires a Pentagon field or subField.");
//        }

        // Before we start, be sure the balls at the ends of each strut have not been deleted or hidden.
        // Restore them just in case. No need to test if they already exist.
        Segment s1 = (Segment) strut1.getFirstConstruction();
        Segment s2 = (Segment) strut2.getFirstConstruction();
        manifestConstruction(new SegmentEndPoint(s1, true));
        manifestConstruction(new SegmentEndPoint(s1, false));
        manifestConstruction(new SegmentEndPoint(s2, true));
        manifestConstruction(new SegmentEndPoint(s2, false));
        redo();

        // first, find the offsets and the endpoint vectors: v0, v1, v2
        // Reverse the offset direction(s) if necessary so the two offsets have a common starting point at v0
        AlgebraicVector offset1 = s1 .getOffset();
        AlgebraicVector offset2 = s2 .getOffset();
        AlgebraicVector v1 = null, v2 = null;
        {
            AlgebraicVector s1s = s1 .getStart();
            AlgebraicVector s1e = s1 .getEnd();
            AlgebraicVector s2s = s2 .getStart();
            AlgebraicVector s2e = s2 .getEnd();
            if ( s1s .equals( s2s ) ) {
                v1 = s1e; v2 = s2e;
            } else if ( s1e .equals( s2s ) ) {
                v1 = s1s; v2 = s2e;
                offset1 = offset1 .negate();
            } else if ( s1e .equals( s2e ) ) {
                v1 = s1s; v2 = s2s;
                offset2 = offset2 .negate();
                offset1 = offset1 .negate();
            } else if ( s1s .equals( s2e ) ) {
                v1 = s1e; v2 = s2s;
                offset2 = offset2 .negate();
            } else {
                fail(errorMsg);
            }
        }

        // now, find the corresponding points
        Point p1 = null, p2 = null;
        for (Manifestation m : mManifestations) {
            if ( m instanceof Connector ) {
                AlgebraicVector loc = m .getLocation();
                if ( loc .equals( v1 ) )
                    p1 = (Point) m .getFirstConstruction();
                else if ( loc .equals( v2 ) )
                    p2 = (Point) m .getFirstConstruction();
            }
        }

        /*
        Pentagon vertex layout:
                [p4]
               /    \
             /        \
           /            \
        (p2)            [p3]
           \            /
             \        /
             (p0)--(p1)
         */

        final AlgebraicNumber phi = field .createPower( 1 );

        // now, construct p3 = p2 + phi*(offset1)
        Transformation transform = new Translation( offset1 .scale( phi ) );
        Point p3 = new TransformedPoint( transform, p2 );
        manifestConstruction( p3 );

        // now, construct p4 = p1 + phi *(offset2)
        transform = new Translation( offset2 .scale( phi ) );
        Point p4 = new TransformedPoint( transform, p1 );
        manifestConstruction( p4 );

        // now, construct struts p1-p3, p2-p4, and p3-p4
        Segment segment = new SegmentJoiningPoints( p1, p3 );
        select( manifestConstruction( segment ) );
        segment = new SegmentJoiningPoints( p2, p4 );
        select( manifestConstruction( segment ) );
        segment = new SegmentJoiningPoints( p3, p4 );
        select( manifestConstruction( segment ) );

        redo();
    }
        
    public AffinePentagon( EditorModel editorModel )
    {
        super( editorModel );
    }

    @Override
    protected String getXmlElementName()
    {
        return "AffinePentagon";
    }
}
