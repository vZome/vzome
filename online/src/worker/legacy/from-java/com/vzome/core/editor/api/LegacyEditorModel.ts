import { Construction } from "../../construction/Construction.js";
import { ImplicitSymmetryParameters } from "./ImplicitSymmetryParameters.js";

export interface LegacyEditorModel extends ImplicitSymmetryParameters {
    addFailedConstruction(cons: Construction);

    hasFailedConstruction(cons: Construction): boolean;
}
