import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Construction } from "./Construction.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @extends Construction
 * @class
 */
export abstract class Segment extends Construction {
    /*private*/ mStart: AlgebraicVector;

    /*private*/ mOffset: AlgebraicVector;

    /*private*/ mEnd: AlgebraicVector;

    constructor(field: AlgebraicField) {
        super(field);
        if (this.mStart === undefined) { this.mStart = null; }
        if (this.mOffset === undefined) { this.mOffset = null; }
        if (this.mEnd === undefined) { this.mEnd = null; }
    }

    public getSignature(): string {
        const start: string = this.mStart.projectTo3d(true).toString();
        const end: string = this.getEnd().projectTo3d(true).toString();
        if (/* compareTo */start.localeCompare(end) <= 0)return start + "," + end; else return end + "," + start;
    }

    /**
     * 
     * @return {boolean}
     */
    public is3d(): boolean {
        return this.mStart.dimension() === 3 && this.mOffset.dimension() === 3;
    }

    setStateVariables(start: AlgebraicVector, offset: AlgebraicVector, impossible: boolean): boolean {
        if (impossible){
            if (this.isImpossible())return false;
            this.setImpossible(true);
            return true;
        }
        if (offset.equals(this.mOffset) && !this.isImpossible() && start.equals(this.mStart))return false;
        this.mOffset = offset;
        this.mStart = start;
        this.mEnd = null;
        this.setImpossible(false);
        return true;
    }

    public getStart(): AlgebraicVector {
        return this.mStart;
    }

    public getEnd(): AlgebraicVector {
        if (this.mEnd == null)this.mEnd = this.mStart.plus(this.mOffset);
        return this.mEnd;
    }

    public getOffset(): AlgebraicVector {
        return this.mOffset;
    }

    public getCentroid(): AlgebraicVector {
        return AlgebraicVectors.getCentroid([this.mStart, this.mEnd]);
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("segment");
        result.setAttribute("start", this.mStart.getVectorExpression$int(AlgebraicField.ZOMIC_FORMAT));
        result.setAttribute("end", this.getEnd().getVectorExpression$int(AlgebraicField.ZOMIC_FORMAT));
        return result;
    }

    /**
     * 
     * @return {string}
     */
    public toString(): string {
        return "segment from " + this.mStart + " to " + this.getEnd();
    }
}
Segment["__class"] = "com.vzome.core.construction.Segment";
