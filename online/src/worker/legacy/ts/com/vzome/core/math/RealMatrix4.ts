/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math {
    /**
     * @param {float[]} m 16 elements in column-major order
     * @param {boolean} transpose
     * @class
     */
    export class RealMatrix4 {
        /*private*/ m00: number;

        /*private*/ m01: number;

        /*private*/ m02: number;

        /*private*/ m03: number;

        /*private*/ m10: number;

        /*private*/ m11: number;

        /*private*/ m12: number;

        /*private*/ m13: number;

        /*private*/ m20: number;

        /*private*/ m21: number;

        /*private*/ m22: number;

        /*private*/ m23: number;

        /*private*/ m30: number;

        /*private*/ m31: number;

        /*private*/ m32: number;

        /*private*/ m33: number;

        public constructor(m?: any, transpose?: any) {
            if (((m != null && m instanceof <any>Array && (m.length == 0 || m[0] == null ||(typeof m[0] === 'number'))) || m === null) && ((typeof transpose === 'boolean') || transpose === null)) {
                let __args = arguments;
                if (this.m00 === undefined) { this.m00 = 0; } 
                if (this.m01 === undefined) { this.m01 = 0; } 
                if (this.m02 === undefined) { this.m02 = 0; } 
                if (this.m03 === undefined) { this.m03 = 0; } 
                if (this.m10 === undefined) { this.m10 = 0; } 
                if (this.m11 === undefined) { this.m11 = 0; } 
                if (this.m12 === undefined) { this.m12 = 0; } 
                if (this.m13 === undefined) { this.m13 = 0; } 
                if (this.m20 === undefined) { this.m20 = 0; } 
                if (this.m21 === undefined) { this.m21 = 0; } 
                if (this.m22 === undefined) { this.m22 = 0; } 
                if (this.m23 === undefined) { this.m23 = 0; } 
                if (this.m30 === undefined) { this.m30 = 0; } 
                if (this.m31 === undefined) { this.m31 = 0; } 
                if (this.m32 === undefined) { this.m32 = 0; } 
                if (this.m33 === undefined) { this.m33 = 0; } 
                this.m00 = m[0];
                this.m01 = m[4];
                this.m02 = m[8];
                this.m03 = m[12];
                this.m10 = m[1];
                this.m11 = m[5];
                this.m12 = m[9];
                this.m13 = m[13];
                this.m20 = m[2];
                this.m21 = m[6];
                this.m22 = m[10];
                this.m23 = m[14];
                this.m30 = m[3];
                this.m31 = m[7];
                this.m32 = m[11];
                this.m33 = m[15];
            } else if (((m != null && m instanceof <any>Array && (m.length == 0 || m[0] == null ||(typeof m[0] === 'number'))) || m === null) && transpose === undefined) {
                let __args = arguments;
                if (this.m00 === undefined) { this.m00 = 0; } 
                if (this.m01 === undefined) { this.m01 = 0; } 
                if (this.m02 === undefined) { this.m02 = 0; } 
                if (this.m03 === undefined) { this.m03 = 0; } 
                if (this.m10 === undefined) { this.m10 = 0; } 
                if (this.m11 === undefined) { this.m11 = 0; } 
                if (this.m12 === undefined) { this.m12 = 0; } 
                if (this.m13 === undefined) { this.m13 = 0; } 
                if (this.m20 === undefined) { this.m20 = 0; } 
                if (this.m21 === undefined) { this.m21 = 0; } 
                if (this.m22 === undefined) { this.m22 = 0; } 
                if (this.m23 === undefined) { this.m23 = 0; } 
                if (this.m30 === undefined) { this.m30 = 0; } 
                if (this.m31 === undefined) { this.m31 = 0; } 
                if (this.m32 === undefined) { this.m32 = 0; } 
                if (this.m33 === undefined) { this.m33 = 0; } 
                this.m00 = m[0];
                this.m01 = m[1];
                this.m02 = m[2];
                this.m03 = m[3];
                this.m10 = m[4];
                this.m11 = m[5];
                this.m12 = m[6];
                this.m13 = m[7];
                this.m20 = m[8];
                this.m21 = m[9];
                this.m22 = m[10];
                this.m23 = m[11];
                this.m30 = m[12];
                this.m31 = m[13];
                this.m32 = m[14];
                this.m33 = m[15];
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} v 4th component treated as 1
         * @return
         * @return {com.vzome.core.math.RealVector}
         */
        public transform3dPt(v: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector {
            return new com.vzome.core.math.RealVector((<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m00 * v.x) + (<any>Math).fround(this.m01 * v.y)) + (<any>Math).fround(this.m02 * v.z)) + this.m03), (<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m10 * v.x) + (<any>Math).fround(this.m11 * v.y)) + (<any>Math).fround(this.m12 * v.z)) + this.m13), (<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m20 * v.x) + (<any>Math).fround(this.m21 * v.y)) + (<any>Math).fround(this.m22 * v.z)) + this.m23));
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} v 4th component treated as 0
         * @return
         * @return {com.vzome.core.math.RealVector}
         */
        public transform3dVec(v: com.vzome.core.math.RealVector): com.vzome.core.math.RealVector {
            return new com.vzome.core.math.RealVector((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m00 * v.x) + (<any>Math).fround(this.m01 * v.y)) + (<any>Math).fround(this.m02 * v.z)), (<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m10 * v.x) + (<any>Math).fround(this.m11 * v.y)) + (<any>Math).fround(this.m12 * v.z)), (<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m20 * v.x) + (<any>Math).fround(this.m21 * v.y)) + (<any>Math).fround(this.m22 * v.z)));
        }

        public transform4d(v: number[]): number[] {
            const x: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m00 * v[0]) + (<any>Math).fround(this.m01 * v[1])) + (<any>Math).fround(this.m02 * v[2])) + (<any>Math).fround(this.m03 * v[3]));
            const y: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m10 * v[0]) + (<any>Math).fround(this.m11 * v[1])) + (<any>Math).fround(this.m12 * v[2])) + (<any>Math).fround(this.m13 * v[3]));
            const z: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m20 * v[0]) + (<any>Math).fround(this.m21 * v[1])) + (<any>Math).fround(this.m22 * v[2])) + (<any>Math).fround(this.m23 * v[3]));
            const w: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround((<any>Math).fround(this.m30 * v[0]) + (<any>Math).fround(this.m31 * v[1])) + (<any>Math).fround(this.m32 * v[2])) + (<any>Math).fround(this.m33 * v[3]));
            return [x, y, z, w];
        }

        /**
         * Helping function that specifies the position and orientation of a
         * view matrix. The inverse of this transform can be used to control
         * the ViewPlatform object within the scene graph.
         * @param {com.vzome.core.math.RealVector} eye the location of the eye
         * @param {com.vzome.core.math.RealVector} center a point in the virtual world where the eye is looking
         * @param {com.vzome.core.math.RealVector} up an up vector specifying the frustum's up direction
         * @return {com.vzome.core.math.RealMatrix4}
         */
        public static lookAt(eye: com.vzome.core.math.RealVector, center: com.vzome.core.math.RealVector, up: com.vzome.core.math.RealVector): RealMatrix4 {
            let forwardx: number;
            let forwardy: number;
            let forwardz: number;
            let invMag: number;
            let upx: number;
            let upy: number;
            let upz: number;
            let sidex: number;
            let sidey: number;
            let sidez: number;
            forwardx = (<any>Math).fround(eye.x - center.x);
            forwardy = (<any>Math).fround(eye.y - center.y);
            forwardz = (<any>Math).fround(eye.z - center.z);
            invMag = (<any>Math).fround((1.0 / Math.sqrt((<any>Math).fround((<any>Math).fround((<any>Math).fround(forwardx * forwardx) + (<any>Math).fround(forwardy * forwardy)) + (<any>Math).fround(forwardz * forwardz)))));
            forwardx = (<any>Math).fround(forwardx * invMag);
            forwardy = (<any>Math).fround(forwardy * invMag);
            forwardz = (<any>Math).fround(forwardz * invMag);
            invMag = (<any>Math).fround((1.0 / Math.sqrt((<any>Math).fround((<any>Math).fround((<any>Math).fround(up.x * up.x) + (<any>Math).fround(up.y * up.y)) + (<any>Math).fround(up.z * up.z)))));
            upx = (<any>Math).fround(up.x * invMag);
            upy = (<any>Math).fround(up.y * invMag);
            upz = (<any>Math).fround(up.z * invMag);
            sidex = (<any>Math).fround((<any>Math).fround(upy * forwardz) - (<any>Math).fround(forwardy * upz));
            sidey = (<any>Math).fround((<any>Math).fround(upz * forwardx) - (<any>Math).fround(upx * forwardz));
            sidez = (<any>Math).fround((<any>Math).fround(upx * forwardy) - (<any>Math).fround(upy * forwardx));
            invMag = (<any>Math).fround((1.0 / Math.sqrt((<any>Math).fround((<any>Math).fround((<any>Math).fround(sidex * sidex) + (<any>Math).fround(sidey * sidey)) + (<any>Math).fround(sidez * sidez)))));
            sidex *= invMag;
            sidey *= invMag;
            sidez *= invMag;
            upx = (<any>Math).fround((<any>Math).fround(forwardy * sidez) - (<any>Math).fround(sidey * forwardz));
            upy = (<any>Math).fround((<any>Math).fround(forwardz * sidex) - (<any>Math).fround(forwardx * sidez));
            upz = (<any>Math).fround((<any>Math).fround(forwardx * sidey) - (<any>Math).fround(forwardy * sidex));
            const m00: number = sidex;
            const m01: number = sidey;
            const m02: number = sidez;
            const m10: number = upx;
            const m11: number = upy;
            const m12: number = upz;
            const m20: number = forwardx;
            const m21: number = forwardy;
            const m22: number = forwardz;
            const m03: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround(-eye.x * m00) + (<any>Math).fround(-eye.y * m01)) + (<any>Math).fround(-eye.z * m02));
            const m13: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround(-eye.x * m10) + (<any>Math).fround(-eye.y * m11)) + (<any>Math).fround(-eye.z * m12));
            const m23: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround(-eye.x * m20) + (<any>Math).fround(-eye.y * m21)) + (<any>Math).fround(-eye.z * m22));
            return new RealMatrix4([m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, 0, 0, 0, 1]);
        }

        /**
         * Creates a perspective projection transform that mimics a standard,
         * camera-based,
         * view-model.
         * (From javax.media.j3d.Transform3d.java)
         * This transform maps coordinates from Eye Coordinates (EC)
         * to Clipping Coordinates (CC).  Note that unlike the similar function
         * in OpenGL, the clipping coordinates generated by the resulting
         * transform are in a right-handed coordinate system
         * (as are all other coordinate systems in Java 3D). Also note that the
         * field of view is specified in radians.
         * @param {number} fovx specifies the field of view in the x direction, in radians
         * @param {number} aspect specifies the aspect ratio and thus the field of
         * view in the x direction. The aspect ratio is the ratio of x to y,
         * or width to height.
         * @param {number} zNear the distance to the frustum's near clipping plane.
         * This value must be positive, (the value -zNear is the location of the
         * near clip plane).
         * @param {number} zFar the distance to the frustum's far clipping plane
         * @return {com.vzome.core.math.RealMatrix4}
         */
        public static perspective(fovx: number, aspect: number, zNear: number, zFar: number): RealMatrix4 {
            let sine: number;
            let cotangent: number;
            let deltaZ: number;
            const half_fov: number = (<any>Math).fround(fovx * 0.5);
            deltaZ = (<any>Math).fround(zFar - zNear);
            sine = (<any>Math).fround(Math.sin(half_fov));
            cotangent = (<any>Math).fround((Math.cos(half_fov) / sine));
            const m00: number = cotangent;
            const m11: number = (<any>Math).fround(cotangent * aspect);
            const m22: number = (<any>Math).fround(((<any>Math).fround(zFar + zNear)) / deltaZ);
            const m23: number = (<any>Math).fround((<any>Math).fround((<any>Math).fround(2.0 * zNear) * zFar) / deltaZ);
            const m32: number = -1.0;
            return new RealMatrix4([m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, m23, 0, 0, m32, 0]);
        }

        /**
         * Creates an orthographic projection transform that mimics a standard,
         * camera-based,
         * view-model.
         * (From javax.media.j3d.Transform3d.java)
         * This transform maps coordinates from Eye Coordinates (EC)
         * to Clipping Coordinates (CC).  Note that unlike the similar function
         * in OpenGL, the clipping coordinates generated by the resulting
         * transform are in a right-handed coordinate system
         * (as are all other coordinate systems in Java 3D).
         * @param {number} left the vertical line on the left edge of the near
         * clipping plane mapped to the left edge of the graphics window
         * @param {number} right the vertical line on the right edge of the near
         * clipping plane mapped to the right edge of the graphics window
         * @param {number} bottom the horizontal line on the bottom edge of the near
         * clipping plane mapped to the bottom edge of the graphics window
         * @param {number} top the horizontal line on the top edge of the near
         * clipping plane mapped to the top edge of the graphics window
         * @param {number} near the distance to the frustum's near clipping plane
         * (the value -near is the location of the near clip plane)
         * @param {number} far the distance to the frustum's far clipping plane
         * @return {com.vzome.core.math.RealMatrix4}
         */
        public static ortho(left: number, right: number, bottom: number, top: number, near: number, far: number): RealMatrix4 {
            const deltax: number = (<any>Math).fround(1 / ((<any>Math).fround(right - left)));
            const deltay: number = (<any>Math).fround(1 / ((<any>Math).fround(top - bottom)));
            const deltaz: number = (<any>Math).fround(1 / ((<any>Math).fround(far - near)));
            const m00: number = (<any>Math).fround(2.0 * deltax);
            const m03: number = (<any>Math).fround(-((<any>Math).fround(right + left)) * deltax);
            const m11: number = (<any>Math).fround(2.0 * deltay);
            const m13: number = (<any>Math).fround(-((<any>Math).fround(top + bottom)) * deltay);
            const m22: number = (<any>Math).fround(2.0 * deltaz);
            const m23: number = (<any>Math).fround(((<any>Math).fround(far + near)) * deltaz);
            const m33: number = 1;
            return new RealMatrix4([m00, 0.0, 0.0, m03, 0.0, m11, 0.0, m13, 0.0, 0.0, m22, m23, 0.0, 0.0, 0.0, m33]);
        }

        public toArray(): number[] {
            return [this.m00, this.m01, this.m02, this.m03, this.m10, this.m11, this.m12, this.m13, this.m20, this.m21, this.m22, this.m23, this.m30, this.m31, this.m32, this.m33];
        }
    }
    RealMatrix4["__class"] = "com.vzome.core.math.RealMatrix4";

}

