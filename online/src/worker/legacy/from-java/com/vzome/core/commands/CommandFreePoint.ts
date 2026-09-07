import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";

/**
 * @author Scott Vorthmann
 * @class
 * @extends AbstractCommand
 */
export class CommandFreePoint extends AbstractCommand {
    static PARAMS: any[][]; public static PARAMS_$LI$(): any[][] { if (CommandFreePoint.PARAMS == null) { CommandFreePoint.PARAMS = []; }  return CommandFreePoint.PARAMS; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandFreePoint.PARAMS_$LI$();
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
        const result: ConstructionList = new ConstructionList();
        const loc: AlgebraicVector = <AlgebraicVector>attributes.get("where");
        const pt2: Point = new FreePoint(loc);
        effects['constructionAdded$com_vzome_core_construction_Construction'](pt2);
        result.addConstruction(pt2);
        return result;
    }

    constructor() {
        super();
    }
}
CommandFreePoint["__class"] = "com.vzome.core.commands.CommandFreePoint";
CommandFreePoint["__interfaces"] = ["com.vzome.core.commands.Command"];
