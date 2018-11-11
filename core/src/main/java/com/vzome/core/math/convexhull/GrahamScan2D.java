package com.vzome.core.math.convexhull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.commands.Command.Failure;

public class GrahamScan2D {
    private GrahamScan2D() {} // private c'tor for class with all static methods 
    
    /**
     * Constructs the 2d convex hull of a coplanar set of 3d points.
     *
     * @param points
     *          a set of 3d input points
     * @return  an array of the vertices of the planar convex hull.
     *          The points are ordered so that the normal of the resulting polygon points AWAY from the origin.
     *          The points in the array are unique, so the last point is NOT the same as the first.
     *          This means that polygon edges derived from this array must connect the last to the first. 
     * @throws Failure
     *          if the number of input points is less than three, 
     *          or if the points are collinear
     *          or if the points are not coplanar.
     */
    public static AlgebraicVector[] buildHull(Set<AlgebraicVector> points) throws Failure {
        if (points.size() < 3) {
            fail("At least three input points are required for a 2d convex hull.\n\n" + points.size() + " specified.");
        }
        AlgebraicVector normal = AlgebraicVectors.getNormal(points);  
        if(normal.isOrigin()) {
            fail("Cannot generate a 2d convex hull from collinear points");
        }
        if(!AlgebraicVectors.areOrthogonalTo(normal, points)) {
            fail("Cannot generate a 2d convex hull from non-coplanar points");
        }

        // Map each 3d point to a 2d projection and rotate it to the XY plane if necessary.
        // Since the 3d points are coplanar, it will be a 1:1 mapping
        // Later, we'll need to map 2d back to 3d so the 2d vector will be the key
        Map<AlgebraicVector, AlgebraicVector> xyTo3dMap = map3dToXY(points, normal);
        
        // calculate the 2d convex hull
        Deque<AlgebraicVector> stack2d = getHull2d(xyTo3dMap.keySet());

        // map the 2d convex hull back to the original 3d points
        AlgebraicVector[] vertices3d = new AlgebraicVector[stack2d.size()];
        
        int i = 0;
        for(AlgebraicVector point2d : stack2d) {
            AlgebraicVector point3d = xyTo3dMap.get(point2d);
            // order vertices3d so the normal will point away from the origin 
            // to make it consistent with the 3d convex hull algorithm
            vertices3d[i++] = point3d;
        }
        return vertices3d;
    }
    
    private static Map<AlgebraicVector, AlgebraicVector> map3dToXY(Collection<AlgebraicVector> points3d, AlgebraicVector normal) {
        int maxAxis = AlgebraicVectors.getMaxComponentIndex(normal);
        // preserve the right hand rule relationship of the 2 retained axes
        int mapX = (maxAxis + 1) % 3;
        int mapY = (maxAxis + 2) % 3;
        
        Map<AlgebraicVector, AlgebraicVector> map = new HashMap<>();
        for(AlgebraicVector point3d : points3d) {
            AlgebraicVector point2d = new AlgebraicVector(point3d.getComponent(mapX), point3d.getComponent(mapY));
            map.put(point2d, point3d);
        }
        return map;
    }

    private static Deque<AlgebraicVector> getHull2d(Set<AlgebraicVector> points2d) {
        List<AlgebraicVector> sortedPoints2d = getSortedPoints(points2d);
        Deque<AlgebraicVector> stack2d = new ArrayDeque<>();
        stack2d.push(sortedPoints2d.get(0));
        stack2d.push(sortedPoints2d.get(1));

        for (int i = 2; i < sortedPoints2d.size(); i++) {
            AlgebraicVector head = sortedPoints2d.get(i);
            AlgebraicVector middle = stack2d.pop();
            AlgebraicVector tail = stack2d.peek();

            int turn = getWindingDirection(tail, middle, head);

            switch(turn) {
                case 1: // COUNTER_CLOCKWISE
                    stack2d.push(middle);
                    stack2d.push(head);
                    break;
                case -1: // CLOCKWISE
                    i--;
                    break;
                case 0: // COLLINEAR
                    stack2d.push(head);
                    break;
                default:
                    throw new IllegalStateException("Illegal turn: " + turn);     
            }
        }
        return stack2d;
    }
        
