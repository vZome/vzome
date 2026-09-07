import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { Direction } from "./Direction.js";
import { Permutation } from "./Permutation.js";
import { Symmetry } from "./Symmetry.js";
import { DomUtils } from "../../../xml/DomUtils.js";
import { Element } from "../../../../../org/w3c/dom/Element.js";

/**
 * Should be called Zone, an infinite family of parallel lines, one member of an orbit (Direction)
 * of a Symmetry group.
 * @class
 */
export class Axis {
    /*private*/ mDirection: Direction;

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

    /*private*/ mRotationPerm: Permutation;

    /*private*/ mRotation: number;

    /*private*/ __normal: AlgebraicVector;

    public constructor(dir?: any, index?: any, sense?: any, rotation?: any, rotPerm?: any, normal?: any, outbound?: any) {
        if (((dir != null && dir instanceof <any>Direction) || dir === null) && ((typeof index === 'number') || index === null) && ((typeof sense === 'number') || sense === null) && ((typeof rotation === 'number') || rotation === null) && ((rotPerm != null && rotPerm instanceof <any>Permutation) || rotPerm === null) && ((normal != null && normal instanceof <any>AlgebraicVector) || normal === null) && ((typeof outbound === 'boolean') || outbound === null)) {
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
        } else if (((dir != null && dir instanceof <any>Direction) || dir === null) && ((typeof index === 'number') || index === null) && ((typeof sense === 'number') || sense === null) && ((typeof rotation === 'number') || rotation === null) && ((rotPerm != null && rotPerm instanceof <any>Permutation) || rotPerm === null) && ((normal != null && normal instanceof <any>AlgebraicVector) || normal === null) && outbound === undefined) {
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
     * @return {AlgebraicVector} AlgebraicVector
     */
    public normal(): AlgebraicVector {
        return this.__normal;
    }

    public isOutbound(): boolean {
        return this.outbound;
    }

    public getLength(vector: AlgebraicVector): AlgebraicNumber {
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
        return this.mDirection.toString() + " " + ((this.mSense === Symmetry.PLUS) ? "+" : "-") + this.orientation + (this.outbound ? "" : "i");
    }

    public getOrbit(): Direction {
        return this.mDirection;
    }

    public getDirection(): Direction {
        return this.mDirection;
    }

    public getOrientation(): number {
        return this.orientation;
    }

    public getRotation(): number {
        return this.mRotation;
    }

    public getCorrectRotation(): number {
        return (this.mRotationPerm == null) ? Symmetry.NO_ROTATION : this.mRotationPerm.mapIndex(0);
    }

    public getRotationPermutation(): Permutation {
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

    public getXML(elem: Element) {
        DomUtils.addAttribute(elem, "symm", this.mDirection.getSymmetry().getName());
        DomUtils.addAttribute(elem, "dir", this.mDirection.getName());
        DomUtils.addAttribute(elem, "orbit", this.mDirection.getCanonicalName());
        DomUtils.addAttribute(elem, "index", /* toString */(''+(this.orientation)));
        if (this.mSense !== Symmetry.PLUS)DomUtils.addAttribute(elem, "sense", "minus");
    }
}
Axis["__class"] = "com.vzome.core.math.symmetry.Axis";
