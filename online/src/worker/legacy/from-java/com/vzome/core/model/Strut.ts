import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Manifestation } from "./Manifestation.js";

export interface Strut extends Manifestation, java.lang.Comparable<Strut> {
    getZoneVector(): AlgebraicVector;

    setZoneVector(vector: AlgebraicVector);

    /**
     * 
     * @param {*} other
     * @return {number}
     */
    compareTo(other: Strut): number;

    getCanonicalLesserEnd(): AlgebraicVector;

    getCanonicalGreaterEnd(): AlgebraicVector;

    getEnd(): AlgebraicVector;

    getOffset(): AlgebraicVector;
}
