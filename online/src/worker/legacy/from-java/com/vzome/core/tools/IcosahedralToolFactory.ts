import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { SymmetryTool } from "./SymmetryTool.js";

export class IcosahedralToolFactory extends AbstractToolFactory {
    static ID: string = "icosahedral";

    static LABEL: string = "Create an icosahedral symmetry tool";

    static TOOLTIP: string = "<p>Each tool produces up to 59 copies of the input<br>selection, using the rotation symmetries of an<br>icosahedron.  To create a tool, select a single<br>ball that defines the center of symmetry.<br><br>Combine with a point reflection tool to achieve<br>all 120 symmetries of the icosahedron, including<br>reflections.<br></p>";

    public constructor(tools: ToolsModel, symmetry: IcosahedralSymmetry) {
        super(tools, symmetry, IcosahedralToolFactory.ID, IcosahedralToolFactory.LABEL, IcosahedralToolFactory.TOOLTIP);
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
IcosahedralToolFactory["__class"] = "com.vzome.core.tools.IcosahedralToolFactory";
IcosahedralToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
