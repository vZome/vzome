import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { AbstractSymmetry } from "./AbstractSymmetry.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { Permutation } from "./Permutation.js";
import { SpecialOrbit } from "./SpecialOrbit.js";
import { Symmetry } from "./Symmetry.js";

/**
 * @author Scott Vorthmann
 * @param {*} field
 * @class
 * @extends AbstractSymmetry
 */
export class DodecagonalSymmetry extends AbstractSymmetry {
    static ORDER: number = 12;

    public IDENTITY: Permutation;

    public constructor(field: AlgebraicField) {
        super(DodecagonalSymmetry.ORDER, field, "blue", null);
        this.IDENTITY = new Permutation(this, null);
    }

    /**
     * 
     */
    createInitialPermutations() {
        this.mOrientations[0] = this.IDENTITY;
        const map: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(DodecagonalSymmetry.ORDER);
        for(let i: number = 0; i < DodecagonalSymmetry.ORDER; i++) {map[i] = (i + 1) % DodecagonalSymmetry.ORDER;}
        this.mOrientations[1] = new Permutation(this, map);
    }

    /**
     * 
     * @param {string} frameColor
     */
    createFrameOrbit(frameColor: string) {
        const xAxis: AlgebraicVector = this.mField.createVector([[1, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
        const dir: Direction = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, 15, xAxis, true);
        dir.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(0, Symmetry.NO_ROTATION, xAxis);
        dir.createAxis$int$int$int_A_A(1, Symmetry.NO_ROTATION, [[0, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(2, Symmetry.NO_ROTATION, [[1, 2, 0, 1], [0, 1, 1, 2], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(3, Symmetry.NO_ROTATION, [[0, 1, 0, 1], [1, 1, 0, 1], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(4, Symmetry.NO_ROTATION, [[-1, 2, 0, 1], [0, 1, 1, 2], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(5, Symmetry.NO_ROTATION, [[0, 1, -1, 2], [1, 2, 0, 1], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(6, Symmetry.NO_ROTATION, [[-1, 1, 0, 1], [0, 1, 0, 1], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(7, Symmetry.NO_ROTATION, [[0, 1, -1, 2], [-1, 2, 0, 1], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(8, Symmetry.NO_ROTATION, [[-1, 2, 0, 1], [0, 1, -1, 2], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(9, Symmetry.NO_ROTATION, [[0, 1, 0, 1], [-1, 1, 0, 1], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(10, Symmetry.NO_ROTATION, [[1, 2, 0, 1], [0, 1, -1, 2], [0, 1, 0, 1]]);
        dir.createAxis$int$int$int_A_A(11, Symmetry.NO_ROTATION, [[0, 1, 1, 2], [-1, 2, 0, 1], [0, 1, 0, 1]]);
        const zAxis: AlgebraicVector = this.mField.createVector([[0, 1, 0, 1], [0, 1, 0, 1], [1, 1, 0, 1]]);
        for(let p: number = 0; p < DodecagonalSymmetry.ORDER; p++) {{
            const x: number = this.mOrientations[p].mapIndex(0);
            const y: number = this.mOrientations[p].mapIndex(3);
            this.mMatrices[p] = new AlgebraicMatrix(dir.getAxis$int$int(Symmetry.PLUS, x).normal(), dir.getAxis$int$int(Symmetry.PLUS, y).normal(), zAxis);
            const axis: Axis = dir.getAxis$int$int(Symmetry.PLUS, p);
            const norm: AlgebraicVector = this.mMatrices[p].timesColumn(xAxis);
            if (!norm.equals(axis.normal()))throw new java.lang.IllegalStateException("matrix wrong: " + p);
        };}
    }

    /**
     * 
     */
    createOtherOrbits() {
        this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("green", 0, Symmetry.NO_ROTATION, [[1, 1, 1, 2], [1, 2, 0, 1], [0, 1, 0, 1]], true);
        this.createZoneOrbit$java_lang_String$int$int$int_A_A$boolean("red", 0, 1, [[0, 1, 0, 1], [0, 1, 0, 1], [1, 1, 0, 1]], true);
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

    /**
     * 
     * @return {Axis}
     */
    public getPreferredAxis(): Axis {
        return this.getDirection("red").getAxis$int$int(0, 0);
    }

    /**
     * 
     * @return {string}
     */
    public getName(): string {
        return "dodecagonal";
    }

    /**
     * 
     * @param {string} name
     * @return {int[]}
     */
    public subgroup(name: string): number[] {
        return null;
    }
}
DodecagonalSymmetry["__class"] = "com.vzome.core.math.symmetry.DodecagonalSymmetry";
DodecagonalSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];
