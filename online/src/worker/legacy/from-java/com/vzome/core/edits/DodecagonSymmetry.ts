import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { SymmetryTransformation } from "../construction/SymmetryTransformation.js";
import { Transformation } from "../construction/Transformation.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ImplicitSymmetryParameters } from "../editor/api/ImplicitSymmetryParameters.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export class DodecagonSymmetry extends ChangeManifestations {
    /*private*/ center: Point;

    /*private*/ symmetry: Symmetry;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.center === undefined) { this.center = null; }
        if (this.symmetry === undefined) { this.symmetry = null; }
        this.center = (<ImplicitSymmetryParameters><any>editor).getCenterPoint();
        this.symmetry = (<SymmetryAware><any>editor)['getSymmetrySystem$']().getSymmetry();
    }

    /**
     * 
     */
    public perform() {
        const transform: Transformation = new SymmetryTransformation(this.symmetry, 1, this.center);
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                let c: Construction = man.getFirstConstruction();
                for(let i: number = 0; i < 11; i++) {{
                    c = transform.transform$com_vzome_core_construction_Construction(c);
                    if (c == null)continue;
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(c));
                };}
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "DodecagonSymmetry";
    }
}
DodecagonSymmetry["__class"] = "com.vzome.core.edits.DodecagonSymmetry";
