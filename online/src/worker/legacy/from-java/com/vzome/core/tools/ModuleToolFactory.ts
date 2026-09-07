import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { ModuleTool } from "./ModuleTool.js";

export class ModuleToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel) {
        super(tools, null, ModuleTool.ID, ModuleTool.LABEL, ModuleTool.TOOLTIP);
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
        return (total > 0);
    }

    /**
     * 
     * @param {string} id
     * @return {Tool}
     */
    public createToolInternal(id: string): Tool {
        return new ModuleTool(id, this.getToolsModel());
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
ModuleToolFactory["__class"] = "com.vzome.core.tools.ModuleToolFactory";
ModuleToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
