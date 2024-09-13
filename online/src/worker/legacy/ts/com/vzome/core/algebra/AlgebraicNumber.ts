/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * 
     * Immutable representation of an Algebraic Number
     * @class
     */
    export interface AlgebraicNumber extends com.vzome.core.algebra.Fields.Element<AlgebraicNumber>, java.lang.Comparable<AlgebraicNumber> {
        greaterThan(other: AlgebraicNumber): boolean;

        lessThan(other: AlgebraicNumber): boolean;

        greaterThanOrEqualTo(other: AlgebraicNumber): boolean;

        lessThanOrEqualTo(other: AlgebraicNumber): boolean;

        getField(): com.vzome.core.algebra.AlgebraicField;

        /**
         * 
         * @param {number} n is the value to be added
         * @return {*} this + n
         */
        plusInt(n: number): AlgebraicNumber;

        /**
         * 
         * @param {number} num is the numerator of the rational value to be added
         * @param {number} den is the denominator of the rational value to be added
         * @return {*} this + (num / den)
         */
        plusRational(num: number, den: number): AlgebraicNumber;

        /**
         * 
         * @param {*} that is the value to be added
         * @return {*} this + n
         */
        plus(that?: any): any;

        /**
         * 
         * @param {number} n is the value to be multiplied
         * @return {*} this * n
         */
        timesInt(n: number): AlgebraicNumber;

        /**
         * 
         * @param {number} num is the numerator of the rational value to be multiplied
         * @param {number} den is the denominator of the rational value to be multiplied
         * @return {*} this * (num / den)
         */
        timesRational(num: number, den: number): AlgebraicNumber;

        /**
         * 
         * @param {*} that
         * @return {*}
         */
        times(that?: any): any;

        /**
         * 
         * @param {number} n is the value to be subtracted
         * @return {*} this - n
         */
        minusInt(n: number): AlgebraicNumber;

        /**
         * 
         * @param {number} num is the numerator of the rational value to be subtracted
         * @param {number} den is the denominator of the rational value to be subtracted
         * @return {*} this - (num / den)
         */
        minusRational(num: number, den: number): AlgebraicNumber;

        /**
         * 
         * @param {*} that is the value to be subtracted
         * @return {*} this - n
         */
        minus(that?: any): any;

        /**
         * 
         * @param {number} divisor
         * @return {*} this / divisor
         */
        dividedByInt(divisor: number): AlgebraicNumber;

        /**
         * 
         * @param {number} num is the numerator of the divisor
         * @param {number} den is the denominator of the divisor
         * @return {*} this / (num / den)
         */
        dividedByRational(num: number, den: number): AlgebraicNumber;

        dividedBy(that: AlgebraicNumber): AlgebraicNumber;

        evaluate(): number;

        isRational(): boolean;

        /**
         * 
         * @return {boolean}
         */
        isZero(): boolean;

        /**
         * 
         * @return {boolean}
         */
        isOne(): boolean;

        signum(): number;

        /**
         * 
         * @return {*}
         */
        negate(): AlgebraicNumber;

        /**
         * 
         * @return {*}
         */
        reciprocal(): AlgebraicNumber;

        /**
         * 
         * @param {java.lang.StringBuffer} buf
         * @param {number} format must be one of the following values.
         * The result is formatted as follows:
         * <br>
         * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
         * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
         * {@code ZOMIC_FORMAT      // 4 3}<br>
         * {@code VEF_FORMAT        // (3,4)}<br>
         */
        getNumberExpression(buf: java.lang.StringBuffer, format: number);

        /**
         * 
         * @param {number} format must be one of the following values.
         * The result is formatted as follows:
         * <br>
         * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
         * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
         * {@code ZOMIC_FORMAT      // 4 3}<br>
         * {@code VEF_FORMAT        // (3,4)}
         * @return {string}
         */
        toString(format: number): string;

        toTrailingDivisor(): number[];
    }

    export namespace AlgebraicNumber {

        export class Views {
            constructor() {
            }
        }
        Views["__class"] = "com.vzome.core.algebra.AlgebraicNumber.Views";


        export namespace Views {

            export interface TrailingDivisor {            }

            export interface Rational {            }

            export interface Real {            }
        }

    }

}

