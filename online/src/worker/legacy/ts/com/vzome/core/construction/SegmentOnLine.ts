/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Line} l3
     * @param {*} len
     * @class
     * @extends com.vzome.core.construction.Segment
     */
    export class SegmentOnLine extends com.vzome.core.construction.Segment {
        /*private*/ mLine: com.vzome.core.construction.Line;

        /*private*/ mLength: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(l3: com.vzome.core.construction.Line, len: com.vzome.core.algebra.AlgebraicNumber) {
            super(l3.field);
            if (this.mLine === undefined) { this.mLine = null; }
            if (this.mLength === undefined) { this.mLength = null; }
            this.mLine = l3;
            this.mLength = len;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mLine.isImpossible())return this.setStateVariables(null, null, true);
            const offset: com.vzome.core.algebra.AlgebraicVector = this.getOffset().scale(this.mLength);
            return this.setStateVariables(this.mLine.getStart(), offset, false);
        }
    }
    SegmentOnLine["__class"] = "com.vzome.core.construction.SegmentOnLine";

}

