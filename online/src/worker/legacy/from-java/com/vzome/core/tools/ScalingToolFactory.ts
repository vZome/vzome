import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AbstractToolFactory } from "../editor/AbstractToolFactory.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Selection } from "../editor/api/Selection.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Strut } from "../model/Strut.js";
import { ScalingTool } from "./ScalingTool.js";

export class ScalingToolFactory extends AbstractToolFactory {
    public constructor(tools: ToolsModel, symmetry: Symmetry) {
        super(tools, symmetry, ScalingTool.ID, ScalingTool.LABEL, ScalingTool.TOOLTIP);
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
        return (total === 3 && balls === 1 && struts === 2);
    }

    /**
     * 
     * @param {string} id
     * @return {Tool}
     */
    public createToolInternal(id: string): Tool {
        const tool: ScalingTool = new ScalingTool(id, this.getSymmetry(), this.getToolsModel());
        let scalePower: number = 0;
        switch((id)) {
        case "scaling.builtin/scale up":
            scalePower = 1;
            break;
        case "scaling.builtin/scale down":
            scalePower = -1;
            break;
        default:
            return tool;
        }
        const field: AlgebraicField = this.getToolsModel().getEditorModel().getRealizedModel().getField();
        tool.setScaleFactor(field['createPower$int'](scalePower));
        return tool;
    }

    /**
     * 
     * @param {*} selection
     * @return {boolean}
     */
    bindParameters(selection: Selection): boolean {
        const symmetry: Symmetry = this.getSymmetry();
        let offset1: AlgebraicVector = null;
        let offset2: AlgebraicVector = null;
        for(let index=selection.iterator();index.hasNext();) {
            let man = index.next();
            if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                const strut: Strut = <Strut><any>man;
                if (offset1 == null)offset1 = strut.getOffset(); else offset2 = strut.getOffset();
            }
        }
        const zone1: Axis = symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](offset1);
        const zone2: Axis = symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](offset2);
        if (zone1 == null || zone2 == null)return false;
        const orbit1: Direction = zone1.getDirection();
        const orbit2: Direction = zone2.getDirection();
        if (orbit1 !== orbit2)return false;
        if (orbit1.isAutomatic())return false;
        const l1: AlgebraicNumber = zone1.getLength(offset1);
        const l2: AlgebraicNumber = zone2.getLength(offset2);
        if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(l1,l2)))return false;
        return true;
    }
}
ScalingToolFactory["__class"] = "com.vzome.core.tools.ScalingToolFactory";
ScalingToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
