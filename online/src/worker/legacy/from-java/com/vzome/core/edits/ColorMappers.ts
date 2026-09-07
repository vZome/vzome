import { Color } from "../construction/Color.js";

/**
 * @author David Hall
 * @class
 */
export class ColorMappers {    }
ColorMappers["__class"] = "com.vzome.core.edits.ColorMappers";


export namespace ColorMappers {

    export interface ColorMapper<T> {
        /**
         * 
         * @param {*} src
         * @return {Color}
         */
        apply(src: T): Color;

        requiresOrderedSelection(): boolean;

        /**
         * We had a default implementation here, using reflection, but that gave me problems
         * when transpiled to JavaScript with JSweet.  Now each class simply implements the method.
         * @return
         * @return {string}
         */
        getName(): string;
    }
}
