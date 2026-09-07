import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Line } from "./Line.js";
import { Point } from "./Point.js";

/**
 * @param step
 * @param start
 * @param end
 * @param {Line} l1
 * @param {Line} l2
 * @param {Point} p
 * @class
 * @extends Line
 * @author Scott Vorthmann
 */
export class PerpendicularLine extends Line {
    /*private*/ mLine1: Line;

    /*private*/ mLine2: Line;

    /*private*/ mPoint: Point;

    public constructor(l1: Line, l2: Line, p: Point) {
        super(l1.field);
        if (this.mLine1 === undefined) { this.mLine1 = null; }
        if (this.mLine2 === undefined) { this.mLine2 = null; }
        if (this.mPoint === undefined) { this.mPoint = null; }
        this.mLine1 = l1;
        this.mLine2 = l2;
        this.mPoint = p;
        this.mapParamsToState();
    }

    /**
     * returns true if something changed.
     * @return
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mLine1.isImpossible() || this.mLine2.isImpossible() || this.mPoint.isImpossible()){
            return this.setStateVariables(null, null, true);
        }
        const normal: AlgebraicVector = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mLine1.getDirection(), this.mLine2.getDirection());
        return this.setStateVariables(this.mPoint.getLocation(), normal, normal.isOrigin());
    }
}
PerpendicularLine["__class"] = "com.vzome.core.construction.PerpendicularLine";
