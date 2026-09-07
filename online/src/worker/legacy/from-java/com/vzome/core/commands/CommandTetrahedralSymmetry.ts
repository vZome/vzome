import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AttributeMap } from "./AttributeMap.js";
import { CommandSymmetry } from "./CommandSymmetry.js";
import { CommandTransform } from "./CommandTransform.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { SymmetryTransformation } from "../construction/SymmetryTransformation.js";
import { Transformation } from "../construction/Transformation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

/**
 * @author Scott Vorthmann
 * @param {*} symmetry
 * @class
 * @extends CommandSymmetry
 */
export class CommandTetrahedralSymmetry extends CommandSymmetry {
    public static SUBGROUP_ATTR_NAME: string = "symmetry.permutations";

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandTetrahedralSymmetry.ATTR_SIGNATURE == null) { CommandTetrahedralSymmetry.ATTR_SIGNATURE = [[CommandTransform.SYMMETRY_CENTER_ATTR_NAME, Point], [CommandTransform.SYMMETRY_GROUP_ATTR_NAME, "com.vzome.core.math.symmetry.Symmetry"], [CommandTetrahedralSymmetry.SUBGROUP_ATTR_NAME, (<any>[].constructor)]]; }  return CommandTetrahedralSymmetry.ATTR_SIGNATURE; }

    public constructor(symmetry: Symmetry = null) {
        super(symmetry);
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTetrahedralSymmetry.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const center: Point = this.setSymmetry(attributes);
        const closure: number[] = this.mSymmetry.subgroup(Symmetry.TETRAHEDRAL);
        const params: Construction[] = parameters.getConstructions();
        const output: ConstructionList = new ConstructionList();
        for(let index = 0; index < params.length; index++) {
            let param = params[index];
            {
                output.addConstruction(param);
            }
        }
        for(let i: number = 1; i < closure.length; i++) {{
            const transform: Transformation = new SymmetryTransformation(this.mSymmetry, closure[i], center);
            output.addAll(this.transform(params, transform, effects));
        };}
        return output;
    }
}
CommandTetrahedralSymmetry["__class"] = "com.vzome.core.commands.CommandTetrahedralSymmetry";
CommandTetrahedralSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];
