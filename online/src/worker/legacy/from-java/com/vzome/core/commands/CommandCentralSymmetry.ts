import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AttributeMap } from "./AttributeMap.js";
import { CommandTransform } from "./CommandTransform.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { PointReflection } from "../construction/PointReflection.js";
import { Transformation } from "../construction/Transformation.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends CommandTransform
 */
export class CommandCentralSymmetry extends CommandTransform {
    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTransform.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const output: ConstructionList = new ConstructionList();
        const center: Point = <Point>attributes.get(CommandTransform.SYMMETRY_CENTER_ATTR_NAME);
        const params: Construction[] = parameters.getConstructions();
        for(let index = 0; index < params.length; index++) {
            let param = params[index];
            {
                output.addConstruction(param);
            }
        }
        const transform: Transformation = new PointReflection(center);
        effects['constructionAdded$com_vzome_core_construction_Construction'](transform);
        return this.transform(params, transform, effects);
    }

    constructor() {
        super();
    }
}
CommandCentralSymmetry["__class"] = "com.vzome.core.commands.CommandCentralSymmetry";
CommandCentralSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];
