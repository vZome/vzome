/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
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
             * @return {com.vzome.core.construction.Color}
             */
            apply(src: T): com.vzome.core.construction.Color;

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

}

