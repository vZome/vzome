/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export class SnubDodecField extends com.vzome.core.algebra.AbstractAlgebraicField {
        public static FIELD_NAME: string = "snubDodec";

        /**
         * 
         * @return {double[]} the coefficients of this AlgebraicField class.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(): number[] {
            return [1.0, SnubDodecField.PHI_VALUE_$LI$(), SnubDodecField.XI_VALUE, SnubDodecField.PHI_VALUE_$LI$() * SnubDodecField.XI_VALUE, SnubDodecField.XI_VALUE * SnubDodecField.XI_VALUE, SnubDodecField.PHI_VALUE_$LI$() * SnubDodecField.XI_VALUE * SnubDodecField.XI_VALUE];
        }

        /**
         * 
         * @return {double[]}
         */
        public getCoefficients(): number[] {
            return SnubDodecField.getFieldCoefficients();
        }

        public constructor(factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            super(SnubDodecField.FIELD_NAME, 6, factory);
        }

        public static PHI_VALUE: number; public static PHI_VALUE_$LI$(): number { if (SnubDodecField.PHI_VALUE == null) { SnubDodecField.PHI_VALUE = (1.0 + Math.sqrt(5.0)) / 2.0; }  return SnubDodecField.PHI_VALUE; }

        public static XI_VALUE: number = 1.7155614996973678;

        static A: number = 0;

        static B: number = 1;

        static C: number = 2;

        static D: number = 3;

        static E: number = 4;

        static F: number = 5;

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} a
         * @param {com.vzome.core.algebra.BigRational[]} b
         * @return {com.vzome.core.algebra.BigRational[]}
         */
        public multiply(a: com.vzome.core.algebra.BigRational[], b: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.BigRational[] {
            const result: com.vzome.core.algebra.BigRational[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.getOrder());
            let factor: com.vzome.core.algebra.BigRational = a[SnubDodecField.A].times(b[SnubDodecField.A]);
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            result[SnubDodecField.A] = factor;
            factor = a[SnubDodecField.B].times(b[SnubDodecField.A]);
            factor = factor.plus(a[SnubDodecField.A].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            result[SnubDodecField.B] = factor;
            factor = a[SnubDodecField.C].times(b[SnubDodecField.A]);
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.A].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            result[SnubDodecField.C] = factor;
            factor = a[SnubDodecField.D].times(b[SnubDodecField.A]);
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.A].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            result[SnubDodecField.D] = factor;
            factor = a[SnubDodecField.E].times(b[SnubDodecField.A]);
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.A].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            result[SnubDodecField.E] = factor;
            factor = a[SnubDodecField.F].times(b[SnubDodecField.A]);
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.B]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.C]));
            factor = factor.plus(a[SnubDodecField.C].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.D].times(b[SnubDodecField.D]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.E]));
            factor = factor.plus(a[SnubDodecField.A].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.B].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.E].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            factor = factor.plus(a[SnubDodecField.F].times(b[SnubDodecField.F]));
            result[SnubDodecField.F] = factor;
            return result;
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
            return this.getUnitTerm(1);
        }

        /**
         * 
         * @return {*}
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getUnitTerm(1);
        }

        static IRRATIONAL_LABELS: string[][]; public static IRRATIONAL_LABELS_$LI$(): string[][] { if (SnubDodecField.IRRATIONAL_LABELS == null) { SnubDodecField.IRRATIONAL_LABELS = [[" ", " "], ["\u03c6", "phi"], ["\u03be", "xi"], ["\u03c6\u03be", "phi*xi"], ["\u03be\u00b2", "xi^2"], ["\u03c6\u03be\u00b2", "phi*xi^2"]]; }  return SnubDodecField.IRRATIONAL_LABELS; }

        public getIrrational$int$int(i: number, format: number): string {
            return SnubDodecField.IRRATIONAL_LABELS_$LI$()[i][format];
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

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} factors
         * @return {number}
         */
        evaluateNumber(factors: com.vzome.core.algebra.BigRational[]): number {
            let result: number = 0.0;
            result += factors[SnubDodecField.A].evaluate();
            result += SnubDodecField.PHI_VALUE_$LI$() * factors[SnubDodecField.B].evaluate();
            result += SnubDodecField.XI_VALUE * factors[SnubDodecField.C].evaluate();
            result += SnubDodecField.PHI_VALUE_$LI$() * SnubDodecField.XI_VALUE * factors[SnubDodecField.D].evaluate();
            result += SnubDodecField.XI_VALUE * SnubDodecField.XI_VALUE * factors[SnubDodecField.E].evaluate();
            result += SnubDodecField.XI_VALUE * SnubDodecField.XI_VALUE * SnubDodecField.PHI_VALUE_$LI$() * factors[SnubDodecField.F].evaluate();
            return result;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.BigRational[]} factors
         * @param {number} whichIrrational
         * @return {com.vzome.core.algebra.BigRational[]}
         */
        scaleBy(factors: com.vzome.core.algebra.BigRational[], whichIrrational: number): com.vzome.core.algebra.BigRational[] {
            switch((whichIrrational)) {
            case 0 /* A */:
                return factors;
            case 1 /* B */:
                return [factors[SnubDodecField.B], factors[SnubDodecField.A].plus(factors[SnubDodecField.B]), factors[SnubDodecField.D], factors[SnubDodecField.C].plus(factors[SnubDodecField.D]), factors[SnubDodecField.F], factors[SnubDodecField.E].plus(factors[SnubDodecField.F])];
            case 2 /* C */:
                return [factors[SnubDodecField.F], factors[SnubDodecField.E].plus(factors[SnubDodecField.F]), factors[SnubDodecField.A].plus(factors[SnubDodecField.E]).plus(factors[SnubDodecField.E]), factors[SnubDodecField.B].plus(factors[SnubDodecField.F]).plus(factors[SnubDodecField.F]), factors[SnubDodecField.C], factors[SnubDodecField.D]];
            case 3 /* D */:
                return this.scaleBy(this.scaleBy(factors, SnubDodecField.B), SnubDodecField.C);
            case 4 /* E */:
                return this.scaleBy(this.scaleBy(factors, SnubDodecField.C), SnubDodecField.C);
            case 5 /* F */:
                return this.scaleBy(this.scaleBy(factors, SnubDodecField.D), SnubDodecField.C);
            default:
                throw new java.lang.IllegalArgumentException(whichIrrational + " is not a valid irrational in this field");
            }
        }
    }
    SnubDodecField["__class"] = "com.vzome.core.algebra.SnubDodecField";
    SnubDodecField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

