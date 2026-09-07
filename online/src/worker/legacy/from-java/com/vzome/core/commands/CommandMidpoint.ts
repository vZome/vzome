import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentMidpoint } from "../construction/SegmentMidpoint.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandMidpoint extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandMidpoint.PARAM_SIGNATURE == null) { CommandMidpoint.PARAM_SIGNATURE = [["segment", Segment]]; }  return CommandMidpoint.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandMidpoint.ATTR_SIGNATURE == null) { CommandMidpoint.ATTR_SIGNATURE = []; }  return CommandMidpoint.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandMidpoint.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandMidpoint.ATTR_SIGNATURE_$LI$();
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
        if (parameters == null || parameters.size() !== 1)throw new Command.Failure("Midpoint can only apply to a single strut.");
        try {
            const segment: Segment = <Segment>parameters.get(0);
            const midpoint: Point = new SegmentMidpoint(segment);
            result.addConstruction(midpoint);
            effects['constructionAdded$com_vzome_core_construction_Construction'](midpoint);
        } catch(e) {
            throw new Command.Failure("Midpoint can only apply to a strut.");
        }
        return result;
    }

    constructor() {
        super();
    }
}
CommandMidpoint["__class"] = "com.vzome.core.commands.CommandMidpoint";
CommandMidpoint["__interfaces"] = ["com.vzome.core.commands.Command"];
