/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class Translation extends com.vzome.core.construction.Transformation {
        /*private*/ mOffset: com.vzome.core.algebra.AlgebraicVector;

        public constructor(offset: com.vzome.core.algebra.AlgebraicVector) {
            super(offset.getField());
            if (this.mOffset === undefined) { this.mOffset = null; }
            this.mOffset = offset;
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            arg = arg.plus(this.mOffset);
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

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return this.setStateVariables(null, null, false);
        }
    }
    Translation["__class"] = "com.vzome.core.construction.Translation";

}

