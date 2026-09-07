import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";

/**
 * @param loc
 * @param {Segment} seg
 * @class
 * @extends Point
 * @author Scott Vorthmann
 */
export class SegmentTauDivision extends Point {
    /*private*/ mSegment: Segment;

    public constructor(seg: Segment) {
        super(seg.field);
        if (this.mSegment === undefined) { this.mSegment = null; }
        if (this.shrink === undefined) { this.shrink = null; }
        this.mSegment = seg;
        this.shrink = this.field['createPower$int'](-1);
        this.mapParamsToState();
    }

    shrink: AlgebraicNumber;

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mSegment.isImpossible())return this.setStateVariable(null, true);
        let loc: AlgebraicVector = this.mSegment.getStart();
        const off: AlgebraicVector = this.mSegment.getOffset().scale(this.shrink);
        loc = loc.plus(off);
        return this.setStateVariable(loc, false);
    }
}
SegmentTauDivision["__class"] = "com.vzome.core.construction.SegmentTauDivision";
