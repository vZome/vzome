import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandJoinPoints extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandJoinPoints.PARAM_SIGNATURE == null) { CommandJoinPoints.PARAM_SIGNATURE = [["start", Point], ["end", Point]]; }  return CommandJoinPoints.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandJoinPoints.ATTR_SIGNATURE == null) { CommandJoinPoints.ATTR_SIGNATURE = []; }  return CommandJoinPoints.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandJoinPoints.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandJoinPoints.ATTR_SIGNATURE_$LI$();
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
        if (parameters == null || parameters.size() !== 2)throw new Command.Failure("parameters must be two points");
        try {
            const pt1: Point = <Point>parameters.get(0);
            const pt2: Point = <Point>parameters.get(1);
            const segment: Segment = new SegmentJoiningPoints(pt1, pt2);
            result.addConstruction(segment);
            effects['constructionAdded$com_vzome_core_construction_Construction'](segment);
        } catch(e) {
            throw new Command.Failure("parameters must be two points");
        }
        return result;
    }

    constructor() {
        super();
    }
}
CommandJoinPoints["__class"] = "com.vzome.core.commands.CommandJoinPoints";
CommandJoinPoints["__interfaces"] = ["com.vzome.core.commands.Command"];
