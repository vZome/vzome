import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { Transformation } from "./Transformation.js";

export class Translation extends Transformation {
    /*private*/ mOffset: AlgebraicVector;

    public constructor(offset: AlgebraicVector) {
        super(offset.getField());
        if (this.mOffset === undefined) { this.mOffset = null; }
        this.mOffset = offset;
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        arg = arg.plus(this.mOffset);
        return arg;
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

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        return this.setStateVariables(null, null, false);
    }
}
Translation["__class"] = "com.vzome.core.construction.Translation";
