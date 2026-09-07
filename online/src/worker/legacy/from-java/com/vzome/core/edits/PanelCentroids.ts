import { FreePoint } from "../construction/FreePoint.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Panel } from "../model/Panel.js";

export class PanelCentroids extends ChangeManifestations {
    public constructor(editorModel: EditorModel) {
        super(editorModel);
    }

    /**
     * 
     */
    public perform() {
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    const panel: Panel = <Panel><any>man;
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new FreePoint(panel.getCentroid())));
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
        return "PanelCentroids";
    }
}
PanelCentroids["__class"] = "com.vzome.core.edits.PanelCentroids";
