/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export interface Embedding {
        embedInR3(v: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.RealVector;

        embedInR3Double(v: com.vzome.core.algebra.AlgebraicVector): number[];

        isTrivial(): boolean;
    }

    export namespace Embedding {

        export class Trivial implements com.vzome.core.math.symmetry.Embedding {
            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} v
             * @return {com.vzome.core.math.RealVector}
             */
            public embedInR3(v: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.RealVector {
                return v.toRealVector();
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} v
             * @return {double[]}
             */
            public embedInR3Double(v: com.vzome.core.algebra.AlgebraicVector): number[] {
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

}

