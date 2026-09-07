import { Document } from "../../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../../org/w3c/dom/Element.js";

export interface SideEffect {
    undo();

    getXml(doc: Document): Element;

    redo();
}
