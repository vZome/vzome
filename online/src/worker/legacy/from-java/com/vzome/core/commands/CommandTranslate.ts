import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandTransform } from "./CommandTransform.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Segment } from "../construction/Segment.js";
import { Transformation } from "../construction/Transformation.js";
import { Translation } from "../construction/Translation.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends CommandTransform
 */
export class CommandTranslate extends CommandTransform {
    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const norm: Segment = <Segment>attributes.get(CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
        if (norm == null){
            throw new Command.Failure("no symmetry axis provided");
        }
        const params: Construction[] = parameters.getConstructions();
        const field: AlgebraicField = norm.getField();
        const offset: AlgebraicVector = field.projectTo3d(norm.getOffset(), true);
        const transform: Transformation = new Translation(offset);
        return this.transform(params, transform, effects);
    }

    constructor() {
        super();
    }
}
CommandTranslate["__class"] = "com.vzome.core.commands.CommandTranslate";
CommandTranslate["__interfaces"] = ["com.vzome.core.commands.Command"];
