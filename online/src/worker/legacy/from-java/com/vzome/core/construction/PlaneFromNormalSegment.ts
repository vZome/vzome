import { Plane } from "./Plane.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";

/**
 * @author Scott Vorthmann
 * @param {Point} intersection
 * @param {Segment} normal
 * @class
 * @extends Plane
 */
export class PlaneFromNormalSegment extends Plane {
    /*private*/ __com_vzome_core_construction_PlaneFromNormalSegment_mNormal: Segment;

    /*private*/ mIntersection: Point;

    public constructor(intersection: Point, normal: Segment) {
        super(intersection.field);
        if (this.__com_vzome_core_construction_PlaneFromNormalSegment_mNormal === undefined) { this.__com_vzome_core_construction_PlaneFromNormalSegment_mNormal = null; }
        if (this.mIntersection === undefined) { this.mIntersection = null; }
        this.__com_vzome_core_construction_PlaneFromNormalSegment_mNormal = normal;
        this.mIntersection = intersection;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.__com_vzome_core_construction_PlaneFromNormalSegment_mNormal.isImpossible() || this.mIntersection.isImpossible())return this.setStateVariables(null, null, true);
        return this.setStateVariables(this.mIntersection.getLocation(), this.__com_vzome_core_construction_PlaneFromNormalSegment_mNormal.getOffset(), false);
    }
}
PlaneFromNormalSegment["__class"] = "com.vzome.core.construction.PlaneFromNormalSegment";
