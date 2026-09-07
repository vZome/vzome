import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandRotate } from "./CommandRotate.js";
import { CommandSymmetry } from "./CommandSymmetry.js";
import { CommandTransform } from "./CommandTransform.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Segment } from "../construction/Segment.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

/**
 * @author Scott Vorthmann
 * @param {*} symmetry
 * @class
 * @extends CommandSymmetry
 */
export class CommandAxialSymmetry extends CommandSymmetry {
    public constructor(symmetry: Symmetry = null) {
        super(symmetry);
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        this.setSymmetry(attributes);
        const norm: Segment = <Segment>attributes.get(CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
        if (norm == null){
            throw new Command.Failure("no symmetry axis provided");
        }
        const output: ConstructionList = new ConstructionList();
        let vector: AlgebraicVector = norm.getOffset();
        vector = norm.getField().projectTo3d(vector, true);
        const axis: Axis = this.mSymmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
        const rotation: Permutation = axis.getRotationPermutation();
        if (rotation == null){
            throw new Command.Failure("symmetry axis does not support axial symmetry");
        }
        const order: number = rotation.getOrder();
        const rotate: CommandRotate = new CommandRotate();
        for(let i: number = 1; i < order; i++) {{
            for(let index=parameters.iterator();index.hasNext();) {
                let param = index.next();
                {
                    output.addConstruction(param);
                }
            }
            parameters = rotate.apply(parameters, attributes, effects);
        };}
        for(let index=parameters.iterator();index.hasNext();) {
            let param = index.next();
            {
                output.addConstruction(param);
            }
        }
        return output;
    }
}
CommandAxialSymmetry["__class"] = "com.vzome.core.commands.CommandAxialSymmetry";
CommandAxialSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];
