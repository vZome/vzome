import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Manifestation } from "./Manifestation.js";

export interface Panel extends Manifestation, java.lang.Iterable<AlgebraicVector> {
    getZoneVector(): AlgebraicVector;

    setZoneVector(vector: AlgebraicVector);

    getFirstVertex(): AlgebraicVector;

    /**
     * 
     * @return {*}
     */
    iterator(): java.util.Iterator<AlgebraicVector>;

    getVertexCount(): number;

    getNormal(embedding?: any): any;

    getQuadrea(): AlgebraicNumber;
}
