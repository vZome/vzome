/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author David Hall
     * @param {*} factory
     * @class
     * @extends com.vzome.core.algebra.ParameterizedField
     */
    export class SnubDodecahedronField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "snubDodecahedron";

        /**
         * 
         * @return {double[]} the coefficients of this AlgebraicField class.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            const PHI_VALUE: number = (1.0 + Math.sqrt(5.0)) / 2.0;
            const XI_VALUE: number = 1.7155614996973678;
            return [1.0, PHI_VALUE, XI_VALUE, PHI_VALUE * XI_VALUE, XI_VALUE * XI_VALUE, PHI_VALUE * XI_VALUE * XI_VALUE];
        }

        /**
         * 
         * @return {double[]}
         */
        public getCoefficients(): number[] {
            return SnubDodecahedronField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(SnubDodecahedronField.FIELD_NAME, 6, factory);
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
            const mm: number[][][] = [[[1, 0, 0, 0, 0, 0], [0, 1, 0, 0, 0, 0], [0, 0, 0, 0, 0, 1], [0, 0, 0, 0, 1, 1], [0, 0, 0, 1, 0, 0], [0, 0, 1, 1, 0, 0]], [[0, 1, 0, 0, 0, 0], [1, 1, 0, 0, 0, 0], [0, 0, 0, 0, 1, 1], [0, 0, 0, 0, 1, 2], [0, 0, 1, 1, 0, 0], [0, 0, 1, 2, 0, 0]], [[0, 0, 1, 0, 0, 0], [0, 0, 0, 1, 0, 0], [1, 0, 0, 0, 2, 0], [0, 1, 0, 0, 0, 2], [0, 0, 2, 0, 0, 1], [0, 0, 0, 2, 1, 1]], [[0, 0, 0, 1, 0, 0], [0, 0, 1, 1, 0, 0], [0, 1, 0, 0, 0, 2], [1, 1, 0, 0, 2, 2], [0, 0, 0, 2, 1, 1], [0, 0, 2, 2, 1, 2]], [[0, 0, 0, 0, 1, 0], [0, 0, 0, 0, 0, 1], [0, 0, 1, 0, 0, 0], [0, 0, 0, 1, 0, 0], [1, 0, 0, 0, 2, 0], [0, 1, 0, 0, 0, 2]], [[0, 0, 0, 0, 0, 1], [0, 0, 0, 0, 1, 1], [0, 0, 0, 1, 0, 0], [0, 0, 1, 1, 0, 0], [0, 1, 0, 0, 0, 2], [1, 1, 0, 0, 2, 2]]];
            this.multiplicationTensor = mm;
        }

        /**
         * 
         */
        initializeLabels() {
            this.irrationalLabels[1] = ["\u03c6", "phi"];
            this.irrationalLabels[2] = ["\u03be", "xi"];
            this.irrationalLabels[3] = ["\u03c6\u03be", "phi*xi"];
            this.irrationalLabels[4] = ["\u03be\u00b2", "xi^2"];
            this.irrationalLabels[5] = ["\u03c6\u03be\u00b2", "phi*xi^2"];
        }

        /**
         * 
         * @return {number}
         */
        public getNumMultipliers(): number {
            return 2;
        }

        /**
         * scalar for an affine pentagon
         * @return {*}
         */
        public getAffineScalar(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getGoldenRatio();
        }

        /**
         * 
         * @return {*}
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getUnitTerm(1);
        }
    }
    SnubDodecahedronField["__class"] = "com.vzome.core.algebra.SnubDodecahedronField";
    SnubDodecahedronField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

