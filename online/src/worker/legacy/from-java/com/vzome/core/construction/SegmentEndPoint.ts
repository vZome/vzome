import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Segment} seg
 * @param {boolean} start
 * @class
 * @extends Point
 */
export class SegmentEndPoint extends Point {
    /*private*/ mSegment: Segment;

    /*private*/ start: boolean;

    public constructor(seg?: any, start?: any) {
        if (((seg != null && seg instanceof <any>Segment) || seg === null) && ((typeof start === 'boolean') || start === null)) {
            let __args = arguments;
            super(seg.field);
            if (this.mSegment === undefined) { this.mSegment = null; } 
            this.start = false;
            this.mSegment = seg;
            this.start = start;
            this.mapParamsToState();
        } else if (((seg != null && seg instanceof <any>Segment) || seg === null) && start === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let start: any = false;
                super(seg.field);
                if (this.mSegment === undefined) { this.mSegment = null; } 
                this.start = false;
                this.mSegment = seg;
                this.start = start;
                this.mapParamsToState();
            }
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mSegment.isImpossible())return this.setStateVariable(null, true);
        const loc: AlgebraicVector = this.start ? this.mSegment.getStart() : this.mSegment.getEnd();
        return this.setStateVariable(loc, false);
    }
}
SegmentEndPoint["__class"] = "com.vzome.core.construction.SegmentEndPoint";
