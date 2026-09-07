import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CentroidPoint } from "../construction/CentroidPoint.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandCentroid extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandCentroid.PARAM_SIGNATURE == null) { CommandCentroid.PARAM_SIGNATURE = [[Command.GENERIC_PARAM_NAME, Point]]; }  return CommandCentroid.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandCentroid.ATTR_SIGNATURE == null) { CommandCentroid.ATTR_SIGNATURE = []; }  return CommandCentroid.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandCentroid.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandCentroid.ATTR_SIGNATURE_$LI$();
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
        if (parameters == null || parameters.size() === 0)throw new Command.Failure("Select two or more balls to compute their centroid.");
        const params: Construction[] = parameters.getConstructions();
        const verticesList: java.util.List<Point> = <any>(new java.util.ArrayList<any>());
        for(let index = 0; index < params.length; index++) {
            let param = params[index];
            {
                if (param != null && param instanceof <any>Point){
                    verticesList.add(<Point>param);
                }
            }
        }
        if (verticesList.isEmpty())throw new Command.Failure("Select two or more balls to compute their centroid.");
        const points: Point[] = [];
        const centroid: CentroidPoint = new CentroidPoint(verticesList.toArray<any>(points));
        effects['constructionAdded$com_vzome_core_construction_Construction'](centroid);
        result.addConstruction(centroid);
        return result;
    }

    constructor() {
        super();
    }
}
CommandCentroid["__class"] = "com.vzome.core.commands.CommandCentroid";
CommandCentroid["__interfaces"] = ["com.vzome.core.commands.Command"];
