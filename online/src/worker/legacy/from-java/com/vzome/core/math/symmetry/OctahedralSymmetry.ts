import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
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
 * 
 * @param {*} field
 * @class
 * @extends AbstractSymmetry
 */
export class OctahedralSymmetry extends AbstractSymmetry {
    static ORDER: number = 24;

    public IDENTITY: Permutation;

    frameColor: string;

    public constructor(field?: any, frameColor?: any) {
        if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof frameColor === 'string') || frameColor === null)) {
            let __args = arguments;
            super(OctahedralSymmetry.ORDER, field, frameColor, null);
            if (this.frameColor === undefined) { this.frameColor = null; } 
            if (this.tetrahedralSubgroup === undefined) { this.tetrahedralSubgroup = null; } 
            this.IDENTITY = new Permutation(this, null);
            this.frameColor = frameColor;
            this.tetrahedralSubgroup = this.closure([0, 2, 4]);
        } else if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && frameColor === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let frameColor: any = "blue";
                super(OctahedralSymmetry.ORDER, field, frameColor, null);
                if (this.frameColor === undefined) { this.frameColor = null; } 
                if (this.tetrahedralSubgroup === undefined) { this.tetrahedralSubgroup = null; } 
                this.IDENTITY = new Permutation(this, null);
                this.frameColor = frameColor;
                this.tetrahedralSubgroup = this.closure([0, 2, 4]);
            }
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @param {SpecialOrbit} which
     * @return {Direction}
     */
    public getSpecialOrbit(which: SpecialOrbit): Direction {
        switch((which)) {
        case SpecialOrbit.BLUE:
            return this.getDirection(this.frameColor);
        case SpecialOrbit.RED:
            return this.getDirection("green");
        case SpecialOrbit.YELLOW:
            return this.getDirection("yellow");
        default:
            return null;
        }
    }

    /**
     * 
     * @return {boolean}
     */
    public reverseOrbitTriangle(): boolean {
        return true;
    }

    /**
     * 
     * @return {AlgebraicVector[]}
     */
    public getOrbitTriangle(): AlgebraicVector[] {
        const greenVertex: AlgebraicVector = this.getDirection("green").getPrototype();
        const blueVertex: AlgebraicVector = this.getDirection("blue").getPrototype();
        const yellowVertex: AlgebraicVector = this.getDirection("yellow").getPrototype();
        return [greenVertex, blueVertex, yellowVertex];
    }

    /**
     * 
     */
    createInitialPermutations() {
        this.mOrientations[0] = this.IDENTITY;
        let map: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(OctahedralSymmetry.ORDER);
        for(let i: number = 0; i < 6; i++) {for(let j: number = 0; j < 4; j++) {map[i * 4 + j] = i * 4 + ((j + 1) % 4);};}
        this.mOrientations[1] = new Permutation(this, map);
        map = (s => { let a=[]; while(s-->0) a.push(0); return a; })(OctahedralSymmetry.ORDER);
        let cycles: number[][] = [[0, 4, 8], [1, 11, 17], [2, 16, 22], [3, 21, 5], [6, 20, 14], [7, 13, 9], [10, 12, 18], [19, 15, 23]];
        for(let index = 0; index < cycles.length; index++) {
            let cycle = cycles[index];
            {
                for(let j: number = 0; j < cycle.length; j++) {{
                    map[cycle[j]] = cycle[(j + 1) % 3];
                };}
            }
        }
        this.mOrientations[4] = new Permutation(this, map);
        map = (s => { let a=[]; while(s-->0) a.push(0); return a; })(OctahedralSymmetry.ORDER);
        cycles = [[0, 5], [1, 8], [4, 9], [15, 20], [12, 19], [16, 23], [2, 17], [13, 10], [21, 6], [22, 3], [7, 14], [11, 18]];
        for(let index = 0; index < cycles.length; index++) {
            let cycle = cycles[index];
            {
                for(let j: number = 0; j < cycle.length; j++) {{
                    map[cycle[j]] = cycle[(j + 1) % 2];
                };}
            }
        }
        this.mOrientations[5] = new Permutation(this, map);
    }

    /**
     * 
     */
    createOtherOrbits() {
        const xAxis: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.X);
        const yAxis: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.Y);
        const zAxis: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.Z);
        const green: AlgebraicVector = xAxis.plus(yAxis);
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("green", 1, 8, green, true);
        const yellow: AlgebraicVector = green.plus(zAxis);
        this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean("yellow", 0, 4, yellow, true);
    }

    /**
     * 
     * @param {string} frameColor
     */
    createFrameOrbit(frameColor: string) {
        const xAxis: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.X);
        const yAxis: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.Y);
        const zAxis: AlgebraicVector = this.mField.basisVector(3, AlgebraicVector.Z);
        let dir: Direction;
        if (this.mField.doubleFrameVectors())dir = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean$boolean$com_vzome_core_algebra_AlgebraicNumber(frameColor, 0, 1, xAxis, true, true, this.mField['createRational$long'](2)); else dir = this.createZoneOrbit$java_lang_String$int$int$com_vzome_core_algebra_AlgebraicVector$boolean(frameColor, 0, 1, xAxis, true);
        this.createBasisAxes(dir, xAxis, 0);
        this.createBasisAxes(dir, xAxis.negate(), 12);
        this.createBasisAxes(dir, yAxis, 5);
        this.createBasisAxes(dir, yAxis.negate(), 7);
        this.createBasisAxes(dir, zAxis, 4);
        this.createBasisAxes(dir, zAxis.negate(), 6);
        for(let p: number = 0; p < OctahedralSymmetry.ORDER; p++) {{
            const x: number = this.mOrientations[p].mapIndex(0);
            const y: number = this.mOrientations[p].mapIndex(8);
            const z: number = this.mOrientations[p].mapIndex(4);
            this.mMatrices[p] = new AlgebraicMatrix(dir.getAxis$int$int(Symmetry.PLUS, x).normal(), dir.getAxis$int$int(Symmetry.PLUS, y).normal(), dir.getAxis$int$int(Symmetry.PLUS, z).normal());
            const axis: Axis = dir.getAxis$int$int(Symmetry.PLUS, p);
            const norm: AlgebraicVector = this.mMatrices[p].timesColumn(xAxis);
            if (!norm.equals(axis.normal()))throw new java.lang.IllegalStateException("matrix wrong: " + p);
        };}
    }

    /*private*/ createBasisAxes(dir: Direction, norm: AlgebraicVector, orientation: number) {
        for(let i: number = 0; i < 4; i++) {{
            const prototype: number = this.mOrientations[orientation].mapIndex(i);
            const rotatedPrototype: number = this.mOrientations[orientation].mapIndex((i + 1) % 4);
            const rotation: number = this.getMapping(prototype, rotatedPrototype);
            dir.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(prototype, rotation, norm);
        };}
    }

    /**
     * 
     * @return {string}
     */
    public getName(): string {
        return "octahedral";
    }

    /*private*/ tetrahedralSubgroup: number[];

    /**
     * 
     * @param {string} name
     * @return {int[]}
     */
    public subgroup(name: string): number[] {
        if (Symmetry.TETRAHEDRAL === name)return this.tetrahedralSubgroup;
        return null;
    }
}
OctahedralSymmetry["__class"] = "com.vzome.core.math.symmetry.OctahedralSymmetry";
OctahedralSymmetry["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetry","com.vzome.core.math.symmetry.Embedding"];
