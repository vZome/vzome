import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { BookmarkTool } from "./BookmarkTool.js";

export class BookmarkToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel) {
        super(tools, null, BookmarkTool.ID, BookmarkTool.LABEL, BookmarkTool.TOOLTIP);
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
        return new BookmarkTool(id, this.getToolsModel());
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
BookmarkToolFactory["__class"] = "com.vzome.core.tools.BookmarkToolFactory";
BookmarkToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
