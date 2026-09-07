import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Transformation } from "./Transformation.js";

export class MatrixTransformation extends Transformation {
    public constructor(matrix: AlgebraicMatrix, center: AlgebraicVector) {
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
