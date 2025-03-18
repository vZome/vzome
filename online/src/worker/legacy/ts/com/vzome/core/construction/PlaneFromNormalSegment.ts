/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Point} intersection
     * @param {com.vzome.core.construction.Segment} normal
     * @class
     * @extends com.vzome.core.construction.Plane
     */
    export class PlaneFromNormalSegment extends com.vzome.core.construction.Plane {
        /*private*/ __com_vzome_core_construction_PlaneFromNormalSegment_mNormal: com.vzome.core.construction.Segment;

        /*private*/ mIntersection: com.vzome.core.construction.Point;

        public constructor(intersection: com.vzome.core.construction.Point, normal: com.vzome.core.construction.Segment) {
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

}

