import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { Axis } from "../math/symmetry/Axis.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Strut } from "../model/Strut.js";
import { SymmetryTool } from "./SymmetryTool.js";

export class OctahedralToolFactory extends AbstractToolFactory {
    static ID: string = "octahedral";

    static LABEL: string = "Create an octahedral symmetry tool";

    static TOOLTIP2: string = "<p>Each tool produces up to 23 copies of the input<br>selection, using the rotation symmetries of a<br>cube or octahedron.  To create a tool, select a<br>ball that defines the center of symmetry.<br><br>Combine with a point reflection tool to achieve<br>all 48 symmetries of the octahedron, including<br>reflections.<br></p>";

    public constructor(tools: ToolsModel, symmetry: Symmetry, id: string = OctahedralToolFactory.ID, label: string = OctahedralToolFactory.LABEL, tooltip: string = OctahedralToolFactory.TOOLTIP2) {
        super(tools, symmetry, id, label, tooltip);
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
        return (total === 1 && balls === 1) || (total === 2 && balls === 1 && struts === 1);
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
        const symmetry: Symmetry = this.getSymmetry();
        const total: number = this.getSelection().size();
        if (symmetry != null && symmetry instanceof <any>IcosahedralSymmetry){
            if (total !== 2)return false;
            for(let index=selection.iterator();index.hasNext();) {
                let man = index.next();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const zone: Axis = symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector']((<Strut><any>man).getOffset());
                    if (zone == null)return false;
                    switch((zone.getDirection().getName())) {
                    case "blue":
                    case "green":
                        return true;
                    default:
                        return false;
                    }
                }
            }
            return false;
        } else {
            return total === 1;
        }
    }
}
OctahedralToolFactory["__class"] = "com.vzome.core.tools.OctahedralToolFactory";
OctahedralToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
