/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class LinePlaneIntersectionPoint extends com.vzome.core.construction.Point {
        /*private*/ mPlane: com.vzome.core.construction.Plane;

        /*private*/ mLine: com.vzome.core.construction.Line;

        public constructor(plane: com.vzome.core.construction.Plane, line: com.vzome.core.construction.Line) {
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
            const plane: com.vzome.core.algebra.Trivector3dHomogeneous = this.mPlane.getHomogeneous();
            const line: com.vzome.core.algebra.Bivector3dHomogeneous = this.mLine.getHomogeneous();
            const intersection: com.vzome.core.algebra.Vector3dHomogeneous = plane.dual().dot(line);
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
            const p1: com.vzome.core.algebra.AlgebraicVector = this.mLine.getStart();
            const p1p2: com.vzome.core.algebra.AlgebraicVector = this.mLine.getDirection();
            const n: com.vzome.core.algebra.AlgebraicVector = this.mPlane.getNormal();
            const p3: com.vzome.core.algebra.AlgebraicVector = this.mPlane.getBase();
            const p: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getLinePlaneIntersection(p1, p1p2, p3, n);
            if (p == null)return this.setStateVariable(null, true); else return this.setStateVariable(p, false);
        }
    }
    LinePlaneIntersectionPoint["__class"] = "com.vzome.core.construction.LinePlaneIntersectionPoint";

}

