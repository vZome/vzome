package com.vzome.core.construction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;

public class StereographicProjection extends Transformation {

    private final Plane projectionPlane;
    private final Point projectionPoint;
    private final AlgebraicVector normal;
    private final AlgebraicVector base;
    private final AlgebraicVector focalPoint;
    private final List<AlgebraicVector> pointsOnPlane = new ArrayList<>();

    public StereographicProjection(Polygon polygon, Point projectionPoint) {
        super(polygon.field);
        this.projectionPlane = new PlaneExtensionOfPolygon(polygon);
        this.projectionPoint = projectionPoint;
        this.normal = projectionPlane.getNormal();
        this.base = projectionPlane.getBase();
        this.focalPoint = projectionPoint.getLocation();
        for (int i = 0; i < polygon.getVertexCount(); i++) {
            pointsOnPlane.add(polygon.getVertex(i));
        }
        mapParamsToState();
    }

    @Override
    protected final boolean mapParamsToState() {
        if (projectionPlane.isImpossible() || projectionPoint.isImpossible()) {
            setStateVariables(null, null, true);
        }
        return setStateVariables(null /* or field identity */, projectionPlane.getBase(), false);
    }

    /**
     * Unlike the PlaneProjection which applies the same projectionVector to all
     * constructions, the StereographicProjection must calculate each
     * projectionVector based on the point(s) being projected.
     * 
     * StereographicProjection also discards projections to infinity or on the
     * opposite side of the focalPoint from the projectionPlane. Note that excluding
     * the "opposite side" projections is not essential to the stereographic
     * projection and could be made optional as noted below.
     * 
     * StereographicProjection uses logic that is similar but not identical to
     * PlaneProjection to handle the cases where a polygon projects to a segment or
     * a segment projects to a point.
     */

    @Override
    public AlgebraicVector transform(AlgebraicVector location) {
        AlgebraicVector projectionVector = focalPoint.minus(location); // direction doesn't matter
        if (normal.dot(projectionVector).isZero()) {
            // If the dot product is zero, the vectors are orthogonal
            // meaning the projection extends parallel to the plane so skip it.
            return null;
        }
        if (location.equals(focalPoint) || pointsOnPlane.contains(location)) {
            return location;
        }
        AlgebraicVector intersection = getProjection(location);
        if (projectionVector.dot(focalPoint.minus(intersection)).signum() == -1) {
            // If the dot product is negative, the vectors span more than 90 degrees
            // meaning the projection is away from the plane so skip it.
            // TODO:: This behavior could be made optional and maybe configurable in the UI.
            return null;
        }
        // The dot product is positive so the vectors span less than 90 degrees
        // meaning the projection is toward the plane so return it.
        return intersection;
    }
    
    private AlgebraicVector getProjection(AlgebraicVector location) {
        AlgebraicVector projectionVector = focalPoint.minus(location); // direction doesn't matter
        return AlgebraicVectors.getLinePlaneIntersection(focalPoint, projectionVector, base, normal);
    }

    private Map<AlgebraicVector, AlgebraicVector> transform(Collection<AlgebraicVector> vectors) {
        Map<AlgebraicVector, AlgebraicVector> map = new HashMap<>(vectors.size());
        for (AlgebraicVector v : vectors) {
            map.put(v, transform(v));
        }
        return map;
    }

    @Override
    public Construction transform(Construction c) {
        // TODO: skip the projections to infinity or away from the plane
        // Reuse some of the logic from PlaneProjection when projection reduces
        // the construction to a lower dimensional class
        // such as Polygon -> Segment or Segment -> Point
        if (c instanceof Point) {
            Point point = (Point) c;
            AlgebraicVector loc = point.getLocation();
            if (loc.equals(focalPoint) || pointsOnPlane.contains(loc)) {
                return c; // point is unmoved by the transformation
            }
            return getProjectedPoint(point); // may be null
        } else if (c instanceof Segment) {
            Segment segment = (Segment) c;
            List<AlgebraicVector> tips = new ArrayList<>(2);
            tips.add(segment.getStart());
            tips.add(segment.getEnd());
            Map<AlgebraicVector, AlgebraicVector> map = transform(tips);
            if (map.containsValue(null)) {
                // one or more tip projects to infinity.
                return null;
            }
            if (map.get(segment.getStart()).equals(map.get(segment.getEnd()))) {
                // If both ends project to the same point
                // then the segment will be transformed to a point.
                return new LinePlaneIntersectionPoint(projectionPlane, new LineExtensionOfSegment(segment));
            }
        } else if (c instanceof Polygon) {
            Polygon polygon = (Polygon) c;
            List<AlgebraicVector> tips = new ArrayList<>(polygon.getVertexCount());
            for (int i = 0; i < polygon.getVertexCount(); i++) {
                tips.add(polygon.getVertex(i));
            }
            Map<AlgebraicVector, AlgebraicVector> map = transform(tips);
            if (map.containsValue(null)) {
                // one or more tip projects to infinity.
                return null;
            }
            if (AlgebraicVectors.areCollinear(map.values())) {
                // If the transformations of the tips are collinear,
                // then it has been transformed to a degenerate polygon.
                // Rather than projecting each individual edge as a Segment,
                // some of which could then project down to a point,
                // we'll return a single Segment
                // with its end points at the min and max points
                // of the degenerate projected Polygon.
                AlgebraicVector min = map.get(polygon.getVertex(0));
                AlgebraicVector max = min;
                for (int i = 1; i < polygon.getVertexCount(); i++) {
                    AlgebraicVector v = polygon.getVertex(i);
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
        }
        // At this point, we know that the projection won't reduce
        // the construction to a lower dimensional class
        // such as Polygon -> Segment or Segment -> Point,
        // so it's OK to call the base class
        return super.transform(c);
    }

    private Point getProjectedPoint(Point point) {
        AlgebraicVector loc = point.getLocation();
        AlgebraicVector ray = focalPoint.minus(loc); // direction doesn't matter
        if (normal.dot(ray).isZero()) {
            // If the dot product is zero, the vectors are orthogonal
            // meanng the projection extends parallel to plane so skip it.
            return null;
        }
        if (loc.equals(focalPoint) || pointsOnPlane.contains(loc)) {
            return point;
        }
        Line line = new LineFromPointAndVector(focalPoint, ray);
        Transformation transformation = new PlaneProjection(projectionPlane, line);
        Point result = (Point) transformation.transform(new FreePoint(loc));
        AlgebraicVector projection = result.getLocation();
        if (focalPoint.minus(loc).dot(focalPoint.minus(projection)).signum() == -1) {
            // If the dot product is negative, the vectors span more than 90 degrees
            // meaning the projection is away from the plane so skip it.
            return null;
        }
        // The dot product is positive so the vectors span less than 90 degrees
        // meaning the projection is toward the plane so we return it.
        return result;
    }

}
