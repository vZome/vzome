/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Segment} s1
     * @param {com.vzome.core.construction.Segment} s2
     * @class
     * @extends com.vzome.core.construction.Segment
     */
    export class SegmentCrossProduct extends com.vzome.core.construction.Segment {
        /*private*/ seg1: com.vzome.core.construction.Segment;

        /*private*/ seg2: com.vzome.core.construction.Segment;

        public constructor(s1: com.vzome.core.construction.Segment, s2: com.vzome.core.construction.Segment) {
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
            const v2: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.seg1.getOffset(), this.seg2.getOffset()).scale(this.field['createPower$int'](-4)).scale(this.field['createRational$long$long'](1, 2));
            return this.setStateVariables(this.seg1.getEnd(), v2, false);
        }
    }
    SegmentCrossProduct["__class"] = "com.vzome.core.construction.SegmentCrossProduct";

}

