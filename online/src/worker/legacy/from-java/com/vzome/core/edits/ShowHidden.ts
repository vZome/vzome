import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";

export class ShowHidden extends ChangeManifestations {
    public constructor(editor: EditorModel) {
        super(editor);
    }

    /**
     * 
     */
    public perform() {
        for(let index=this.mManifestations.iterator();index.hasNext();) {
            let m = index.next();
            {
                if (m.isHidden()){
                    this.showManifestation(m);
                    this.select$com_vzome_core_model_Manifestation(m);
                } else if (this.mSelection.manifestationSelected(m))this.unselect$com_vzome_core_model_Manifestation(m);
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "ShowHidden";
    }
}
ShowHidden["__class"] = "com.vzome.core.edits.ShowHidden";
