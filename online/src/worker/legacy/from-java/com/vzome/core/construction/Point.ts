import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @extends Construction
 * @class
 */
export abstract class Point extends Construction {
    /*private*/ mLocation: AlgebraicVector;

    constructor(field: AlgebraicField) {
        super(field);
        if (this.mLocation === undefined) { this.mLocation = null; }
    }

    public getSignature(): string {
        return this.mLocation.projectTo3d(true).toString();
    }

    /**
     * 
     * @return {boolean}
     */
    public is3d(): boolean {
        return this.mLocation.dimension() === 3;
    }

    setStateVariable(loc: AlgebraicVector, impossible: boolean): boolean {
        if (impossible){
            if (this.isImpossible())return false;
            this.setImpossible(true);
            return true;
        }
        if (loc.equals(this.mLocation) && !this.isImpossible())return false;
        this.mLocation = loc;
        this.setImpossible(false);
        return true;
    }

    public getLocation(): AlgebraicVector {
        return this.mLocation;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("point");
        result.setAttribute("at", this.getLocation().getVectorExpression$int(AlgebraicField.ZOMIC_FORMAT));
        return result;
    }

    /**
     * 
     * @return {string}
     */
    public toString(): string {
        return "point at " + this.mLocation;
    }
}
Point["__class"] = "com.vzome.core.construction.Point";
