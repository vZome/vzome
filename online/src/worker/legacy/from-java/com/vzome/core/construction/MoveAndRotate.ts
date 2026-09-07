import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { MatrixTransformation } from "./MatrixTransformation.js";
import { Transformation } from "./Transformation.js";
import { Translation } from "./Translation.js";

export class MoveAndRotate extends Transformation {
    /*private*/ rotation: MatrixTransformation;

    /*private*/ translation: Translation;

    public constructor(rotation: AlgebraicMatrix, start: AlgebraicVector, end: AlgebraicVector) {
        super(start.getField());
        if (this.rotation === undefined) { this.rotation = null; }
        if (this.translation === undefined) { this.translation = null; }
        this.rotation = new MatrixTransformation(rotation, start);
        this.translation = new Translation(end.minus(start));
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        return this.rotation.mapParamsToState() && this.translation.mapParamsToState();
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        return this.translation.transform$com_vzome_core_algebra_AlgebraicVector(this.rotation.transform$com_vzome_core_algebra_AlgebraicVector(arg));
    }

    /**
     * 
     * @param {AlgebraicVector} arg
     * @return {AlgebraicVector}
     */
    public transform(arg?: any): any {
        if (((arg != null && arg instanceof <any>AlgebraicVector) || arg === null)) {
            return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
        } else if (((arg != null && arg instanceof <any>Construction) || arg === null)) {
            return <any>this.transform$com_vzome_core_construction_Construction(arg);
        } else throw new Error('invalid overload');
    }
}
MoveAndRotate["__class"] = "com.vzome.core.construction.MoveAndRotate";
