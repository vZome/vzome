import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { Embedding } from "./Embedding.js";
import { OrbitSet } from "./OrbitSet.js";
import { Permutation } from "./Permutation.js";
import { SpecialOrbit } from "./SpecialOrbit.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export interface Symmetry extends Embedding {
    getChiralOrder(): number;

    getName(): string;

    getAxis(vector?: any, orbits?: any): Axis;

    getMapping(from: number, to: number): number;

    getPermutation(i: number): Permutation;

    getMatrix(i: number): AlgebraicMatrix;

    inverse(orientation: number): number;

    getDirectionNames(): string[];

    getDirection(name: string): Direction;

    getField(): AlgebraicField;

    getOrbitSet(): OrbitSet;

    /**
     * Generate a subgroup, by taking the closure of some collection of Permutations
     * @param {int[]} perms an array of Permutations indices
     * @return {int[]} an array of Permutations indices
     */
    closure(perms: number[]): number[];

    subgroup(name: string): number[];

    createNewZoneOrbit(name: string, prototype: number, rotatedPrototype: number, vector: AlgebraicVector): Direction;

    getIncidentOrientations(orientation: number): number[];

    getSpecialOrbit(which: SpecialOrbit): Direction;

    getPreferredAxis(): Axis;

    /**
     * Get the transformation matrix that maps zone 7 to zone -7 (for example).
     * If null, the matrix is implicitly a central inversion, negating vectors.
     * @return {AlgebraicMatrix} {@link AlgebraicMatrix}
     */
    getPrincipalReflection(): AlgebraicMatrix;

    getOrbitTriangle(): AlgebraicVector[];

    /**
     * Compute the orbit triangle dots for all existing orbits,
     * and leave behind an OrbitDotLocator for new ones.
     * The result is just a VEF string, for debugging.
     * @return
     * @return {string}
     */
    computeOrbitDots(): string;

    reverseOrbitTriangle(): boolean;

    getDirections(): java.lang.Iterable<Direction>;
}

export namespace Symmetry {

    export const PLUS: number = 0;

    export const MINUS: number = 1;

    export const NO_ROTATION: number = -1;

    export const TETRAHEDRAL: string = "tetrahedral";

    export const PYRITOHEDRAL: string = "pyritohedral";
}
