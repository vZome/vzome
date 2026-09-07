import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";

export class DeselectAll extends ChangeSelection {
    /**
     * 
     */
    public perform() {
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
            }
        }
        super.perform();
    }

    public constructor(editor: EditorModel) {
        super(editor.getSelection());
    }

    /**
     * 
     * @return {boolean}
     */
    groupingAware(): boolean {
        return true;
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "DeselectAll";
    }
}
DeselectAll["__class"] = "com.vzome.core.edits.DeselectAll";
