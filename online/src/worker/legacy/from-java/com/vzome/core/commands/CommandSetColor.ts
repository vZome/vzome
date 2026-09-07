import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";

export class CommandSetColor extends AbstractCommand {
    public static MANIFESTATION_ATTR: string = "manifestation.context";

    public static COLOR_ATTR: string = "color";

    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandSetColor.PARAM_SIGNATURE == null) { CommandSetColor.PARAM_SIGNATURE = []; }  return CommandSetColor.PARAM_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandSetColor.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return null;
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        return parameters;
    }

    constructor() {
        super();
    }
}
CommandSetColor["__class"] = "com.vzome.core.commands.CommandSetColor";
CommandSetColor["__interfaces"] = ["com.vzome.core.commands.Command"];
