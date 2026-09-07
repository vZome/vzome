import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../../algebra/AlgebraicVectors.js";
import { Command } from "../../commands/Command.js";

export class GrahamScan2D {
    constructor() {
    }

    /**
     * Constructs the 2d convex hull of a coplanar set of 3d points.
     * 
     * @param {*} points
     * a set of 3d input points
     * @return  {AlgebraicVector[]} an array of the vertices of the planar convex hull.
     * The points are ordered so that the normal of the resulting polygon points AWAY from the origin.
     * The points in the array are unique, so the last point is NOT the same as the first.
     * This means that polygon edges derived from this array must connect the last to the first.
     * @throws Failure
     * if the number of input points is less than three,
     * or if the points are collinear
     * or if the points are not coplanar.
     */
    public static buildHull(points: java.util.Set<AlgebraicVector>): AlgebraicVector[] {
        if (points.size() < 3){
            GrahamScan2D.fail("At least three input points are required for a 2d convex hull.\n\n" + points.size() + " specified.");
        }
        const normal: AlgebraicVector = AlgebraicVectors.getNormal$java_util_Collection(points);
        if (normal.isOrigin()){
            GrahamScan2D.fail("Cannot generate a 2d convex hull from collinear points");
        }
        if (!AlgebraicVectors.areOrthogonalTo(normal, points)){
            GrahamScan2D.fail("Cannot generate a 2d convex hull from non-coplanar points");
        }
        const keySet: java.util.Collection<AlgebraicVector> = <any>(new java.util.ArrayList<any>());
        const xyTo3dMap: java.util.Map<string, AlgebraicVector> = GrahamScan2D.map3dToXY(points, normal, keySet);
        const stack2d: java.util.Deque<AlgebraicVector> = GrahamScan2D.getHull2d(keySet);
        const vertices3d: AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(stack2d.size());
        let i: number = 0;
        for(let index=stack2d.iterator();index.hasNext();) {
            let point2d = index.next();
            {
                const point3d: AlgebraicVector = xyTo3dMap.get(point2d.toString(AlgebraicField.VEF_FORMAT));
                vertices3d[i++] = point3d;
            }
        }
        return vertices3d;
    }

    /*private*/ static map3dToXY(points3d: java.util.Collection<AlgebraicVector>, normal: AlgebraicVector, keySet: java.util.Collection<AlgebraicVector>): java.util.Map<string, AlgebraicVector> {
        const maxAxis: number = AlgebraicVectors.getMaxComponentIndex(normal);
        const mapX: number = (maxAxis + 1) % 3;
        const mapY: number = (maxAxis + 2) % 3;
        const map: java.util.Map<string, AlgebraicVector> = <any>(new java.util.HashMap<any, any>());
        for(let index=points3d.iterator();index.hasNext();) {
            let point3d = index.next();
            {
                const point2d: AlgebraicVector = new AlgebraicVector(point3d.getComponent(mapX), point3d.getComponent(mapY));
                keySet.add(point2d);
                map.put(point2d.toString(AlgebraicField.VEF_FORMAT), point3d);
            }
        }
        return map;
    }

    /*private*/ static getHull2d(points2d: java.util.Collection<AlgebraicVector>): java.util.Deque<AlgebraicVector> {
        const sortedPoints2d: java.util.List<AlgebraicVector> = GrahamScan2D.getSortedPoints(points2d);
        const stack2d: java.util.Deque<AlgebraicVector> = <any>(new java.util.ArrayDeque<any>());
        stack2d.push(sortedPoints2d.get(0));
        stack2d.push(sortedPoints2d.get(1));
        for(let i: number = 2; i < sortedPoints2d.size(); i++) {{
            const head: AlgebraicVector = sortedPoints2d.get(i);
            const middle: AlgebraicVector = stack2d.pop();
            const tail: AlgebraicVector = stack2d.peek();
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
    /*private*/ static getSortedPoints(points2d: java.util.Collection<AlgebraicVector>): java.util.List<AlgebraicVector> {
        const lowest: AlgebraicVector = GrahamScan2D.getLowest2dPoint(points2d);
        const list: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>(points2d));
        java.util.Collections.sort<any>(list, (a: AlgebraicVector, b: AlgebraicVector) => {
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
            const lengthSqA: AlgebraicNumber = AlgebraicVectors.getMagnitudeSquared(a.minus(lowest));
            const lengthSqB: AlgebraicNumber = AlgebraicVectors.getMagnitudeSquared(b.minus(lowest));
            return lengthSqA.compareTo(lengthSqB);
        });
        return list;
    }

    /**
     * @param {*} points2d a collection of 2d points from which to determine the lowest point.
     * @return  {AlgebraicVector} the point with the lowest y coordinate.
     * In case more than one point has the same minimum y coordinate,
     * the one with the lowest x coordinate is returned.
     */
    static getLowest2dPoint(points2d: java.util.Collection<AlgebraicVector>): AlgebraicVector {
        let lowest: AlgebraicVector = null;
        for(let index=points2d.iterator();index.hasNext();) {
            let point2d = index.next();
            {
                if (lowest == null){
                    lowest = point2d;
                } else {
                    const signum: number = point2d.getComponent(AlgebraicVector.Y)['minus$com_vzome_core_algebra_AlgebraicNumber'](lowest.getComponent(AlgebraicVector.Y)).signum();
                    switch((signum)) {
                    case -1:
                        lowest = point2d;
                        break;
                    case 0:
                        if (point2d.getComponent(AlgebraicVector.X).lessThan(lowest.getComponent(AlgebraicVector.X))){
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
     * @param {AlgebraicVector} a 2d coordinate
     * @param {AlgebraicVector} b 2d coordinate
     * @param {AlgebraicVector} c 2d coordinate
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
    /*private*/ static getWindingDirection(a: AlgebraicVector, b: AlgebraicVector, c: AlgebraicVector): number {
        const ab: AlgebraicVector = b.minus(a);
        const ac: AlgebraicVector = c.minus(a);
        return (new AlgebraicMatrix(ab, ac)).determinant().signum();
    }

    /*private*/ static fail(msg: string) {
        throw new Command.Failure(msg);
    }
}
GrahamScan2D["__class"] = "com.vzome.core.math.convexhull.GrahamScan2D";
