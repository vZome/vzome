import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Bivector3dHomogeneous } from "../algebra/Bivector3dHomogeneous.js";
import { Trivector3dHomogeneous } from "../algebra/Trivector3dHomogeneous.js";
import { Vector3dHomogeneous } from "../algebra/Vector3dHomogeneous.js";
import { Line } from "./Line.js";
import { Plane } from "./Plane.js";
import { Point } from "./Point.js";

export class LinePlaneIntersectionPoint extends Point {
    /*private*/ mPlane: Plane;

    /*private*/ mLine: Line;

    public constructor(plane: Plane, line: Line) {
        super(line.field);
        if (this.mPlane === undefined) { this.mPlane = null; }
        if (this.mLine === undefined) { this.mLine = null; }
        this.mPlane = plane;
        this.mLine = line;
        this.mapParamsToState();
    }

    /**
     * From Vince, GA4CG, p. 196.
     * 
     * @author Scott Vorthmann
     * @return {boolean}
     */
    mapParamsToState_usingGA(): boolean {
        if (this.mPlane.isImpossible() || this.mLine.isImpossible())return this.setStateVariable(null, true);
        const plane: Trivector3dHomogeneous = this.mPlane.getHomogeneous();
        const line: Bivector3dHomogeneous = this.mLine.getHomogeneous();
        const intersection: Vector3dHomogeneous = plane.dual().dot(line);
        if (!intersection.exists())return this.setStateVariable(null, true);
        return this.setStateVariable(intersection.getVector(), false);
    }

    /**
     * from http://astronomy.swin.edu.au/~pbourke/geometry/planeline/:
     * 
     * 
     * The equation of a plane (points P are on the plane with normal N and point P3 on the plane) can be written as
     * 
     * N dot (P - P3) = 0
     * 
     * The equation of the line (points P on the line passing through points P1 and P2) can be written as
     * 
     * P = P1 + u (P2 - P1)
     * 
     * The intersection of these two occurs when
     * 
     * N dot (P1 + u (P2 - P1)) = N dot P3
     * 
     * Solving for u gives
     * 
     * u = ( N dot (P3-P1) ) / ( N dot (P2-P1) )
     * 
     * If the denominator is zero, the line is parallel to the plane.
     * 
     * @author Scott Vorthmann
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mPlane.isImpossible() || this.mLine.isImpossible())return this.setStateVariable(null, true);
        const p1: AlgebraicVector = this.mLine.getStart();
        const p1p2: AlgebraicVector = this.mLine.getDirection();
        const n: AlgebraicVector = this.mPlane.getNormal();
        const p3: AlgebraicVector = this.mPlane.getBase();
        const p: AlgebraicVector = AlgebraicVectors.getLinePlaneIntersection(p1, p1p2, p3, n);
        if (p == null)return this.setStateVariable(null, true); else return this.setStateVariable(p, false);
    }
}
LinePlaneIntersectionPoint["__class"] = "com.vzome.core.construction.LinePlaneIntersectionPoint";
