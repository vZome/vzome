package com.vzome.core.edits;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class Parallelepiped extends ChangeManifestations
{
	public Parallelepiped( EditorModel editor )
    {
        super( editor );
	}

	@Override
	protected String getXmlElementName()
	{
		return "Parallelepiped";
	}
	
    @Override
    public void perform() throws Command.Failure
    {
        final String errorMsg = "Parallelepiped command requires three selected struts with a common vertex.";

        Strut strut1 = null, strut2 = null, strut3 = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            if ( man instanceof Strut ) {
                if ( strut1 == null ) {
                    strut1 = Strut.class.cast(man);
                } else if ( strut2 == null ) {
                    strut2 = Strut.class.cast(man);
                } else if ( strut3 == null ) {
                	strut3 = Strut.class.cast(man);
                } else
                    fail( errorMsg );
            } else
                fail( errorMsg );
        }

        if (strut1 == null || strut2 == null || strut3 == null) {
            fail(errorMsg);
        }

        // first, find the offsets and the endpoint vectors: v1, v2, v3
        // Reverse the offset direction(s) if necessary so the two offsets have a common starting point at v0
        Segment s1 = Segment.class.cast(strut1.getFirstConstruction());
        Segment s2 = Segment.class.cast(strut2.getFirstConstruction());
        Segment s3 = Segment.class.cast(strut3.getFirstConstruction());
        AlgebraicVector offset1 = s1 .getOffset();
        AlgebraicVector offset2 = s2 .getOffset();
        AlgebraicVector offset3 = s3 .getOffset();
        AlgebraicVector v0 = null, v1 = null, v2 = null, v3 = null;
        {
            AlgebraicVector s1s = s1 .getStart();
            AlgebraicVector s1e = s1 .getEnd();
            AlgebraicVector s2s = s2 .getStart();
            AlgebraicVector s2e = s2 .getEnd();
            if ( s1s .equals( s2s ) ) {
                v1 = s1e; v2 = s2e; v0 = s2s;
            } else if ( s1e .equals( s2s ) ) {
                v1 = s1s; v2 = s2e; v0 = s2s;
                offset1 = offset1 .negate();
            } else if ( s1e .equals( s2e ) ) {
                v1 = s1s; v2 = s2s; v0 = s2e;
                offset2 = offset2 .negate();
                offset1 = offset1 .negate();
            } else if ( s1s .equals( s2e ) ) {
                v1 = s1e; v2 = s2s; v0 = s2e;
                offset2 = offset2 .negate();
            } else {
                fail(errorMsg);
            }
            AlgebraicVector s3s = s3 .getStart();
            AlgebraicVector s3e = s3 .getEnd();
            if ( s3s .equals( v0 ) ) {
                v3 = s3e;
            } else if ( s3e .equals( v0 ) ) {
                v3 = s3s;
                offset3 = offset3 .negate();
            } else {
                fail(errorMsg);
            }
        }

        // Now we know the selection is valid, so let's proceed with side-effects.
        
        redo();  // get the unselects out of the way, in case anything needs to be re-selected later

        // Before we start, be sure the balls at the ends of each strut have not been deleted or hidden.
        // Restore them just in case. No need to test if they already exist.
        Point p0 = new FreePoint( v0 );
        Point p1 = new FreePoint( v1 );
        Point p2 = new FreePoint( v2 );
        Point p3 = new FreePoint( v3 );
        manifestConstruction( p0 );
        manifestConstruction( p1 );
        manifestConstruction( p2 );
        manifestConstruction( p3 );
        redo();

        /*
         
        Rhombohedron vertex layout:
          Initial vertices (*)
          Constructed vertices [*]
        
            [p4]-------[p7]
            /  \       /  \
           /    \     /    \
          /      \          \
        (p2)- -  (p3)-------[p5]  [p6] in the back, behind (p3)
          \	     /          /
           \    /     \    /
            \  /       \  /
            (p0)-------(p1)
         */


        AlgebraicVector v4 = v2 .plus( offset3 );
        Point p4 = new FreePoint( v4 );
        select( manifestConstruction( p4 ) );
        select( manifestConstruction( new SegmentJoiningPoints( p2,  p4 ) ) );
        select( manifestConstruction( new SegmentJoiningPoints( p3,  p4 ) ) );

        AlgebraicVector v5 = v3 .plus( offset1 );
        Point p5 = new FreePoint( v5 );
        select( manifestConstruction( p5 ) );
        select( manifestConstruction( new SegmentJoiningPoints( p1,  p5 ) ) );
        select( manifestConstruction( new SegmentJoiningPoints( p3,  p5 ) ) );

        AlgebraicVector v6 = v1 .plus( offset2 );
        Point p6 = new FreePoint( v6 );
        select( manifestConstruction( p6 ) );
        select( manifestConstruction( new SegmentJoiningPoints( p1,  p6 ) ) );
        select( manifestConstruction( new SegmentJoiningPoints( p2,  p6 ) ) );

        AlgebraicVector v7 = v4 .plus( offset1 );
        Point p7 = new FreePoint( v7 );
        select( manifestConstruction( p7 ) );
        select( manifestConstruction( new SegmentJoiningPoints( p4,  p7 ) ) );
        select( manifestConstruction( new SegmentJoiningPoints( p5,  p7 ) ) );
        select( manifestConstruction( new SegmentJoiningPoints( p6,  p7 ) ) );

        select( manifestConstruction( new PolygonFromVertices( new Point[]{ p0, p3, p4, p2 } ) ) );
        select( manifestConstruction( new PolygonFromVertices( new Point[]{ p0, p1, p5, p3 } ) ) );
        select( manifestConstruction( new PolygonFromVertices( new Point[]{ p0, p2, p6, p1 } ) ) );
        select( manifestConstruction( new PolygonFromVertices( new Point[]{ p7, p4, p3, p5 } ) ) );
        select( manifestConstruction( new PolygonFromVertices( new Point[]{ p7, p6, p2, p4 } ) ) );
        select( manifestConstruction( new PolygonFromVertices( new Point[]{ p7, p5, p1, p6 } ) ) );
        redo();
    }
}
