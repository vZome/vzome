/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param loc
     * @param {com.vzome.core.construction.Segment} seg
     * @class
     * @extends com.vzome.core.construction.Point
     * @author Scott Vorthmann
     */
    export class SegmentTauDivision extends com.vzome.core.construction.Point {
        /*private*/ mSegment: com.vzome.core.construction.Segment;

        public constructor(seg: com.vzome.core.construction.Segment) {
            super(seg.field);
            if (this.mSegment === undefined) { this.mSegment = null; }
            if (this.shrink === undefined) { this.shrink = null; }
            this.mSegment = seg;
            this.shrink = this.field['createPower$int'](-1);
            this.mapParamsToState();
        }

        shrink: com.vzome.core.algebra.AlgebraicNumber;

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mSegment.isImpossible())return this.setStateVariable(null, true);
            let loc: com.vzome.core.algebra.AlgebraicVector = this.mSegment.getStart();
            const off: com.vzome.core.algebra.AlgebraicVector = this.mSegment.getOffset().scale(this.shrink);
            loc = loc.plus(off);
            return this.setStateVariable(loc, false);
        }
    }
    SegmentTauDivision["__class"] = "com.vzome.core.construction.SegmentTauDivision";

}

