/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export interface Symmetry extends com.vzome.core.math.symmetry.Embedding {
        getChiralOrder(): number;

        getName(): string;

        getAxis(vector?: any, orbits?: any): com.vzome.core.math.symmetry.Axis;

        getMapping(from: number, to: number): number;

        getPermutation(i: number): com.vzome.core.math.symmetry.Permutation;

        getMatrix(i: number): com.vzome.core.algebra.AlgebraicMatrix;

        inverse(orientation: number): number;

        getDirectionNames(): string[];

        getDirection(name: string): com.vzome.core.math.symmetry.Direction;

        getField(): com.vzome.core.algebra.AlgebraicField;

        getOrbitSet(): com.vzome.core.math.symmetry.OrbitSet;

        /**
         * Generate a subgroup, by taking the closure of some collection of Permutations
         * @param {int[]} perms an array of Permutations indices
         * @return {int[]} an array of Permutations indices
         */
        closure(perms: number[]): number[];

        subgroup(name: string): number[];

        createNewZoneOrbit(name: string, prototype: number, rotatedPrototype: number, vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Direction;

        getIncidentOrientations(orientation: number): number[];

        getSpecialOrbit(which: com.vzome.core.math.symmetry.SpecialOrbit): com.vzome.core.math.symmetry.Direction;

        getPreferredAxis(): com.vzome.core.math.symmetry.Axis;

        /**
         * Get the transformation matrix that maps zone 7 to zone -7 (for example).
         * If null, the matrix is implicitly a central inversion, negating vectors.
         * @return {com.vzome.core.algebra.AlgebraicMatrix} {@link AlgebraicMatrix}
         */
        getPrincipalReflection(): com.vzome.core.algebra.AlgebraicMatrix;

        getOrbitTriangle(): com.vzome.core.algebra.AlgebraicVector[];

        /**
         * Compute the orbit triangle dots for all existing orbits,
         * and leave behind an OrbitDotLocator for new ones.
         * The result is just a VEF string, for debugging.
         * @return
         * @return {string}
         */
        computeOrbitDots(): string;

        reverseOrbitTriangle(): boolean;

        getDirections(): java.lang.Iterable<com.vzome.core.math.symmetry.Direction>;
    }

    export namespace Symmetry {

        export const PLUS: number = 0;

        export const MINUS: number = 1;

        export const NO_ROTATION: number = -1;

        export const TETRAHEDRAL: string = "tetrahedral";

        export const PYRITOHEDRAL: string = "pyritohedral";
    }

}

