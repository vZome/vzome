
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.Translation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class AffinePentagon extends ChangeConstructions
{
    private final ModelRoot root;
    
    public void perform() throws Command.Failure
    {
        AlgebraicField field = root .getField();
        Segment s1 = null, s2 = null;
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Strut )
            {
                if ( s1 == null )
                    s1 = (Segment) man .getConstructions() .next();
                else if ( s2 == null )
                    s2 = (Segment) man .getConstructions() .next();
            }
        }

        redo();  // get the unselects out of the way, if anything needs to be re-selected

        if ( s1 == null || s2 == null )
            fail( "Affine pentagon command requires two selected struts." );
        
        AlgebraicVector offset1 = s1 .getOffset();
        AlgebraicVector offset2 = s2 .getOffset();
        // first, find the three vectors: common, p1, p2
        AlgebraicVector v1 = null, v2 = null;
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
        } else
            fail( "Selected struts must share a vertex." );

        // now, find the corresponding points
        Point p1 = null, p2 = null;
        for ( Iterator all = mManifestations .getAllManifestations(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
            if ( m instanceof Connector )
            {
                AlgebraicVector loc = ((Connector) m) .getLocation();
                if ( loc .equals( v1 ) )
                    p1 = (Point) m .getConstructions() .next();
                else if ( loc .equals( v2 ) )
                    p2 = (Point) m .getConstructions() .next();
            }
        }

        // now, construct p3 = p2 + tau*(segment1)
        AlgebraicNumber scale = field .createPower( 1 );
        Transformation transform = new Translation( offset1 .scale( scale ), root );
        Point p3 = new TransformedPoint( transform, p2 );
        manifestConstruction( p3 );
        // now, construct p4 = p1 + tau*(p2-common)
        transform = new Translation( offset2 .scale( scale ), root );
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

    public AffinePentagon( Selection selection, RealizedModel realized, ModelRoot root, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
        this.root = root;
    }
        
    protected String getXmlElementName()
    {
        return "AffinePentagon";
    }
}
