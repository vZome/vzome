/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param loc
     * @param {com.vzome.core.construction.Segment} seg
     * @class
     * @extends com.vzome.core.construction.Point
     * @author Scott Vorthmann
     */
    export class SegmentMidpoint extends com.vzome.core.construction.Point {
        /*private*/ mSegment: com.vzome.core.construction.Segment;

        public constructor(seg: com.vzome.core.construction.Segment) {
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
            const half: com.vzome.core.algebra.AlgebraicNumber = this.field['createRational$long$long'](1, 2);
            let loc: com.vzome.core.algebra.AlgebraicVector = this.mSegment.getStart();
            loc = loc.plus(this.mSegment.getOffset().scale(half));
            return this.setStateVariable(loc, false);
        }
    }
    SegmentMidpoint["__class"] = "com.vzome.core.construction.SegmentMidpoint";

}

