import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { SegmentTauDivision } from "../construction/SegmentTauDivision.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandTauDivision extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandTauDivision.PARAM_SIGNATURE == null) { CommandTauDivision.PARAM_SIGNATURE = [["start", Point], ["end", Point]]; }  return CommandTauDivision.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTauDivision.ATTR_SIGNATURE == null) { CommandTauDivision.ATTR_SIGNATURE = []; }  return CommandTauDivision.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandTauDivision.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTauDivision.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {boolean}
     */
    public ordersSelection(): boolean {
        return true;
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attrs
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attrs: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const result: ConstructionList = new ConstructionList();
        if (parameters == null || parameters.size() !== 2)throw new Command.Failure("Tau division applies to two balls.");
        try {
            const start: Point = <Point>parameters.get(0);
            const end: Point = <Point>parameters.get(1);
            const join: Segment = new SegmentJoiningPoints(start, end);
            const midpoint: Point = new SegmentTauDivision(join);
            result.addConstruction(midpoint);
            effects['constructionAdded$com_vzome_core_construction_Construction'](midpoint);
        } catch(e) {
            throw new Command.Failure("Tau division applies to two balls.");
        }
        return result;
    }

    constructor() {
        super();
    }
}
CommandTauDivision["__class"] = "com.vzome.core.commands.CommandTauDivision";
CommandTauDivision["__interfaces"] = ["com.vzome.core.commands.Command"];
