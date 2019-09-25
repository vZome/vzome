package com.vzome.core.construction;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;

public class PolygonPolygonProjectionToSegment extends Segment {

    private final Polygon[] polygons = new Polygon[2];

    public PolygonPolygonProjectionToSegment(Polygon polygon0, Polygon polygon1) {
        super(polygon0.getField());
        polygons[0] = polygon0;
        polygons[1] = polygon1;
        mapParamsToState();
    }

    @Override
    protected boolean mapParamsToState() {
        if( polygons[0].isImpossible() || polygons[1].isImpossible()) {
            return setStateVariables(null, null, true);
        }
        if (AlgebraicVectors.areParallel(polygons[0].getNormal(), polygons[1].getNormal())) {
            return setStateVariables(null, null, true);
        }

        Set<AlgebraicVector> intersections = new HashSet<>(2);
        // first attempt looks for two edges of one polygon that intersect the plane of the other at unique points.
        for (int poly = 0; poly < 2; poly++) {
            Polygon edgePolygon = polygons[poly]; // get edges from this one
            Polygon planePolygon = polygons[(poly + 1) % 2]; // get plane from the other one
            AlgebraicVector centroid = planePolygon.getCentroid();
            AlgebraicVector normal = planePolygon.getNormal();
            int nVertices = edgePolygon.getVertexCount();
            for (int i = 0; i < nVertices; i++) {
                AlgebraicVector lineStart = edgePolygon.getVertex(i);
                // last edge ends at the first vertex to close the loop
                AlgebraicVector lineDirection = lineStart.minus(edgePolygon.getVertex((i + 1) % nVertices));
                if (!lineDirection.isOrigin()) {
                    AlgebraicVector intersection = AlgebraicVectors.getLinePlaneIntersection(lineStart, lineDirection, centroid, normal);
                    if (intersection != null) {
                        intersections.add(intersection);
                        if (intersections.size() == 2) {
                            break;
                        }
                    }
                }
            }
            if (intersections.size() == 2) {
                break;
            }
        }

        if (intersections.size() != 2) {
            // it's possible that both polygons are triangles with one common point and
            // their other two edges parallel
            // in which case, we need to try again using the radial centroid to corner 
            // vectors instead of the edge vectors.
            for (int poly = 0; poly < 2; poly++) {
                Polygon edgePolygon = polygons[poly]; // get edges from this one
                Polygon planePolygon = polygons[(poly + 1) % 2]; // get plane from the other one
                AlgebraicVector centroid = planePolygon.getCentroid();
                AlgebraicVector normal = planePolygon.getNormal();
                // note that lineStart is calculated differently than in the initial attempt above.
                AlgebraicVector lineStart = edgePolygon.getCentroid();
                for (int i = 0; i < edgePolygon.getVertexCount(); i++) {
                    AlgebraicVector lineDirection = lineStart.minus(edgePolygon.getVertex(i));
                    if (!lineDirection.isOrigin()) {
                        AlgebraicVector intersection = AlgebraicVectors.getLinePlaneIntersection(lineStart, lineDirection, centroid, normal);
                        if (intersection != null) {
                            intersections.add(intersection);
                            if (intersections.size() == 2) {
                                break;
                            }
                        }
                    }
                }
                if (intersections.size() == 2) {
                    break;
                }
            }
        }

        if (intersections.size() != 2) {
            // something went wrong... TODO: Log this...
            return setStateVariables(null, null, true);
        }

        // At this stage, intersections has two unique intersection points that define the line
        // where the two planes intersect.
        AlgebraicVector v0 = null, v1 = null;
        for(AlgebraicVector v : intersections) {
            if(v0 == null) {
                v0 = v;
            } else {
                v1 = v;
            }
        }
        Line intersectionLine = new LineExtensionOfSegment(new SegmentJoiningPoints(new FreePoint(v0), new FreePoint(v1)));
        
        // Now, we need to get a sorted set of all points where the vertices of the
        // two polygons project onto this line.
        Set<AlgebraicVector> projections = new TreeSet<>(); // TreeSet auto-sorts its contents
        
        // for each polygon, get a projection vector on its plane that is orthogonal to the line of intersection
        for (int poly = 0; poly < 2; poly++) {
            Polygon polygon = polygons[poly];
            AlgebraicVector v2 = v0.plus(polygon.getNormal());
            AlgebraicVector vProjection = AlgebraicVectors.getNormal(v0, v1, v2);
            // then use vProjection to project each vertex to intersectionLine
            for (int i = 0; i < polygon.getVertexCount(); i++) {
                AlgebraicVector vertex = polygon.getVertex(i);
                if(AlgebraicVectors.areCollinear(v0, v1, vertex)) {
                    // vertex is on the line of intersection, so use it as-is
                    projections.add(vertex);
                } else {
                    // I'm just going to use a local LineLineIntersectionPoint 
                    // to do the work instead of duplicating the math
                    Line projectionLine = new LineFromPointAndVector(vertex, vProjection);
                    Point projection = new LineLineIntersectionPoint(intersectionLine, projectionLine);
                    projections.add(projection.getLocation());
                }
            }
        }        
        
        AlgebraicVector start = null, offset = null;
        int n = 0;
        for (AlgebraicVector v : projections) {
            if (n == 0) {
                start = v; // this will be the smallest
            } else if (n == projections.size() - 1) {
                offset = v.minus(start); // this will be the offset of the largest
            }
            n++;
        }

        return setStateVariables(start, offset, (start == null || offset == null));
    }

}
