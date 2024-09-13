/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author David Hall
     * @param {*} factory
     * @class
     * @extends com.vzome.core.algebra.ParameterizedField
     */
    export class SnubCubeField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "snubCube";

        /**
         * 
         * @param radicand
         * @return {double[]} the coefficients of a SnubCubeField.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            const tribonacciConstant: number = (1.0 + /* cbrt */Math.pow(19.0 - (3.0 * Math.sqrt(33)), 1/3) + /* cbrt */Math.pow(19.0 + (3.0 * Math.sqrt(33)), 1/3)) / 3.0;
            return [1.0, tribonacciConstant, tribonacciConstant * tribonacciConstant];
        }

        /**
         * 
         * @return {double[]}
         */
        public getCoefficients(): number[] {
            return SnubCubeField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(SnubCubeField.FIELD_NAME, 3, factory);
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
            const mm: number[][][] = [[[1, 0, 0], [0, 0, 1], [0, 1, 1]], [[0, 1, 0], [1, 0, 1], [0, 1, 2]], [[0, 0, 1], [0, 1, 1], [1, 1, 2]]];
            this.multiplicationTensor = mm;
        }

        /**
         * 
         */
        initializeLabels() {
            this.irrationalLabels[1] = ["\u03c8", "psi"];
            this.irrationalLabels[2] = ["\u03c8\u00b2", "psi^2"];
        }

        /**
         * 
         * @return {number}
         */
        public getNumMultipliers(): number {
            return 1;
        }
    }
    SnubCubeField["__class"] = "com.vzome.core.algebra.SnubCubeField";
    SnubCubeField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

