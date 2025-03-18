/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsAlgebraicNumber implements com.vzome.core.algebra.AlgebraicNumber {
        /*private*/ field: com.vzome.jsweet.JsAlgebraicField;

        /*private*/ factors: number[];

        public constructor(field: com.vzome.jsweet.JsAlgebraicField, factors: number[]) {
            if (this.field === undefined) { this.field = null; }
            if (this.factors === undefined) { this.factors = null; }
            this.field = field;
            this.factors = <any>((<any>(factors).slice()));
        }

        /**
         * 
         * @return {*}
         */
        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /**
         * 
         * @return {number}
         */
        public evaluate(): number {
            return this.field.evaluateNumber(this.factors);
        }

        /**
         * 
         * @return {int[]}
         */
        public toTrailingDivisor(): number[] {
            return <any>((<any>(this.factors).slice()));
        }

        /**
         * 
         * @param {number} n is the value to be added
         * @return {*} this + n
         */
        public plusInt(n: number): com.vzome.core.algebra.AlgebraicNumber {
            return n === 0 ? this : this.plus$com_vzome_core_algebra_AlgebraicNumber(this.field.createRational$long(n));
        }

        /**
         * 
         * @param {number} num is the numerator of the rational value to be added
         * @param {number} den is the denominator of the rational value to be added
         * @return {*} this + (num / den)
         */
        public plusRational(num: number, den: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.plus$com_vzome_core_algebra_AlgebraicNumber(this.field.createRational$long$long(num, den));
        }

        public plus$com_vzome_core_algebra_AlgebraicNumber(that: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.algebra.AlgebraicNumber {
            const factors: number[] = this.field.add(this.factors, (<JsAlgebraicNumber><any>that).factors);
            return new JsAlgebraicNumber(this.field, factors);
        }

        /**
         * 
         * @param {*} that
         * @return {*}
         */
        public plus(that?: any): any {
            if (((that != null && (that.constructor != null && that.constructor["__interfaces"] != null && that.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || that === null)) {
                return <any>this.plus$com_vzome_core_algebra_AlgebraicNumber(that);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {number} n
         * @return {*}
         */
        public minusInt(n: number): com.vzome.core.algebra.AlgebraicNumber {
            return n === 0 ? this : this.minus$com_vzome_core_algebra_AlgebraicNumber(this.field.createRational$long(n));
        }

        /**
         * 
         * @param {number} num
         * @param {number} den
         * @return {*}
         */
        public minusRational(num: number, den: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.minus$com_vzome_core_algebra_AlgebraicNumber(this.field.createRational$long$long(num, den));
        }

        public minus$com_vzome_core_algebra_AlgebraicNumber(that: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.algebra.AlgebraicNumber {
            const factors: number[] = this.field.subtract(this.factors, (<JsAlgebraicNumber><any>that).factors);
            return new JsAlgebraicNumber(this.field, factors);
        }

        /**
         * 
         * @param {*} that
         * @return {*}
         */
        public minus(that?: any): any {
            if (((that != null && (that.constructor != null && that.constructor["__interfaces"] != null && that.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || that === null)) {
                return <any>this.minus$com_vzome_core_algebra_AlgebraicNumber(that);
            } else throw new Error('invalid overload');
        }

        public times$com_vzome_core_algebra_AlgebraicNumber(that: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.algebra.AlgebraicNumber {
            const factors: number[] = this.field.multiply(this.factors, (<JsAlgebraicNumber><any>that).factors);
            return new JsAlgebraicNumber(this.field, factors);
        }

        /**
         * 
         * @param {*} that
         * @return {*}
         */
        public times(that?: any): any {
            if (((that != null && (that.constructor != null && that.constructor["__interfaces"] != null && that.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || that === null)) {
                return <any>this.times$com_vzome_core_algebra_AlgebraicNumber(that);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {number} divisor
         * @return {*}
         */
        public dividedByInt(divisor: number): com.vzome.core.algebra.AlgebraicNumber {
            return divisor === 1 ? this : this.times$com_vzome_core_algebra_AlgebraicNumber(this.field.createRational$long$long(1, divisor));
        }

        /**
         * 
         * @param {number} num
         * @param {number} den
         * @return {*}
         */
        public dividedByRational(num: number, den: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.times$com_vzome_core_algebra_AlgebraicNumber(this.field.createRational$long$long(den, num));
        }

        /**
         * 
         * @param {*} that
         * @return {*}
         */
        public dividedBy(that: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.algebra.AlgebraicNumber {
            const recip: number[] = this.field.reciprocal((<JsAlgebraicNumber><any>that).factors);
            const factors: number[] = this.field.multiply(this.factors, recip);
            return new JsAlgebraicNumber(this.field, factors);
        }

        public equals(that: com.vzome.core.algebra.AlgebraicNumber): boolean {
            return java.util.Arrays.equals(this.factors, (<JsAlgebraicNumber><any>that).factors);
        }

        /**
         * 
         * @return {*}
         */
        public negate(): com.vzome.core.algebra.AlgebraicNumber {
            const factors: number[] = this.field.negate(this.factors);
            return new JsAlgebraicNumber(this.field, factors);
        }

        /**
         * 
         * @return {boolean}
         */
        public isZero(): boolean {
            return this.equals(this.field.zero());
        }

        /**
         * 
         * @return {boolean}
         */
        public isOne(): boolean {
            return this.equals(this.field.one());
        }

        /**
         * 
         * @return {*}
         */
        public reciprocal(): com.vzome.core.algebra.AlgebraicNumber {
            return new JsAlgebraicNumber(this.field, (<com.vzome.jsweet.JsAlgebraicField>this.field).reciprocal(this.factors));
        }

        /**
         * @param {java.lang.StringBuffer} buf
         * @param {number} format must be one of the following values.
         * The result is formatted as follows:
         * <br>
         * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
         * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
         * {@code ZOMIC_FORMAT      // 4 3}<br>
         * {@code VEF_FORMAT        // (3,4)}<br>
         */
        public getNumberExpression(buf: java.lang.StringBuffer, format: number) {
            buf.append(this.toString(format));
        }

        /**
         * @param {number} format must be one of the following values.
         * The result is formatted as follows:
         * <br>
         * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
         * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
         * {@code ZOMIC_FORMAT      // 4 3}<br>
         * {@code VEF_FORMAT        // (3,4)}
         * @return {string}
         */
        public toString(format: number): string {
            return this.field.toString(this.factors, format);
        }

        /**
         * 
         * @return {string}
         */
        public getMathML(): string {
            return this.field.getMathML(this.factors);
        }

        /**
         * 
         * @param {*} other
         * @return {number}
         */
        public compareTo(other: com.vzome.core.algebra.AlgebraicNumber): number {
            if (this === other){
                return 0;
            }
            if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(other,this))){
                return 0;
            }
            const d1: number = this.evaluate();
            const d2: number = other.evaluate();
            return /* compareTo */(<any>((o1: any, o2: any) => { if (o1 && o1.compareTo) { return o1.compareTo(o2); } else { return o1 < o2 ? -1 : o2 < o1 ? 1 : 0; } })(d1,d2));
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public greaterThan(other: com.vzome.core.algebra.AlgebraicNumber): boolean {
            return this.compareTo(other) > 0;
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public lessThan(other: com.vzome.core.algebra.AlgebraicNumber): boolean {
            return this.compareTo(other) < 0;
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public greaterThanOrEqualTo(other: com.vzome.core.algebra.AlgebraicNumber): boolean {
            return this.compareTo(other) >= 0;
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public lessThanOrEqualTo(other: com.vzome.core.algebra.AlgebraicNumber): boolean {
            return this.compareTo(other) <= 0;
        }

        /**
         * 
         * @return {number}
         */
        public signum(): number {
            return /* intValue */(javaemul.internal.DoubleHelper.valueOf(/* signum */(f => { if (f > 0) { return 1; } else if (f < 0) { return -1; } else { return 0; } })(this.evaluate()))|0);
        }

        /**
         * 
         * @param {number} n
         * @return {*}
         */
        public timesInt(n: number): com.vzome.core.algebra.AlgebraicNumber {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicNumber.timesInt");
        }

        /**
         * 
         * @param {number} num
         * @param {number} den
         * @return {*}
         */
        public timesRational(num: number, den: number): com.vzome.core.algebra.AlgebraicNumber {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicNumber.times");
        }

        /**
         * 
         * @return {boolean}
         */
        public isRational(): boolean {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicNumber.isRational");
        }
    }
    JsAlgebraicNumber["__class"] = "com.vzome.jsweet.JsAlgebraicNumber";
    JsAlgebraicNumber["__interfaces"] = ["com.vzome.core.algebra.Fields.Element","com.vzome.core.algebra.AlgebraicNumber","java.lang.Comparable"];


}

