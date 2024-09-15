/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.Quaternion} leftQuaternion
     * @param {com.vzome.core.algebra.Quaternion} rightQuaternion
     * @param {com.vzome.core.construction.Point} prototype
     * @class
     * @extends com.vzome.core.construction.Point
     */
    export class PointRotated4D extends com.vzome.core.construction.Point {
        /*private*/ mLeftQuaternion: com.vzome.core.algebra.Quaternion;

        /*private*/ mRightQuaternion: com.vzome.core.algebra.Quaternion;

        /*private*/ mPrototype: com.vzome.core.construction.Point;

        public constructor(leftQuaternion: com.vzome.core.algebra.Quaternion, rightQuaternion: com.vzome.core.algebra.Quaternion, prototype: com.vzome.core.construction.Point) {
            super(prototype.field);
            if (this.mLeftQuaternion === undefined) { this.mLeftQuaternion = null; }
            if (this.mRightQuaternion === undefined) { this.mRightQuaternion = null; }
            if (this.mPrototype === undefined) { this.mPrototype = null; }
            this.mLeftQuaternion = leftQuaternion;
            this.mRightQuaternion = rightQuaternion;
            this.mPrototype = prototype;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mPrototype.isImpossible())return this.setStateVariable(null, true);
            const field: com.vzome.core.algebra.AlgebraicField = this.mPrototype.getField();
            let loc: com.vzome.core.algebra.AlgebraicVector = field.origin(4);
            const loc3d: com.vzome.core.algebra.AlgebraicVector = this.mPrototype.getLocation();
            loc = loc3d.inflateTo4d$boolean(true);
            loc = this.mRightQuaternion.leftMultiply(loc);
            loc = this.mLeftQuaternion.rightMultiply(loc);
            return this.setStateVariable(loc, false);
        }
    }
    PointRotated4D["__class"] = "com.vzome.core.construction.PointRotated4D";

}

