import { Polygon } from "../construction/Polygon.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";

/**
 * Work in progress, to help someone create correctly oriented surfaces for vZome part export,
 * or for StL 3D printing export.
 * 
 * @author Scott Vorthmann
 * @param {*} singlePanel
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 */
export class ReversePanel extends ChangeManifestations {
    /**
     * 
     */
    public perform() {
        if (this.panel != null){
            if (this.mSelection.manifestationSelected(this.panel))this.unselect$com_vzome_core_model_Manifestation(this.panel);
            const polygon: Polygon = <Polygon>this.panel.getFirstConstruction();
            this.unmanifestConstruction(polygon);
        }
        this.redo();
    }

    /*private*/ panel: Panel;

    public constructor(singlePanel: Manifestation, editor: EditorModel) {
        super(editor);
        if (this.panel === undefined) { this.panel = null; }
        if (singlePanel != null)this.panel = <Panel><any>singlePanel; else this.panel = null;
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "ReversePanel";
    }
}
ReversePanel["__class"] = "com.vzome.core.edits.ReversePanel";
