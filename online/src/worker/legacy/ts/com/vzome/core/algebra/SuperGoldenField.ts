/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class SuperGoldenField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "superGolden";

        /**
         * 
         * @return {double[]} the coefficients of a SuperGoldenField.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            const narayanaCowNumber: number = 1.465571231876768;
            return [1.0, narayanaCowNumber, narayanaCowNumber * narayanaCowNumber];
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
            return SuperGoldenField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(SuperGoldenField.FIELD_NAME, 3, factory);
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
            const mt: number[][][] = [[[1, 0, 0], [0, 0, 1], [0, 1, 1]], [[0, 1, 0], [1, 0, 0], [0, 0, 1]], [[0, 0, 1], [0, 1, 1], [1, 1, 1]]];
            this.multiplicationTensor = mt;
        }

        /**
         * 
         */
        initializeLabels() {
            this.irrationalLabels[1] = ["\u03c8", "psi"];
            this.irrationalLabels[2] = ["\u03c8\u00b2", "psi^2"];
        }
    }
    SuperGoldenField["__class"] = "com.vzome.core.algebra.SuperGoldenField";
    SuperGoldenField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

