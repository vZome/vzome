import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { Point } from "./Point.js";
import { Transformation } from "./Transformation.js";

export class PointToPointTranslation extends Transformation {
    public constructor(p1: Point, p2: Point) {
        super(p1.field);
        this.mOffset = this.field.projectTo3d(p2.getLocation().minus(p1.getLocation()), true);
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        return arg.plus(this.mOffset);
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
PointToPointTranslation["__class"] = "com.vzome.core.construction.PointToPointTranslation";
