import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Line } from "./Line.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Line} l3
 * @param {*} len
 * @class
 * @extends Segment
 */
export class SegmentOnLine extends Segment {
    /*private*/ mLine: Line;

    /*private*/ mLength: AlgebraicNumber;

    public constructor(l3: Line, len: AlgebraicNumber) {
        super(l3.field);
        if (this.mLine === undefined) { this.mLine = null; }
        if (this.mLength === undefined) { this.mLength = null; }
        this.mLine = l3;
        this.mLength = len;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mLine.isImpossible())return this.setStateVariables(null, null, true);
        const offset: AlgebraicVector = this.getOffset().scale(this.mLength);
        return this.setStateVariables(this.mLine.getStart(), offset, false);
    }
}
SegmentOnLine["__class"] = "com.vzome.core.construction.SegmentOnLine";
