/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class PlasticPhiField extends com.vzome.core.algebra.ParameterizedField {
        public static FIELD_NAME: string = "plasticPhi";

        /**
         * @return {double[]} the coefficients of a PlasticPhiField.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            const plasticNumber: number = 1.32471795724475;
            const phi: number = (1.0 + Math.sqrt(5)) / 2.0;
            return [1.0, phi, plasticNumber, plasticNumber * phi, plasticNumber * plasticNumber, plasticNumber * plasticNumber * phi];
        }

        /**
         * 
         * @return {number}
         */
        public getNumMultipliers(): number {
            return 2;
        }

        /**
         * 
         * @return {double[]}
         */
        public getCoefficients(): number[] {
            return PlasticPhiField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(PlasticPhiField.FIELD_NAME, 6, factory);
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
            const tensor: number[][][] = [[[1, 0, 0, 0, 0, 0], [0, 1, 0, 0, 0, 0], [0, 0, 0, 0, 1, 0], [0, 0, 0, 0, 0, 1], [0, 0, 1, 0, 0, 0], [0, 0, 0, 1, 0, 0]], [[0, 1, 0, 0, 0, 0], [1, 1, 0, 0, 0, 0], [0, 0, 0, 0, 0, 1], [0, 0, 0, 0, 1, 1], [0, 0, 0, 1, 0, 0], [0, 0, 1, 1, 0, 0]], [[0, 0, 1, 0, 0, 0], [0, 0, 0, 1, 0, 0], [1, 0, 0, 0, 1, 0], [0, 1, 0, 0, 0, 1], [0, 0, 1, 0, 1, 0], [0, 0, 0, 1, 0, 1]], [[0, 0, 0, 1, 0, 0], [0, 0, 1, 1, 0, 0], [0, 1, 0, 0, 0, 1], [1, 1, 0, 0, 1, 1], [0, 0, 0, 1, 0, 1], [0, 0, 1, 1, 1, 1]], [[0, 0, 0, 0, 1, 0], [0, 0, 0, 0, 0, 1], [0, 0, 1, 0, 0, 0], [0, 0, 0, 1, 0, 0], [1, 0, 0, 0, 1, 0], [0, 1, 0, 0, 0, 1]], [[0, 0, 0, 0, 0, 1], [0, 0, 0, 0, 1, 1], [0, 0, 0, 1, 0, 0], [0, 0, 1, 1, 0, 0], [0, 1, 0, 0, 0, 1], [1, 1, 0, 0, 1, 1]]];
            this.multiplicationTensor = tensor;
        }

        /**
         * 
         */
        initializeLabels() {
            const upperRho: string = "\u03a1";
            const lowerPhi: string = "\u03c6";
            this.irrationalLabels[1] = [lowerPhi, "phi"];
            this.irrationalLabels[2] = [upperRho, "P"];
            this.irrationalLabels[3] = [upperRho + "\u03c6", "Pphi"];
            this.irrationalLabels[4] = [upperRho + "\u00b2", "P^2"];
            this.irrationalLabels[5] = [upperRho + "\u00b2\u03c6", "P^2phi"];
        }

        /**
         * 
         * @return {*}
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getUnitTerm(1);
        }
    }
    PlasticPhiField["__class"] = "com.vzome.core.algebra.PlasticPhiField";
    PlasticPhiField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

