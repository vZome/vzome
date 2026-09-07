import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { Command } from "../commands/Command.js";
import { SymmetryPerspective } from "./SymmetryPerspective.js";
import { ToolsModel } from "./ToolsModel.js";
import { Symmetries4D } from "../math/symmetry/Symmetries4D.js";

export interface FieldApplication extends Symmetries4D {
    getField(): AlgebraicField;

    getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective>;

    getDefaultSymmetryPerspective(): SymmetryPerspective;

    getSymmetryPerspective(name: string): SymmetryPerspective;

    getName(): string;

    getLabel(): string;

    registerToolFactories(toolFactories: java.util.Map<string, Tool.Factory>, tools: ToolsModel);

    /**
     * These commands should all be symmetry-INDEPENDANT.
     * Contrast with {@code FieldApplication.SymmetryPerspective.getLegacyCommand(action) }.
     * @param {string} action
     * @return
     * @return {*}
     */
    getLegacyCommand(action: string): Command;
}
