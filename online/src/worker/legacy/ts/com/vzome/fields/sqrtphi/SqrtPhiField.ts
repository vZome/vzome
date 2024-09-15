/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.fields.sqrtphi {
    /**
     * @author David Hall
     * @param {*} factory
     * @class
     * @extends com.vzome.core.algebra.ParameterizedField
     */
    export class SqrtPhiField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "sqrtPhi";

        public static PHI_VALUE: number; public static PHI_VALUE_$LI$(): number { if (SqrtPhiField.PHI_VALUE == null) { SqrtPhiField.PHI_VALUE = (1.0 + Math.sqrt(5.0)) / 2.0; }  return SqrtPhiField.PHI_VALUE; }

        public static SQRT_PHI_VALUE: number; public static SQRT_PHI_VALUE_$LI$(): number { if (SqrtPhiField.SQRT_PHI_VALUE == null) { SqrtPhiField.SQRT_PHI_VALUE = Math.sqrt(SqrtPhiField.PHI_VALUE_$LI$()); }  return SqrtPhiField.SQRT_PHI_VALUE; }

        /**
         * 
         * @return {double[]} the coefficients of a SqrtPhiField.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            return [1.0, SqrtPhiField.SQRT_PHI_VALUE_$LI$(), SqrtPhiField.PHI_VALUE_$LI$(), SqrtPhiField.PHI_VALUE_$LI$() * SqrtPhiField.SQRT_PHI_VALUE_$LI$()];
        }

        /**
         * 
         * @return {number}
         */
        public getNumMultipliers(): number {
            return 1;
        }

        /**
         * 
         * @return {double[]}
         */
        public getCoefficients(): number[] {
            return SqrtPhiField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(SqrtPhiField.FIELD_NAME, 4, factory);
            this.initialize();
        }

        /**
         * 
         */
        initializeCoefficients() {
            const temp: number[] = this.getCoefficients();
            let i: number = 0;
            for(let index = 0; index < temp.length; index++) {
                let coefficient = temp[index];
                {
                    this.coefficients[i++] = coefficient;
                }
            }
        }

        /**
         * 
         */
        initializeMultiplicationTensor() {
            const tensor: number[][][] = [[[1, 0, 0, 0], [0, 0, 0, 1], [0, 0, 1, 0], [0, 1, 0, 1]], [[0, 1, 0, 0], [1, 0, 0, 0], [0, 0, 0, 1], [0, 0, 1, 0]], [[0, 0, 1, 0], [0, 1, 0, 1], [1, 0, 1, 0], [0, 1, 0, 2]], [[0, 0, 0, 1], [0, 0, 1, 0], [0, 1, 0, 1], [1, 0, 1, 0]]];
            this.multiplicationTensor = tensor;
        }

        /**
         * 
         */
        initializeLabels() {
            this.irrationalLabels[1] = ["\u221a\u03c6", "sqrt(phi)"];
            this.irrationalLabels[2] = ["\u03c6", "phi"];
            this.irrationalLabels[3] = ["\u03c6\u221a\u03c6", "phi*sqrt(phi)"];
        }

        /**
         * 
         * @param {long[]} pairs
         * @return {long[]}
         */
        convertGoldenNumberPairs(pairs: number[]): number[] {
            return (pairs.length === 4) ? [pairs[0], pairs[1], 0, 1, pairs[2], pairs[3], 0, 1] : pairs;
        }

        /**
         * 
         * @return {*}
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getUnitTerm(2);
        }
    }
    SqrtPhiField["__class"] = "com.vzome.fields.sqrtphi.SqrtPhiField";
    SqrtPhiField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

