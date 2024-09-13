/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export abstract class AbstractAlgebraicField implements com.vzome.core.algebra.AlgebraicField {
        /* Default method injected from com.vzome.core.algebra.AlgebraicField */
        supportsSubfield(fieldName: string): boolean {
            if (fieldName === this.getName())return true;
            return (fieldName === ("golden")) && this.getGoldenRatio() != null;
        }
        abstract multiply(v1: com.vzome.core.algebra.BigRational[], v2: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.BigRational[];

        abstract evaluateNumber(factors: com.vzome.core.algebra.BigRational[]): number;

        abstract scaleBy(factors: com.vzome.core.algebra.BigRational[], whichIrrational: number): com.vzome.core.algebra.BigRational[];

        public abstract getCoefficients(): number[];

        /**
         * The integers should be the same indices used by getUnitTerm().
         * Subclasses must override to usefully participate in the generation
         * of AlgebraicSeries.
         * @param {*} input
         * @return
         * @return {*}
         */
        public recurrence(input: java.util.List<number>): java.util.List<number> {
            return input;
        }

        normalize(factors: com.vzome.core.algebra.BigRational[]) {
        }

        /**
         * 
         * @return {number}
         */
        public getOrder(): number {
            return this.order;
        }

        /**
         * 
         * @return {number}
         */
        public getNumIrrationals(): number {
            return this.order - 1;
        }

        /**
         * 
         * @return {boolean}
         */
        public scale4dRoots(): boolean {
            return false;
        }

        /**
         * 
         * @return {boolean}
         */
        public doubleFrameVectors(): boolean {
            return false;
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getNumberByName(name: string): com.vzome.core.algebra.AlgebraicNumber {
            switch((name)) {
            case "zero":
                return this.zero();
            case "one":
                return this.one();
            case "phi":
            case "\u03c6":
                return this.getGoldenRatio();
            case "\u221a5":
            case "root5":
            case "sqrt5":
                {
                    const n: com.vzome.core.algebra.AlgebraicNumber = this.getGoldenRatio();
                    return n == null ? null : n['plus$com_vzome_core_algebra_AlgebraicNumber'](n)['minus$com_vzome_core_algebra_AlgebraicNumber'](this.one());
                };
            case "\u221a8":
            case "root8":
            case "sqrt8":
                {
                    const n: com.vzome.core.algebra.AlgebraicNumber = this.getNumberByName("sqrt2");
                    return n == null ? null : n['times$com_vzome_core_algebra_AlgebraicNumber'](this.createRational$long(2));
                };
            default:
                for(let format: number = AbstractAlgebraicField.DEFAULT_FORMAT; format <= AbstractAlgebraicField.EXPRESSION_FORMAT; format++) {{
                    for(let i: number = 1; i < this.getOrder(); i++) {{
                        if (this['getIrrational$int$int'](i, format) === name){
                            return this.getUnitTerm(i);
                        }
                    };}
                };}
            }
            return null;
        }

        public getIrrational$int(i: number): string {
            return this['getIrrational$int$int'](i, AbstractAlgebraicField.DEFAULT_FORMAT);
        }

        name: string;

        /*private*/ order: number;

        /*private*/ __hashCode: number;

        __one: com.vzome.core.algebra.AlgebraicNumber;

        __zero: com.vzome.core.algebra.AlgebraicNumber;

        /**
         * Positive powers of the irrationals.
         */
        /*private*/ positivePowers: java.util.ArrayList<com.vzome.core.algebra.AlgebraicNumber>[];

        /**
         * Negative powers of the irrationals.
         */
        /*private*/ negativePowers: java.util.ArrayList<com.vzome.core.algebra.AlgebraicNumber>[];

        static SMALL_SERIES_THRESHOLD: number = 30.0;

        /*private*/ smallSeries: com.vzome.core.algebra.AlgebraicSeries;

        numberFactory: com.vzome.core.algebra.AlgebraicNumberFactory;

        public constructor(name: string, order: number, factory: com.vzome.core.algebra.AlgebraicNumberFactory) {
            if (this.name === undefined) { this.name = null; }
            if (this.order === undefined) { this.order = 0; }
            if (this.__hashCode === undefined) { this.__hashCode = null; }
            if (this.__one === undefined) { this.__one = null; }
            if (this.__zero === undefined) { this.__zero = null; }
            if (this.positivePowers === undefined) { this.positivePowers = null; }
            if (this.negativePowers === undefined) { this.negativePowers = null; }
            if (this.smallSeries === undefined) { this.smallSeries = null; }
            if (this.numberFactory === undefined) { this.numberFactory = null; }
            this.name = name;
            this.order = order;
            this.numberFactory = factory;
            this.__zero = this.numberFactory.createRational(this, 0, 1);
            this.__one = this.numberFactory.createRational(this, 1, 1);
            this.positivePowers = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
            this.negativePowers = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
        }

        /*private*/ initSmallSeries() {
            if (this.smallSeries == null){
                this.smallSeries = this.generateSeries(AbstractAlgebraicField.SMALL_SERIES_THRESHOLD);
            }
        }

        public nearestAlgebraicNumber(target: number): com.vzome.core.algebra.AlgebraicNumber {
            this.initSmallSeries();
            return this.smallSeries.nearestAlgebraicNumber(target);
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} target
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public nearestAlgebraicVector(target: com.vzome.core.math.RealVector): com.vzome.core.algebra.AlgebraicVector {
            this.initSmallSeries();
            return new com.vzome.core.algebra.AlgebraicVector(this.smallSeries.nearestAlgebraicNumber(target.x), this.smallSeries.nearestAlgebraicNumber(target.y), this.smallSeries.nearestAlgebraicNumber(target.z));
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return this.name;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return this.getName();
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            if (this.__hashCode == null){
                const prime: number = 43;
                this.__hashCode = 7;
                const coefficients: number[] = this.getCoefficients();
                for(let i: number = 0; i < coefficients.length; i++) {{
                    const coefficient: number = coefficients[i];
                    this.__hashCode = prime * this.__hashCode + /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(coefficient));
                };}
            }
            return this.__hashCode;
        }

        /**
         * With the use of parameterized fields, it's possible for two fields
         * of different classes to be equal
         * or for two fields of the same class to not be equal.
         * For example RootTwoField equals SqrtField(2)
         * but SqrtField(2) does not equal SqrtField(3).
         * Similarly, PolygonField(4) equals SqrtField(2)
         * and PolygonField(6) equals SqrtField(3).
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj){
                return true;
            }
            if (obj == null){
                return false;
            }
            if (!(obj != null && obj instanceof <any>com.vzome.core.algebra.AbstractAlgebraicField)){
                return false;
            }
            const that: AbstractAlgebraicField = <AbstractAlgebraicField>obj;
            if (this.getName() === that.getName()){
                return true;
            }
            if (this.getOrder() !== that.getOrder()){
                return false;
            }
            const thisCoefficients: number[] = this.getCoefficients();
            const thatCoefficients: number[] = that.getCoefficients();
            for(let i: number = 0; i < thisCoefficients.length; i++) {{
                if (thisCoefficients[i] - thatCoefficients[i] !== 0.0){
                    return false;
                }
            };}
            return true;
        }

        /**
         * This method is intended to allow subclasses to intercept a 4 element int array
         * representing the numerators and denominators of a pair of terms (units and phis)
         * from the golden field and remap them as needed for that field.
         * Otherwise, the terms are returned unchanged.
         * @param terms
         * @return
         * @param {long[]} pairs
         * @return {long[]}
         */
        convertGoldenNumberPairs(pairs: number[]): number[] {
            if (pairs.length === 2 * this.order)return pairs; else {
                const newPairs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(2 * this.order);
                for(let i: number = 0; i < this.order; i++) {{
                    newPairs[2 * i + 0] = (i >= 2) ? 0 : pairs[2 * i + 0];
                    newPairs[2 * i + 1] = (i >= 2) ? 1 : pairs[2 * i + 1];
                };}
                return newPairs;
            }
        }

        public createAlgebraicNumber$int_A(terms: number[]): com.vzome.core.algebra.AlgebraicNumber {
            return this.numberFactory.createAlgebraicNumber(this, terms, 1);
        }

        /**
         * Generates an AlgebraicNumber from a "trailing divisor" int array representation.
         * @param {int[]} trailingDivisorForm numerators trailed by a common denominator for all numerators
         * @return
         * @return {*}
         */
        public createAlgebraicNumberFromTD(trailingDivisorForm: number[]): com.vzome.core.algebra.AlgebraicNumber {
            let terms: number = trailingDivisorForm.length - 1;
            if (terms === 2 && this.getOrder() > 2){
                let pairs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(2 * terms);
                const divisor: number = trailingDivisorForm[terms];
                for(let i: number = 0; i < terms; i++) {{
                    pairs[2 * i + 0] = trailingDivisorForm[i];
                    pairs[2 * i + 1] = divisor;
                };}
                pairs = this.convertGoldenNumberPairs(pairs);
                terms = (pairs.length / 2|0);
                trailingDivisorForm = (s => { let a=[]; while(s-->0) a.push(0); return a; })(terms + 1);
                trailingDivisorForm[terms] = (<number>pairs[1]|0);
                for(let i: number = 0; i < (pairs.length / 2|0); i++) {{
                    trailingDivisorForm[i] = (<number>pairs[2 * i]|0);
                };}
            }
            return this.numberFactory.createAlgebraicNumberFromTD(this, trailingDivisorForm);
        }

        public createAlgebraicNumber$int_A$int(numerators: number[], denominator: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.numberFactory.createAlgebraicNumber(this, numerators, denominator);
        }

        public createAlgebraicNumber$int$int$int$int(ones: number, irrat: number, denominator: number, scalePower: number): com.vzome.core.algebra.AlgebraicNumber {
            const factors: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(this.order + 1);
            factors[0] = ones;
            factors[1] = irrat;
            for(let i: number = 2; i < this.order; i++) {{
                factors[i] = 0;
            };}
            factors[this.order] = denominator;
            const result: com.vzome.core.algebra.AlgebraicNumber = this.numberFactory.createAlgebraicNumberFromTD(this, factors);
            if (scalePower !== 0){
                const multiplier: com.vzome.core.algebra.AlgebraicNumber = this.createPower$int(scalePower);
                return result['times$com_vzome_core_algebra_AlgebraicNumber'](multiplier);
            } else return result;
        }

        /**
         * 
         * @param {number} ones
         * @param {number} irrat
         * @param {number} denominator
         * @param {number} scalePower
         * @return {*}
         */
        public createAlgebraicNumber(ones?: any, irrat?: any, denominator?: any, scalePower?: any): com.vzome.core.algebra.AlgebraicNumber {
            if (((typeof ones === 'number') || ones === null) && ((typeof irrat === 'number') || irrat === null) && ((typeof denominator === 'number') || denominator === null) && ((typeof scalePower === 'number') || scalePower === null)) {
                return <any>this.createAlgebraicNumber$int$int$int$int(ones, irrat, denominator, scalePower);
            } else if (((ones != null && ones instanceof <any>Array && (ones.length == 0 || ones[0] == null ||(typeof ones[0] === 'number'))) || ones === null) && ((typeof irrat === 'number') || irrat === null) && denominator === undefined && scalePower === undefined) {
                return <any>this.createAlgebraicNumber$int_A$int(ones, irrat);
            } else if (((ones != null && ones instanceof <any>Array && (ones.length == 0 || ones[0] == null ||(typeof ones[0] === 'number'))) || ones === null) && irrat === undefined && denominator === undefined && scalePower === undefined) {
                return <any>this.createAlgebraicNumber$int_A(ones);
            } else throw new Error('invalid overload');
        }

        /**
         * The golden ratio (and thus icosahedral symmetry and related tools)
         * can be generated by some fields even though it's not one of their irrational coefficients.
         * For example, SqrtField(5) and PolygonField(10) can both generate the golden ratio
         * so they can support icosa symmetry and related tools.
         * In some such cases, the resulting AlgebraicNumber
         * may have multiple terms and/or factors other than one.
         * 
         * @return {*} An AlgebraicNumber which evaluates to the golden ratio, or null if not possible in this field.
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            return null;
        }

        public createPower$int(power: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.createPower$int$int(power, 1);
        }

        public createPower$int$int(power: number, irr: number): com.vzome.core.algebra.AlgebraicNumber {
            const one: com.vzome.core.algebra.AlgebraicNumber = this.one();
            if (power === 0 || irr === 0)return one;
            irr -= 1;
            if (power > 0){
                if (this.positivePowers[irr] == null)this.positivePowers[irr] = <any>(new java.util.ArrayList<any>(8));
                if (power >= this.positivePowers[irr].size()){
                    if (this.positivePowers[irr].isEmpty()){
                        this.positivePowers[irr].add(one);
                        this.positivePowers[irr].add(this.getUnitTerm(irr + 1));
                    }
                    const size: number = this.positivePowers[irr].size();
                    const irrat: com.vzome.core.algebra.AlgebraicNumber = this.positivePowers[irr].get(1);
                    let last: com.vzome.core.algebra.AlgebraicNumber = this.positivePowers[irr].get(size - 1);
                    for(let i: number = size; i <= power; i++) {{
                        const next: com.vzome.core.algebra.AlgebraicNumber = last['times$com_vzome_core_algebra_AlgebraicNumber'](irrat);
                        this.positivePowers[irr].add(next);
                        last = next;
                    };}
                }
                return this.positivePowers[irr].get(power);
            } else {
                power = -power;
                if (this.negativePowers[irr] == null)this.negativePowers[irr] = <any>(new java.util.ArrayList<any>(8));
                if (power >= this.negativePowers[irr].size()){
                    if (this.negativePowers[irr].isEmpty()){
                        this.negativePowers[irr].add(one);
                        this.negativePowers[irr].add(this.getUnitTerm(irr + 1).reciprocal());
                    }
                    const size: number = this.negativePowers[irr].size();
                    const irrat: com.vzome.core.algebra.AlgebraicNumber = this.negativePowers[irr].get(1);
                    let last: com.vzome.core.algebra.AlgebraicNumber = this.negativePowers[irr].get(size - 1);
                    for(let i: number = size; i <= power; i++) {{
                        const next: com.vzome.core.algebra.AlgebraicNumber = last['times$com_vzome_core_algebra_AlgebraicNumber'](irrat);
                        this.negativePowers[irr].add(next);
                        last = next;
                    };}
                }
                return this.negativePowers[irr].get(power);
            }
        }

        /**
         * 
         * @param {number} power
         * @param {number} irr
         * @return {*}
         */
        public createPower(power?: any, irr?: any): com.vzome.core.algebra.AlgebraicNumber {
            if (((typeof power === 'number') || power === null) && ((typeof irr === 'number') || irr === null)) {
                return <any>this.createPower$int$int(power, irr);
            } else if (((typeof power === 'number') || power === null) && irr === undefined) {
                return <any>this.createPower$int(power);
            } else throw new Error('invalid overload');
        }

        public createRational$long(wholeNumber: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.numberFactory.createRational(this, wholeNumber, 1);
        }

        public createRational$long$long(numerator: number, denominator: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.numberFactory.createRational(this, numerator, denominator);
        }

        /**
         * @param {number} numerator
         * @param {number} denominator
         * @return {*} AlgebraicNumber
         */
        public createRational(numerator?: any, denominator?: any): com.vzome.core.algebra.AlgebraicNumber {
            if (((typeof numerator === 'number') || numerator === null) && ((typeof denominator === 'number') || denominator === null)) {
                return <any>this.createRational$long$long(numerator, denominator);
            } else if (((typeof numerator === 'number') || numerator === null) && denominator === undefined) {
                return <any>this.createRational$long(numerator);
            } else throw new Error('invalid overload');
        }

        /**
         * @return {*} The AlgebraicNumber to be use for the Chord Ratio construction in the given field.
         * This method can be used to generalize an AffinePolygon tool and a PolygonalAntiprismSymmetry.
         * This base class returns one, which is the scalar for an affine square and works in any field.
         * Derived classes should override this method if they can be used to generate any other affine polygon.
         */
        public getAffineScalar(): com.vzome.core.algebra.AlgebraicNumber {
            return this.__one;
        }

        /**
         * @param {number} n specifies the ordinal of the term in the AlgebraicNumber which will be set to one.
         * When {@code n == 0}, the result is the same as {@code createRational(1)}.
         * When {@code n == 1}, the result is the same as {@code createPower(1)}.
         * When {@code n < 0}, the result will be {@code zero()}.
         * When {@code n >= getOrder()}, an IndexOutOfBoundsException will be thrown.
         * @return {*} an AlgebraicNumber with the factor specified by {@code n} set to one.
         */
        public getUnitTerm(n: number): com.vzome.core.algebra.AlgebraicNumber {
            if (n < 0){
                return this.zero();
            }
            const factors: number[] = this.zero().toTrailingDivisor();
            factors[n] = factors[factors.length - 1];
            return this.numberFactory.createAlgebraicNumberFromTD(this, factors);
        }

        /**
         * Drop one coordinate from the 4D vector. If wFirst (the usual), then drop
         * the first coordinate, taking the "imaginary part" of the vector. If
         * !wFirst (for old VEF import, etc.), drop the last coordinate.
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} source
         * @param {boolean} wFirst
         * @return
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public projectTo3d(source: com.vzome.core.algebra.AlgebraicVector, wFirst: boolean): com.vzome.core.algebra.AlgebraicVector {
            if (source.dimension() === 3)return source; else {
                const result: com.vzome.core.algebra.AlgebraicVector = this.origin(3);
                for(let i: number = 0; i < 3; i++) {result.setComponent(i, source.getComponent(wFirst ? i + 1 : i));}
                return result;
            }
        }

        /**
         * 
         * @param {number} dims
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public origin(dims: number): com.vzome.core.algebra.AlgebraicVector {
            return new com.vzome.core.algebra.AlgebraicVector(this, dims);
        }

        /**
         * 
         * @param {number} dims
         * @param {number} axis
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public basisVector(dims: number, axis: number): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.origin(dims);
            return result.setComponent(axis, this.one());
        }

        reciprocal(fieldElement: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.BigRational[] {
            const length: number = fieldElement.length;
            const representation: com.vzome.core.algebra.BigRational[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([length, length]);
            let isZero: boolean = true;
            for(let i: number = 0; i < length; i++) {{
                isZero = isZero && fieldElement[i].isZero();
                representation[0][i] = fieldElement[i];
            };}
            if (isZero)throw new java.lang.RuntimeException("Denominator is zero");
            for(let j: number = 1; j < length; j++) {{
                const column: com.vzome.core.algebra.BigRational[] = this.scaleBy(fieldElement, j);
                java.lang.System.arraycopy(column, 0, representation[j], 0, length);
            };}
            const reciprocal: com.vzome.core.algebra.BigRational[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([length, length]);
            for(let j: number = 0; j < length; j++) {{
                for(let i: number = 0; i < length; i++) {{
                    reciprocal[j][i] = (i === j) ? this.numberFactory.one() : this.numberFactory.zero();
                };}
            };}
            const rank: number = com.vzome.core.algebra.Fields.gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A$com_vzome_core_algebra_Fields_Element_A_A(representation, reciprocal);
            const reciprocalFactors: com.vzome.core.algebra.BigRational[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(length);
            java.lang.System.arraycopy(reciprocal[0], 0, reciprocalFactors, 0, length);
            return (rank === length) ? reciprocalFactors : this.onReciprocalRankDeficient(rank, reciprocal, reciprocalFactors);
        }

        /**
         * Subclasses can overloading this method to handle special cases. (e.g. SqrtField of a perfect square)
         * @param {number} rank
         * @param {com.vzome.core.algebra.BigRational[][]} reciprocal
         * @param {com.vzome.core.algebra.BigRational[]} reciprocalFactors
         * @throws IllegalStateException
         * @return {com.vzome.core.algebra.BigRational[]}
         */
        onReciprocalRankDeficient(rank: number, reciprocal: com.vzome.core.algebra.BigRational[][], reciprocalFactors: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.BigRational[] {
            const msg: string = this.getName() + " expects reciprocal matrix to be full rank (" + reciprocal.length + "), but it is " + rank + ".";
            console.error(msg);
            throw new java.lang.IllegalStateException(msg);
        }

        public static DEFAULT_FORMAT: number = 0;

        public static EXPRESSION_FORMAT: number = 1;

        public static ZOMIC_FORMAT: number = 2;

        public static VEF_FORMAT: number = 3;

        /**
         * 
         * @return {*}
         */
        public zero(): com.vzome.core.algebra.AlgebraicNumber {
            return this.__zero;
        }

        /**
         * 
         * @return {*}
         */
        public one(): com.vzome.core.algebra.AlgebraicNumber {
            return this.__one;
        }

        /**
         * 
         * @param {int[][]} nums is an array of integer arrays: One array of coordinate terms per dimension.
         * Initially, this is designed to simplify migration of order 2 golden directions
         * to new fields of higher order having golden subfields as their first two factors.
         * {@code
         * field.createVector( new int[]  {  0,1,2,3,   4,5,6,7,   8,9,0,1  } );   // older code like this...
         * field.createVector( new int[][]{ {0,1,2,3}, {4,5,6,7}, {8,9,0,1} } );   // should be replaced by this.
         * }
         * The older code shown in the first example requires an order 2 field.
         * The second example will work with any field of order 2 or greater.
         * This new overload has the advantage that the internal arrays representing the individual dimensions are more clearly delineated and controlled.
         * Inner arrays require an even number of elements since they represent a sequence of numerator/denominator pairs.
         * 
         * createVector is currently limited to int valued vectors, not long, and definitely not BigInteger
         * In most cases, this is adequate, but in the case where it's called by XmlSaveFormat.parseAlgebraicObject(),
         * it seems possible that a value larger than Integer.MAX_VALUE could be saved to the XML which could not subsequently be parsed.
         * TODO: Consider refactoring createVector to use long[][] instead of int[][] if this becomes an issue.
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector} an AlgebraicVector
         */
        public createVector(nums: number[][]): com.vzome.core.algebra.AlgebraicVector {
            const dims: number = nums.length;
            const coords: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
            for(let c: number = 0; c < coords.length; c++) {{
                const coordLength: number = nums[c].length;
                if (coordLength % 2 !== 0){
                    throw new java.lang.IllegalStateException("Vector dimension " + c + " has " + coordLength + " components. An even number is required.");
                }
                const nTerms: number = (coordLength / 2|0);
                if (nTerms > this.getOrder()){
                    throw new java.lang.IllegalStateException("Vector dimension " + c + " has " + ((coordLength / 2|0)) + " terms. Each dimension of the " + this.getName() + " field is limited to " + this.getOrder() + " terms. Each term consists of a numerator and a denominator.");
                }
                let pairs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(nums[c].length);
                for(let i: number = 0; i < pairs.length; i++) {{
                    pairs[i] = nums[c][i];
                };}
                if (pairs.length === 4 && this.getOrder() > 2){
                    pairs = this.convertGoldenNumberPairs(pairs);
                }
                coords[c] = this.numberFactory.createAlgebraicNumberFromPairs(this, pairs);
            };}
            return new com.vzome.core.algebra.AlgebraicVector(coords);
        }

        /**
         * 
         * @param {int[][]} nums
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public createVectorFromTDs(nums: number[][]): com.vzome.core.algebra.AlgebraicVector {
            const x: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromTD(nums[0]);
            const y: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromTD(nums[1]);
            const z: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromTD(nums[2]);
            return new com.vzome.core.algebra.AlgebraicVector(x, y, z);
        }

        /**
         * Generates an AlgebraicVector with all AlgebraicNumber terms being integers (having unit denominators).
         * Contrast this with {@code createVector(int[][] nums)} which requires all denominators to be specified.
         * @param {int[][]} nums is a 2 dimensional integer array. The length of nums becomes the number of dimensions in the resulting AlgebraicVector.
         * For example, {@code (new PentagonField()).createIntegerVector( new int[][]{ {0,-1}, {2,3}, {4,5} } ); }
         * generates the 3 dimensional vector (-φ, 2 +3φ, 4 +5φ) having all integer terms.
         * @return {com.vzome.core.algebra.AlgebraicVector} an AlgebraicVector
         */
        public createIntegerVector(nums: number[][]): com.vzome.core.algebra.AlgebraicVector {
            const dims: number = nums.length;
            const result: com.vzome.core.algebra.AlgebraicVector = this.origin(dims);
            for(let dim: number = 0; dim < dims; dim++) {{
                result.setComponent(dim, this.createAlgebraicNumber$int_A(nums[dim]));
            };}
            return result;
        }

        /**
         * Create a 3x3 square matrix from integer data.
         * TODO: Generalize this method to create a matrix with dimensions matching the dimensions of the data array
         * Sample input data for an order-4 field:
         * {{{7,5,0,1,-4,5,0,1},{-2,5,0,1,4,5,0,1},{0,1,-8,5,0,1,6,5}},
         * {{-2,5,0,1,4,5,0,1},{7,5,0,1,-4,5,0,1},{0,1,8,5,0,1,-6,5}},
         * {{0,1,-8,5,0,1,6,5},{0,1,8,5,0,1,-6,5},{-9,5,0,1,8,5,0,1}}}
         * @param field
         * @param {int[][][]} data integer coordinates, in row-major order, complete with denominators.
         * @return
         * @return {com.vzome.core.algebra.AlgebraicMatrix}
         */
        public createMatrix(data: number[][][]): com.vzome.core.algebra.AlgebraicMatrix {
            const col1: com.vzome.core.algebra.AlgebraicVector = this.createVector([data[0][0], data[1][0], data[2][0]]);
            const col2: com.vzome.core.algebra.AlgebraicVector = this.createVector([data[0][1], data[1][1], data[2][1]]);
            const col3: com.vzome.core.algebra.AlgebraicVector = this.createVector([data[0][2], data[1][2], data[2][2]]);
            return new com.vzome.core.algebra.AlgebraicMatrix(col1, col2, col3);
        }

        /**
         * 
         * @param {java.lang.StringBuffer} buf
         * @param {com.vzome.core.algebra.BigRational[]} factors
         * @param {number} format must be one of the following values.
         * The result is formatted as follows:
         * <br>
         * {@code DEFAULT_FORMAT    // 4 + 3φ}<br>
         * {@code EXPRESSION_FORMAT // 4 +3*phi}<br>
         * {@code ZOMIC_FORMAT      // 4 3}<br>
         * {@code VEF_FORMAT        // (3,4)}
         */
        getNumberExpression(buf: java.lang.StringBuffer, factors: com.vzome.core.algebra.BigRational[], format: number) {
            switch((format)) {
            case 2 /* ZOMIC_FORMAT */:
                for(let i: number = 0; i < factors.length; i++) {{
                    if (i > 0)buf.append(" ");
                    buf.append(factors[i].toString());
                };}
                break;
            case 3 /* VEF_FORMAT */:
                buf.append("(");
                for(let i: number = factors.length; i > 0; i--) {{
                    buf.append(factors[i - 1].toString());
                    if (i > 1)buf.append(",");
                };}
                buf.append(")");
                break;
            default:
                let first: number = 0;
                for(let i: number = 0; i < factors.length; i++) {{
                    let factor: com.vzome.core.algebra.BigRational = factors[i];
                    if (factor.isZero()){
                        ++first;
                        continue;
                    }
                    if (i > first){
                        buf.append(" ");
                    }
                    if (factor.isNegative()){
                        factor = factor.negate();
                        buf.append("-");
                    } else if (i > first){
                        buf.append("+");
                    }
                    if (i === 0)buf.append(factor.toString()); else {
                        if (!factor.isOne()){
                            buf.append(factor.toString());
                            if (format === AbstractAlgebraicField.EXPRESSION_FORMAT)buf.append("*");
                        }
                        const multiplier: string = this['getIrrational$int$int'](i, format);
                        buf.append(multiplier);
                    }
                };}
                if (first === factors.length)buf.append("0");
                break;
            }
        }

        /**
         * 
         * @param {string} val
         * @return {*}
         */
        public parseLegacyNumber(val: string): com.vzome.core.algebra.AlgebraicNumber {
            throw new java.lang.IllegalStateException("This field does not support vZome 2.x files.");
        }

        /**
         * 
         * @param {string} string
         * @param {boolean} isRational
         * @return {*}
         */
        public parseVefNumber(string: string, isRational: boolean): com.vzome.core.algebra.AlgebraicNumber {
            let pairs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(this.getOrder() * 2);
            for(let i: number = 1; i < pairs.length; i += 2) {{
                pairs[i] = 1;
            };}
            if ((!isRational) && /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "(") && /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(string, ")")){
                const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(string.substring(1, string.length - 1), ",");
                const numStack: java.util.Stack<number> = <any>(new java.util.Stack<any>());
                const denomStack: java.util.Stack<number> = <any>(new java.util.Stack<any>());
                while((tokens.hasMoreTokens())) {{
                    if (numStack.size() >= this.getOrder()){
                        throw new java.lang.RuntimeException("VEF format error: \"" + string + "\" has too many factors for " + this.getName() + " field");
                    }
                    const parts: string[] = tokens.nextToken().split("/");
                    numStack.push(javaemul.internal.IntegerHelper.parseInt(parts[0]));
                    denomStack.push((parts.length > 1) ? javaemul.internal.IntegerHelper.parseInt(parts[1]) : 1);
                }};
                let i: number = 0;
                while((!numStack.empty())) {{
                    pairs[i++] = numStack.pop();
                    pairs[i++] = denomStack.pop();
                }};
                if (i === 4 && this.getOrder() > 2){
                    pairs = this.convertGoldenNumberPairs([pairs[0], pairs[1], pairs[2], pairs[3]]);
                }
            } else {
                const parts: string[] = string.split("/");
                pairs[0] = javaemul.internal.IntegerHelper.parseInt(parts[0]);
                pairs[1] = (parts.length > 1) ? javaemul.internal.IntegerHelper.parseInt(parts[1]) : 1;
            }
            return this.numberFactory.createAlgebraicNumberFromPairs(this, pairs);
        }

        public parseNumber$java_lang_String(nums: string): com.vzome.core.algebra.AlgebraicNumber {
            const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(nums, " ");
            return this.parseNumber$java_util_StringTokenizer(tokens);
        }

        /**
         * 
         * @param {string} nums
         * @return {*}
         */
        public parseNumber(nums?: any): com.vzome.core.algebra.AlgebraicNumber {
            if (((typeof nums === 'string') || nums === null)) {
                return <any>this.parseNumber$java_lang_String(nums);
            } else if (((nums != null && nums instanceof <any>java.util.StringTokenizer) || nums === null)) {
                return <any>this.parseNumber$java_util_StringTokenizer(nums);
            } else throw new Error('invalid overload');
        }

        /*private*/ parseNumber$java_util_StringTokenizer(tokens: java.util.StringTokenizer): com.vzome.core.algebra.AlgebraicNumber {
            const order: number = this.getOrder();
            const pairs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(order * 2);
            for(let i: number = 0; i < order; i++) {{
                const digit: string = tokens.nextToken();
                const parts: string[] = digit.split("/");
                pairs[i * 2] = javaemul.internal.LongHelper.parseLong(parts[0]);
                if (parts.length > 1)pairs[i * 2 + 1] = javaemul.internal.LongHelper.parseLong(parts[1]); else pairs[i * 2 + 1] = 1;
            };}
            return this.numberFactory.createAlgebraicNumberFromPairs(this, pairs);
        }

        /**
         * 
         * @param {string} nums
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public parseVector(nums: string): com.vzome.core.algebra.AlgebraicVector {
            const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(nums, " ");
            const numToks: number = tokens.countTokens();
            if (numToks % this.getOrder() !== 0)throw new java.lang.IllegalStateException("Field order (" + this.getOrder() + ") does not divide token count: " + numToks + ", for \'" + nums + "\'");
            const dims: number = (numToks / this.getOrder()|0);
            const coords: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
            for(let i: number = 0; i < dims; i++) {{
                coords[i] = this.parseNumber$java_util_StringTokenizer(tokens);
            };}
            return new com.vzome.core.algebra.AlgebraicVector(coords);
        }

        /**
         * 
         * @param {number} dims
         * @return {com.vzome.core.algebra.AlgebraicMatrix}
         */
        public identityMatrix(dims: number): com.vzome.core.algebra.AlgebraicMatrix {
            const columns: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
            for(let i: number = 0; i < columns.length; i++) {{
                columns[i] = this.basisVector(dims, i);
            };}
            return new com.vzome.core.algebra.AlgebraicMatrix(columns);
        }

        /**
         * @return {number} the number of independent multipliers in this field.
         * These are the primitive elements of the field.
         * The value should be less than or equal to getNumIrrationals.
         * It will be less whenever the irrationals are dependent.
         * For example, in the field for sqrt(phi), there is only one
         * multiplier, since the other irrational is just the square of that one.
         */
        public getNumMultipliers(): number {
            return this.getNumIrrationals();
        }

        public generateSeries(threshold: number): com.vzome.core.algebra.AlgebraicSeries {
            const multiplier: com.vzome.core.algebra.AlgebraicNumber = this.createPower$int$int(1, this.getNumIrrationals());
            let cover: com.vzome.core.algebra.AlgebraicNumber = this.one();
            let power: number = 0;
            while((cover.evaluate() < threshold)) {{
                cover = cover['times$com_vzome_core_algebra_AlgebraicNumber'](multiplier);
                ++power;
            }};
            return new com.vzome.core.algebra.AlgebraicSeries(this, power);
        }

        public getMathML(factors: com.vzome.core.algebra.BigRational[]): string {
            const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
            let first: number = 0;
            for(let i: number = 0; i < factors.length; i++) {{
                let factor: com.vzome.core.algebra.BigRational = factors[i];
                if (factor.isZero()){
                    ++first;
                    continue;
                }
                if (factor.isNegative()){
                    factor = factor.negate();
                    buf.append("<mo>-</mo>");
                } else if (i > first){
                    buf.append("<mo>+</mo>");
                }
                if (i === 0)buf.append(factor.getMathML()); else {
                    if (!factor.isOne()){
                        buf.append(factor.getMathML());
                    }
                    const multiplier: string = this['getIrrational$int$int'](i, AbstractAlgebraicField.DEFAULT_FORMAT);
                    buf.append("<mi>");
                    buf.append(multiplier);
                    buf.append("</mi>");
                }
            };}
            if (first === factors.length)return "<mn>0</mn>"; else if (factors.length - first > 1)return "<mrow>" + buf.toString() + "</mrow>"; else return buf.toString();
        }

        /**
         * 
         * @param {string} script
         * @param {string} language
         * @param {com.vzome.core.construction.Point} offset
         * @param {*} symmetry
         * @param {*} effects
         */
        public interpretScript(script: string, language: string, offset: com.vzome.core.construction.Point, symmetry: com.vzome.core.math.symmetry.Symmetry, effects: com.vzome.core.construction.ConstructionChanges) {
            throw new Error("Scripts are only supported in the golden field.");
        }

        public abstract getIrrational(i?: any, format?: any): any;    }
    AbstractAlgebraicField["__class"] = "com.vzome.core.algebra.AbstractAlgebraicField";
    AbstractAlgebraicField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

