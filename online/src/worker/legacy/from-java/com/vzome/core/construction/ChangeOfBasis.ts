import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";
import { Transformation } from "./Transformation.js";

/**
 * This assumes that the starting basis is the usual X,Y,Z basis
 * @param {boolean} originalScaling
 * @param prototype
 * @param {Segment} newX
 * @param {Segment} newY
 * @param {Segment} newZ
 * @param {Point} kernel
 * @class
 * @extends Transformation
 * @author Scott Vorthmann
 */
export class ChangeOfBasis extends Transformation {
    /*private*/ mOld: Segment[];

    /*private*/ mNew: Segment[];

    /*private*/ mKernel: Point;

    /*private*/ scale: AlgebraicNumber;

    public constructor(newX?: any, newY?: any, newZ?: any, kernel?: any, originalScaling?: any) {
        if (((newX != null && newX instanceof <any>Segment) || newX === null) && ((newY != null && newY instanceof <any>Segment) || newY === null) && ((newZ != null && newZ instanceof <any>Segment) || newZ === null) && ((kernel != null && kernel instanceof <any>Point) || kernel === null) && ((typeof originalScaling === 'boolean') || originalScaling === null)) {
            let __args = arguments;
            super(newX.field);
            if (this.mOld === undefined) { this.mOld = null; } 
            if (this.mNew === undefined) { this.mNew = null; } 
            if (this.mKernel === undefined) { this.mKernel = null; } 
            if (this.scale === undefined) { this.scale = null; } 
            this.mNew = [newX, newY, newZ];
            this.mOld = null;
            this.mKernel = kernel;
            if (originalScaling)this.scale = this.field['createPower$int'](-5); else this.scale = this.field['createRational$long$long'](1, 2)['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createPower$int'](-3));
            this.mapParamsToState();
        } else if (((newX != null && newX instanceof <any>Array && (newX.length == 0 || newX[0] == null ||(newX[0] != null && newX[0] instanceof <any>Segment))) || newX === null) && ((newY != null && newY instanceof <any>Array && (newY.length == 0 || newY[0] == null ||(newY[0] != null && newY[0] instanceof <any>Segment))) || newY === null) && ((newZ != null && newZ instanceof <any>Point) || newZ === null) && kernel === undefined && originalScaling === undefined) {
            let __args = arguments;
            let oldBasis: any = __args[0];
            let newBasis: any = __args[1];
            let kernel: any = __args[2];
            super(oldBasis[0].field);
            if (this.mOld === undefined) { this.mOld = null; } 
            if (this.mNew === undefined) { this.mNew = null; } 
            if (this.mKernel === undefined) { this.mKernel = null; } 
            if (this.scale === undefined) { this.scale = null; } 
            this.mOld = oldBasis;
            this.mNew = newBasis;
            this.mKernel = kernel;
            this.scale = this.field['createRational$long'](2)['times$com_vzome_core_algebra_AlgebraicNumber'](this.field['createPower$int'](-7));
            this.mapParamsToState();
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        const loc: AlgebraicVector = this.mKernel.getLocation();
        if (this.mOld != null){
            const oldCommon: AlgebraicVector = ChangeOfBasis.findCommonVertex(this.mOld[0], this.mOld[1]);
            const offsets: AlgebraicVector[] = [null, null, null];
            for(let i: number = 0; i < offsets.length; i++) {{
                offsets[i] = this.mOld[i].getOffset().scale(this.scale);
                if (oldCommon.equals(this.mOld[i].getEnd()))offsets[i] = offsets[i].negate();
            };}
            const oldMatrix: AlgebraicMatrix = new AlgebraicMatrix(offsets);
            const newCommon: AlgebraicVector = ChangeOfBasis.findCommonVertex(this.mNew[0], this.mNew[1]);
            for(let i: number = 0; i < offsets.length; i++) {{
                offsets[i] = this.mNew[i].getOffset().scale(this.scale);
                if (newCommon.equals(this.mNew[i].getEnd()))offsets[i] = offsets[i].negate();
            };}
            const newMatrix: AlgebraicMatrix = new AlgebraicMatrix(offsets);
            const transform: AlgebraicMatrix = newMatrix.times(oldMatrix.inverse());
            return this.setStateVariables(transform, loc, false);
        } else {
            const transform: AlgebraicMatrix = new AlgebraicMatrix(this.mNew[0].getOffset().scale(this.scale), this.mNew[1].getOffset().scale(this.scale), this.mNew[2].getOffset().scale(this.scale));
            return this.setStateVariables(transform, loc, false);
        }
    }

    public static findCommonVertex(s1: Segment, s2: Segment): AlgebraicVector {
        let common: AlgebraicVector = s1.getStart();
        if (common.equals(s2.getStart()) || common.equals(s2.getEnd()))return common; else {
            common = s1.getEnd();
            if (common.equals(s2.getStart()) || common.equals(s2.getEnd()))return common; else return null;
        }
    }
}
ChangeOfBasis["__class"] = "com.vzome.core.construction.ChangeOfBasis";
