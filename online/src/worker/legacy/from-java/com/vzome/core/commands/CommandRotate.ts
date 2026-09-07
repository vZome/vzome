import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandSymmetry } from "./CommandSymmetry.js";
import { CommandTransform } from "./CommandTransform.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SymmetryTransformation } from "../construction/SymmetryTransformation.js";
import { Transformation } from "../construction/Transformation.js";
import { Axis } from "../math/symmetry/Axis.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends CommandSymmetry
 */
export class CommandRotate extends CommandSymmetry {
    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const center: Point = this.setSymmetry(attributes);
        const norm: Segment = <Segment>attributes.get(CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
        if (norm == null){
            throw new Command.Failure("no symmetry axis provided");
        }
        const params: Construction[] = parameters.getConstructions();
        const output: ConstructionList = new ConstructionList();
        let vector: AlgebraicVector = norm.getOffset();
        vector = norm.getField().projectTo3d(vector, true);
        const axis: Axis = this.mSymmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
        const rotation: number = axis.getRotation();
        const transform: Transformation = new SymmetryTransformation(this.mSymmetry, rotation, center);
        effects['constructionAdded$com_vzome_core_construction_Construction'](transform);
        output.addAll(this.transform(params, transform, effects));
        return output;
    }

    constructor() {
        super();
    }
}
CommandRotate["__class"] = "com.vzome.core.commands.CommandRotate";
CommandRotate["__interfaces"] = ["com.vzome.core.commands.Command"];
