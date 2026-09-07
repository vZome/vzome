import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Point} p1
 * @param {Point} p2
 * @class
 * @extends Segment
 */
export class SegmentJoiningPoints extends Segment {
    /*private*/ __com_vzome_core_construction_SegmentJoiningPoints_mStart: Point;

    /*private*/ __com_vzome_core_construction_SegmentJoiningPoints_mEnd: Point;

    public constructor(p1: Point, p2: Point) {
        super(p1.field);
        if (this.__com_vzome_core_construction_SegmentJoiningPoints_mStart === undefined) { this.__com_vzome_core_construction_SegmentJoiningPoints_mStart = null; }
        if (this.__com_vzome_core_construction_SegmentJoiningPoints_mEnd === undefined) { this.__com_vzome_core_construction_SegmentJoiningPoints_mEnd = null; }
        this.__com_vzome_core_construction_SegmentJoiningPoints_mStart = p1;
        this.__com_vzome_core_construction_SegmentJoiningPoints_mEnd = p2;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.__com_vzome_core_construction_SegmentJoiningPoints_mStart.isImpossible() || this.__com_vzome_core_construction_SegmentJoiningPoints_mEnd.isImpossible())return this.setStateVariables(null, null, true);
        let startV: AlgebraicVector = this.__com_vzome_core_construction_SegmentJoiningPoints_mStart.getLocation();
        let endV: AlgebraicVector = this.__com_vzome_core_construction_SegmentJoiningPoints_mEnd.getLocation();
        if (startV.dimension() === 3 || endV.dimension() === 3){
            startV = startV.projectTo3d(true);
            endV = endV.projectTo3d(true);
        }
        const offset: AlgebraicVector = endV.minus(startV);
        return this.setStateVariables(startV, offset, false);
    }
}
SegmentJoiningPoints["__class"] = "com.vzome.core.construction.SegmentJoiningPoints";
