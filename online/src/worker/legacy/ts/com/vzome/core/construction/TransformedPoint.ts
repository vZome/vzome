/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Transformation} transform
     * @param {com.vzome.core.construction.Point} prototype
     * @class
     * @extends com.vzome.core.construction.Point
     */
    export class TransformedPoint extends com.vzome.core.construction.Point {
        /*private*/ mTransform: com.vzome.core.construction.Transformation;

        /*private*/ mPrototype: com.vzome.core.construction.Point;

        public constructor(transform: com.vzome.core.construction.Transformation, prototype: com.vzome.core.construction.Point) {
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
            if (this.mTransform.isImpossible() || this.mPrototype.isImpossible())return this.setStateVariable(null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getLocation());
            return this.setStateVariable(loc, loc == null);
        }
    }
    TransformedPoint["__class"] = "com.vzome.core.construction.TransformedPoint";

}

