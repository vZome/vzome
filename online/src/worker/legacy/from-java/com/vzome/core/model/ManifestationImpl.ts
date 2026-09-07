import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { Group } from "./Group.js";
import { HasRenderedObject } from "./HasRenderedObject.js";
import { Manifestation } from "./Manifestation.js";
import { RenderedObject } from "./RenderedObject.js";
import { RenderedManifestation } from "../render/RenderedManifestation.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export abstract class ManifestationImpl implements Manifestation, HasRenderedObject {
    mManifests: java.util.List<Construction>;

    mRendered: RenderedManifestation;

    /*private*/ hidden: boolean;

    /*private*/ mId: number;

    /*private*/ color: Color;

    static NO_ID: number = -1;

    static NEXT_ID: number = 0;

    resetId() {
        ManifestationImpl.NEXT_ID = 0;
        this.mId = ManifestationImpl.NO_ID;
    }

    getId(): number {
        if (this.mId === ManifestationImpl.NO_ID)this.mId = ManifestationImpl.NEXT_ID++;
        return this.mId;
    }

    public addConstruction(c: Construction) {
        this.mManifests.add(c);
    }

    public removeConstruction(c: Construction) {
        this.mManifests.remove(c);
    }

    public getConstructions(): java.util.Iterator<Construction> {
        return this.mManifests.iterator();
    }

    /**
     * This is different from toConstruction, because we must support
     * the legacy behavior, which used the iterator.
     * @return
     * @return {Construction}
     */
    public getFirstConstruction(): Construction {
        if (this.mManifests.isEmpty())return null;
        return this.mManifests.iterator().next();
    }

    public isUnnecessary(): boolean {
        return this.mManifests.isEmpty();
    }

    public getColor(): Color {
        if (this.color == null && this.mRendered != null){
            this.color = this.mRendered.getColor();
        }
        return this.color;
    }

    public setColor(color: Color) {
        this.color = color;
    }

    public setRenderedObject(obj: RenderedObject) {
        this.mRendered = <RenderedManifestation><any>obj;
        if (this.mRendered != null)this.color = this.mRendered.getColor();
    }

    public getRenderedObject(): RenderedObject {
        return this.mRendered;
    }

    public isHidden(): boolean {
        return this.hidden;
    }

    public abstract getLocation(): AlgebraicVector;

    public abstract getCentroid(): AlgebraicVector;

    /**
     * This is guaranteed to return a 3D construction,
     * and will return the same object as getFirstConstruction()
     * when possible.
     * @return
     * @return {Construction}
     */
    public abstract toConstruction(): Construction;

    /*private*/ mContainer: Group;

    public getContainer(): Group {
        return this.mContainer;
    }

    /**
     * 
     * @param {Group} container
     */
    public setContainer(container: Group) {
        this.mContainer = container;
    }

    public setHidden(hidden: boolean) {
        this.hidden = hidden;
    }

    public isRendered(): boolean {
        return this.mRendered != null;
    }

    public getXml(doc: Document): Element {
        return this.toConstruction().getXml(doc);
    }

    public abstract getLabel(): any;
    public abstract setLabel(label?: any): any;
    constructor() {
        this.mManifests = <any>(new java.util.ArrayList<any>());
        this.mRendered = null;
        this.hidden = false;
        this.mId = ManifestationImpl.NO_ID;
        if (this.color === undefined) { this.color = null; }
        if (this.mContainer === undefined) { this.mContainer = null; }
    }
}
ManifestationImpl["__class"] = "com.vzome.core.model.ManifestationImpl";
ManifestationImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Manifestation"];
