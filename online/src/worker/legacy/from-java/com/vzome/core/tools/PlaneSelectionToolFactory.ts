import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { PlaneSelectionTool } from "./PlaneSelectionTool.js";

export class PlaneSelectionToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel) {
        super(tools, null, "plane", "", "");
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
        return (total === 3 && balls === 3);
    }

    /**
     * 
     * @param {string} id
     * @return {Tool}
     */
    public createToolInternal(id: string): Tool {
        return new PlaneSelectionTool(id, this.getToolsModel());
    }

    /**
     * 
     * @param {*} selection
     * @return {boolean}
     */
    bindParameters(selection: Selection): boolean {
        return true;
    }
}
PlaneSelectionToolFactory["__class"] = "com.vzome.core.tools.PlaneSelectionToolFactory";
PlaneSelectionToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
