import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { Group } from "./Group.js";
import { GroupElement } from "./GroupElement.js";
import { RenderedObject } from "./RenderedObject.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export interface Manifestation extends GroupElement {
    getLocation(): AlgebraicVector;

    getConstructions(): java.util.Iterator<Construction>;

    getFirstConstruction(): Construction;

    getXml(doc: Document): Element;

    isHidden(): boolean;

    isRendered(): boolean;

    toConstruction(): Construction;

    getCentroid(): AlgebraicVector;

    isUnnecessary(): boolean;

    addConstruction(mConstruction: Construction);

    removeConstruction(mConstruction: Construction);

    setHidden(b: boolean);

    getContainer(): Group;

    getColor(): Color;

    setColor(color: Color);

    setRenderedObject(renderedObject: RenderedObject);

    setLabel(label: string);

    getLabel(): string;
}
