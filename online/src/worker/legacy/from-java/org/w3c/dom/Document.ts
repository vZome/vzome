import { Element } from "./Element.js";
import { Node } from "./Node.js";
import { Text } from "./Text.js";

export interface Document {
    createElement(name: string): Element;

    createTextNode(data: string): Text;

    importNode(importedNode: Node, deep: boolean): Node;
}
