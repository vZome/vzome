/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    /**
     * Construct a new vector from its coordinate values.
     * @param {number} x
     * @param {number} y
     * @param {number} z
     * @class
     */
    export class RealVector {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!RealVector.__static_initialized) { RealVector.__static_initialized = true; RealVector.__static_initializer_0(); } }

        public static ORIGIN: RealVector; public static ORIGIN_$LI$(): RealVector { RealVector.__static_initialize(); if (RealVector.ORIGIN == null) { RealVector.ORIGIN = new RealVector(0.0, 0.0, 0.0); }  return RealVector.ORIGIN; }

        static FORMAT: java.text.NumberFormat; public static FORMAT_$LI$(): java.text.NumberFormat { RealVector.__static_initialize(); if (RealVector.FORMAT == null) { RealVector.FORMAT = java.text.NumberFormat.getNumberInstance(java.util.Locale.US); }  return RealVector.FORMAT; }

        public static DIRECTION_0: RealVector; public static DIRECTION_0_$LI$(): RealVector { RealVector.__static_initialize(); if (RealVector.DIRECTION_0 == null) { RealVector.DIRECTION_0 = new RealVector(10.0, 0.1, -0.1); }  return RealVector.DIRECTION_0; }

        static  __static_initializer_0() {
            RealVector.FORMAT_$LI$().setMaximumFractionDigits(5);
            RealVector.FORMAT_$LI$().setMinimumFractionDigits(1);
        }

        public x: number;

        public y: number;

        public z: number;

        public constructor(x?: any, y?: any, z?: any) {
            if (((typeof x === 'number') || x === null) && ((typeof y === 'number') || y === null) && ((typeof z === 'number') || z === null)) {
                let __args = arguments;
                if (this.x === undefined) { this.x = 0; } 
                if (this.y === undefined) { this.y = 0; } 
                if (this.z === undefined) { this.z = 0; } 
                this.x = (<any>Math).fround(x);
                this.y = (<any>Math).fround(y);
                this.z = (<any>Math).fround(z);
            } else if (((x != null && x instanceof <any>com.vzome.core.math.RealVector) || x === null) && y === undefined && z === undefined) {
                let __args = arguments;
                let that: any = __args[0];
                {
                    let __args = arguments;
                    let x: any = that.x;
                    let y: any = that.y;
                    let z: any = that.z;
                    if (this.x === undefined) { this.x = 0; } 
                    if (this.y === undefined) { this.y = 0; } 
                    if (this.z === undefined) { this.z = 0; } 
                    this.x = (<any>Math).fround(x);
                    this.y = (<any>Math).fround(y);
                    this.z = (<any>Math).fround(z);
                }
            } else if (x === undefined && y === undefined && z === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let x: any = 0.0;
                    let y: any = 0.0;
                    let z: any = 0.0;
                    if (this.x === undefined) { this.x = 0; } 
                    if (this.y === undefined) { this.y = 0; } 
                    if (this.z === undefined) { this.z = 0; } 
                    this.x = (<any>Math).fround(x);
                    this.y = (<any>Math).fround(y);
                    this.z = (<any>Math).fround(z);
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {*}
         */
        public clone(): any {
            return new RealVector(this);
        }

        public isZero(): boolean {
            return this.x === 0 && this.y === 0 && this.z === 0;
        }

        /**
         * Return a string representing this vector as XML attribute values.
         * @return {string}
         */
        public toXmlAttributes(): string {
            return "x=\"" + RealVector.FORMAT_$LI$().format(this.x) + "\" y=\"" + RealVector.FORMAT_$LI$().format(this.y) + "\" z=\"" + RealVector.FORMAT_$LI$().format(this.z) + "\"";
        }

        public toString$java_text_NumberFormat(format: java.text.NumberFormat): string {
            return format.format(this.x) + "," + format.format(this.y) + "," + format.format(this.z);
        }

        /**
         * Return a string representing this vector in the form "x,y,z".
         * @param {java.text.NumberFormat} format
         * @return {string}
         */
        public toString(format?: any): string {
            if (((format != null && format instanceof <any>java.text.NumberFormat) || format === null)) {
                return <any>this.toString$java_text_NumberFormat(format);
            } else if (format === undefined) {
                return <any>this.toString$();
            } else throw new Error('invalid overload');
        }

        public toString$(): string {
            return RealVector.FORMAT_$LI$().format(this.x) + "," + RealVector.FORMAT_$LI$().format(this.y) + "," + RealVector.FORMAT_$LI$().format(this.z);
        }

        /**
         * Return a string representing this vector in the form "x y z".
         * @return {string}
         */
        public spacedString(): string {
            const result: string = RealVector.FORMAT_$LI$().format(this.x) + " " + RealVector.FORMAT_$LI$().format(this.y) + " " + RealVector.FORMAT_$LI$().format(this.z);
            return result;
        }

        /**
         * Return the sum of this vector plus the vector "other",
         * as a new Vector3D.
         * @param {com.vzome.core.math.RealVector} other
         * @return {com.vzome.core.math.RealVector}
         */
        public plus(other: RealVector): RealVector {
            return new RealVector((<any>Math).fround(this.x + other.x), (<any>Math).fround(this.y + other.y), (<any>Math).fround(this.z + other.z));
        }

        /**
         * Return the difference of this vector minus the vector "other",
         * as a new Vector3D.
         * @param {com.vzome.core.math.RealVector} other
         * @return {com.vzome.core.math.RealVector}
         */
        public minus(other: RealVector): RealVector {
            return new RealVector((<any>Math).fround(this.x - other.x), (<any>Math).fround(this.y - other.y), (<any>Math).fround(this.z - other.z));
        }

        public negate(): RealVector {
            return new RealVector(-this.x, -this.y, -this.z);
        }

        /**
         * Return a new vector equal to this vector scaled by the given factor.
         * @param {number} factor
         * @return {com.vzome.core.math.RealVector}
         */
        public scale(factor: number): RealVector {
            return new RealVector(this.x * factor, this.y * factor, this.z * factor);
        }

        /**
         * Return the scalar (dot) product with the other vector
         * @param {com.vzome.core.math.RealVector} other
         * @return {number}
         */
        public dot(other: RealVector): number {
            return (<any>Math).fround((<any>Math).fround((<any>Math).fround(this.x * other.x) + (<any>Math).fround(this.y * other.y)) + (<any>Math).fround(this.z * other.z));
        }

        public cross(that: RealVector): RealVector {
            return new RealVector((<any>Math).fround((<any>Math).fround(this.y * that.z) - (<any>Math).fround(this.z * that.y)), (<any>Math).fround((<any>Math).fround(this.z * that.x) - (<any>Math).fround(this.x * that.z)), (<any>Math).fround((<any>Math).fround(this.x * that.y) - (<any>Math).fround(this.y * that.x)));
        }

        /**
         * Return the length of this vector.
         * @return {number}
         */
        public length(): number {
            return Math.sqrt(this.dot(this));
        }

        public normalize(): RealVector {
            return this.scale(1.0 / this.length());
        }

        public write(buf: java.nio.FloatBuffer, offset: number) {
            buf.put(offset, this.x);
            buf.put(offset + 1, this.y);
            buf.put(offset + 2, this.z);
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public equals(other: any): boolean {
            if (other == null){
                return false;
            }
            if (other === this){
                return true;
            }
            if (!(other != null && other instanceof <any>com.vzome.core.math.RealVector))return false;
            const v: RealVector = <RealVector>other;
            return this.x === v.x && this.y === v.y && this.z === v.z;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            let hash: number = 3;
            hash = 41 * hash + (<number>(javaemul.internal.DoubleHelper.doubleToLongBits(this.x) ^ (javaemul.internal.DoubleHelper.doubleToLongBits(this.x) >>> 32))|0);
            hash = 41 * hash + (<number>(javaemul.internal.DoubleHelper.doubleToLongBits(this.y) ^ (javaemul.internal.DoubleHelper.doubleToLongBits(this.y) >>> 32))|0);
            hash = 41 * hash + (<number>(javaemul.internal.DoubleHelper.doubleToLongBits(this.z) ^ (javaemul.internal.DoubleHelper.doubleToLongBits(this.z) >>> 32))|0);
            return hash;
        }

        public addTo(addend: number[], sum: number[]) {
            sum[0] = ((<any>Math).fround(addend[0] + this.x));
            sum[1] = ((<any>Math).fround(addend[1] + this.y));
            sum[2] = ((<any>Math).fround(addend[2] + this.z));
        }

        public toArray(output: number[]) {
            output[0] = this.x;
            output[1] = this.y;
            output[2] = this.z;
        }
    }
    RealVector["__class"] = "com.vzome.core.math.RealVector";

}


com.vzome.core.math.RealVector.__static_initialize();
