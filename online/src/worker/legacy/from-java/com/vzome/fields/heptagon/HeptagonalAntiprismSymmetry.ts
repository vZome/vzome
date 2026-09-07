import { AlgebraicField } from "../../core/algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../core/algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../../core/algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { RealVector } from "../../core/math/RealVector.js";
import { AbstractSymmetry } from "../../core/math/symmetry/AbstractSymmetry.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { Direction } from "../../core/math/symmetry/Direction.js";
import { Permutation } from "../../core/math/symmetry/Permutation.js";
import { SpecialOrbit } from "../../core/math/symmetry/SpecialOrbit.js";
import { Symmetry } from "../../core/math/symmetry/Symmetry.js";

export class HeptagonalAntiprismSymmetry extends AbstractSymmetry {
    /*private*/ sigmaX2: number;

    /*private*/ skewFactor: number;

    /*private*/ correctedOrbits: boolean;

    /*private*/ preferredAxis: Axis;

    public constructor(field?: any, frameColor?: any, correctedOrbits?: any) {
        if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof frameColor === 'string') || frameColor === null) && ((typeof correctedOrbits === 'boolean') || correctedOrbits === null)) {
            let __args = arguments;
            super(14, field, frameColor, correctedOrbits ? new AlgebraicMatrix(field.basisVector(3, AlgebraicVector.X), field.basisVector(3, AlgebraicVector.Y), field.basisVector(3, AlgebraicVector.Z).negate()) : null);
            if (this.sigmaX2 === undefined) { this.sigmaX2 = 0; } 
            if (this.skewFactor === undefined) { this.skewFactor = 0; } 
            if (this.correctedOrbits === undefined) { this.correctedOrbits = false; } 
            if (this.preferredAxis === undefined) { this.preferredAxis = null; } 
            this.sigmaX2 = field.getUnitTerm(2).timesInt(2).evaluate();
            this.skewFactor = Math.sin((3.0 / 7.0) * Math.PI);
            this.correctedOrbits = correctedOrbits;
        } else if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof frameColor === 'string') || frameColor === null) && correctedOrbits === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let correctedOrbits: any = false;
                super(14, field, frameColor, correctedOrbits ? new AlgebraicMatrix(field.basisVector(3, AlgebraicVector.X), field.basisVector(3, AlgebraicVector.Y), field.basisVector(3, AlgebraicVector.Z).negate()) : null);
                if (this.sigmaX2 === undefined) { this.sigmaX2 = 0; } 
                if (this.skewFactor === undefined) { this.skewFactor = 0; } 
                if (this.correctedOrbits === undefined) { this.correctedOrbits = false; } 
                if (this.preferredAxis === undefined) { this.preferredAxis = null; } 
                this.sigmaX2 = field.getUnitTerm(2).timesInt(2).evaluate();
                this.skewFactor = Math.sin((3.0 / 7.0) * Math.PI);
                this.correctedOrbits = correctedOrbits;
            }
        } else throw new Error('invalid overload');
    }

    /**
     * Called by the super constructor.
     */
    createInitialPermutations() {
        this.mOrientations[0] = new Permutation(this, null);
        let map: number[] = [1, 2, 3, 4, 5, 6, 0, 8, 9, 10, 11, 12, 13, 7];
        this.mOrientations[1] = new Permutation(this, map);
        map = [7, 13, 12, 11, 10, 9, 8, 0, 6, 5, 4, 3, 2, 1];
        this.mOrientations[7] = new Permutation(this, map);
    }

    /**
     * 
     * @param {string} frameColor
     */
    createFrameOrbit(frameColor: string) {
        const hf: AlgebraicField = this.mField;
        const one: AlgebraicNumber = hf.one();
        const s: AlgebraicNumber = hf.getUnitTerm(2).reciprocal();
        const R: AlgebraicNumber = hf['createPower$int'](1)['times$com_vzome_core_algebra_AlgebraicNumber'](s);
        const zAxis: AlgebraicVector = hf.basisVector(3, AlgebraicVector.Z);
        const zAxisNeg: AlgebraicVector = zAxis.negate();
        const axis0: AlgebraicVector = hf.basisVector(3, AlgebraicVector.X);
        const axis1: AlgebraicVector = hf.origin(3).setComponent(AlgebraicVector.X, s).setComponent(AlgebraicVector.Y, R);
        const axis2: AlgebraicVector = hf.origin(3).setComponent(AlgebraicVector.X, s.negate()).setComponent(AlgebraicVector.Y, one);
        const axis3: AlgebraicVector = hf.origin(3).setComponent(AlgebraicVector.X, one.negate()).setComponent(AlgebraicVector.Y, s);
        const axis4: AlgebraicVector = hf.origin(3).setComponent(AlgebraicVector.X, R.negate()).setComponent(AlgebraicVector.Y, s.negate());
        const axis5: AlgebraicVector = hf.origin(3).setComponent(AlgebraicVector.Y, one.negate());
        const axis6: AlgebraicVector = hf.origin(3).setComponent(AlgebraicVector.X, R).setComponent(AlgebraicVector.Y, R.negate());
        this.mMatrices[0] = hf.identityMatrix(3);
        this.mMatrices[1] = new AlgebraicMatrix(axis1, axis6.negate(), zAxis);
        this.mMatrices[2] = new AlgebraicMatrix(axis2, axis0.negate(), zAxis);
        this.mMatrices[3] = new AlgebraicMatrix(axis3, axis1.negate(), zAxis);
        this.mMatrices[4] = new AlgebraicMatrix(axis4, axis2.negate(), zAxis);
        this.mMatrices[5] = new AlgebraicMatrix(axis5, axis3.negate(), zAxis);
        this.mMatrices[6] = new AlgebraicMatrix(axis6, axis4.negate(), zAxis);
        this.mMatrices[7] = new AlgebraicMatrix(axis0, axis2.negate(), zAxisNeg);
        this.mMatrices[8] = this.mMatrices[1].times(this.mMatrices[7]);
        this.mMatrices[9] = this.mMatrices[2].times(this.mMatrices[7]);
        this.mMatrices[10] = this.mMatrices[3].times(this.mMatrices[7]);
        this.mMatrices[11] = this.mMatrices[4].times(this.mMatrices[7]);
        this.mMatrices[12] = this.mMatrices[5].times(this.mMatrices[7]);
        this.mMatrices[13] = this.mMatrices[6].times(this.mMatrices[7]);
    }

    /**
     * 
     */
    createOtherOrbits() {
    }

    public createStandardOrbits(frameColor: string): HeptagonalAntiprismSymmetry {
        const redOrbit: Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("red", 0, 1, this.mField.basisVector(3, AlgebraicVector.Z), true);
        this.preferredAxis = redOrbit.getAxis$int$int(Symmetry.PLUS, 0);
        const blueFrameVector: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.X);
        const blueOrbit: Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, 7, blueFrameVector, true);
        const blueRotatedVector: AlgebraicVector = blueOrbit.getAxis$int$int(Symmetry.PLUS, ((7 + 1) / 2|0)).normal();
        const greenVector: AlgebraicVector = blueFrameVector.minus(blueRotatedVector);
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector("green", 0, 7, greenVector);
        return this;
    }

    /**
     * 
     * @return {Axis}
     */
    public getPreferredAxis(): Axis {
        return this.preferredAxis;
    }

    /**
     * 
     * @param {AlgebraicVector} v
     * @return {RealVector}
     */
    public embedInR3(v: AlgebraicVector): RealVector {
        const rv: RealVector = super.embedInR3(v);
        const x: number = rv.x + (rv.y / this.sigmaX2);
        const y: number = rv.y * this.skewFactor;
        return new RealVector(x, y, rv.z);
    }

    /**
     * 
     * @param {AlgebraicVector} v
     * @return {double[]}
     */
    public embedInR3Double(v: AlgebraicVector): number[] {
        const dv: number[] = super.embedInR3Double(v);
        const x: number = dv[0] + (dv[1] / this.sigmaX2);
        const y: number = dv[1] * this.skewFactor;
        return [x, y, dv[2]];
    }

    /**
     * 
     * @return {boolean}
     */
    public isTrivial(): boolean {
        return false;
    }

    /**
     * 
     * @return {string}
     */
    public getName(): string {
        if (this.correctedOrbits)return "heptagonal antiprism corrected"; else return "heptagonal antiprism";
    }

    /**
     * 
     * @param {string} name
     * @return {int[]}
     */
    public subgroup(name: string): number[] {
        return null;
    }

    /**
     * 
     * @return {AlgebraicVector[]}
     */
    public getOrbitTriangle(): AlgebraicVector[] {
        const field: AlgebraicField = this.getField();
        const zero: AlgebraicNumber = field.zero();
        let x: AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, -1, -1]).dividedBy(field['createAlgebraicNumber$int_A']([0, 0, 2]));
        const orthoVertex: AlgebraicVector = new AlgebraicVector(x, zero, zero);
        const sideVertex: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z);
        x = field['createRational$long'](-1);
        const y: AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, -1, 1]);
        const topVertex: AlgebraicVector = new AlgebraicVector(x, y, zero);
        return [orthoVertex, sideVertex, topVertex];
    }

    /**
     * 
     * @param {SpecialOrbit} which
     * @return {Direction}
     */
    public getSpecialOrbit(which: SpecialOrbit): Direction {
        switch((which)) {
        case SpecialOrbit.BLUE:
            return this.getDirection("blue");
        case SpecialOrbit.RED:
            return this.getDirection("red");
        case SpecialOrbit.YELLOW:
            return this.getDirection("blue");
        default:
            return null;
        }
    }
}
HeptagonalAntiprismSymmetry["__class"] = "com.vzome.fields.heptagon.HeptagonalAntiprismSymmetry";
HeptagonalAntiprismSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];
