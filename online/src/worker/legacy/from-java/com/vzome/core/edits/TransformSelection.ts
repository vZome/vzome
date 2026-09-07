import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Construction } from "../construction/Construction.js";
import { Transformation } from "../construction/Transformation.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestation } from "../model/Manifestation.js";

export class TransformSelection extends ChangeManifestations {
    transform: Transformation;

    public constructor(editor: EditorModel, transform: Transformation) {
        super(editor);
        if (this.transform === undefined) { this.transform = null; }
        this.transform = transform;
    }

    /**
     * 
     */
    public perform() {
        const inputs: java.util.List<Manifestation> = <any>(new java.util.ArrayList<any>());
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                inputs.add(man);
            }
        }
        this.redo();
        for(let index=inputs.iterator();index.hasNext();) {
            let m = index.next();
            {
                if (!m.isRendered())continue;
                const c: Construction = m.getFirstConstruction();
                const result: Construction = this.transform.transform$com_vzome_core_construction_Construction(c);
                this.select$com_vzome_core_model_Manifestation$boolean(this.manifestConstruction(result), true);
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "TransformSelection";
    }
}
TransformSelection["__class"] = "com.vzome.core.edits.TransformSelection";
