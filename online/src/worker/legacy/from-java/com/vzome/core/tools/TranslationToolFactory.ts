import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { TranslationTool } from "./TranslationTool.js";

export class TranslationToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel) {
        super(tools, null, TranslationTool.ID, TranslationTool.LABEL, TranslationTool.TOOLTIP);
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
        return (total === 2 && balls === 2);
    }

    /**
     * 
     * @param {string} id
     * @return {Tool}
     */
    public createToolInternal(id: string): Tool {
        return new TranslationTool(id, this.getToolsModel());
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
TranslationToolFactory["__class"] = "com.vzome.core.tools.TranslationToolFactory";
TranslationToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
