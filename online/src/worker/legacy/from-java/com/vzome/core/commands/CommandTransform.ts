import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { Transformation } from "../construction/Transformation.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export abstract class CommandTransform extends AbstractCommand {
    /**
     * 
     * @param {AttributeMap} attributes
     * @param {XmlSaveFormat} format
     */
    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        if (format.getScale() !== 0)attributes.put(CommandTransform.SCALE_ATTR_NAME, format.getScale());
        super.setFixedAttributes(attributes, format);
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        return null;
    }

    public static SYMMETRY_GROUP_ATTR_NAME: string = "symmetry.group";

    public static SYMMETRY_CENTER_ATTR_NAME: string = "symmetry.center";

    public static SYMMETRY_AXIS_ATTR_NAME: string = "symmetry.axis.segment";

    public static SCALE_ATTR_NAME: string = "scale.factor";

    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandTransform.PARAM_SIGNATURE == null) { CommandTransform.PARAM_SIGNATURE = [[Command.GENERIC_PARAM_NAME, Construction]]; }  return CommandTransform.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTransform.ATTR_SIGNATURE == null) { CommandTransform.ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, Point]]; }  return CommandTransform.ATTR_SIGNATURE; }

    static AXIS_ATTR_SIGNATURE: any[][]; public static AXIS_ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTransform.AXIS_ATTR_SIGNATURE == null) { CommandTransform.AXIS_ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, Point], [CommandTransform.SYMMETRY_AXIS_ATTR_NAME, Segment]]; }  return CommandTransform.AXIS_ATTR_SIGNATURE; }

    static GROUP_ATTR_SIGNATURE: any[][]; public static GROUP_ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTransform.GROUP_ATTR_SIGNATURE == null) { CommandTransform.GROUP_ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, Point], [CommandTransform.SYMMETRY_GROUP_ATTR_NAME, "com.vzome.core.math.symmetry.Symmetry"]]; }  return CommandTransform.GROUP_ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandTransform.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTransform.AXIS_ATTR_SIGNATURE_$LI$();
    }

    transform(params: Construction[], transform: Transformation, effects: ConstructionChanges): ConstructionList {
        const output: ConstructionList = new ConstructionList();
        effects['constructionAdded$com_vzome_core_construction_Construction'](transform);
        for(let index = 0; index < params.length; index++) {
            let param = params[index];
            {
                const result: Construction = transform.transform$com_vzome_core_construction_Construction(param);
                if (result == null)continue;
                effects['constructionAdded$com_vzome_core_construction_Construction'](result);
                output.addConstruction(result);
            }
        }
        return output;
    }

    constructor() {
        super();
    }
}
CommandTransform["__class"] = "com.vzome.core.commands.CommandTransform";
CommandTransform["__interfaces"] = ["com.vzome.core.commands.Command"];
