/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class MoveAndRotate extends com.vzome.core.construction.Transformation {
        /*private*/ rotation: com.vzome.core.construction.MatrixTransformation;

        /*private*/ translation: com.vzome.core.construction.Translation;

        public constructor(rotation: com.vzome.core.algebra.AlgebraicMatrix, start: com.vzome.core.algebra.AlgebraicVector, end: com.vzome.core.algebra.AlgebraicVector) {
            super(start.getField());
            if (this.rotation === undefined) { this.rotation = null; }
            if (this.translation === undefined) { this.translation = null; }
            this.rotation = new com.vzome.core.construction.MatrixTransformation(rotation, start);
            this.translation = new com.vzome.core.construction.Translation(end.minus(start));
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return this.rotation.mapParamsToState() && this.translation.mapParamsToState();
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            return this.translation.transform$com_vzome_core_algebra_AlgebraicVector(this.rotation.transform$com_vzome_core_algebra_AlgebraicVector(arg));
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
    MoveAndRotate["__class"] = "com.vzome.core.construction.MoveAndRotate";

}

