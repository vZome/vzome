import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Segment} s1
 * @param {Segment} s2
 * @class
 * @extends Segment
 */
export class SegmentCrossProduct extends Segment {
    /*private*/ seg1: Segment;

    /*private*/ seg2: Segment;

    public constructor(s1: Segment, s2: Segment) {
        super(s1.field);
        if (this.seg1 === undefined) { this.seg1 = null; }
        if (this.seg2 === undefined) { this.seg2 = null; }
        this.seg1 = s1;
        this.seg2 = s2;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.seg1.isImpossible() || this.seg2.isImpossible()){
            return this.setStateVariables(null, null, true);
        }
        const v2: AlgebraicVector = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.seg1.getOffset(), this.seg2.getOffset()).scale(this.field['createPower$int'](-4)).scale(this.field['createRational$long$long'](1, 2));
        return this.setStateVariables(this.seg1.getEnd(), v2, false);
    }
}
SegmentCrossProduct["__class"] = "com.vzome.core.construction.SegmentCrossProduct";
