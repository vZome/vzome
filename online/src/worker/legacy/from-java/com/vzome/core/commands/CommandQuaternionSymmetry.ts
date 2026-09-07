import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { AttributeMap } from "./AttributeMap.js";
import { CommandTransform } from "./CommandTransform.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { XmlSymmetryFormat } from "./XmlSymmetryFormat.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { PointRotated4D } from "../construction/PointRotated4D.js";
import { Polygon } from "../construction/Polygon.js";
import { PolygonRotated4D } from "../construction/PolygonRotated4D.js";
import { Segment } from "../construction/Segment.js";
import { SegmentRotated4D } from "../construction/SegmentRotated4D.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";

/**
 * @author Scott Vorthmann
 * @param {QuaternionicSymmetry} left
 * @param {QuaternionicSymmetry} right
 * @class
 * @extends CommandTransform
 */
export class CommandQuaternionSymmetry extends CommandTransform {
    /**
     * 
     * @param {AttributeMap} attributes
     * @param {XmlSaveFormat} format
     */
    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        super.setFixedAttributes(attributes, format);
        if (!attributes.containsKey(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$())){
            this.mLeft = (<XmlSymmetryFormat>format).getQuaternionicSymmetry("H_4");
            attributes.put(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$(), this.mLeft);
        }
        if (!attributes.containsKey(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME)){
            this.mRight = (<XmlSymmetryFormat>format).getQuaternionicSymmetry("H_4");
            attributes.put(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME, this.mRight);
        }
    }

    public static LEFT_SYMMETRY_GROUP_ATTR_NAME: string; public static LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$(): string { if (CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME == null) { CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME = CommandTransform.SYMMETRY_GROUP_ATTR_NAME; }  return CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME; }

    public static RIGHT_SYMMETRY_GROUP_ATTR_NAME: string = "right.symmetry.group";

    /*private*/ mLeft: QuaternionicSymmetry;

    /*private*/ mRight: QuaternionicSymmetry;

    public constructor(left?: any, right?: any) {
        if (((left != null && left instanceof <any>QuaternionicSymmetry) || left === null) && ((right != null && right instanceof <any>QuaternionicSymmetry) || right === null)) {
            let __args = arguments;
            super();
            if (this.mLeft === undefined) { this.mLeft = null; } 
            if (this.mRight === undefined) { this.mRight = null; } 
            this.mLeft = left;
            this.mRight = right;
        } else if (left === undefined && right === undefined) {
            let __args = arguments;
            super();
            if (this.mLeft === undefined) { this.mLeft = null; } 
            if (this.mRight === undefined) { this.mRight = null; } 
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTransform.GROUP_ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        if (this.mLeft == null)this.mLeft = <QuaternionicSymmetry>attributes.get(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$()); else if (!attributes.containsKey(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$()))attributes.put(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$(), this.mLeft);
        if (this.mRight == null)this.mRight = <QuaternionicSymmetry>attributes.get(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME); else if (!attributes.containsKey(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME))attributes.put(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME, this.mRight);
        const leftRoots: Quaternion[] = this.mLeft.getRoots();
        const rightRoots: Quaternion[] = this.mRight.getRoots();
        const params: Construction[] = parameters.getConstructions();
        const output: ConstructionList = new ConstructionList();
        for(let index = 0; index < params.length; index++) {
            let param = params[index];
            {
                output.addConstruction(param);
            }
        }
        for(let index = 0; index < leftRoots.length; index++) {
            let leftRoot = leftRoots[index];
            {
                for(let index1 = 0; index1 < rightRoots.length; index1++) {
                    let rightRoot = rightRoots[index1];
                    {
                        for(let index2 = 0; index2 < params.length; index2++) {
                            let param = params[index2];
                            {
                                let result: Construction = null;
                                if (param != null && param instanceof <any>Point){
                                    result = new PointRotated4D(leftRoot, rightRoot, <Point>param);
                                } else if (param != null && param instanceof <any>Segment){
                                    result = new SegmentRotated4D(leftRoot, rightRoot, <Segment>param);
                                } else if (param != null && param instanceof <any>Polygon){
                                    result = new PolygonRotated4D(leftRoot, rightRoot, <Polygon>param);
                                } else {
                                }
                                if (result == null)continue;
                                effects['constructionAdded$com_vzome_core_construction_Construction'](result);
                                output.addConstruction(result);
                            }
                        }
                    }
                }
            }
        }
        return output;
    }
}
CommandQuaternionSymmetry["__class"] = "com.vzome.core.commands.CommandQuaternionSymmetry";
CommandQuaternionSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];
