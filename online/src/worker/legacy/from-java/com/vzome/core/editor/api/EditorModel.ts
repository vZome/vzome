import { SelectionSummary } from "../SelectionSummary.js";
import { Selection } from "./Selection.js";
import { SymmetryAware } from "./SymmetryAware.js";
import { RealizedModel } from "../../model/RealizedModel.js";

export interface EditorModel extends SymmetryAware {
    getRealizedModel(): RealizedModel;

    getSelection(): Selection;

    addSelectionSummaryListener(listener: SelectionSummary.Listener);
}
