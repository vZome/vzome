package com.vzome.core.construction;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;

public class StereographicProjection extends Transformation {

    private final Plane projectionPlane;
    private final Point projectionPoint;
    private final AlgebraicVector normal;
    private final AlgebraicVector base;
    private final AlgebraicVector focalPoint;

    public StereographicProjection(Polygon polygon, Point projectionPoint) {
        super(polygon.field);
        this.projectionPlane = new PlaneExtensionOfPolygon(polygon);
        this.projectionPoint = projectionPoint;
        this.normal = projectionPlane.getNormal();
        this.base = projectionPlane.getBase();
        this.focalPoint = projectionPoint.getLocation();
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState() {
        if (projectionPlane.isImpossible() || projectionPoint.isImpossible()) {
            setStateVariables(null, null, true);
        }
        return setStateVariables(null /* or field identity */, projectionPlane.getBase(), false);
    }

    @Override
    public AlgebraicVector transform(AlgebraicVector location) {
        AlgebraicVector projectionVector = focalPoint.minus(location);
        return AlgebraicVectors.getLinePlaneIntersection(focalPoint, projectionVector, base, normal);
    }
    
    /**
     * Unlike the PlaneProjection which applies the same projectionVector to all
     * constructions, the StereographicProjection must calculate each
     * projectionVector based on the coordinate(s) being projected.
     * 
     * StereographicProjection discards all constructions with projections to infinity.
     * 
     * StereographicProjection uses logic that is similar but not identical to
     * PlaneProjection to handle the cases where a polygon projects to a segment or
     * a segment projects to a point.
     */
    @Override
    public Construction transform(Construction c) {
        // Use logic simlar to PlaneProjection for when projection reduces
        // the construction to a lower dimensional class
        // such as Polygon -> Segment or Segment -> Point
        if (c instanceof Point) {
            if(transform(((Point) c).getLocation()) == null) {
                return null;
            }
            // fall thru to super implementation
        } else if (c instanceof Segment) {
            Segment segment = (Segment) c;
            AlgebraicVector transformedStart = transform(segment.getStart()); 
            AlgebraicVector transformedEnd = transform(segment.getEnd()); 
            if (transformedStart == null || transformedEnd == null) {
                // one or both tips projects to infinity.
                return null;
            }
            if (transformedStart.equals(transformedEnd)) {
                // If both ends project to the same point
                // then the segment will be transformed to a point.
                return new LinePlaneIntersectionPoint(projectionPlane, new LineExtensionOfSegment(segment));
            }
            // fall thru to super implementation
        } else if (c instanceof Polygon) {
            Polygon polygon = (Polygon) c;
            List<AlgebraicVector> transformedPoints = new ArrayList<>(polygon.getVertexCount());
            for (int i = 0; i < polygon.getVertexCount(); i++) {
                AlgebraicVector projection = transform(polygon.getVertex(i));
                if (projection == null) {
                    // this vertex projects to infinity so we're done.
                    return null;
                }
                transformedPoints.add(projection);
            }
            if (AlgebraicVectors.areCollinear(transformedPoints)) {
                // If the transformations of the tips are collinear,
                // then it has been transformed to a degenerate polygon.
                // Rather than projecting each individual edge as a Segment,
                // some of which could then project down to a point,
                // we'll return a single Segment
                // with its end points at the min and max points
                // of the degenerate projected Polygon.
                AlgebraicVector min = transformedPoints.get(0);
                AlgebraicVector max = min;
                for (AlgebraicVector v : transformedPoints) {
                    if (v.compareTo(min) == -1) {
                        min = v;
                    }
                    if (v.compareTo(max) == 1) {
                        max = v;
                    }
                }
                Point p1 = new FreePoint(min);
                Point p2 = new FreePoint(max);
                return new SegmentJoiningPoints(p1, p2);
            }
            // fall thru to super implementation
        }
        // At this point, we know that none of the construction's vertices 
        // project to infinity and that the projection won't reduce
        // the construction to a lower dimensional class
        // such as Polygon -> Segment or Segment -> Point,
        // so it's OK to call the base class
        return super.transform(c);
    }

}
