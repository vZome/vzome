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
export class SegmentMidpoint extends Point {
    /*private*/ mSegment: Segment;

    public constructor(seg: Segment) {
        super(seg.field);
        if (this.mSegment === undefined) { this.mSegment = null; }
        this.mSegment = seg;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mSegment.isImpossible())return this.setStateVariable(null, true);
        const half: AlgebraicNumber = this.field['createRational$long$long'](1, 2);
        let loc: AlgebraicVector = this.mSegment.getStart();
        loc = loc.plus(this.mSegment.getOffset().scale(half));
        return this.setStateVariable(loc, false);
    }
}
SegmentMidpoint["__class"] = "com.vzome.core.construction.SegmentMidpoint";
