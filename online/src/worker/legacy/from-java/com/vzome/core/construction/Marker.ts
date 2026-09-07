import { Construction } from "./Construction.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class Marker extends Construction {
    /*private*/ mTarget: Construction;

    public constructor(target: Construction) {
        super(target.field);
        if (this.mTarget === undefined) { this.mTarget = null; }
        this.mTarget = target;
    }

    public getTarget(): Construction {
        return this.mTarget;
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        return false;
    }

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("marker");
        return result;
    }

    /**
     * 
     * @return {boolean}
     */
    public is3d(): boolean {
        return this.mTarget.is3d();
    }
}
Marker["__class"] = "com.vzome.core.construction.Marker";
