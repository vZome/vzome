/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author vorth
     * @param {*} n1
     * @param {*} n2
     * @param {*} n3
     * @param {*} n4
     * @param {*} n5
     * @class
     */
    export class AlgebraicVector implements java.lang.Comparable<AlgebraicVector> {
        public static X: number = 0;

        public static Y: number = 1;

        public static Z: number = 2;

        public static W4: number = 0;

        public static X4: number = 1;

        public static Y4: number = 2;

        public static Z4: number = 3;

        /*private*/ coordinates: com.vzome.core.algebra.AlgebraicNumber[];

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(n1?: any, n2?: any, n3?: any, n4?: any, n5?: any) {
            if (((n1 != null && (n1.constructor != null && n1.constructor["__interfaces"] != null && n1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n1 === null) && ((n2 != null && (n2.constructor != null && n2.constructor["__interfaces"] != null && n2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n2 === null) && ((n3 != null && (n3.constructor != null && n3.constructor["__interfaces"] != null && n3.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n3 === null) && ((n4 != null && (n4.constructor != null && n4.constructor["__interfaces"] != null && n4.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n4 === null) && ((n5 != null && (n5.constructor != null && n5.constructor["__interfaces"] != null && n5.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n5 === null)) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let field: any = n1.getField();
                    let dims: any = 5;
                    if (this.coordinates === undefined) { this.coordinates = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
                    for(let i: number = 0; i < dims; i++) {{
                        this.coordinates[i] = field.zero();
                    };}
                    this.field = field;
                }
                (() => {
                    this.coordinates[0] = n1;
                    this.coordinates[1] = n2;
                    this.coordinates[2] = n3;
                    this.coordinates[3] = n4;
                    this.coordinates[4] = n5;
                })();
            } else if (((n1 != null && (n1.constructor != null && n1.constructor["__interfaces"] != null && n1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n1 === null) && ((n2 != null && (n2.constructor != null && n2.constructor["__interfaces"] != null && n2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n2 === null) && ((n3 != null && (n3.constructor != null && n3.constructor["__interfaces"] != null && n3.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n3 === null) && ((n4 != null && (n4.constructor != null && n4.constructor["__interfaces"] != null && n4.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n4 === null) && n5 === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let field: any = n1.getField();
                    let dims: any = 4;
                    if (this.coordinates === undefined) { this.coordinates = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
                    for(let i: number = 0; i < dims; i++) {{
                        this.coordinates[i] = field.zero();
                    };}
                    this.field = field;
                }
                (() => {
                    this.coordinates[0] = n1;
                    this.coordinates[1] = n2;
                    this.coordinates[2] = n3;
                    this.coordinates[3] = n4;
                })();
            } else if (((n1 != null && (n1.constructor != null && n1.constructor["__interfaces"] != null && n1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n1 === null) && ((n2 != null && (n2.constructor != null && n2.constructor["__interfaces"] != null && n2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n2 === null) && ((n3 != null && (n3.constructor != null && n3.constructor["__interfaces"] != null && n3.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n3 === null) && n4 === undefined && n5 === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let field: any = n1.getField();
                    let dims: any = 3;
                    if (this.coordinates === undefined) { this.coordinates = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
                    for(let i: number = 0; i < dims; i++) {{
                        this.coordinates[i] = field.zero();
                    };}
                    this.field = field;
                }
                (() => {
                    this.coordinates[0] = n1;
                    this.coordinates[1] = n2;
                    this.coordinates[2] = n3;
                })();
            } else if (((n1 != null && (n1.constructor != null && n1.constructor["__interfaces"] != null && n1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n1 === null) && ((n2 != null && (n2.constructor != null && n2.constructor["__interfaces"] != null && n2.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n2 === null) && n3 === undefined && n4 === undefined && n5 === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let field: any = n1.getField();
                    let dims: any = 2;
                    if (this.coordinates === undefined) { this.coordinates = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
                    for(let i: number = 0; i < dims; i++) {{
                        this.coordinates[i] = field.zero();
                    };}
                    this.field = field;
                }
                (() => {
                    this.coordinates[0] = n1;
                    this.coordinates[1] = n2;
                })();
            } else if (((n1 != null && (n1.constructor != null && n1.constructor["__interfaces"] != null && n1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || n1 === null) && ((typeof n2 === 'number') || n2 === null) && n3 === undefined && n4 === undefined && n5 === undefined) {
                let __args = arguments;
                let field: any = __args[0];
                let dims: any = __args[1];
                if (this.coordinates === undefined) { this.coordinates = null; } 
                if (this.field === undefined) { this.field = null; } 
                this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
                for(let i: number = 0; i < dims; i++) {{
                    this.coordinates[i] = field.zero();
                };}
                this.field = field;
            } else if (((n1 != null && n1 instanceof <any>Array && (n1.length == 0 || n1[0] == null ||(n1[0] != null && (n1[0].constructor != null && n1[0].constructor["__interfaces"] != null && n1[0].constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)))) || n1 === null) && n2 === undefined && n3 === undefined && n4 === undefined && n5 === undefined) {
                let __args = arguments;
                let n: any = __args[0];
                if (this.coordinates === undefined) { this.coordinates = null; } 
                if (this.field === undefined) { this.field = null; } 
                this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(n.length);
                java.lang.System.arraycopy(n, 0, this.coordinates, 0, n.length);
                this.field = n[0].getField();
            } else if (((n1 != null && (n1.constructor != null && n1.constructor["__interfaces"] != null && n1.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || n1 === null) && n2 === undefined && n3 === undefined && n4 === undefined && n5 === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let field: any = n1.getField();
                    let dims: any = 1;
                    if (this.coordinates === undefined) { this.coordinates = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dims);
                    for(let i: number = 0; i < dims; i++) {{
                        this.coordinates[i] = field.zero();
                    };}
                    this.field = field;
                }
                (() => {
                    this.coordinates[0] = n1;
                })();
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            result = prime * result + java.util.Arrays.hashCode(this.coordinates);
            return result;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj)return true;
            if (obj == null)return false;
            if ((<any>this.constructor) !== (<any>obj.constructor))return false;
            const other: AlgebraicVector = <AlgebraicVector>obj;
            if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.field,other.field))){
                const reason: string = "Invalid comparison of " + /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>this.constructor)) + "swith different fields: " + this.field.getName() + " and " + other.field.getName();
                throw new java.lang.IllegalStateException(reason);
            }
            return java.util.Arrays.equals(this.coordinates, other.coordinates);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} other
         * @return {number}
         */
        public compareTo(other: AlgebraicVector): number {
            if (this === other){
                return 0;
            }
            if (other.equals(this)){
                return 0;
            }
            let comparison: number = /* compare */(this.coordinates.length - other.coordinates.length);
            if (comparison !== 0){
                return comparison;
            }
            for(let i: number = 0; i < this.coordinates.length; i++) {{
                const n1: com.vzome.core.algebra.AlgebraicNumber = this.coordinates[i];
                const n2: com.vzome.core.algebra.AlgebraicNumber = other.coordinates[i];
                comparison = n1.compareTo(n2);
                if (comparison !== 0){
                    return comparison;
                }
            };}
            return comparison;
        }

        public toRealVector(): com.vzome.core.math.RealVector {
            return new com.vzome.core.math.RealVector(this.coordinates[0].evaluate(), this.coordinates[1].evaluate(), this.coordinates[2].evaluate());
        }

        public to3dDoubleVector(): number[] {
            return [this.coordinates[0].evaluate(), this.coordinates[1].evaluate(), this.coordinates[2].evaluate()];
        }

        /**
         * @return {string} A String with no extended characters so it's suitable for writing
         * to an 8 bit stream such as System.out or an ASCII text log file in Windows.
         * Contrast this with {@link toString()} which contains extended characters (e.g. φ (phi))
         */
        public toASCIIString(): string {
            return this.getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT);
        }

        /**
         * @return {string} A String representation that can be persisted to XML and parsed by XmlSaveFormat.parseRationalVector().
         */
        public toParsableString(): string {
            return this.getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT);
        }

        public toString(format: number = com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT): string {
            return this.getVectorExpression$int(format);
        }

        public getComponent(i: number): com.vzome.core.algebra.AlgebraicNumber {
            return this.coordinates[i];
        }

        public getComponents(): com.vzome.core.algebra.AlgebraicNumber[] {
            return this.coordinates;
        }

        public setComponent(component: number, coord: com.vzome.core.algebra.AlgebraicNumber): AlgebraicVector {
            this.coordinates[component] = coord;
            return this;
        }

        public negate(): AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.coordinates.length);
            for(let i: number = 0; i < result.length; i++) {{
                result[i] = this.coordinates[i].negate();
            };}
            return new AlgebraicVector(result);
        }

        public scale(scale: com.vzome.core.algebra.AlgebraicNumber): AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.coordinates.length);
            for(let i: number = 0; i < result.length; i++) {{
                result[i] = this.coordinates[i]['times$com_vzome_core_algebra_AlgebraicNumber'](scale);
            };}
            return new AlgebraicVector(result);
        }

        public isOrigin(): boolean {
            for(let index = 0; index < this.coordinates.length; index++) {
                let coordinate = this.coordinates[index];
                {
                    if (!coordinate.isZero()){
                        return false;
                    }
                }
            }
            return true;
        }

        public plus(that: AlgebraicVector): AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.coordinates.length);
            for(let i: number = 0; i < result.length; i++) {{
                result[i] = this.coordinates[i]['plus$com_vzome_core_algebra_AlgebraicNumber'](that.coordinates[i]);
            };}
            return new AlgebraicVector(result);
        }

        public minus(that: AlgebraicVector): AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.coordinates.length);
            for(let i: number = 0; i < result.length; i++) {{
                result[i] = this.coordinates[i]['minus$com_vzome_core_algebra_AlgebraicNumber'](that.coordinates[i]);
            };}
            return new AlgebraicVector(result);
        }

        public dimension(): number {
            return this.coordinates.length;
        }

        public cross(that: AlgebraicVector): AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicNumber[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.coordinates.length);
            for(let i: number = 0; i < result.length; i++) {{
                const j: number = (i + 1) % 3;
                const k: number = (i + 2) % 3;
                result[i] = this.coordinates[j]['times$com_vzome_core_algebra_AlgebraicNumber'](that.coordinates[k])['minus$com_vzome_core_algebra_AlgebraicNumber'](this.coordinates[k]['times$com_vzome_core_algebra_AlgebraicNumber'](that.coordinates[j]));
            };}
            return new AlgebraicVector(result);
        }

        public inflateTo4d$(): AlgebraicVector {
            return this.inflateTo4d$boolean(true);
        }

        public inflateTo4d$boolean(wFirst: boolean): AlgebraicVector {
            if (this.coordinates.length === 4){
                if (wFirst)return this; else return new AlgebraicVector([this.coordinates[1], this.coordinates[2], this.coordinates[3], this.coordinates[0]]);
            }
            if (wFirst)return new AlgebraicVector([this.field.zero(), this.coordinates[0], this.coordinates[1], this.coordinates[2]]); else return new AlgebraicVector([this.coordinates[0], this.coordinates[1], this.coordinates[2], this.field.zero()]);
        }

        public inflateTo4d(wFirst?: any): AlgebraicVector {
            if (((typeof wFirst === 'boolean') || wFirst === null)) {
                return <any>this.inflateTo4d$boolean(wFirst);
            } else if (wFirst === undefined) {
                return <any>this.inflateTo4d$();
            } else throw new Error('invalid overload');
        }

        public projectTo3d(wFirst: boolean): AlgebraicVector {
            if (this.dimension() === 3)return this;
            if (wFirst)return new AlgebraicVector([this.coordinates[1], this.coordinates[2], this.coordinates[3]]); else return new AlgebraicVector([this.coordinates[0], this.coordinates[1], this.coordinates[2]]);
        }

        public getVectorExpression$java_lang_StringBuffer$int(buf: java.lang.StringBuffer, format: number) {
            if (format === com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT)buf.append("(");
            for(let i: number = 0; i < this.coordinates.length; i++) {{
                if (i > 0)if (format === com.vzome.core.algebra.AlgebraicField.VEF_FORMAT || format === com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT)buf.append(" "); else buf.append(", ");
                this.coordinates[i].getNumberExpression(buf, format);
            };}
            if (format === com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT)buf.append(")");
        }

        /**
         * 
         * @param {java.lang.StringBuffer} buf a StringBuffer to which the formatted vector will be appended.
         * @param {number} format may be any of the following:
         * {@code AlgebraicField.DEFAULT_FORMAT = 0; // 4 + 3φ}
         * {@code AlgebraicField.EXPRESSION_FORMAT = 1; // 4 +3*phi}
         * {@code AlgebraicField.ZOMIC_FORMAT = 2; // 4 3}
         * {@code AlgebraicField.VEF_FORMAT = 3; // (3,4)}
         */
        public getVectorExpression(buf?: any, format?: any) {
            if (((buf != null && buf instanceof <any>java.lang.StringBuffer) || buf === null) && ((typeof format === 'number') || format === null)) {
                return <any>this.getVectorExpression$java_lang_StringBuffer$int(buf, format);
            } else if (((typeof buf === 'number') || buf === null) && format === undefined) {
                return <any>this.getVectorExpression$int(buf);
            } else throw new Error('invalid overload');
        }

        public getVectorExpression$int(format: number): string {
            const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
            this.getVectorExpression$java_lang_StringBuffer$int(buf, format);
            return buf.toString();
        }

        public dot(that: AlgebraicVector): com.vzome.core.algebra.AlgebraicNumber {
            let result: com.vzome.core.algebra.AlgebraicNumber = this.field.zero();
            for(let i: number = 0; i < that.dimension(); i++) {{
                result = result['plus$com_vzome_core_algebra_AlgebraicNumber'](this.coordinates[i]['times$com_vzome_core_algebra_AlgebraicNumber'](that.coordinates[i]));
            };}
            return result;
        }

        public getLength(unit: AlgebraicVector): com.vzome.core.algebra.AlgebraicNumber {
            for(let i: number = 0; i < this.coordinates.length; i++) {{
                if (this.coordinates[i].isZero())continue;
                return this.coordinates[i].dividedBy(unit.coordinates[i]);
            };}
            throw new java.lang.IllegalStateException("vector is the origin!");
        }

        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }
    }
    AlgebraicVector["__class"] = "com.vzome.core.algebra.AlgebraicVector";
    AlgebraicVector["__interfaces"] = ["java.lang.Comparable"];


}

