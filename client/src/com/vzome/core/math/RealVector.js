import { NumberFormat } from '../../../../java/text/NumberFormat';
/**
 * Construct a new vector from its coordinate values.
 * @param {number} x
 * @param {number} y
 * @param {number} z
 * @class
 */

const javaemul = {} // ADDED MANUALLY, for now

export class RealVector {
    constructor(x, y, z) {
        if (((typeof x === 'number') || x === null) && ((typeof y === 'number') || y === null) && ((typeof z === 'number') || z === null)) {
            let __args = arguments;
            if (this.x === undefined)
                this.x = 0;
            if (this.y === undefined)
                this.y = 0;
            if (this.z === undefined)
                this.z = 0;
            if (this.x === undefined)
                this.x = 0;
            if (this.y === undefined)
                this.y = 0;
            if (this.z === undefined)
                this.z = 0;
            (() => {
                this.x = Math.fround(x);
                this.y = Math.fround(y);
                this.z = Math.fround(z);
            })();
        }
        else if (x === undefined && y === undefined && z === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let x = 0.0;
                let y = 0.0;
                let z = 0.0;
                if (this.x === undefined)
                    this.x = 0;
                if (this.y === undefined)
                    this.y = 0;
                if (this.z === undefined)
                    this.z = 0;
                if (this.x === undefined)
                    this.x = 0;
                if (this.y === undefined)
                    this.y = 0;
                if (this.z === undefined)
                    this.z = 0;
                (() => {
                    this.x = Math.fround(x);
                    this.y = Math.fround(y);
                    this.z = Math.fround(z);
                })();
            }
        }
        else
            throw new Error('invalid overload');
    }
    static __static_initialize() { if (!RealVector.__static_initialized) {
        RealVector.__static_initialized = true;
        RealVector.__static_initializer_0();
    } }
    static ORIGIN_$LI$() { RealVector.__static_initialize(); if (RealVector.ORIGIN == null)
        RealVector.ORIGIN = new RealVector(0.0, 0.0, 0.0); return RealVector.ORIGIN; }
    ;
    static FORMAT_$LI$() { RealVector.__static_initialize(); if (RealVector.FORMAT == null)
        RealVector.FORMAT = NumberFormat.getNumberInstance(null); return RealVector.FORMAT; }
    ;
    static DIRECTION_0_$LI$() { RealVector.__static_initialize(); if (RealVector.DIRECTION_0 == null)
        RealVector.DIRECTION_0 = new RealVector(10.0, 0.1, -0.1); return RealVector.DIRECTION_0; }
    ;
    static __static_initializer_0() {
        RealVector.FORMAT_$LI$().setMaximumFractionDigits(5);
        RealVector.FORMAT_$LI$().setMinimumFractionDigits(1);
    }
    /**
     * Return a string representing this vector as XML attribute values.
     * @return {string}
     */
    toXmlAttributes() {
        return "x=\"" + RealVector.FORMAT_$LI$().format(this.x) + "\" y=\"" + RealVector.FORMAT_$LI$().format(this.y) + "\" z=\"" + RealVector.FORMAT_$LI$().format(this.z) + "\"";
    }
    toString$java_text_NumberFormat(format) {
        return format.format(this.x) + "," + format.format(this.y) + "," + format.format(this.z);
    }
    /**
     * Return a string representing this vector in the form "x,y,z".
     * @param {NumberFormat} format
     * @return {string}
     */
    toString(format) {
        if (((format != null && format instanceof NumberFormat) || format === null)) {
            return this.toString$java_text_NumberFormat(format);
        }
        else if (format === undefined) {
            return this.toString$();
        }
        else
            throw new Error('invalid overload');
    }
    toString$() {
        return RealVector.FORMAT_$LI$().format(this.x) + "," + RealVector.FORMAT_$LI$().format(this.y) + "," + RealVector.FORMAT_$LI$().format(this.z);
    }
    /**
     * Return a string representing this vector in the form "x y z".
     * @return {string}
     */
    spacedString() {
        let result = RealVector.FORMAT_$LI$().format(this.x) + " " + RealVector.FORMAT_$LI$().format(this.y) + " " + RealVector.FORMAT_$LI$().format(this.z);
        return result;
    }
    /**
     * Return the sum of this vector plus the vector "other",
     * as a new Vector3D.
     * @param {RealVector} other
     * @return {RealVector}
     */
    plus(other) {
        return new RealVector(Math.fround(this.x + other.x), Math.fround(this.y + other.y), Math.fround(this.z + other.z));
    }
    /**
     * Return the difference of this vector minus the vector "other",
     * as a new Vector3D.
     * @param {RealVector} other
     * @return {RealVector}
     */
    minus(other) {
        return new RealVector(Math.fround(this.x - other.x), Math.fround(this.y - other.y), Math.fround(this.z - other.z));
    }
    /**
     * Return a new vector equal to this vector scaled by the given factor.
     * @param {number} factor
     * @return {RealVector}
     */
    scale(factor) {
        return new RealVector(this.x * factor, this.y * factor, this.z * factor);
    }
    /**
     * Return the scalar (dot) product with the other vector
     * @param {RealVector} other
     * @return {number}
     */
    dot(other) {
        return Math.fround(Math.fround(Math.fround(this.x * other.x) + Math.fround(this.y * other.y)) + Math.fround(this.z * other.z));
    }
    cross(that) {
        return new RealVector(Math.fround(Math.fround(this.y * that.z) - Math.fround(this.z * that.y)), Math.fround(Math.fround(this.z * that.x) - Math.fround(this.x * that.z)), Math.fround(Math.fround(this.x * that.y) - Math.fround(this.y * that.x)));
    }
    /**
     * Return the length of this vector.
     * @return {number}
     */
    length() {
        return Math.sqrt(this.dot(this));
    }
    normalize() {
        return this.scale(1.0 / this.length());
    }
    write(buf, offset) {
        buf.put(offset, this.x);
        buf.put(offset + 1, this.y);
        buf.put(offset + 2, this.z);
    }
    /**
     *
     * @param {*} other
     * @return {boolean}
     */
    equals(other) {
        if (other == null) {
            return false;
        }
        if (other === this) {
            return true;
        }
        if (!(other != null && other instanceof RealVector))
            return false;
        let v = other;
        return this.x === v.x && this.y === v.y && this.z === v.z;
    }
    /**
     *
     * @return {number}
     */
    hashCode() {
        let hash = 3;
        hash = 41 * hash + ((javaemul.internal.DoubleHelper.doubleToLongBits(this.x) ^ (javaemul.internal.DoubleHelper.doubleToLongBits(this.x) >>> 32)) | 0);
        hash = 41 * hash + ((javaemul.internal.DoubleHelper.doubleToLongBits(this.y) ^ (javaemul.internal.DoubleHelper.doubleToLongBits(this.y) >>> 32)) | 0);
        hash = 41 * hash + ((javaemul.internal.DoubleHelper.doubleToLongBits(this.z) ^ (javaemul.internal.DoubleHelper.doubleToLongBits(this.z) >>> 32)) | 0);
        return hash;
    }
    addTo(addend, sum) {
        sum[0] = (Math.fround(addend[0] + this.x));
        sum[1] = (Math.fround(addend[1] + this.y));
        sum[2] = (Math.fround(addend[2] + this.z));
    }
    toArray(output) {
        output[0] = this.x;
        output[1] = this.y;
        output[2] = this.z;
    }
}
RealVector.__static_initialized = false;
RealVector["__class"] = "com.vzome.core.math.RealVector";
RealVector.__static_initialize();
