/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsAlgebraicField implements com.vzome.core.algebra.AlgebraicField {
        /* Default method injected from com.vzome.core.algebra.AlgebraicField */
        supportsSubfield(fieldName: string): boolean {
            if (fieldName === this.getName())return true;
            return (fieldName === ("golden")) && this.getGoldenRatio() != null;
        }
        /*private*/ delegate: Object;

        /**
         * Positive powers of the irrationals.
         */
        /*private*/ positivePowers: java.util.ArrayList<com.vzome.core.algebra.AlgebraicNumber>[];

        /**
         * Negative powers of the irrationals.
         */
        /*private*/ negativePowers: java.util.ArrayList<com.vzome.core.algebra.AlgebraicNumber>[];

        public constructor(delegate: Object) {
            if (this.delegate === undefined) { this.delegate = null; }
            if (this.positivePowers === undefined) { this.positivePowers = null; }
            if (this.negativePowers === undefined) { this.negativePowers = null; }
            if (this.zomicModule === undefined) { this.zomicModule = null; }
            if (this.vzomePkg === undefined) { this.vzomePkg = null; }
            this.delegate = delegate;
            const order: number = <any>(delegate["order"]);
            this.positivePowers = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
            this.negativePowers = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return <any>(this.delegate["name"]);
        }

        /**
         * 
         * @return {number}
         */
        public getOrder(): number {
            return <any>(this.delegate["order"]);
        }

        /**
         * 
         * @return {number}
         */
        public getNumIrrationals(): number {
            return this.getOrder() - 1;
        }

        /**
         * 
         * @return {number}
         */
        public getNumMultipliers(): number {
            return this.getNumIrrationals();
        }

        parseInt(s: string): number {
            const f: Function = <any>(this.delegate["parseInt"]);
            return <any>(f(<any>((<any>(s)))));
        }

        add(v1: number[], v2: number[]): number[] {
            const f: Function = <any>(this.delegate["plus"]);
            return <any>(f(<any>((<any>(v1))), <any>((<any>(v2)))));
        }

        subtract(v1: number[], v2: number[]): number[] {
            const f: Function = <any>(this.delegate["minus"]);
            return <any>(f(<any>((<any>(v1))), <any>((<any>(v2)))));
        }

        multiply(v1: number[], v2: number[]): number[] {
            const f: Function = <any>(this.delegate["times"]);
            return <any>(f(<any>((<any>(v1))), <any>((<any>(v2)))));
        }

        evaluateNumber(factors: number[]): number {
            const f: Function = <any>(this.delegate["embed"]);
            return <any>(f(<any>((<any>(factors)))));
        }

        reciprocal(factors: number[]): number[] {
            const f: Function = <any>(this.delegate["reciprocal"]);
            return <any>(f(<any>((<any>(factors)))));
        }

        negate(factors: number[]): number[] {
            const f: Function = <any>(this.delegate["negate"]);
            return <any>(f(<any>((<any>(factors)))));
        }

        toString(factors: number[], format: number): string {
            const f: Function = <any>(this.delegate["toString"]);
            return <any>(f(<any>((<any>(factors))), <any>((<any>(format)))));
        }

        public getMathML(v1: number[]): string {
            const f: Function = <any>(this.delegate["toString"]);
            return <any>(f(<any>((<any>(v1))), <any>((<any>(4)))));
        }

        /**
         * 
         * @return {*}
         */
        public zero(): com.vzome.core.algebra.AlgebraicNumber {
            return new com.vzome.jsweet.JsAlgebraicNumber(this, <any>(this.delegate["zero"]));
        }

        /**
         * 
         * @return {*}
         */
        public one(): com.vzome.core.algebra.AlgebraicNumber {
            return new com.vzome.jsweet.JsAlgebraicNumber(this, <any>(this.delegate["one"]));
        }

        /**
         * 
         * @param {int[][]} nums
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public createVector(nums: number[][]): com.vzome.core.algebra.AlgebraicVector {
            const x: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromPairs(nums[0]);
            const y: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromPairs(nums[1]);
            const z: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromPairs(nums[2]);
            return new com.vzome.core.algebra.AlgebraicVector(x, y, z);
        }

        /**
         * 
         * @param {int[][]} nums
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public createVectorFromTDs(nums: number[][]): com.vzome.core.algebra.AlgebraicVector {
            const dims: number = nums.length;
            const coords: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
            for(let c: number = 0; c < coords.length; c++) {{
                coords[c] = this.createAlgebraicNumberFromTD(nums[c]);
            };}
            return new com.vzome.core.algebra.AlgebraicVector(coords);
        }

        /**
         * 
         * @param {number} dims
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public origin(dims: number): com.vzome.core.algebra.AlgebraicVector {
            const zero: com.vzome.core.algebra.AlgebraicNumber = this.zero();
            switch((dims)) {
            case 1:
                return new com.vzome.core.algebra.AlgebraicVector(zero);
            case 2:
                return new com.vzome.core.algebra.AlgebraicVector(zero, zero);
            case 3:
                return new com.vzome.core.algebra.AlgebraicVector(zero, zero, zero);
            case 4:
                return new com.vzome.core.algebra.AlgebraicVector(zero, zero, zero, zero);
            case 5:
                return new com.vzome.core.algebra.AlgebraicVector(zero, zero, zero, zero, zero);
            default:
                return null;
            }
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
         * @param {number} dims
         * @param {number} axis
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public basisVector(dims: number, axis: number): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.origin(dims);
            return result.setComponent(axis, this.one());
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
            return new com.vzome.jsweet.JsAlgebraicNumber(this, factors);
        }

        public createRational$long(wholeNumber: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.createRational$long$long(wholeNumber, 1);
        }

        /**
         * 
         * @param {int[]} trailingDivisorForm
         * @return {*}
         */
        public createAlgebraicNumberFromTD(trailingDivisorForm: number[]): com.vzome.core.algebra.AlgebraicNumber {
            const f: Function = <any>(this.delegate["createNumber"]);
            const simplified: number[] = <any>(f(<any>((<any>(trailingDivisorForm)))));
            return new com.vzome.jsweet.JsAlgebraicNumber(this, simplified);
        }

        public createAlgebraicNumberFromPairs(pairs: number[]): com.vzome.core.algebra.AlgebraicNumber {
            const f: Function = <any>(this.delegate["createNumberFromPairs"]);
            const simplified: number[] = <any>(f(<any>((<any>(pairs)))));
            return new com.vzome.jsweet.JsAlgebraicNumber(this, simplified);
        }

        public createRational$long$long(numerator: number, denominator: number): com.vzome.core.algebra.AlgebraicNumber {
            const f: Function = <any>(this.delegate["createNumberFromPairs"]);
            const simplified: number[] = <any>(f(<any>((<any>([numerator, denominator])))));
            return new com.vzome.jsweet.JsAlgebraicNumber(this, simplified);
        }

        /**
         * 
         * @param {number} numerator
         * @param {number} denominator
         * @return {*}
         */
        public createRational(numerator?: any, denominator?: any): com.vzome.core.algebra.AlgebraicNumber {
            if (((typeof numerator === 'number') || numerator === null) && ((typeof denominator === 'number') || denominator === null)) {
                return <any>this.createRational$long$long(numerator, denominator);
            } else if (((typeof numerator === 'number') || numerator === null) && denominator === undefined) {
                return <any>this.createRational$long(numerator);
            } else throw new Error('invalid overload');
        }

        public createAlgebraicNumber$int_A(terms: number[]): com.vzome.core.algebra.AlgebraicNumber {
            return this.createAlgebraicNumber$int_A$int(terms, 1);
        }

        public createAlgebraicNumber$int_A$int(numerators: number[], denominator: number): com.vzome.core.algebra.AlgebraicNumber {
            const factors: number[] = this.zero().toTrailingDivisor();
            java.lang.System.arraycopy(numerators, 0, factors, 0, numerators.length);
            factors[numerators.length] = denominator;
            return this.createAlgebraicNumberFromTD(factors);
        }

        /**
         * 
         * @return {*}
         */
        public getGoldenRatio(): com.vzome.core.algebra.AlgebraicNumber {
            const value: number[] = <any>(this.delegate["goldenRatio"]);
            if (value == null)return null;
            return new com.vzome.jsweet.JsAlgebraicNumber(this, value);
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
         * Modeled after AbstractAlgebraicField, with a switch from BigRationals to int[]s.
         * @param {string} string
         * @param {boolean} isRational
         * @return {*}
         */
        public parseVefNumber(string: string, isRational: boolean): com.vzome.core.algebra.AlgebraicNumber {
            const pairs: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(this.getOrder() + 1);
            if ((!isRational) && /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "(") && /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(string, ")")){
                const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(string.substring(1, string.length - 1), ",");
                const numStack: java.util.Stack<number> = <any>(new java.util.Stack<any>());
                const denomStack: java.util.Stack<number> = <any>(new java.util.Stack<any>());
                while((tokens.hasMoreTokens())) {{
                    if (numStack.size() >= this.getOrder()){
                        throw new java.lang.RuntimeException("VEF format error: \"" + string + "\" has too many factors for " + this.getName() + " field");
                    }
                    const parts: string[] = tokens.nextToken().split("/");
                    numStack.push(this.parseInt(parts[0]));
                    denomStack.push((parts.length > 1) ? this.parseInt(parts[1]) : 1);
                }};
                let i: number = 0;
                while((!numStack.empty())) {{
                    pairs[i++] = numStack.pop();
                    pairs[i++] = denomStack.pop();
                }};
            } else {
                const parts: string[] = string.split("/");
                pairs[0] = this.parseInt(parts[0]);
                pairs[1] = (parts.length > 1) ? this.parseInt(parts[1]) : 1;
            }
            return this.createAlgebraicNumberFromPairs(pairs);
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

        public createAlgebraicNumber$int$int$int$int(ones: number, irrat: number, denominator: number, scalePower: number): com.vzome.core.algebra.AlgebraicNumber {
            const result: com.vzome.core.algebra.AlgebraicNumber = this.createAlgebraicNumberFromTD([ones, irrat, denominator]);
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
         * 
         * @param {string} nums
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public parseVector(nums: string): com.vzome.core.algebra.AlgebraicVector {
            const noLF: string = /* replace */nums.split('\n').join(' ');
            const noCRLF: string = /* replace */noLF.split('\r').join(' ');
            const tokens: java.util.StringTokenizer = new java.util.StringTokenizer(noCRLF, " ");
            const numToks: number = tokens.countTokens();
            if (numToks % this.getOrder() !== 0)throw new java.lang.IllegalStateException("Field order (" + this.getOrder() + ") does not divide token count: " + numToks + ", for \'" + noCRLF + "\'");
            const dims: number = (numToks / this.getOrder()|0);
            const coords: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
            for(let i: number = 0; i < dims; i++) {{
                coords[i] = this.parseNumber$java_util_StringTokenizer(tokens);
            };}
            return new com.vzome.core.algebra.AlgebraicVector(coords);
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
                pairs[i * 2] = this.parseInt(parts[0]);
                if (parts.length > 1)pairs[i * 2 + 1] = this.parseInt(parts[1]); else pairs[i * 2 + 1] = 1;
            };}
            return this.createAlgebraicNumberFromPairs(pairs);
        }

        /**
         * 
         * @param {string} string
         * @return {*}
         */
        public parseLegacyNumber(string: string): com.vzome.core.algebra.AlgebraicNumber {
            let div: number = 1;
            if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "(")){
                const closeParen: number = string.indexOf(')');
                div = this.parseInt(string.substring(closeParen + 2));
                string = string.substring(1, closeParen);
            }
            let phis: number = 0;
            const phiIndex: number = string.indexOf("phi");
            if (phiIndex >= 0){
                const part: string = string.substring(0, phiIndex);
                if (part.length === 0)phis = 1; else if (part === ("-"))phis = -1; else phis = this.parseInt(part);
                string = string.substring(phiIndex + 3);
            }
            let ones: number;
            if (string.length === 0)ones = 0; else {
                if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(string, "+"))string = string.substring(1);
                ones = this.parseInt(string);
            }
            return this.createAlgebraicNumber$int$int$int$int(ones, phis, div, 0);
        }

        public getIrrational$int$int(i: number, format: number): string {
            const f: Function = <any>(this.delegate["getIrrational"]);
            return <any>(f(<any>((<any>(i)))));
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

        public getIrrational$int(which: number): string {
            return this.getIrrational$int$int(which, 0);
        }

        /*private*/ zomicModule: Object;

        /*private*/ vzomePkg: Object;

        public setInterpreterModule(module: Object, vzomePkg: Object) {
            this.zomicModule = module;
            this.vzomePkg = vzomePkg;
        }

        public interpretScript(script: string, language: string, offset: com.vzome.core.construction.Point, symmetry: com.vzome.core.math.symmetry.Symmetry, effects: com.vzome.core.construction.ConstructionChanges) {
            if (this.zomicModule == null)throw new Error("The Zomic module was not loaded.");
            const f: Function = <any>(this.zomicModule["interpretScript"]);
            f(<any>((<any>(script))), <any>((<any>(language))), <any>((<any>(offset))), <any>((<any>(symmetry))), <any>((<any>(effects))), <any>((<any>(this.vzomePkg))));
        }

        public getNumberByName(name: string): com.vzome.core.algebra.AlgebraicNumber {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicField.getNumberByName");
        }

        scaleBy(factors: number[], whichIrrational: number): number[] {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicField.scaleBy");
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} target
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public nearestAlgebraicVector(target: com.vzome.core.math.RealVector): com.vzome.core.algebra.AlgebraicVector {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicField.nearestAlgebraicVector");
        }

        /**
         * 
         * @param {int[][]} nums
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public createIntegerVector(nums: number[][]): com.vzome.core.algebra.AlgebraicVector {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicField.createIntegerVector");
        }

        /**
         * 
         * @param {int[][][]} data
         * @return {com.vzome.core.algebra.AlgebraicMatrix}
         */
        public createMatrix(data: number[][][]): com.vzome.core.algebra.AlgebraicMatrix {
            throw new java.lang.RuntimeException("unimplemented JsAlgebraicField.createMatrix");
        }
    }
    JsAlgebraicField["__class"] = "com.vzome.jsweet.JsAlgebraicField";
    JsAlgebraicField["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField"];


}

