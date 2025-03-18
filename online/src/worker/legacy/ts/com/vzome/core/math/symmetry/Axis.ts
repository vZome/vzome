/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * Should be called Zone, an infinite family of parallel lines, one member of an orbit (Direction)
     * of a Symmetry group.
     * @class
     */
    export class Axis {
        /*private*/ mDirection: com.vzome.core.math.symmetry.Direction;

        /*private*/ orientation: number;

        public static PLUS: number = 0;

        public static MINUS: number = 1;

        /*private*/ mSense: number;

        /**
         * Only false for orbits when Symmetry.getPrincipalReflection() != null,
         * and then for only half of the axes.  See HeptagonalAntiprismSymmetry.
         * For such groups, mSense==MINUS does not imply an inverted normal
         * relative to mSense==PLUS, but probably a specific reflection.
         * Each zone is oriented, and the inbound and outbound axes DO have opposite normals.
         * 
         * Typical group, where getPrincipalReflection() == null:
         * 
         * sense    outbound        normal
         * --------+------------+------------------
         * PLUS  |   true     +    (+x, +y, +z)
         * --------+------------+------------------
         * MINUS  |   true     +    (-x, -y, -z)
         * --------+------------+------------------
         * PLUS  |   false    +    (-x, -y, -z)   // no Axis created, just aliased
         * --------+------------+------------------
         * MINUS  |   false    +    (+x, +y, +z)   // no Axis created, just aliased
         * 
         * Odd prismatic group, where getPrincipalReflection() != null:
         * 
         * sense    outbound        normal
         * --------+------------+------------------
         * PLUS  |   true     +    (+x, +y, +z)
         * --------+------------+------------------
         * MINUS  |   true     +    (+x, +y, -z)   // PLUS outbound reflected in XY plane (for example)
         * --------+------------+------------------
         * PLUS  |   false    +    (-x, -y, -z)   // PLUS outbound reflected through origin
         * --------+------------+------------------
         * MINUS  |   false    +    (-x, -y, +z)
         */
        /*private*/ outbound: boolean;

        /*private*/ mRotationPerm: com.vzome.core.math.symmetry.Permutation;

        /*private*/ mRotation: number;

        /*private*/ __normal: com.vzome.core.algebra.AlgebraicVector;

        public constructor(dir?: any, index?: any, sense?: any, rotation?: any, rotPerm?: any, normal?: any, outbound?: any) {
            if (((dir != null && dir instanceof <any>com.vzome.core.math.symmetry.Direction) || dir === null) && ((typeof index === 'number') || index === null) && ((typeof sense === 'number') || sense === null) && ((typeof rotation === 'number') || rotation === null) && ((rotPerm != null && rotPerm instanceof <any>com.vzome.core.math.symmetry.Permutation) || rotPerm === null) && ((normal != null && normal instanceof <any>com.vzome.core.algebra.AlgebraicVector) || normal === null) && ((typeof outbound === 'boolean') || outbound === null)) {
                let __args = arguments;
                if (this.mDirection === undefined) { this.mDirection = null; } 
                if (this.orientation === undefined) { this.orientation = 0; } 
                if (this.mSense === undefined) { this.mSense = 0; } 
                if (this.mRotationPerm === undefined) { this.mRotationPerm = null; } 
                if (this.mRotation === undefined) { this.mRotation = 0; } 
                if (this.__normal === undefined) { this.__normal = null; } 
                this.outbound = true;
                this.mDirection = dir;
                this.mRotation = rotation;
                this.mRotationPerm = rotPerm;
                this.orientation = index;
                this.__normal = normal;
                this.mSense = sense;
                this.outbound = outbound;
            } else if (((dir != null && dir instanceof <any>com.vzome.core.math.symmetry.Direction) || dir === null) && ((typeof index === 'number') || index === null) && ((typeof sense === 'number') || sense === null) && ((typeof rotation === 'number') || rotation === null) && ((rotPerm != null && rotPerm instanceof <any>com.vzome.core.math.symmetry.Permutation) || rotPerm === null) && ((normal != null && normal instanceof <any>com.vzome.core.algebra.AlgebraicVector) || normal === null) && outbound === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let outbound: any = true;
                    if (this.mDirection === undefined) { this.mDirection = null; } 
                    if (this.orientation === undefined) { this.orientation = 0; } 
                    if (this.mSense === undefined) { this.mSense = 0; } 
                    if (this.mRotationPerm === undefined) { this.mRotationPerm = null; } 
                    if (this.mRotation === undefined) { this.mRotation = 0; } 
                    if (this.__normal === undefined) { this.__normal = null; } 
                    this.outbound = true;
                    this.mDirection = dir;
                    this.mRotation = rotation;
                    this.mRotationPerm = rotPerm;
                    this.orientation = index;
                    this.__normal = normal;
                    this.mSense = sense;
                    this.outbound = outbound;
                }
            } else throw new Error('invalid overload');
        }

        /**
         * Return the normal vector for this axis.
         * Note that this vector may not have length=1.0, but it will have length
         * equal to one "unit" for this axis.
         * @return {com.vzome.core.algebra.AlgebraicVector} AlgebraicVector
         */
        public normal(): com.vzome.core.algebra.AlgebraicVector {
            return this.__normal;
        }

        public isOutbound(): boolean {
            return this.outbound;
        }

        public getLength(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicNumber {
            return vector.getLength(this.__normal);
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            result = prime * result + ((this.mDirection == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.mDirection)));
            result = prime * result + this.mSense;
            result = prime * result + ((this.__normal == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.__normal)));
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
            const other: Axis = <Axis>obj;
            if (this.mDirection == null){
                if (other.mDirection != null)return false;
            } else if (!this.mDirection.equals(other.mDirection))return false;
            if (this.mSense !== other.mSense)return false;
            if (this.__normal == null){
                if (other.__normal != null)return false;
            } else if (!this.__normal.equals(other.__normal))return false;
            return true;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return this.mDirection.toString() + " " + ((this.mSense === com.vzome.core.math.symmetry.Symmetry.PLUS) ? "+" : "-") + this.orientation + (this.outbound ? "" : "i");
        }

        public getOrbit(): com.vzome.core.math.symmetry.Direction {
            return this.mDirection;
        }

        public getDirection(): com.vzome.core.math.symmetry.Direction {
            return this.mDirection;
        }

        public getOrientation(): number {
            return this.orientation;
        }

        public getRotation(): number {
            return this.mRotation;
        }

        public getCorrectRotation(): number {
            return (this.mRotationPerm == null) ? com.vzome.core.math.symmetry.Symmetry.NO_ROTATION : this.mRotationPerm.mapIndex(0);
        }

        public getRotationPermutation(): com.vzome.core.math.symmetry.Permutation {
            return this.mRotationPerm;
        }

        public getSense(): number {
            return this.mSense;
        }

        /**
         * @param plus
         * @param {number} orientation2
         * @param {boolean} outbound
         * @param {number} sense
         */
        public rename(sense: number, orientation2: number, outbound: boolean) {
            this.mSense = sense;
            this.orientation = orientation2;
            this.outbound = outbound;
        }

        public getXML(elem: org.w3c.dom.Element) {
            com.vzome.xml.DomUtils.addAttribute(elem, "symm", this.mDirection.getSymmetry().getName());
            com.vzome.xml.DomUtils.addAttribute(elem, "dir", this.mDirection.getName());
            com.vzome.xml.DomUtils.addAttribute(elem, "orbit", this.mDirection.getCanonicalName());
            com.vzome.xml.DomUtils.addAttribute(elem, "index", /* toString */(''+(this.orientation)));
            if (this.mSense !== com.vzome.core.math.symmetry.Symmetry.PLUS)com.vzome.xml.DomUtils.addAttribute(elem, "sense", "minus");
        }
    }
    Axis["__class"] = "com.vzome.core.math.symmetry.Axis";

}

