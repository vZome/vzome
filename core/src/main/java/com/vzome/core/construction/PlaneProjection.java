

package com.vzome.core.construction;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;


/**
 * @author Scott Vorthmann
 */
public class PlaneProjection extends Transformation
{
    // parameters
    private final Plane projectionPlane;
    private final AlgebraicVector projectionVector;

    /**
     * @param prototype
     */
    public PlaneProjection( Plane projectionPlane, Line projectionLine )
    {
        super( projectionPlane .field );
        this.projectionPlane = projectionPlane;
        if ( projectionLine == null )
            this .projectionVector = projectionPlane .getNormal();
        else
            this .projectionVector = projectionLine .getDirection();
        
        mapParamsToState();
    }
    
    @Override
    protected final boolean mapParamsToState()
    {
        if ( projectionPlane .isImpossible() )
            setStateVariables( null, null, true );
        
        AlgebraicVector loc = projectionPlane .getBase();
        
        return setStateVariables( null /*or field identity*/, loc, false );
    }

    @Override
    public AlgebraicVector transform( AlgebraicVector arg )
    {
        Line line = new LineFromPointAndVector( arg, this .projectionVector );
        Point point = new LinePlaneIntersectionPoint( this .projectionPlane, line );
        return point .getLocation();
    }

    @Override
    public Construction transform( Construction c )
    {
        if ( c instanceof Segment ) {
            if(AlgebraicVectors.areParallel(projectionVector, ((Segment) c).getOffset())) {
                // If this segment is parallel to projectionVector,
                // then the segment will be transformed to a point. 
                return new LinePlaneIntersectionPoint(projectionPlane, new LineExtensionOfSegment((Segment) c));
            }
        } else if ( c instanceof Polygon ) {
            Polygon p = (Polygon) c;
            List<AlgebraicVector> points = new ArrayList<>(1 + p.getVertexCount());
            points.add(p.getVertex(0).plus(projectionVector));
            for(int i = 0; i < p.getVertexCount(); i++) {
                points.add(p.getVertex(i));
            }
            if(AlgebraicVectors.areCoplanar(points)) {
                // If this polygon is is parallel to projectionVector,
                // then its vertices will be transformed 
                // to a degenerate polygon with all vertices being collinear.
                // Rather than projecting each individual edge as a Segment,
                // some of which could then project down to a point,
                // we'll return a single Segment 
                // with its end points at the min and max points 
                // of the degenerate projected Polygon.
                p = (Polygon) super.transform( p );
                AlgebraicVector min = p.getVertex(0);
                AlgebraicVector max = min;
                for(int i = 1; i < p.getVertexCount(); i++) {
                    AlgebraicVector v = p.getVertex(i);
                    if(v.compareTo(min) == -1) {
                        min = v;
                    }
                    if(v.compareTo(max) == 1) {
                        max = v;
                    }
                }
                Point p1 = new FreePoint(min);
                Point p2 = new FreePoint(max);
                return new SegmentJoiningPoints(p1, p2);
            }
        }
        // At this point, we know that the projection won't reduce 
        // the construction to a lower dimensional class
        // such as Polygon -> Segment or Segment -> Point,
        // so it's OK to call the base class
        return super.transform( c );
    }
}
