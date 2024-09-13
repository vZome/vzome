/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author David Hall
     * @param {string} name
     * @param {number} order
     * @param {*} factory
     * @class
     * @extends com.vzome.core.algebra.AbstractAlgebraicField
     */
    export abstract class ParameterizedField extends com.vzome.core.algebra.AbstractAlgebraicField {
        coefficients: number[];

        multiplicationTensor: number[][][];

        irrationalLabels: string[][];

        public constructor(name: string, order: number, factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(name, order, factory);
            if (this.coefficients === undefined) { this.coefficients = null; }
            if (this.multiplicationTensor === undefined) { this.multiplicationTensor = null; }
            if (this.irrationalLabels === undefined) { this.irrationalLabels = null; }
            this.normalizer = (field,factors) => { return ParameterizedField.doNothing(field,factors) };
            this.coefficients = (s => { let a=[]; while(s-->0) a.push(0); return a; })(order);
            this.multiplicationTensor = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return 0; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([order, order, order]);
            this.irrationalLabels = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([order, 2]);
            this.irrationalLabels[0] = [" ", " "];
        }

        /**
         * doNothing is the default normalizer method.
         * @param {com.vzome.core.algebra.BigRational[]} factors
         * @param {*} field
         */
        static doNothing(field: com.vzome.core.algebra.AlgebraicField, factors: com.vzome.core.algebra.BigRational[]) {
        }

        /**
         * Subclasses may need different normalization methods based on their parameters.
         * For example, SqrtField(2) doesn't need normalization, but SqrtField(4) does since 4 is a perfect square
         * By assigning an appropriate normalizer method once in the c'tor,
         * the method can avoid the repeated overhead of checking isPerfectSquare() within the normalizer method itself.
         */
        normalizer: (p1: com.vzome.core.algebra.AlgebraicField, p2: com.vzome.core.algebra.BigRational[]) => void;

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} factors
         */
        normalize(factors: com.vzome.core.algebra.BigRational[]) {
            (target => (typeof target === 'function') ? target(this, factors) : (<any>target).accept(this, factors))(this.normalizer);
        }

        initialize() {
            this.initializeNormalizer();
            this.initializeMultiplicationTensor();
            this.initializeCoefficients();
            this.initializeLabels();
        }

        initializeNormalizer() {
            this.normalizer = (field,factors) => { return ParameterizedField.doNothing(field,factors) };
        }

        abstract initializeMultiplicationTensor();

        abstract initializeCoefficients();

        abstract initializeLabels();

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} v1
         * @param {com.vzome.core.algebra.BigRational[]} v2
         * @return {com.vzome.core.algebra.BigRational[]}
         */
        multiply(v1: com.vzome.core.algebra.BigRational[], v2: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.BigRational[] {
            const order: number = this.getOrder();
            const result: com.vzome.core.algebra.BigRational[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
            for(let i: number = 0; i < order; i++) {{
                result[i] = this.numberFactory.zero();
                for(let j: number = 0; j < order; j++) {{
                    for(let k: number = 0; k < order; k++) {{
                        const multiplier: number = this.multiplicationTensor[i][j][k];
                        if (multiplier !== 0){
                            let product: com.vzome.core.algebra.BigRational = v1[j].times(v2[k]);
                            if (multiplier !== 1){
                                product = product.timesInt(multiplier);
                            }
                            result[i] = result[i].plus(product);
                        }
                    };}
                };}
            };}
            return result;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} factors
         * @param {number} whichIrrational
         * @return {com.vzome.core.algebra.BigRational[]}
         */
        scaleBy(factors: com.vzome.core.algebra.BigRational[], whichIrrational: number): com.vzome.core.algebra.BigRational[] {
            if (whichIrrational === 0){
                return factors;
            }
            const order: number = this.getOrder();
            const result: com.vzome.core.algebra.BigRational[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
            for(let i: number = 0; i < order; i++) {{
                result[i] = this.numberFactory.zero();
                for(let j: number = 0; j < order; j++) {{
                    const multiplier: number = this.multiplicationTensor[i][j][whichIrrational];
                    if (multiplier !== 0){
                        if (multiplier === 1){
                            result[i] = result[i].plus(factors[j]);
                        } else {
                            result[i] = result[i].plus(factors[j].timesInt(multiplier));
                        }
                    }
                };}
            };}
            this.normalize(result);
            return result;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} factors
         * @return {number}
         */
        evaluateNumber(factors: com.vzome.core.algebra.BigRational[]): number {
            let result: number = 0.0;
            const order: number = this.getOrder();
            for(let i: number = 0; i < order; i++) {{
                result += factors[i].evaluate() * this.coefficients[i];
            };}
            return result;
        }

        public getIrrational$int$int(i: number, format: number): string {
            return this.irrationalLabels[i][format];
        }

        /**
         * 
         * @param {number} i
         * @param {number} format
         * @return {string}
         */
        public getIrrational(i?: any, format?: any): string {
            if (((typeof i === 'number') || i === null) && ((typeof format === 'number') || format === null)) {
                return <any>this.getIrrational$int$int(i, format);
            } else if (((typeof i === 'number') || i === null) && format === undefined) {
                return <any>this.getIrrational$int(i);
            } else throw new Error('invalid overload');
        }

        public getCoefficient(i: number): number {
            return this.coefficients[i];
        }
    }
    ParameterizedField["__class"] = "com.vzome.core.algebra.ParameterizedField";
    ParameterizedField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

