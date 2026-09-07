import { Node } from "./Node.js";

export interface NodeList {
    item(index: number): Node;

    getLength(): number;
}
