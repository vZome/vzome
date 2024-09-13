/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param step
     * @param {com.vzome.core.construction.Point} start
     * @param {com.vzome.core.math.symmetry.Axis} axis
     * @param {*} length
     * @class
     * @extends com.vzome.core.construction.Segment
     * @author Scott Vorthmann
     */
    export class AnchoredSegment extends com.vzome.core.construction.Segment {
        /*private*/ mAnchor: com.vzome.core.construction.Point;

        public mAxis: com.vzome.core.math.symmetry.Axis;

        public mLength: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber, start: com.vzome.core.construction.Point) {
            super(start.field);
            if (this.mAnchor === undefined) { this.mAnchor = null; }
            if (this.mAxis === undefined) { this.mAxis = null; }
            if (this.mLength === undefined) { this.mLength = null; }
            this.mAnchor = start;
            this.mAxis = axis;
            this.mLength = length;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mAnchor.isImpossible() || this.mLength.isZero())return this.setStateVariables(null, null, true);
            const gv: com.vzome.core.algebra.AlgebraicVector = this.mAnchor.getLocation().projectTo3d(true);
            const offset: com.vzome.core.algebra.AlgebraicVector = this.mAxis.normal().scale(this.mLength);
            return this.setStateVariables(gv, offset, false);
        }

        public getAxis(): com.vzome.core.math.symmetry.Axis {
            return this.mAxis;
        }

        public getLength(): com.vzome.core.algebra.AlgebraicNumber {
            return this.mLength;
        }

        public getUnitVector(): com.vzome.core.algebra.AlgebraicVector {
            return this.mAxis.normal();
        }
    }
    AnchoredSegment["__class"] = "com.vzome.core.construction.AnchoredSegment";

}

