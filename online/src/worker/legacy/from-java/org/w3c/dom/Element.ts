import { Document } from "./Document.js";
import { Node } from "./Node.js";
import { NodeList } from "./NodeList.js";

export interface Element extends Node {
    getOwnerDocument(): Document;

    setAttribute(name: string, value: string);

    getElementsByTagName(name: string): NodeList;

    setAttributeNS(namespaceURI: string, qualifiedName: string, value: string);

    getAttribute(name: string): string;

    setTextContent(text: string);

    getUserData(key: string): any;
}
