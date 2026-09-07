import { NodeList } from "./NodeList.js";

export interface Node {
    appendChild(newChild: Node): Node;

    getChildNodes(): NodeList;

    getTextContent(): string;

    getLocalName(): string;
}
