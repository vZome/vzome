import { AlgebraicField } from "../../core/algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../core/algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { AbstractSymmetry } from "../../core/math/symmetry/AbstractSymmetry.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { Direction } from "../../core/math/symmetry/Direction.js";
import { Permutation } from "../../core/math/symmetry/Permutation.js";
import { SpecialOrbit } from "../../core/math/symmetry/SpecialOrbit.js";
import { Symmetry } from "../../core/math/symmetry/Symmetry.js";

export class PentagonalAntiprismSymmetry extends AbstractSymmetry {
    static FIVEFOLD_AXIS: number[][]; public static FIVEFOLD_AXIS_$LI$(): number[][] { if (PentagonalAntiprismSymmetry.FIVEFOLD_AXIS == null) { PentagonalAntiprismSymmetry.FIVEFOLD_AXIS = [[-3, 1, 0, 1, -5, 1, 0, 1], [3, 1, 0, 1, 5, 1, 0, 1], [0, 1, 1, 1, 0, 1, 2, 1]]; }  return PentagonalAntiprismSymmetry.FIVEFOLD_AXIS; }

    static TWOFOLD_AXIS: number[][]; public static TWOFOLD_AXIS_$LI$(): number[][] { if (PentagonalAntiprismSymmetry.TWOFOLD_AXIS == null) { PentagonalAntiprismSymmetry.TWOFOLD_AXIS = [[0, 1], [2, 1, 0, 1, 3, 1, 0, 1], [0, 1, -3, 1, 0, 1, -5, 1]]; }  return PentagonalAntiprismSymmetry.TWOFOLD_AXIS; }

    static NEXT_TWOFOLD_AXIS: number[][]; public static NEXT_TWOFOLD_AXIS_$LI$(): number[][] { if (PentagonalAntiprismSymmetry.NEXT_TWOFOLD_AXIS == null) { PentagonalAntiprismSymmetry.NEXT_TWOFOLD_AXIS = [[2, 1, 0, 1, 3, 1, 0, 1], [3, 1, 0, 1, 5, 1, 0, 1], [0, 1, -2, 1, 0, 1, -3, 1]]; }  return PentagonalAntiprismSymmetry.NEXT_TWOFOLD_AXIS; }

    static FIVEFOLD_ROTATION: number[][][]; public static FIVEFOLD_ROTATION_$LI$(): number[][][] { if (PentagonalAntiprismSymmetry.FIVEFOLD_ROTATION == null) { PentagonalAntiprismSymmetry.FIVEFOLD_ROTATION = [[[-1, 1, 0, 1, 1, 1, 0, 1], [0, 1, 0, 1, 0, 1, 0, 1], [0, 1, 1, 1, 0, 1, -1, 1]], [[1, 1, 0, 1, -1, 1, 0, 1], [-1, 1, 0, 1, 1, 1, 0, 1], [0, 1, -2, 1, 0, 1, 1, 1]], [[0, 1, 2, 1, 0, 1, -1, 1], [0, 1, -1, 1, 0, 1, 1, 1], [2, 1, 0, 1, -1, 1, 0, 1]]]; }  return PentagonalAntiprismSymmetry.FIVEFOLD_ROTATION; }

    static TWOFOLD_ROTATION: number[][][]; public static TWOFOLD_ROTATION_$LI$(): number[][][] { if (PentagonalAntiprismSymmetry.TWOFOLD_ROTATION == null) { PentagonalAntiprismSymmetry.TWOFOLD_ROTATION = [[[-1, 1, 0, 1, 0, 1, 0, 1], [0, 1, 0, 1, 0, 1, 0, 1], [0, 1, 0, 1, 0, 1, 0, 1]], [[0, 1, 0, 1, 0, 1, 0, 1], [1, 1, 0, 1, -1, 1, 0, 1], [0, 1, 1, 1, 0, 1, -1, 1]], [[0, 1, 0, 1, 0, 1, 0, 1], [0, 1, 1, 1, 0, 1, -1, 1], [-1, 1, 0, 1, 1, 1, 0, 1]]]; }  return PentagonalAntiprismSymmetry.TWOFOLD_ROTATION; }

