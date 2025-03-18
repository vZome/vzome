/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class PointToPointTranslation extends com.vzome.core.construction.Transformation {
        public constructor(p1: com.vzome.core.construction.Point, p2: com.vzome.core.construction.Point) {
            super(p1.field);
            this.mOffset = this.field.projectTo3d(p2.getLocation().minus(p1.getLocation()), true);
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            return arg.plus(this.mOffset);
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

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return this.setStateVariables(null, null, false);
        }
    }
    PointToPointTranslation["__class"] = "com.vzome.core.construction.PointToPointTranslation";

}

