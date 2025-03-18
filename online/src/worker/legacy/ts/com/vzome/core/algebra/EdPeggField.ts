/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class EdPeggField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "edPegg";

        /**
         * 
         * @return {double[]} the coefficients of an EdPeggField.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            const a: number = 1.76929235423863;
            return [1.0, a, a * a];
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
            return EdPeggField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(EdPeggField.FIELD_NAME, 3, factory);
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
            const mt: number[][][] = [[[1, 0, 0], [0, 0, 2], [0, 2, 0]], [[0, 1, 0], [1, 0, 2], [0, 2, 2]], [[0, 0, 1], [0, 1, 0], [1, 0, 2]]];
            this.multiplicationTensor = mt;
        }

        /**
         * 
         */
        initializeLabels() {
            this.irrationalLabels[1] = ["\u03b5", "epsilon"];
            this.irrationalLabels[2] = ["\u03b5\u00b2", "epsilon^2"];
        }
    }
    EdPeggField["__class"] = "com.vzome.core.algebra.EdPeggField";
    EdPeggField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

