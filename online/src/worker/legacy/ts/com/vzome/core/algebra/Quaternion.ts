/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class Quaternion {
        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "Quaternion: " + this.vector.toString();
        }

        representation: com.vzome.core.algebra.AlgebraicMatrix;

        transpose: com.vzome.core.algebra.AlgebraicMatrix;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ vector: com.vzome.core.algebra.AlgebraicVector;

        public constructor(field: com.vzome.core.algebra.AlgebraicField, vector: com.vzome.core.algebra.AlgebraicVector) {
            if (this.representation === undefined) { this.representation = null; }
            if (this.transpose === undefined) { this.transpose = null; }
            if (this.field === undefined) { this.field = null; }
            if (this.vector === undefined) { this.vector = null; }
            this.field = field;
            this.vector = vector;
            let w_offset: number = 0;
            let factor: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long'](0);
            if (vector.dimension() > 3){
                factor = vector.getComponent(0);
                w_offset = 1;
            }
            this.representation = field.identityMatrix(4).timesScalar(factor);
            factor = vector.getComponent(0 + w_offset);
            this.representation.setElement(1, 0, factor);
            this.representation.setElement(3, 2, factor);
            factor = factor.negate();
            this.representation.setElement(0, 1, factor);
            this.representation.setElement(2, 3, factor);
            factor = vector.getComponent(1 + w_offset);
            this.representation.setElement(1, 3, factor);
            this.representation.setElement(2, 0, factor);
            factor = factor.negate();
            this.representation.setElement(3, 1, factor);
            this.representation.setElement(0, 2, factor);
            factor = vector.getComponent(2 + w_offset);
            this.representation.setElement(3, 0, factor);
            this.representation.setElement(2, 1, factor);
            factor = factor.negate();
            this.representation.setElement(1, 2, factor);
            this.representation.setElement(0, 3, factor);
            if (w_offset === 1)factor = vector.getComponent(0); else factor = field['createRational$long'](0);
            this.transpose = field.identityMatrix(4).timesScalar(factor);
            factor = vector.getComponent(0 + w_offset);
            this.transpose.setElement(0, 1, factor);
            this.transpose.setElement(2, 3, factor);
            factor = factor.negate();
            this.transpose.setElement(1, 0, factor);
            this.transpose.setElement(3, 2, factor);
            factor = vector.getComponent(1 + w_offset);
            this.transpose.setElement(3, 1, factor);
            this.transpose.setElement(0, 2, factor);
            factor = factor.negate();
            this.transpose.setElement(1, 3, factor);
            this.transpose.setElement(2, 0, factor);
            factor = vector.getComponent(2 + w_offset);
            this.transpose.setElement(1, 2, factor);
            this.transpose.setElement(0, 3, factor);
            factor = factor.negate();
            this.transpose.setElement(3, 0, factor);
            this.transpose.setElement(2, 1, factor);
        }

        public getVector(): com.vzome.core.algebra.AlgebraicVector {
            return this.vector;
        }

        /*private*/ conjugate(q: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.field.origin(4);
            result.setComponent(3, q.getComponent(3).negate());
            result.setComponent(1, q.getComponent(1).negate());
            result.setComponent(2, q.getComponent(2).negate());
            result.setComponent(0, q.getComponent(0));
            return result;
        }

        public reflect(v: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            let reflection: com.vzome.core.algebra.AlgebraicVector = this.rightMultiply(this.conjugate(v));
            reflection = this.leftMultiply(reflection);
            return reflection.negate();
        }

        /**
         * Compute the product this * q.
         * @param {com.vzome.core.algebra.AlgebraicVector} q
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public rightMultiply(q: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            return this.representation.timesColumn(q);
        }

        /**
         * Compute the product q*this.
         * This is computed using the identities:
         * 
         * conjugate( q*this ) == conjugate( this ) * conjugate( q )
         * 
         * q * this == conjugate( conjugate( this ) * conjugate( q ) )
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} q
         * @return
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public leftMultiply(q: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            let result: com.vzome.core.algebra.AlgebraicVector = this.conjugate(q);
            result = this.transpose.timesColumn(result);
            return this.conjugate(result);
        }
    }
    Quaternion["__class"] = "com.vzome.core.algebra.Quaternion";

}

