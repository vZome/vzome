import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { RealVector } from "../RealVector.js";

export interface Embedding {
    embedInR3(v: AlgebraicVector): RealVector;

    embedInR3Double(v: AlgebraicVector): number[];

    isTrivial(): boolean;
}

export namespace Embedding {

    export class Trivial implements Embedding {
        /**
         * 
         * @param {AlgebraicVector} v
         * @return {RealVector}
         */
        public embedInR3(v: AlgebraicVector): RealVector {
            return v.toRealVector();
        }

        /**
         * 
         * @param {AlgebraicVector} v
         * @return {double[]}
         */
        public embedInR3Double(v: AlgebraicVector): number[] {
            return v.to3dDoubleVector();
        }

        /**
         * 
         * @return {boolean}
         */
        public isTrivial(): boolean {
            return true;
        }

        constructor() {
        }
    }
    Trivial["__class"] = "com.vzome.core.math.symmetry.Embedding.Trivial";
    Trivial["__interfaces"] = ["com.vzome.core.math.symmetry.Embedding"];


}
