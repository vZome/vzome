/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Point} p1
     * @param {com.vzome.core.construction.Point} p2
     * @class
     * @extends com.vzome.core.construction.Segment
     */
    export class SegmentJoiningPoints extends com.vzome.core.construction.Segment {
        /*private*/ __com_vzome_core_construction_SegmentJoiningPoints_mStart: com.vzome.core.construction.Point;

        /*private*/ __com_vzome_core_construction_SegmentJoiningPoints_mEnd: com.vzome.core.construction.Point;

        public constructor(p1: com.vzome.core.construction.Point, p2: com.vzome.core.construction.Point) {
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
            let startV: com.vzome.core.algebra.AlgebraicVector = this.__com_vzome_core_construction_SegmentJoiningPoints_mStart.getLocation();
            let endV: com.vzome.core.algebra.AlgebraicVector = this.__com_vzome_core_construction_SegmentJoiningPoints_mEnd.getLocation();
            if (startV.dimension() === 3 || endV.dimension() === 3){
                startV = startV.projectTo3d(true);
                endV = endV.projectTo3d(true);
            }
            const offset: com.vzome.core.algebra.AlgebraicVector = endV.minus(startV);
            return this.setStateVariables(startV, offset, false);
        }
    }
    SegmentJoiningPoints["__class"] = "com.vzome.core.construction.SegmentJoiningPoints";

}

