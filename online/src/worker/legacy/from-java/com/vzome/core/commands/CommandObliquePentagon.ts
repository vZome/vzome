import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Segment } from "../construction/Segment.js";

export class CommandObliquePentagon extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandObliquePentagon.PARAM_SIGNATURE == null) { CommandObliquePentagon.PARAM_SIGNATURE = [["segment1", Segment], ["segment2", Segment]]; }  return CommandObliquePentagon.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandObliquePentagon.ATTR_SIGNATURE == null) { CommandObliquePentagon.ATTR_SIGNATURE = []; }  return CommandObliquePentagon.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandObliquePentagon.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandObliquePentagon.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        throw new Command.Failure("Oblique pentagon should never be called.");
    }

    constructor() {
        super();
    }
}
CommandObliquePentagon["__class"] = "com.vzome.core.commands.CommandObliquePentagon";
CommandObliquePentagon["__interfaces"] = ["com.vzome.core.commands.Command"];
