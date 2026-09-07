import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { InversionTool } from "./InversionTool.js";

export class InversionToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel) {
        super(tools, null, InversionTool.ID, InversionTool.LABEL, InversionTool.TOOLTIP);
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
        return new InversionTool(id, this.getToolsModel());
    }

    /**
     * 
     * @return {Tool}
     */
    public createTool(): Tool {
        const result: Tool = super.createTool();
        result.setCopyColors(false);
        return result;
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
InversionToolFactory["__class"] = "com.vzome.core.tools.InversionToolFactory";
InversionToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
