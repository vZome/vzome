import { Command } from "../commands/Command.js";
import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";

export class ValidateSelection extends ChangeSelection {
    /**
     * 
     */
    public perform() {
        if (this.mSelection.size() === 0)throw new Command.Failure("selection is empty");
    }

    public constructor(editor: EditorModel) {
        super(editor.getSelection());
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "ValidateSelection";
    }
}
ValidateSelection["__class"] = "com.vzome.core.edits.ValidateSelection";
