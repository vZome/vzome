import { AbstractSymmetry } from "../math/symmetry/AbstractSymmetry.js";
import { AbstractShapes } from "./AbstractShapes.js";
import { ExportedVEFShapes } from "./ExportedVEFShapes.js";
import { File } from "../../../../java/io/File.js";

export class SchochShapes extends ExportedVEFShapes {
    public constructor(prefsFolder: File, name: string, alias: string, symmetry: AbstractSymmetry, defaultShapes: AbstractShapes) {
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
