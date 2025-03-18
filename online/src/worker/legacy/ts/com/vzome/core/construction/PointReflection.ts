/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param prototype
     * @param {com.vzome.core.construction.Point} center
     * @class
     * @extends com.vzome.core.construction.Transformation
     * @author Scott Vorthmann
     */
    export class PointReflection extends com.vzome.core.construction.Transformation {
        /*private*/ mCenter: com.vzome.core.construction.Point;

        public constructor(center: com.vzome.core.construction.Point) {
            super(center.field);
            if (this.mCenter === undefined) { this.mCenter = null; }
            this.mCenter = center;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mCenter.isImpossible())this.setStateVariables(null, null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.mCenter.getLocation().projectTo3d(true);
            return this.setStateVariables(null, loc, false);
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            arg = arg.minus(this.mOffset);
            arg = this.mOffset.minus(arg);
            return arg;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} arg
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public transform(arg?: any): any {
            if (((arg != null && arg instanceof <any>com.vzome.core.algebra.AlgebraicVector) || arg === null)) {
                return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
            } else if (((arg != null && arg instanceof <any>com.vzome.core.construction.Construction) || arg === null)) {
                return <any>this.transform$com_vzome_core_construction_Construction(arg);
            } else throw new Error('invalid overload');
        }
    }
    PointReflection["__class"] = "com.vzome.core.construction.PointReflection";

}