    /**
     * @param points2d set of 2d points to be sorted
     * @return a list of points sorted: 
     *  1) in increasing order of the angle they and the lowest point make with the x-axis.
     *  2) by increasing distance from the lowest point.
     */
    private static List<AlgebraicVector> getSortedPoints(Set<AlgebraicVector> points2d) {
        AlgebraicVector lowest = getLowest2dPoint(points2d);
        List<AlgebraicVector> list = new ArrayList<>(points2d);
        list.sort(new Comparator<AlgebraicVector>() {
            @Override
            public int compare(AlgebraicVector a, AlgebraicVector b) {
                if(a.equals(b)) {
                    return 0;
                }
                if(a.equals(lowest)) {
                    return -1; // a equals lowest so it must be less than b
                }
                if(b.equals(lowest)) {
                    return 1; // b equals lowest so it must be greater than a
                }
                int turn = getWindingDirection(lowest, a, b);
                if(turn != 0) {
                    return -turn;
                }
                // a & b are collinear with lowest, so the closer of a and b will be the lesser
                AlgebraicNumber lengthSqA = AlgebraicVectors.getMagnitudeSquared(a.minus(lowest));
                AlgebraicNumber lengthSqB = AlgebraicVectors.getMagnitudeSquared(b.minus(lowest));
                return lengthSqA.compareTo(lengthSqB);
            }
        });
        return list;
    }
    
    /**
     * @param points2d a collection of 2d points from which to determine the lowest point.
     * @return  the point with the lowest y coordinate. 
     *          In case more than one point has the same minimum y coordinate, 
     *          the one with the lowest x coordinate is returned.
     */
    protected static AlgebraicVector getLowest2dPoint(Collection<AlgebraicVector> points2d) {
        AlgebraicVector lowest = null;
        for(AlgebraicVector point2d : points2d) {
            if(lowest == null) {
                lowest = point2d;
            } else {
                int signum = point2d.getComponent(AlgebraicVector.Y).minus(lowest.getComponent(AlgebraicVector.Y)).signum();
                switch(signum) {
                case -1:
                    lowest = point2d;
                    break;
                case 0:
                    if(point2d.getComponent(AlgebraicVector.X).lessThan(lowest.getComponent(AlgebraicVector.X))) {
                        lowest = point2d;
                    }
                    break;
                }
            }
        }
        return lowest;
    }
    
    /**
     * 
     * @param a 2d coordinate
     * @param b 2d coordinate
     * @param c 2d coordinate
     * @return -1, 0 or 1, depending on the orientation of vector ac with respect to vector ab:
     *       1: COUNTER_CLOCKWISE
     *             c
     *            /
     *           /
     *          a-----b
     *      -1: CLOCKWISE
     *             b
     *            /
     *           /
     *          a-----c
     *       0: COLLINEAR
     *          a-----b--c
     */
    private static int getWindingDirection(AlgebraicVector a, AlgebraicVector b, AlgebraicVector c) {
        AlgebraicVector ab = b.minus(a);
        AlgebraicVector ac = c.minus(a);
        return (new AlgebraicMatrix(ab, ac)).determinant().signum();
        
//        // This does the same thing, but doesn't make the math as clear
//        AlgebraicNumber ax = a.getComponent(X);
//        AlgebraicNumber bx = b.getComponent(X);
//        AlgebraicNumber cx = c.getComponent(X);
//        AlgebraicNumber ay = a.getComponent(Y);
//        AlgebraicNumber by = b.getComponent(Y);
//        AlgebraicNumber cy = c.getComponent(Y);
//
//        AlgebraicNumber crossProductDeterminant = ((bx.minus(ax)) .times (cy.minus(ay))) 
//                                           .minus ((by.minus(ay)) .times (cx.minus(ax)));
//        return crossProductDeterminant.signum(); 
    }
    
    private static void fail(String msg) throws Failure {
        throw new Failure(msg);
    }

}