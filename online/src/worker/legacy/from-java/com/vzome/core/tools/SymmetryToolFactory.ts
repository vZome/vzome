import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { SymmetryTool } from "./SymmetryTool.js";

export class SymmetryToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel, symmetry: Symmetry) {
        super(tools, symmetry, SymmetryTool.ID, SymmetryTool.LABEL, SymmetryTool.TOOLTIP);
    }

    /**
     * 
     * @param {number} total
     * @param {number} balls
     * @param {number} struts
     * @param {number} panels
     * @return {boolean}
     */
    countsAreValid(total: number, balls: number, struts: number, panels: number): boolean {
        return (total === 1 && balls === 1);
    }

    /**
     * 
     * @param {string} id
     * @return {Tool}
     */
    public createToolInternal(id: string): Tool {
        return new SymmetryTool(id, this.getSymmetry(), this.getToolsModel());
    }

    /**
     * 
     * @param {*} selection
     * @return {boolean}
     */
    bindParameters(selection: Selection): boolean {
        return selection.size() === 1 && (selection.iterator().next() != null && (selection.iterator().next().constructor != null && selection.iterator().next().constructor["__interfaces"] != null && selection.iterator().next().constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0));
    }
}
SymmetryToolFactory["__class"] = "com.vzome.core.tools.SymmetryToolFactory";
SymmetryToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
