import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { ChangeOfBasis } from "../construction/ChangeOfBasis.js";
import { Segment } from "../construction/Segment.js";
import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { LinearMapTool } from "./LinearMapTool.js";

export class LinearMapToolFactory extends AbstractToolFactory {
    /*private*/ originalScaling: boolean;

    public constructor(tools: ToolsModel, symmetry: Symmetry, originalScaling: boolean) {
        super(tools, symmetry, LinearMapTool.CATEGORY, LinearMapTool.LABEL, LinearMapTool.TOOLTIP);
        if (this.originalScaling === undefined) { this.originalScaling = false; }
        this.originalScaling = originalScaling;
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
        return (total === 7 && balls === 1 && struts === 6) || (total === 4 && balls === 1 && struts === 3);
    }

    /**
     * 
     * @param {string} id
     * @return {Tool}
     */
    public createToolInternal(id: string): Tool {
        return new LinearMapTool(id, this.getToolsModel(), this.originalScaling);
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
        let index: number = 0;
        const segments: Segment[] = [null, null, null, null, null, null];
        for(let index1=selection.iterator();index1.hasNext();) {
            let man = index1.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))segments[index++] = <Segment>man.getFirstConstruction();
            }
        }
        let c1: AlgebraicVector = ChangeOfBasis.findCommonVertex(segments[0], segments[1]);
        let c2: AlgebraicVector = ChangeOfBasis.findCommonVertex(segments[2], segments[1]);
        if (c1 == null || c2 == null || !c1.equals(c2))return false;
        if (index === 3)return true;
        c1 = ChangeOfBasis.findCommonVertex(segments[3], segments[4]);
        c2 = ChangeOfBasis.findCommonVertex(segments[5], segments[4]);
        if (c1 == null || c2 == null || !c1.equals(c2))return false;
        return true;
    }
}
LinearMapToolFactory["__class"] = "com.vzome.core.tools.LinearMapToolFactory";
LinearMapToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
