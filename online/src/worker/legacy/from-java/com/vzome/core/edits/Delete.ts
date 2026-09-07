import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestation } from "../model/Manifestation.js";

export class Delete extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        const inputs: java.util.ArrayList<Manifestation> = <any>(new java.util.ArrayList<any>());
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                inputs.add(man);
                this.unselect$com_vzome_core_model_Manifestation(man);
            }
        }
        this.redo();
        for(let index=inputs.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.deleteManifestation(man);
            }
        }
        super.perform();
    }

    public constructor(editorModel: EditorModel) {
        super(editorModel);
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "Delete";
    }
}
Delete["__class"] = "com.vzome.core.edits.Delete";
