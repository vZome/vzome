import { Bivector3dHomogeneous } from "../algebra/Bivector3dHomogeneous.js";
import { Vector3dHomogeneous } from "../algebra/Vector3dHomogeneous.js";
import { Line } from "./Line.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Segment} seg
 * @class
 * @extends Line
 */
export class LineExtensionOfSegment extends Line {
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
        if (this.mSegment.isImpossible())return this.setStateVariables(null, null, true);
        return this.setStateVariables(this.mSegment.getStart(), this.mSegment.getOffset(), false);
    }

    /**
     * 
     * @return {Bivector3dHomogeneous}
     */
    public getHomogeneous(): Bivector3dHomogeneous {
        const v1: Vector3dHomogeneous = new Vector3dHomogeneous(this.mSegment.getStart(), this.getField());
        const v2: Vector3dHomogeneous = new Vector3dHomogeneous(this.mSegment.getEnd(), this.getField());
        return v1.outer(v2);
    }
}
LineExtensionOfSegment["__class"] = "com.vzome.core.construction.LineExtensionOfSegment";
