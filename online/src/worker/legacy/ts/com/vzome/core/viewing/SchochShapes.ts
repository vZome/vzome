/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.viewing {
    export class SchochShapes extends com.vzome.core.viewing.ExportedVEFShapes {
        public constructor(prefsFolder: java.io.File, name: string, alias: string, symmetry: com.vzome.core.math.symmetry.AbstractSymmetry, defaultShapes: com.vzome.core.viewing.AbstractShapes) {
            super(prefsFolder, name, alias, symmetry, defaultShapes);
        }

        /**
         * 
         * @return {number}
         */
        public getCmScaling(): number {
            return 1.0;
        }
    }
    SchochShapes["__class"] = "com.vzome.core.viewing.SchochShapes";
    SchochShapes["__interfaces"] = ["com.vzome.core.editor.api.Shapes"];


}

