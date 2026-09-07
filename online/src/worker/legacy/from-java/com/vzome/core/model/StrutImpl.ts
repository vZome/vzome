import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ManifestationImpl } from "./ManifestationImpl.js";
import { Strut } from "./Strut.js";

/**
 * @author Scott Vorthmann
 * @param {AlgebraicVector} end1
 * @param {AlgebraicVector} end2
 * @class
 * @extends ManifestationImpl
 */
export class StrutImpl extends ManifestationImpl implements Strut {
    /*private*/ m_end1: AlgebraicVector;

    /*private*/ m_end2: AlgebraicVector;

    /*private*/ zoneVector: AlgebraicVector;

    /*private*/ label: string;

    public constructor(end1: AlgebraicVector, end2: AlgebraicVector) {
        super();
        if (this.m_end1 === undefined) { this.m_end1 = null; }
        if (this.m_end2 === undefined) { this.m_end2 = null; }
        if (this.zoneVector === undefined) { this.zoneVector = null; }
        if (this.label === undefined) { this.label = null; }
        this.m_end1 = end1;
        this.m_end2 = end2;
    }

    public getZoneVector(): AlgebraicVector {
        if (this.zoneVector != null)return this.zoneVector; else return this.getOffset();
    }

    public setZoneVector(vector: AlgebraicVector) {
        this.zoneVector = vector;
    }

    /**
     * 
     * @return {number}
     */
    public hashCode(): number {
        const result: number = /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_end1)) ^ /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_end2));
        return result;
    }

    /**
     * 
     * @param {*} obj
     * @return {boolean}
     */
    public equals(obj: any): boolean {
        if (obj == null)return false;
        if (obj === this)return true;
        if (!(obj != null && obj instanceof <any>StrutImpl))return false;
        const other: StrutImpl = <StrutImpl>obj;
        const otherStart: AlgebraicVector = other.m_end1;
        const otherEnd: AlgebraicVector = other.m_end2;
        if (otherStart.equals(this.m_end1))return otherEnd.equals(this.m_end2); else if (otherEnd.equals(this.m_end1))return otherStart.equals(this.m_end2); else return false;
    }

    /**
     * 
     * @param {*} other
     * @return {number}
     */
    public compareTo(other: Strut): number {
        if (this === other){
            return 0;
        }
        if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(other,this))){
            return 0;
        }
        const thisFirst: AlgebraicVector = this.getCanonicalLesserEnd();
        const thisLast: AlgebraicVector = this.getCanonicalGreaterEnd();
        const otherFirst: AlgebraicVector = other.getCanonicalLesserEnd();
        const otherLast: AlgebraicVector = other.getCanonicalGreaterEnd();
        const comparison: number = thisFirst.compareTo(otherFirst);
        return (comparison === 0) ? thisLast.compareTo(otherLast) : comparison;
    }

    public getCanonicalLesserEnd(): AlgebraicVector {
        return (this.m_end1.compareTo(this.m_end2) < 0) ? this.m_end1 : this.m_end2;
    }

    public getCanonicalGreaterEnd(): AlgebraicVector {
        return (this.m_end1.compareTo(this.m_end2) > 0) ? this.m_end1 : this.m_end2;
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getLocation(): AlgebraicVector {
        return this.m_end1;
    }

    /**
     * 
     * @return {AlgebraicVector}
     */
    public getCentroid(): AlgebraicVector {
        return AlgebraicVectors.getCentroid([this.m_end1, this.m_end2]);
    }

    /**
     * 
     * @return {Construction}
     */
    public toConstruction(): Construction {
        const first: Construction = this.getFirstConstruction();
        if (first != null && first.is3d())return first;
        const field: AlgebraicField = this.m_end1.getField();
        const pt1: Point = new FreePoint(field.projectTo3d(this.m_end1, true));
        const pt2: Point = new FreePoint(field.projectTo3d(this.m_end2, true));
        return new SegmentJoiningPoints(pt1, pt2);
    }

    public getEnd(): AlgebraicVector {
        return this.m_end2;
    }

    public getOffset(): AlgebraicVector {
        return this.m_end2.minus(this.m_end1);
    }

    /**
     * 
     * @return {string}
     */
    public toString(): string {
        return "strut from " + this.m_end1.toString() + " to " + this.m_end2.toString();
    }

    /**
     * 
     * @param {string} label
     */
    public setLabel(label: string) {
        this.label = label;
    }

    /**
     * 
     * @return {string}
     */
    public getLabel(): string {
        return this.label;
    }
}
StrutImpl["__class"] = "com.vzome.core.model.StrutImpl";
StrutImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Strut","java.lang.Comparable","com.vzome.core.model.Manifestation"];
