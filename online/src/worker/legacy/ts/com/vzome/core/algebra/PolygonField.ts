/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author David Hall
     * @param {number} polygonSides
     * @param {*} factory
     * @class
     * @extends com.vzome.core.algebra.ParameterizedField
     */
    export class PolygonField extends com.vzome.core.algebra.ParameterizedField {
        static PI: number = 3.141592653589793;

        /**
         * 
         * @param {number} nSides
         * @return {double[]} the coefficients of a PolygonField given the same parameter.
         * This can be used to determine when two fields have compatible coefficients
         * without having to generate an instance of the class.
         */
        public static getFieldCoefficients(nSides: number): number[] {
            const order: number = PolygonField.getOrder(nSides);
            const coefficients: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(order);
            const diagLengths: number[] = PolygonField.getDiagonalLengths(nSides);
            for(let i: number = 0; i < order; i++) {{
                coefficients[i] = diagLengths[i];
            };}
            return coefficients;
        }

        /**
         * 
         * @param {number} nSides
         * @return {double[]} an array with the unique lengths in increasing order
         * of the diagonals of a regular N-gon having a unit edge length.
         */
        public static getDiagonalLengths(nSides: number): number[] {
            const count: number = PolygonField.diagonalCount(nSides);
            const diagLengths: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(count);
            const unitLength: number = Math.sin(PolygonField.PI / nSides);
            diagLengths[0] = 1.0;
            for(let i: number = 1; i < count; i++) {{
                diagLengths[i] = Math.sin((i + 1) * PolygonField.PI / nSides) / unitLength;
            };}
            switch((nSides)) {
            case 6:
                diagLengths[2] = 2.0;
                diagLengths[1] = Math.sqrt(3);
                break;
            case 5:
                diagLengths[1] = (1.0 + Math.sqrt(5.0)) / 2.0;
                break;
            }
            return diagLengths;
        }

        public static FIELD_PREFIX: string = "polygon";

        public static getOrder(nSides: number): number {
            return PolygonField.primaryDiagonalCount(nSides);
        }

        public static diagonalCount(nSides: number): number {
            return (nSides / 2|0);
        }

        public static primaryDiagonalCount(nSides: number): number {
            return (<number>((n => n<0?Math.ceil(n):Math.floor(n))(PolygonField.eulerTotient(2 * nSides) / 2))|0);
        }

        public static secondaryDiagonalCount(nSides: number): number {
            return PolygonField.diagonalCount(nSides) - PolygonField.primaryDiagonalCount(nSides);
        }

        public static eulerTotient(n: number): number {
            let result: number = n;
            for(let i: number = 2; i * i <= n; i++) {{
                if (n % i === 0)result -= (n => n<0?Math.ceil(n):Math.floor(n))(result / i);
                while((n % i === 0)) {{
                    n = (n => n<0?Math.ceil(n):Math.floor(n))(n / i);
                }};
            };}
            if (n > 1){
                result -= (n => n<0?Math.ceil(n):Math.floor(n))(result / n);
            }
            return result;
        }

        public static isPowerOfTwo(n: number): boolean {
            return (n !== 0) && ((n & -n) === n);
        }

        public isPrime(n: number): boolean {
            return this.numberFactory.isPrime(n);
        }

        /*private*/ distinctPrimeFactors(n: number): java.util.List<number> {
            const factors: java.util.List<number> = <any>(new java.util.ArrayList<any>());
            for(let prime: number = 2; prime <= n; prime = this.numberFactory.nextPrime(prime)) {{
                if (n % prime === 0){
                    factors.add(prime);
                }
                while((n % prime === 0)) {{
                    n = (n => n<0?Math.ceil(n):Math.floor(n))(n / prime);
                }};
            };}
            return factors;
        }

        public getNormalizedMultiplicationTensor(nSides: number): number[][][] {
            const tensor: number[][][] = PolygonField.getExtendedMultiplicationTensor(nSides);
            if (this.isPrime(nSides) || PolygonField.isPowerOfTwo(nSides)){
                return tensor;
            }
            const length: number = PolygonField.primaryDiagonalCount(nSides);
            const result: number[][][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return 0; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([length, length, length]);
            for(let i: number = 0; i < length; i++) {{
                for(let j: number = 0; j < length; j++) {{
                    for(let k: number = 0; k < length; k++) {{
                        result[i][j][k] = tensor[i][j][k];
                    };}
                };}
            };}
            const normalizerMatrix: number[][] = this.getNormalizerMatrix(nSides);
            let n: number = 0;
            for(let term: number = length; term < PolygonField.diagonalCount(nSides); term++) {{
                for(let r: number = 0; r < length; r++) {{
                    for(let c: number = 0; c < length; c++) {{
                        const omit: number = tensor[term][r][c];
                        if (omit !== 0){
                            for(let t: number = 0; t < length; t++) {{
                                const alt: number = normalizerMatrix[n][t];
                                if (alt !== 0){
                                    const adjust: number = omit * alt;
                                    result[t][r][c] = (<number>(result[t][r][c] + adjust)|0);
                                }
                            };}
                        }
                    };}
                };}
                n++;
            };}
            return result;
        }

        public getNormalizerMatrix(nSides: number): number[][] {
            if (nSides < PolygonField.MIN_SIDES){
                throw new java.lang.IllegalArgumentException("nSides = " + nSides + " but must be greater than or equal to " + PolygonField.MIN_SIDES);
            }
            const nSecondaryDiags: number = PolygonField.secondaryDiagonalCount(nSides);
            if (nSecondaryDiags === 0){
                return null;
            }
            const nPrimaryDiags: number = PolygonField.primaryDiagonalCount(nSides);
            const nDiags: number = nPrimaryDiags + nSecondaryDiags;
            const primeFactors: java.util.List<number> = this.distinctPrimeFactors(nSides);
            if (primeFactors.get(0) === 2){
                primeFactors.remove(0);
            }
            let nEquations: number = 0;
            for(let index=primeFactors.iterator();index.hasNext();) {
                let prime = index.next();
                {
                    nEquations += (nDiags / /* intValue */(prime|0)|0);
                }
            }
            const primaryDiags: com.vzome.core.algebra.BigRational[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([nEquations, nPrimaryDiags]);
            const secondaryDiags: com.vzome.core.algebra.BigRational[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([nEquations, nSecondaryDiags]);
            let equationRow: number = 0;
            for(let index=primeFactors.iterator();index.hasNext();) {
                let factor = index.next();
                {
                    const period: number = (nSides / factor|0);
                    const steps: number = (period / 2|0);
                    const parity: number = period % 2;
                    for(let step: number = 0; step < steps; step++) {{
                        let n: number = (step === 0 && parity === 0) ? 2 : 1;
                        if (nSides % 2 === parity){
                            n *= -1;
                        }
                        const terms: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(nDiags);
                        terms[step] = 1;
                        for(let mid: number = period - parity; mid < nDiags; mid += period) {{
                            terms[mid + step + parity] = terms[mid - step] = n;
                            n *= -1;
                        };}
                        primaryDiags[equationRow] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(nPrimaryDiags);
                        secondaryDiags[equationRow] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(nSecondaryDiags);
                        for(let t: number = 0; t < terms.length; t++) {{
                            let term: number = terms[t];
                            if (t < nSecondaryDiags){
                                secondaryDiags[equationRow][t] = this.numberFactory.createBigRational(term, 1);
                            } else {
                                term *= -1;
                                primaryDiags[equationRow][t - nSecondaryDiags] = this.numberFactory.createBigRational(term, 1);
                            }
                        };}
                        equationRow++;
                    };}
                }
            }
            const rank: number = com.vzome.core.algebra.Fields.gaussJordanReduction$com_vzome_core_algebra_Fields_Element_A_A$com_vzome_core_algebra_Fields_Element_A_A(secondaryDiags, primaryDiags);
            if (rank !== nSecondaryDiags){
                throw new java.lang.IllegalStateException("System of equations has unexpected rank: " + rank);
            }
            for(let r: number = rank; r < primaryDiags.length; r++) {{
                for(let c: number = 0; c < primaryDiags[0].length; c++) {{
                    if (!primaryDiags[r][c].isZero()){
                        throw new java.lang.IllegalStateException("System of equations is inconsistent. Rank = " + rank);
                    }
                };}
            };}
            const results: number[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return 0; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([rank, nPrimaryDiags]);
            for(let r: number = 0; r < rank; r++) {{
                for(let c: number = nPrimaryDiags - 1; c >= 0; c--) {{
                    const bigTerm: com.vzome.core.algebra.BigRational = primaryDiags[rank - 1 - r][nPrimaryDiags - 1 - c];
                    results[r][c] = /* shortValue */(javaemul.internal.DoubleHelper.valueOf(bigTerm.evaluate())|0);
                };}
            };}
            return results;
        }

        public static getExtendedMultiplicationTensor(nSides: number): number[][][] {
            const nDiags: number = PolygonField.diagonalCount(nSides);
            const tensor: number[][][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return 0; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([nDiags, nDiags, nDiags]);
            for(let i: number = 0; i < nDiags; i++) {{
                for(let j: number = 0; j < nDiags; j++) {{
                    for(let k: number = 0; k < nDiags; k++) {{
                        tensor[i][j][k] = 0;
                    };}
                };}
            };}
            for(let layer: number = 0; layer < nDiags; layer++) {{
                const midWay: number = (layer / 2|0);
                for(let bx: number = layer, by: number = 0; bx > midWay || bx === by; bx--, by++) {{
                    for(let x: number = bx, y: number = by; x < nDiags && y < nDiags; x++, y++) {{
                        tensor[layer][y][x] += 1;
                        if (x !== y){
                            tensor[layer][x][y] += 1;
                        }
                    };}
                };}
            };}
            const box: number = nSides - 2;
            const parity: number = (nSides + 1) % 2;
            for(let layer: number = 0; layer < nDiags - parity; layer++) {{
                const base: number = box - layer;
                for(let xb: number = base, yb: number = 0; xb >= 0; xb--, yb++) {{
                    let x: number = xb;
                    let y: number = yb;
                    while((x < nDiags && y < nDiags)) {{
                        tensor[layer][y][x] += 1;
                        x++;
                        y++;
                    }};
                };}
            };}
            return tensor;
        }

        public static subscriptString(i: number): string {
            return /* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* replace *//* toString */(''+(i)).split("0").join("\u2080").split("1").join("\u2081").split("2").join("\u2082").split("3").join("\u2083").split("4").join("\u2084").split("5").join("\u2085").split("6").join("\u2086").split("7").join("\u2087").split("8").join("\u2088").split("9").join("\u2089").split("+").join("\u208a").split("-").join("\u208b");
        }

        public static MIN_SIDES: number = 4;

        /*private*/ __isEven: boolean;

        /*private*/ goldenRatio: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ __polygonSides: number;

        public constructor(name?: any, polygonSides?: any, factory?: any) {
            if (((typeof name === 'string') || name === null) && ((typeof polygonSides === 'number') || polygonSides === null) && ((factory != null && (factory.constructor != null && factory.constructor["__interfaces"] != null && factory.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumberFactory") >= 0)) || factory === null)) {
                let __args = arguments;
                super(name, PolygonField.getOrder(polygonSides), factory);
                if (this.__isEven === undefined) { this.__isEven = false; } 
                if (this.goldenRatio === undefined) { this.goldenRatio = null; } 
                if (this.__polygonSides === undefined) { this.__polygonSides = 0; } 
                if (this.normalizerMatrix === undefined) { this.normalizerMatrix = null; } 
                this.__polygonSides = polygonSides;
                this.validate();
                this.initialize();
                this.__isEven = polygonSides % 2 === 0;
                this.goldenRatio = this.getDiagonalRatio$int(5);
            } else if (((typeof name === 'number') || name === null) && ((polygonSides != null && (polygonSides.constructor != null && polygonSides.constructor["__interfaces"] != null && polygonSides.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumberFactory") >= 0)) || polygonSides === null) && factory === undefined) {
                let __args = arguments;
                let polygonSides: any = __args[0];
                let factory: any = __args[1];
                {
                    let __args = arguments;
                    let name: any = PolygonField.FIELD_PREFIX + __args[1];
                    super(name, PolygonField.getOrder(polygonSides), factory);
                    if (this.__isEven === undefined) { this.__isEven = false; } 
                    if (this.goldenRatio === undefined) { this.goldenRatio = null; } 
                    if (this.__polygonSides === undefined) { this.__polygonSides = 0; } 
                    if (this.normalizerMatrix === undefined) { this.normalizerMatrix = null; } 
                    this.__polygonSides = polygonSides;
                    this.validate();
                    this.initialize();
                    this.__isEven = polygonSides % 2 === 0;
                    this.goldenRatio = this.getDiagonalRatio$int(5);
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * u = units numerator
         * U = units denominator
         * p = phis numerator
         * P = phis denominator
         * ____ = 0,1
         * COMBO ... see comments inline below
         * Remapping the 4 element pairs array [u,U, p,P]
         * looks like this based on polygonSides:
         * 5  [  u, U,   p, P] // unchanged
         * 10  [ COMBO,  ____,   p, P    ... // the two units elements combine all of the input pairs
         * 15  [  u, U,  -p,-P,   ____,   p, P    ...
         * 20  [  u, U,   ____,  -p,-P,   ____,   p, P    ...
         * 25  [  u, U,   ____,   ____,  -p,-P,   ____,   p, P    ...
         * 30  [  u, U,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
         * 35  [  u, U,   ____,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
         * 40  [  u, U,   ____,   ____,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
         * 45  [  u, U,   ____,   ____,   ____,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
         * index   0  1    2  3    4  5    6  7    8  9   10 11   12 13   14 15   16 17   18 19
         * @param {long[]} pairs
         * @return {long[]}
         */
        convertGoldenNumberPairs(pairs: number[]): number[] {
            if (this.__polygonSides % 5 === 0 && pairs.length === 4 && this.getOrder() > 2){
                const u: number = pairs[0];
                const U: number = pairs[1];
                const p: number = pairs[2];
                const P: number = pairs[3];
                const remapped: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(2 * this.getOrder());
                for(let den: number = 1; den < remapped.length; den += 2) {{
                    remapped[den] = 1;
                };}
                const i: number = ((this.__polygonSides / 5|0)) * 2;
                remapped[i - 4] = -p;
                remapped[i - 3] = P;
                remapped[i + 0] = p;
                remapped[i + 1] = P;
                if (this.__polygonSides === 10){
                    remapped[0] = PolygonField.safeSubtract((u * P), (U * p));
                    remapped[1] = U * P;
                } else {
                    remapped[0] = u;
                    remapped[1] = U;
                }
                return remapped;
            }
            return pairs;
        }

        /**
         * @param {number} j
         * @param {number} k
         * @return {number} a long that equals j - k
         * @throws ArithmeticException if the subtraction causes an integer overflow
         */
        static safeSubtract(j: number, k: number): number {
            const result: number = j - k;
            if ((k > 0 && result >= j) || (k < 0 && result <= j)){
                throw new java.lang.ArithmeticException("Arithmetic Overflow: " + j + " - " + k + " = " + result + ". Result exceeds the size of a long.");
            }
            return result;
        }

        public diagonalCount(): number {
            return PolygonField.diagonalCount(this.polygonSides());
        }

        /**
         * 
         * @return {double[]}
         */
        public getCoefficients(): number[] {
            return PolygonField.getFieldCoefficients(this.polygonSides());
        }

        validate() {
            if (this.polygonSides() < PolygonField.MIN_SIDES){
                const msg: string = "polygon sides = " + this.polygonSides() + ". It must be at least " + PolygonField.MIN_SIDES + ".";
                throw new java.lang.IllegalArgumentException(msg);
            }
        }

        /**
         * 
         */
        initializeLabels() {
            const nSides: number = this.polygonSides();
            if (this.irrationalLabels.length !== PolygonField.diagonalCount(nSides)){
                const unitLabels: string[] = this.irrationalLabels[0];
                this.irrationalLabels = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([PolygonField.diagonalCount(nSides), unitLabels.length]);
                this.irrationalLabels[0] = unitLabels;
            }
            switch((this.polygonSides())) {
            case 4:
                this.irrationalLabels[1] = ["\u221a2", "sqrtTwo"];
                break;
            case 5:
                this.irrationalLabels[1] = ["\u03c6", "phi"];
                break;
            case 6:
                this.irrationalLabels[1] = ["\u221a3", "sqrtThree"];
                this.irrationalLabels[2] = ["\u03b2", "beta"];
                break;
            case 7:
                this.irrationalLabels[1] = ["\u03c1", "rho"];
                this.irrationalLabels[2] = ["\u03c3", "sigma"];
                break;
            case 9:
                this.irrationalLabels[1] = ["\u03b1", "alpha"];
                this.irrationalLabels[2] = ["\u03b2", "beta"];
                this.irrationalLabels[3] = ["\u03b3", "gamma"];
                break;
            case 11:
                this.irrationalLabels[1] = ["\u03b8", "theta"];
                this.irrationalLabels[2] = ["\u03ba", "kappa"];
                this.irrationalLabels[3] = ["\u03bb", "lambda"];
                this.irrationalLabels[4] = ["\u03bc", "mu"];
                break;
            case 13:
                this.irrationalLabels[1] = ["\u03b1", "alpha"];
                this.irrationalLabels[2] = ["\u03b2", "beta"];
                this.irrationalLabels[3] = ["\u03b3", "gamma"];
                this.irrationalLabels[4] = ["\u03b4", "delta"];
                this.irrationalLabels[5] = ["\u03b5", "epsilon"];
                break;
            default:
                const alphabet: string = "abcdefghijklmnopqrstuvwxyz";
                const length: number = this.irrationalLabels.length;
                if (length - 1 <= alphabet.length){
                    for(let i: number = 1; i < length; i++) {{
                        const name: string = alphabet.substring(i - 1, i);
                        this.irrationalLabels[i] = [name, "d[" + i + "]"];
                    };}
                } else {
                    for(let i: number = 1; i < this.irrationalLabels.length; i++) {{
                        this.irrationalLabels[i] = ["d" + PolygonField.subscriptString(i), "d[" + i + "]"];
                    };}
                }
                break;
            }
        }

        /**
         * getUnitTerm(n) expects n < getOrder().
         * This method handles normalized diagonal lengths
         * where getOrder() <= n < diagonalCount()
         * In these cases, the resulting AlgebraicNumber will not have just the nth term set to 1,
         * but rather, will have the normalized equivalent.
         * For example, since a normalized PolygonField(6) is of order 2, but diagonalCount() == 3,
         * PolygonField(6).getUnitTerm(2) would return an AlgebraicNumber with terms of {2,0} rather than {0,0,1}.
         * @param {number} n
         * @return {*}
         */
        public getUnitDiagonal(n: number): com.vzome.core.algebra.AlgebraicNumber {
            if (n >= this.getOrder() && n < this.diagonalCount()){
                const numerators: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(this.getOrder());
                const row: number = n - this.getOrder();
                for(let i: number = 0; i < numerators.length; i++) {{
                    numerators[i] = this.normalizerMatrix[row][i];
                };}
                return this.numberFactory.createAlgebraicNumber(this, numerators, 1);
            }
            return super.getUnitTerm(n);
        }

        /**
         * 
         * @return {*}
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            return this.goldenRatio;
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getNumberByName(name: string): com.vzome.core.algebra.AlgebraicNumber {
            switch((name)) {
            case "\u221a2":
            case "root2":
            case "sqrt2":
                return this.getRoot2();
            case "\u221a3":
            case "root3":
            case "sqrt3":
                return this.getRoot3();
            case "\u221a5":
            case "root5":
            case "sqrt5":
                return super.getNumberByName("root5");
            case "\u221a6":
            case "root6":
            case "sqrt6":
                return this.getRoot6();
            case "\u221a7":
            case "root7":
            case "sqrt7":
                return this.getRoot7();
            case "\u221a8":
            case "root8":
            case "sqrt8":
                return super.getNumberByName("root8");
            case "\u221a10":
            case "root10":
            case "sqrt10":
                return this.getRoot10();
            case "rho":
                return this.getDiagonalRatio$int(7);
            case "sigma":
                return this.getDiagonalRatio$int$int(7, 3);
            case "alpha":
                return this.getDiagonalRatio$int$int((this.__polygonSides % 9 === 0 ? 9 : 13), 2);
            case "beta":
                return this.getDiagonalRatio$int$int((this.__polygonSides % 9 === 0 ? 9 : 13), 3);
            case "gamma":
                return this.getDiagonalRatio$int$int((this.__polygonSides % 9 === 0 ? 9 : 13), 4);
            case "delta":
                return this.getDiagonalRatio$int$int(13, 5);
            case "epsilon":
                return this.getDiagonalRatio$int$int(13, 6);
            case "theta":
                return this.getDiagonalRatio$int$int(11, 2);
            case "kappa":
                return this.getDiagonalRatio$int$int(11, 3);
            case "lambda":
                return this.getDiagonalRatio$int$int(11, 4);
            case "mu":
                return this.getDiagonalRatio$int$int(11, 5);
            }
            return super.getNumberByName(name);
        }

        /*private*/ getRoot2(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getDiagonalRatio$int(4);
        }

        /*private*/ getRoot3(): com.vzome.core.algebra.AlgebraicNumber {
            return this.getDiagonalRatio$int(6);
        }

        /*private*/ getRoot6(): com.vzome.core.algebra.AlgebraicNumber {
            const r3: com.vzome.core.algebra.AlgebraicNumber = this.getNumberByName("root3");
            if (r3 != null){
                const r2: com.vzome.core.algebra.AlgebraicNumber = this.getNumberByName("root2");
                return r2 == null ? null : r2['times$com_vzome_core_algebra_AlgebraicNumber'](r3);
            }
            return null;
        }

        /*private*/ getRoot7(): com.vzome.core.algebra.AlgebraicNumber {
            if (this.__polygonSides % 14 === 0){
                const n: number = (this.__polygonSides / 14|0);
                const d0: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((1 * n) - 1).negate();
                const d1: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((2 * n) - 1);
                const d2: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((3 * n) - 1);
                const d3: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((4 * n) - 1);
                const d4: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((5 * n) - 1);
                const d5: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((6 * n) - 1);
                const cotA: com.vzome.core.algebra.AlgebraicNumber = d4.dividedBy(d1);
                const cotB: com.vzome.core.algebra.AlgebraicNumber = d2.dividedBy(d3);
                const cotC: com.vzome.core.algebra.AlgebraicNumber = d0.dividedBy(d5);
                return cotA['plus$com_vzome_core_algebra_AlgebraicNumber'](cotB)['plus$com_vzome_core_algebra_AlgebraicNumber'](cotC);
            }
            return null;
        }

        /*private*/ getRoot10(): com.vzome.core.algebra.AlgebraicNumber {
            const r5: com.vzome.core.algebra.AlgebraicNumber = this.getNumberByName("root5");
            if (r5 != null){
                const r2: com.vzome.core.algebra.AlgebraicNumber = this.getNumberByName("root2");
                return r2 == null ? null : r2['times$com_vzome_core_algebra_AlgebraicNumber'](r5);
            }
            return null;
        }

        /*private*/ getDiagonalRatio$int(divisor: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.getDiagonalRatio$int$int(divisor, 2);
        }

        public getDiagonalRatio$int$int(divisor: number, step: number): com.vzome.core.algebra.AlgebraicNumber {
            if (this.__polygonSides % divisor === 0 && step > 1 && step * 2 <= this.__polygonSides){
                const n: number = (this.__polygonSides / divisor|0);
                const denominator: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal(n - 1);
                const numerator: com.vzome.core.algebra.AlgebraicNumber = this.getUnitDiagonal((step * n) - 1);
                return numerator.dividedBy(denominator);
            }
            return null;
        }

        public getDiagonalRatio(divisor?: any, step?: any): com.vzome.core.algebra.AlgebraicNumber {
            if (((typeof divisor === 'number') || divisor === null) && ((typeof step === 'number') || step === null)) {
                return <any>this.getDiagonalRatio$int$int(divisor, step);
            } else if (((typeof divisor === 'number') || divisor === null) && step === undefined) {
                return <any>this.getDiagonalRatio$int(divisor);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         */
        initializeCoefficients() {
            const temp: number[] = this.getCoefficients();
            for(let i: number = 0; i < this.coefficients.length; i++) {{
                this.coefficients[i] = temp[i];
            };}
        }

        /**
         * 
         */
        initializeMultiplicationTensor() {
            this.multiplicationTensor = this.getNormalizedMultiplicationTensor(this.polygonSides());
        }

        normalizerMatrix: number[][];

        /**
         * 
         */
        initializeNormalizer() {
            this.normalizerMatrix = this.getNormalizerMatrix(this.polygonSides());
        }

        public polygonSides(): number {
            return this.__polygonSides;
        }

        public isEven(): boolean {
            return this.__isEven;
        }

        public isOdd(): boolean {
            return !this.__isEven;
        }
    }
    PolygonField["__class"] = "com.vzome.core.algebra.PolygonField";
    PolygonField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

