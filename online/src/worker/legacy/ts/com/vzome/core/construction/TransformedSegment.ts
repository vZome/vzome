/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Transformation} transform
     * @param {com.vzome.core.construction.Segment} prototype
     * @class
     * @extends com.vzome.core.construction.Segment
     */
    export class TransformedSegment extends com.vzome.core.construction.Segment {
        /*private*/ mTransform: com.vzome.core.construction.Transformation;

        /*private*/ mPrototype: com.vzome.core.construction.Segment;

        public constructor(transform: com.vzome.core.construction.Transformation, prototype: com.vzome.core.construction.Segment) {
            super(prototype.field);
            if (this.mTransform === undefined) { this.mTransform = null; }
            if (this.mPrototype === undefined) { this.mPrototype = null; }
            this.mTransform = transform;
            this.mPrototype = prototype;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mTransform.isImpossible() || this.mPrototype.isImpossible())return this.setStateVariables(null, null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getStart().projectTo3d(true));
            const end: com.vzome.core.algebra.AlgebraicVector = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getEnd().projectTo3d(true));
            if (end == null || loc == null)return this.setStateVariables(null, null, true);
            return this.setStateVariables(loc, end.minus(loc), false);
        }
    }
    TransformedSegment["__class"] = "com.vzome.core.construction.TransformedSegment";

}

