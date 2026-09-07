import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Bivector3dHomogeneous } from "../algebra/Bivector3dHomogeneous.js";
import { Vector3dHomogeneous } from "../algebra/Vector3dHomogeneous.js";
import { Construction } from "./Construction.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @extends Construction
 * @class
 */
export abstract class Line extends Construction {
    /*private*/ mDirection: AlgebraicVector;

    /*private*/ mStart: AlgebraicVector;

    constructor(field: AlgebraicField) {
        super(field);
        if (this.mDirection === undefined) { this.mDirection = null; }
        if (this.mStart === undefined) { this.mStart = null; }
    }

    /**
     * 
     * @return {boolean}
     */
    public is3d(): boolean {
        return true;
    }

    /**
     * 
     * @param {AlgebraicVector} start
     * @param {AlgebraicVector} norm need not be normalized yet
     * @return
     * @param {boolean} impossible
     * @return {boolean}
     */
    setStateVariables(start: AlgebraicVector, norm: AlgebraicVector, impossible: boolean): boolean {
        if (impossible){
            if (this.isImpossible())return false;
            this.setImpossible(true);
            return true;
        }
        if (norm.equals(this.mDirection) && start.equals(this.mStart) && !this.isImpossible())return false;
        this.mDirection = norm;
        this.mStart = start;
        this.setImpossible(false);
        return true;
    }

    public getStart(): AlgebraicVector {
        return this.mStart;
    }

    /**
     * @return {AlgebraicVector} a "unit" vector... always normalized
     */
    public getDirection(): AlgebraicVector {
        return this.mDirection;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("line");
        return result;
    }

    public getHomogeneous(): Bivector3dHomogeneous {
        const v1: Vector3dHomogeneous = new Vector3dHomogeneous(this.mStart, this.getField());
        const v2: Vector3dHomogeneous = new Vector3dHomogeneous(this.mStart.plus(this.mDirection), this.getField());
        return v1.outer(v2);
    }
}
Line["__class"] = "com.vzome.core.construction.Line";
