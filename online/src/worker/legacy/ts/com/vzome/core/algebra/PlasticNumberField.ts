/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author David Hall
     * @param {*} factory
     * @class
     * @extends com.vzome.core.algebra.ParameterizedField
     */
    export class PlasticNumberField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "plasticNumber";

        /**
         * 
         * @return {double[]} the coefficients of a PlasticNumberField.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            const plasticNumber: number = 1.32471795724475;
            return [1.0, plasticNumber, plasticNumber * plasticNumber];
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
            return PlasticNumberField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(PlasticNumberField.FIELD_NAME, 3, factory);
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
            const tensor: number[][][] = [[[1, 0, 0], [0, 0, 1], [0, 1, 0]], [[0, 1, 0], [1, 0, 1], [0, 1, 1]], [[0, 0, 1], [0, 1, 0], [1, 0, 1]]];
            this.multiplicationTensor = tensor;
        }

        /**
         * 
         */
        initializeLabels() {
            const upperRho: string = "\u03a1";
            this.irrationalLabels[1] = [upperRho, "P"];
            this.irrationalLabels[2] = [upperRho + "\u00b2", "P^2"];
        }
    }
    PlasticNumberField["__class"] = "com.vzome.core.algebra.PlasticNumberField";
    PlasticNumberField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

