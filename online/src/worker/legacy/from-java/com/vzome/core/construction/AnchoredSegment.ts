import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";
import { Axis } from "../math/symmetry/Axis.js";

/**
 * @param step
 * @param {Point} start
 * @param {Axis} axis
 * @param {*} length
 * @class
 * @extends Segment
 * @author Scott Vorthmann
 */
export class AnchoredSegment extends Segment {
    /*private*/ mAnchor: Point;

    public mAxis: Axis;

    public mLength: AlgebraicNumber;

    public constructor(axis: Axis, length: AlgebraicNumber, start: Point) {
        super(start.field);
        if (this.mAnchor === undefined) { this.mAnchor = null; }
        if (this.mAxis === undefined) { this.mAxis = null; }
        if (this.mLength === undefined) { this.mLength = null; }
        this.mAnchor = start;
        this.mAxis = axis;
        this.mLength = length;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mAnchor.isImpossible() || this.mLength.isZero())return this.setStateVariables(null, null, true);
        const gv: AlgebraicVector = this.mAnchor.getLocation().projectTo3d(true);
        const offset: AlgebraicVector = this.mAxis.normal().scale(this.mLength);
        return this.setStateVariables(gv, offset, false);
    }

    public getAxis(): Axis {
        return this.mAxis;
    }

    public getLength(): AlgebraicNumber {
        return this.mLength;
    }

    public getUnitVector(): AlgebraicVector {
        return this.mAxis.normal();
    }
}
AnchoredSegment["__class"] = "com.vzome.core.construction.AnchoredSegment";
