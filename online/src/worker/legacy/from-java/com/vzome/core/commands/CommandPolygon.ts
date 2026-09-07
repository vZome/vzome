import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";

export class CommandPolygon extends AbstractCommand {
    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandPolygon.PARAM_SIGNATURE == null) { CommandPolygon.PARAM_SIGNATURE = [[Command.GENERIC_PARAM_NAME, Point]]; }  return CommandPolygon.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandPolygon.ATTR_SIGNATURE == null) { CommandPolygon.ATTR_SIGNATURE = []; }  return CommandPolygon.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandPolygon.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandPolygon.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {boolean}
     */
    public ordersSelection(): boolean {
        return true;
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attrs
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attrs: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const points: java.util.List<Point> = <any>(new java.util.ArrayList<any>());
        {
            let array = parameters.getConstructions();
            for(let index = 0; index < array.length; index++) {
                let param = array[index];
                {
                    if (param != null && param instanceof <any>Point){
                        points.add(<Point>param);
                    }
                }
            }
        }
        let errorMsg: string = null;
        if (points.size() < 3){
            errorMsg = "A polygon requires at least three vertices.";
        } else if (points.get(0).is3d() && points.get(1).is3d() && points.get(1).is3d()){
            const normal: AlgebraicVector = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(points.get(0).getLocation(), points.get(1).getLocation(), points.get(2).getLocation());
            if (normal.isOrigin()){
                errorMsg = "First 3 points cannot be collinear.";
            } else {
                let base: AlgebraicVector = null;
                for(let index=points.iterator();index.hasNext();) {
                    let point = index.next();
                    {
                        if (base == null){
                            base = point.getLocation();
                        } else {
                            if (!point.getLocation().minus(base).dot(normal).isZero()){
                                errorMsg = "Points are not coplanar.";
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (errorMsg != null && attrs.get(Command.LOADING_FROM_FILE) == null){
            throw new Command.Failure(errorMsg);
        }
        const poly: PolygonFromVertices = new PolygonFromVertices(points);
        if (errorMsg != null){
            poly.setFailed();
        } else {
            effects['constructionAdded$com_vzome_core_construction_Construction'](poly);
        }
        const result: ConstructionList = new ConstructionList();
        result.addConstruction(poly);
        return result;
    }

    constructor() {
        super();
    }
}
CommandPolygon["__class"] = "com.vzome.core.commands.CommandPolygon";
CommandPolygon["__interfaces"] = ["com.vzome.core.commands.Command"];