    static PRINCIPAL_REFLECTION: number[][][]; public static PRINCIPAL_REFLECTION_$LI$(): number[][][] { if (PentagonalAntiprismSymmetry.PRINCIPAL_REFLECTION == null) { PentagonalAntiprismSymmetry.PRINCIPAL_REFLECTION = [[[7, 5, 0, 1, -4, 5, 0, 1], [-2, 5, 0, 1, 4, 5, 0, 1], [0, 1, -8, 5, 0, 1, 6, 5]], [[-2, 5, 0, 1, 4, 5, 0, 1], [7, 5, 0, 1, -4, 5, 0, 1], [0, 1, 8, 5, 0, 1, -6, 5]], [[0, 1, -8, 5, 0, 1, 6, 5], [0, 1, 8, 5, 0, 1, -6, 5], [-9, 5, 0, 1, 8, 5, 0, 1]]]; }  return PentagonalAntiprismSymmetry.PRINCIPAL_REFLECTION; }

    /*private*/ preferredAxis: Axis;

    public constructor(field: AlgebraicField, frameColor: string) {
        super(10, field, frameColor, field.createMatrix(PentagonalAntiprismSymmetry.PRINCIPAL_REFLECTION_$LI$()));
        if (this.preferredAxis === undefined) { this.preferredAxis = null; }
    }

    /**
     * Called by the super constructor.
     */
    createInitialPermutations() {
        this.mOrientations[0] = new Permutation(this, null);
        let map: number[] = [1, 2, 3, 4, 0, 6, 7, 8, 9, 5];
        this.mOrientations[1] = new Permutation(this, map);
        map = [5, 9, 8, 7, 6, 0, 4, 3, 2, 1];
        this.mOrientations[5] = new Permutation(this, map);
    }

    /**
     * 
     * @param {string} frameColor
     */
    createFrameOrbit(frameColor: string) {
        this.mMatrices[0] = this.mField.identityMatrix(3);
        this.mMatrices[1] = this.mField.createMatrix(PentagonalAntiprismSymmetry.FIVEFOLD_ROTATION_$LI$());
        this.mMatrices[2] = this.mMatrices[1].times(this.mMatrices[1]);
        this.mMatrices[3] = this.mMatrices[1].times(this.mMatrices[2]);
        this.mMatrices[4] = this.mMatrices[1].times(this.mMatrices[3]);
        this.mMatrices[5] = this.mField.createMatrix(PentagonalAntiprismSymmetry.TWOFOLD_ROTATION_$LI$());
        this.mMatrices[6] = this.mMatrices[1].times(this.mMatrices[5]);
        this.mMatrices[7] = this.mMatrices[2].times(this.mMatrices[5]);
        this.mMatrices[8] = this.mMatrices[3].times(this.mMatrices[5]);
        this.mMatrices[9] = this.mMatrices[4].times(this.mMatrices[5]);
    }

    /**
     * 
     */
    createOtherOrbits() {
    }

    /**
     * 
     * @return {AlgebraicVector[]}
     */
    public getOrbitTriangle(): AlgebraicVector[] {
        const redVertex: AlgebraicVector = this.mField.createVector(PentagonalAntiprismSymmetry.FIVEFOLD_AXIS_$LI$());
        const greenVertex: AlgebraicVector = this.mField.createVector(PentagonalAntiprismSymmetry.TWOFOLD_AXIS_$LI$());
        const orthoVertex: AlgebraicVector = this.mField.createVector(PentagonalAntiprismSymmetry.NEXT_TWOFOLD_AXIS_$LI$());
        return [greenVertex, redVertex, orthoVertex];
    }

    public createStandardOrbits(frameColor: string): PentagonalAntiprismSymmetry {
        const redVertex: AlgebraicVector = this.mField.createVector(PentagonalAntiprismSymmetry.FIVEFOLD_AXIS_$LI$());
        const greenVertex: AlgebraicVector = this.mField.createVector(PentagonalAntiprismSymmetry.TWOFOLD_AXIS_$LI$());
        let unitLength: AlgebraicNumber = this.mField['createPower$int'](-6);
        const redOrbit: Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("red", 0, 1, redVertex, true, false, unitLength);
        this.preferredAxis = redOrbit.getAxis$int$int(Symmetry.PLUS, 0);
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("green", 0, 5, greenVertex, true, false, unitLength);
        unitLength = this.mField['createPower$int'](6);
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber("blue", 0, -1, this.mField.basisVector(3, AlgebraicVector.X), true, false, unitLength);
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
     * @return {string}
     */
    public getName(): string {
        return "pentagonal";
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
            return this.getDirection("green");
        default:
            return null;
        }
    }
}
PentagonalAntiprismSymmetry["__class"] = "com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry";
PentagonalAntiprismSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];
