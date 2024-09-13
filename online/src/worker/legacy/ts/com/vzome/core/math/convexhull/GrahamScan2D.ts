/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.convexhull {
    export class GrahamScan2D {
        constructor() {
        }

        /**
         * Constructs the 2d convex hull of a coplanar set of 3d points.
         * 
         * @param {*} points
         * a set of 3d input points
         * @return  {com.vzome.core.algebra.AlgebraicVector[]} an array of the vertices of the planar convex hull.
         * The points are ordered so that the normal of the resulting polygon points AWAY from the origin.
         * The points in the array are unique, so the last point is NOT the same as the first.
         * This means that polygon edges derived from this array must connect the last to the first.
         * @throws Failure
         * if the number of input points is less than three,
         * or if the points are collinear
         * or if the points are not coplanar.
         */
        public static buildHull(points: java.util.Set<com.vzome.core.algebra.AlgebraicVector>): com.vzome.core.algebra.AlgebraicVector[] {
            if (points.size() < 3){
                GrahamScan2D.fail("At least three input points are required for a 2d convex hull.\n\n" + points.size() + " specified.");
            }
            const normal: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$java_util_Collection(points);
            if (normal.isOrigin()){
                GrahamScan2D.fail("Cannot generate a 2d convex hull from collinear points");
            }
            if (!com.vzome.core.algebra.AlgebraicVectors.areOrthogonalTo(normal, points)){
                GrahamScan2D.fail("Cannot generate a 2d convex hull from non-coplanar points");
            }
            const keySet: java.util.Collection<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            const xyTo3dMap: java.util.Map<string, com.vzome.core.algebra.AlgebraicVector> = GrahamScan2D.map3dToXY(points, normal, keySet);
            const stack2d: java.util.Deque<com.vzome.core.algebra.AlgebraicVector> = GrahamScan2D.getHull2d(keySet);
            const vertices3d: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(stack2d.size());
            let i: number = 0;
            for(let index=stack2d.iterator();index.hasNext();) {
                let point2d = index.next();
                {
                    const point3d: com.vzome.core.algebra.AlgebraicVector = xyTo3dMap.get(point2d.toString(com.vzome.core.algebra.AlgebraicField.VEF_FORMAT));
                    vertices3d[i++] = point3d;
                }
            }
            return vertices3d;
        }

        /*private*/ static map3dToXY(points3d: java.util.Collection<com.vzome.core.algebra.AlgebraicVector>, normal: com.vzome.core.algebra.AlgebraicVector, keySet: java.util.Collection<com.vzome.core.algebra.AlgebraicVector>): java.util.Map<string, com.vzome.core.algebra.AlgebraicVector> {
            const maxAxis: number = com.vzome.core.algebra.AlgebraicVectors.getMaxComponentIndex(normal);
            const mapX: number = (maxAxis + 1) % 3;
            const mapY: number = (maxAxis + 2) % 3;
            const map: java.util.Map<string, com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.HashMap<any, any>());
            for(let index=points3d.iterator();index.hasNext();) {
                let point3d = index.next();
                {
                    const point2d: com.vzome.core.algebra.AlgebraicVector = new com.vzome.core.algebra.AlgebraicVector(point3d.getComponent(mapX), point3d.getComponent(mapY));
                    keySet.add(point2d);
                    map.put(point2d.toString(com.vzome.core.algebra.AlgebraicField.VEF_FORMAT), point3d);
                }
            }
            return map;
        }

        /*private*/ static getHull2d(points2d: java.util.Collection<com.vzome.core.algebra.AlgebraicVector>): java.util.Deque<com.vzome.core.algebra.AlgebraicVector> {
            const sortedPoints2d: java.util.List<com.vzome.core.algebra.AlgebraicVector> = GrahamScan2D.getSortedPoints(points2d);
            const stack2d: java.util.Deque<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayDeque<any>());
            stack2d.push(sortedPoints2d.get(0));
            stack2d.push(sortedPoints2d.get(1));
            for(let i: number = 2; i < sortedPoints2d.size(); i++) {{
                const head: com.vzome.core.algebra.AlgebraicVector = sortedPoints2d.get(i);
                const middle: com.vzome.core.algebra.AlgebraicVector = stack2d.pop();
                const tail: com.vzome.core.algebra.AlgebraicVector = stack2d.peek();
                const turn: number = GrahamScan2D.getWindingDirection(tail, middle, head);
                switch((turn)) {
                case 1:
                    stack2d.push(middle);
                    stack2d.push(head);
                    break;
                case -1:
                    i--;
                    break;
                case 0:
                    stack2d.push(head);
                    break;
                default:
                    throw new java.lang.IllegalStateException("Illegal turn: " + turn);
                }
            };}
            return stack2d;
        }

        /**
         * @param {*} points2d set of 2d points to be sorted
         * @return {*} a list of points sorted:
         * 1) in increasing order of the angle they and the lowest point make with the x-axis.
         * 2) by increasing distance from the lowest point.
         * @private
         */
        /*private*/ static getSortedPoints(points2d: java.util.Collection<com.vzome.core.algebra.AlgebraicVector>): java.util.List<com.vzome.core.algebra.AlgebraicVector> {
            const lowest: com.vzome.core.algebra.AlgebraicVector = GrahamScan2D.getLowest2dPoint(points2d);
            const list: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(points2d));
            java.util.Collections.sort<any>(list, (a: com.vzome.core.algebra.AlgebraicVector, b: com.vzome.core.algebra.AlgebraicVector) => {
                if (a.equals(b)){
                    return 0;
                }
                if (a.equals(lowest)){
                    return -1;
                }
                if (b.equals(lowest)){
                    return 1;
                }
                const turn: number = GrahamScan2D.getWindingDirection(lowest, a, b);
                if (turn !== 0){
                    return -turn;
                }
                const lengthSqA: com.vzome.core.algebra.AlgebraicNumber = com.vzome.core.algebra.AlgebraicVectors.getMagnitudeSquared(a.minus(lowest));
                const lengthSqB: com.vzome.core.algebra.AlgebraicNumber = com.vzome.core.algebra.AlgebraicVectors.getMagnitudeSquared(b.minus(lowest));
                return lengthSqA.compareTo(lengthSqB);
            });
            return list;
        }

        /**
         * @param {*} points2d a collection of 2d points from which to determine the lowest point.
         * @return  {com.vzome.core.algebra.AlgebraicVector} the point with the lowest y coordinate.
         * In case more than one point has the same minimum y coordinate,
         * the one with the lowest x coordinate is returned.
         */
        static getLowest2dPoint(points2d: java.util.Collection<com.vzome.core.algebra.AlgebraicVector>): com.vzome.core.algebra.AlgebraicVector {
            let lowest: com.vzome.core.algebra.AlgebraicVector = null;
            for(let index=points2d.iterator();index.hasNext();) {
                let point2d = index.next();
                {
                    if (lowest == null){
                        lowest = point2d;
                    } else {
                        const signum: number = point2d.getComponent(com.vzome.core.algebra.AlgebraicVector.Y)['minus$com_vzome_core_algebra_AlgebraicNumber'](lowest.getComponent(com.vzome.core.algebra.AlgebraicVector.Y)).signum();
                        switch((signum)) {
                        case -1:
                            lowest = point2d;
                            break;
                        case 0:
                            if (point2d.getComponent(com.vzome.core.algebra.AlgebraicVector.X).lessThan(lowest.getComponent(com.vzome.core.algebra.AlgebraicVector.X))){
                                lowest = point2d;
                            }
                            break;
                        }
                    }
                }
            }
            return lowest;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} a 2d coordinate
         * @param {com.vzome.core.algebra.AlgebraicVector} b 2d coordinate
         * @param {com.vzome.core.algebra.AlgebraicVector} c 2d coordinate
         * @return {number} -1, 0 or 1, depending on the orientation of vector ac with respect to vector ab:
         * 1: COUNTER_CLOCKWISE
         * c
         * /
         * /
         * a-----b
         * -1: CLOCKWISE
         * b
         * /
         * /
         * a-----c
         * 0: COLLINEAR
         * a-----b--c
         * @private
         */
        /*private*/ static getWindingDirection(a: com.vzome.core.algebra.AlgebraicVector, b: com.vzome.core.algebra.AlgebraicVector, c: com.vzome.core.algebra.AlgebraicVector): number {
            const ab: com.vzome.core.algebra.AlgebraicVector = b.minus(a);
            const ac: com.vzome.core.algebra.AlgebraicVector = c.minus(a);
            return (new com.vzome.core.algebra.AlgebraicMatrix(ab, ac)).determinant().signum();
        }

        /*private*/ static fail(msg: string) {
            throw new com.vzome.core.commands.Command.Failure(msg);
        }
    }
    GrahamScan2D["__class"] = "com.vzome.core.math.convexhull.GrahamScan2D";

}

