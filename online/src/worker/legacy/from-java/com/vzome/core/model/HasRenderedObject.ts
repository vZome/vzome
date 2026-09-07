import { RenderedObject } from "./RenderedObject.js";

export interface HasRenderedObject {
    getRenderedObject(): RenderedObject;
}
