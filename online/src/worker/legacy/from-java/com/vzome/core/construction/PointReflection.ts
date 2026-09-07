import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { Point } from "./Point.js";
import { Transformation } from "./Transformation.js";

/**
 * @param prototype
 * @param {Point} center
 * @class
 * @extends Transformation
 * @author Scott Vorthmann
 */
export class PointReflection extends Transformation {
    /*private*/ mCenter: Point;

    public constructor(center: Point) {
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
        const loc: AlgebraicVector = this.mCenter.getLocation().projectTo3d(true);
        return this.setStateVariables(null, loc, false);
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        arg = arg.minus(this.mOffset);
        arg = this.mOffset.minus(arg);
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
}
PointReflection["__class"] = "com.vzome.core.construction.PointReflection";
