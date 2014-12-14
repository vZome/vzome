
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Arrays;
import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
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
        
        int[] /*AlgebraicVector*/ offset1 = s1 .getOffset();
        int[] /*AlgebraicVector*/ offset2 = s2 .getOffset();
        // first, find the three vectors: common, p1, p2
        int[] /*AlgebraicVector*/ v1 = null, v2 = null;
        int[] /*AlgebraicVector*/ s1s = s1 .getStart();
        int[] /*AlgebraicVector*/ s1e = s1 .getEnd();
        int[] /*AlgebraicVector*/ s2s = s2 .getStart();
        int[] /*AlgebraicVector*/ s2e = s2 .getEnd();
        if ( Arrays .equals( s1s, s2s ) ) {
            v1 = s1e; v2 = s2e;
        } else if ( Arrays .equals( s1e, s2s ) ) {
            v1 = s1s; v2 = s2e;
            offset1 = field .negate( offset1 );
        } else if ( Arrays .equals( s1e, s2e ) ) {
            v1 = s1s; v2 = s2s;
            offset2 = field .negate( offset2 );
            offset1 = field .negate( offset1 );
        } else if ( Arrays .equals( s1s, s2e ) ) {
            v1 = s1e; v2 = s2s;
            offset2 = field .negate( offset2 );
        } else
            fail( "Selected struts must share a vertex." );

        // now, find the corresponding points
        Point p1 = null, p2 = null;
        for ( Iterator all = mManifestations .getAllManifestations(); all .hasNext(); ) {
            Manifestation m = (Manifestation) all .next();
            if ( m instanceof Connector )
            {
                int[] /*AlgebraicVector*/ loc = ((Connector) m) .getLocation();
                if ( Arrays .equals( loc, v1 ) )
                    p1 = (Point) m .getConstructions() .next();
                else if ( Arrays .equals( loc, v2 ) )
                    p2 = (Point) m .getConstructions() .next();
            }
        }

        // now, construct p3 = p2 + tau*(segment1)
        int[] scale = field .createPower( 1 );
        Transformation transform = new Translation( field .scaleVector( offset1, scale ), root );
        Point p3 = new TransformedPoint( transform, p2 );
        addConstruction( p3 );
        manifestConstruction( p3 );
        // now, construct p4 = p1 + tau*(p2-common)
        transform = new Translation( field .scaleVector( offset2, scale ), root );
        Point p4 = new TransformedPoint( transform, p1 );
        addConstruction( p4 );
        manifestConstruction( p4 );

        // now, construct struts p1-p3, p2-p4, and p3-p4
        Segment segment = new SegmentJoiningPoints( p1, p3 );
        addConstruction( segment );
        select( manifestConstruction( segment ) );
        segment = new SegmentJoiningPoints( p2, p4 );
        addConstruction( segment );
        select( manifestConstruction( segment ) );
        segment = new SegmentJoiningPoints( p3, p4 );
        addConstruction( segment );
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
