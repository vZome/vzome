/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class MatrixTransformation extends com.vzome.core.construction.Transformation {
        public constructor(matrix: com.vzome.core.algebra.AlgebraicMatrix, center: com.vzome.core.algebra.AlgebraicVector) {
            super(center.getField());
            this.setStateVariables(matrix, center, false);
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return true;
        }
    }
    MatrixTransformation["__class"] = "com.vzome.core.construction.MatrixTransformation";

}

