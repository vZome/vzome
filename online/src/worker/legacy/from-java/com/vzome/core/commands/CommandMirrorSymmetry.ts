import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandTransform } from "./CommandTransform.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Plane } from "../construction/Plane.js";
import { PlaneFromNormalSegment } from "../construction/PlaneFromNormalSegment.js";
import { PlaneReflection } from "../construction/PlaneReflection.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { Transformation } from "../construction/Transformation.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends CommandTransform
 */
export class CommandMirrorSymmetry extends CommandTransform {
    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const center: Point = <Point>attributes.get(CommandTransform.SYMMETRY_CENTER_ATTR_NAME);
        const norm: Segment = <Segment>attributes.get(CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
        if (norm == null){
            throw new Command.Failure("no symmetry axis provided");
        }
        const params: Construction[] = parameters.getConstructions();
        const mirror: Plane = new PlaneFromNormalSegment(center, norm);
        effects['constructionAdded$com_vzome_core_construction_Construction'](mirror);
        const transform: Transformation = new PlaneReflection(mirror);
        return this.transform(params, transform, effects);
    }

    constructor() {
        super();
    }
}
CommandMirrorSymmetry["__class"] = "com.vzome.core.commands.CommandMirrorSymmetry";
CommandMirrorSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];
