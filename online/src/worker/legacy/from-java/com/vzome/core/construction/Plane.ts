import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Trivector3dHomogeneous } from "../algebra/Trivector3dHomogeneous.js";
import { Construction } from "./Construction.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @extends Construction
 * @class
 */
export abstract class Plane extends Construction {
    /*private*/ mBase: AlgebraicVector;

    /*private*/ mNormal: AlgebraicVector;

    constructor(field: AlgebraicField) {
        super(field);
        if (this.mBase === undefined) { this.mBase = null; }
        if (this.mNormal === undefined) { this.mNormal = null; }
    }

    /**
     * 
     * @return {boolean}
     */
    public is3d(): boolean {
        return true;
    }

    setStateVariables(base: AlgebraicVector, normal: AlgebraicVector, impossible: boolean): boolean {
        if (impossible){
            if (this.isImpossible())return false;
            this.setImpossible(true);
            return true;
        }
        if (normal.equals(this.mNormal) && !this.isImpossible() && base.equals(this.mBase))return false;
        normal = normal.projectTo3d(true);
        this.mNormal = normal;
        this.mBase = base.projectTo3d(true);
        this.setImpossible(false);
        return true;
    }

    public getBase(): AlgebraicVector {
        return this.mBase;
    }

    public getNormal(): AlgebraicVector {
        return this.mNormal;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("plane");
        return result;
    }

    public getHomogeneous(): Trivector3dHomogeneous {
        return null;
    }
}
Plane["__class"] = "com.vzome.core.construction.Plane";
