import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Line } from "../construction/Line.js";
import { LineExtensionOfSegment } from "../construction/LineExtensionOfSegment.js";
import { LinePlaneIntersectionPoint } from "../construction/LinePlaneIntersectionPoint.js";
import { Plane } from "../construction/Plane.js";
import { PlaneExtensionOfPolygon } from "../construction/PlaneExtensionOfPolygon.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { Segment } from "../construction/Segment.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandLinePlaneIntersect extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandLinePlaneIntersect.PARAM_SIGNATURE == null) { CommandLinePlaneIntersect.PARAM_SIGNATURE = [["panel", Polygon], ["segment", Segment]]; }  return CommandLinePlaneIntersect.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandLinePlaneIntersect.ATTR_SIGNATURE == null) { CommandLinePlaneIntersect.ATTR_SIGNATURE = []; }  return CommandLinePlaneIntersect.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandLinePlaneIntersect.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandLinePlaneIntersect.ATTR_SIGNATURE_$LI$();
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
        if (parameters == null || parameters.size() !== 2)throw new Command.Failure("Intersection requires a panel and a strut.");
        try {
            let panel: Polygon;
            let segment: Segment;
            const first: Construction = parameters.get(0);
            if (first != null && first instanceof <any>Polygon){
                panel = <Polygon>first;
                segment = <Segment>parameters.get(1);
            } else {
                segment = <Segment>first;
                panel = <Polygon>parameters.get(1);
            }
            const plane: Plane = new PlaneExtensionOfPolygon(panel);
            const line: Line = new LineExtensionOfSegment(segment);
            const point: Point = new LinePlaneIntersectionPoint(plane, line);
            result.addConstruction(point);
            effects['constructionAdded$com_vzome_core_construction_Construction'](point);
        } catch(e) {
            throw new Command.Failure("Intersection requires a panel and a strut.");
        }
        return result;
    }

    constructor() {
        super();
    }
}
CommandLinePlaneIntersect["__class"] = "com.vzome.core.commands.CommandLinePlaneIntersect";
CommandLinePlaneIntersect["__interfaces"] = ["com.vzome.core.commands.Command"];
