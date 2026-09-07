import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { Plane } from "./Plane.js";
import { Transformation } from "./Transformation.js";

/**
 * @param prototype
 * @param {Plane} mirror
 * @class
 * @extends Transformation
 * @author Scott Vorthmann
 */
export class PlaneReflection extends Transformation {
    /*private*/ mMirror: Plane;

    /*private*/ mNormal: AlgebraicVector;

    /*private*/ mBase: AlgebraicVector;

    /*private*/ mNormDotReciprocal: AlgebraicNumber;

    public constructor(mirror: Plane) {
        super(mirror.field);
        if (this.mMirror === undefined) { this.mMirror = null; }
        if (this.mNormal === undefined) { this.mNormal = null; }
        if (this.mBase === undefined) { this.mBase = null; }
        if (this.mNormDotReciprocal === undefined) { this.mNormDotReciprocal = null; }
        this.mMirror = mirror;
        this.mNormal = mirror.getNormal();
        this.mBase = mirror.getBase();
        this.mNormDotReciprocal = this.mNormal.dot(this.mNormal).reciprocal();
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.mMirror.isImpossible())this.setStateVariables(null, null, true);
        const loc: AlgebraicVector = this.mMirror.getBase();
        return this.setStateVariables(null, loc, false);
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        arg = arg.minus(this.mBase);
        let xy: AlgebraicNumber = arg.dot(this.mNormal);
        xy = xy['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createRational$long'](2));
        arg = arg.minus(this.mNormal.scale(xy['times$com_vzome_core_algebra_AlgebraicNumber'](this.mNormDotReciprocal)));
        return arg.plus(this.mBase);
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
PlaneReflection["__class"] = "com.vzome.core.construction.PlaneReflection";
