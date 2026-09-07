import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { RealizedModel } from "../model/RealizedModel.js";

export class InvertSelection extends ChangeSelection {
    mManifestations: RealizedModel;

    public constructor(editor: EditorModel) {
        super(editor.getSelection());
        if (this.mManifestations === undefined) { this.mManifestations = null; }
        this.mManifestations = editor.getRealizedModel();
    }

    /**
     * 
     */
    public perform() {
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                if (m.isRendered()){
                    if (this.mSelection.manifestationSelected(m))this.unselect$com_vzome_core_model_Manifestation(m); else this.select$com_vzome_core_model_Manifestation(m);
                }
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "InvertSelection";
    }
}
InvertSelection["__class"] = "com.vzome.core.edits.InvertSelection";
