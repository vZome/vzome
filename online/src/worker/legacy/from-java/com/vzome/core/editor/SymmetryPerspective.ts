import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Command } from "../commands/Command.js";
import { ToolsModel } from "./ToolsModel.js";
import { Shapes } from "./api/Shapes.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export interface SymmetryPerspective {
    getGeometries(): java.util.List<Shapes>;

    getDefaultGeometry(): Shapes;

    getName(): string;

    getSymmetry(): Symmetry;

    createToolFactories(kind: Tool.Kind, model: ToolsModel): java.util.List<Tool.Factory>;

    predefineTools(kind: Tool.Kind, model: ToolsModel): java.util.List<Tool>;

    /**
     * These commands should all be symmetry-DEPENDANT.
     * Contrast with {@code FieldApplication.getLegacyCommand(action) }.
     * @param {string} action
     * @return
     * @return {*}
     */
    getLegacyCommand(action: string): Command;

    getModelResourcePath(): string;

    orbitIsStandard(orbit: Direction): boolean;

    orbitIsBuildDefault(orbit: Direction): boolean;

    getOrbitUnitLength(orbit: Direction): AlgebraicNumber;

    getLabel(): string;
}
