import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AttributeMap } from "./AttributeMap.js";
import { CommandTransform } from "./CommandTransform.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { XmlSymmetryFormat } from "./XmlSymmetryFormat.js";
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
 * @extends CommandTransform
 */
export class CommandSymmetry extends CommandTransform {
    mSymmetry: Symmetry;

    public constructor(symmetry?: any) {
        if (((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null)) {
            let __args = arguments;
            super();
            if (this.mSymmetry === undefined) { this.mSymmetry = null; } 
            this.mSymmetry = symmetry;
        } else if (symmetry === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let symmetry: any = null;
                super();
                if (this.mSymmetry === undefined) { this.mSymmetry = null; } 
                this.mSymmetry = symmetry;
            }
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTransform.GROUP_ATTR_SIGNATURE_$LI$();
    }

    setSymmetry(attributes: AttributeMap): Point {
        if (this.mSymmetry == null)this.mSymmetry = <Symmetry><any>attributes.get(CommandTransform.SYMMETRY_GROUP_ATTR_NAME); else if (!attributes.containsKey(CommandTransform.SYMMETRY_GROUP_ATTR_NAME))attributes.put(CommandTransform.SYMMETRY_GROUP_ATTR_NAME, this.mSymmetry);
        if (this.mSymmetry == null)throw new java.lang.IllegalStateException("null symmetry no longer supported");
        const center: Point = <Point>attributes.get(CommandTransform.SYMMETRY_CENTER_ATTR_NAME);
        return center;
    }

    /**
     * 
     * @param {AttributeMap} attributes
     * @param {XmlSaveFormat} format
     */
    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        if (!attributes.containsKey(CommandTransform.SYMMETRY_GROUP_ATTR_NAME)){
            const icosahedralSymmetry: Symmetry = (<XmlSymmetryFormat>format).parseSymmetry("icosahedral");
            attributes.put(CommandTransform.SYMMETRY_GROUP_ATTR_NAME, icosahedralSymmetry);
        }
        super.setFixedAttributes(attributes, format);
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
        const params: Construction[] = parameters.getConstructions();
        const output: ConstructionList = new ConstructionList();
        for(let index = 0; index < params.length; index++) {
            let param = params[index];
            {
                output.addConstruction(param);
            }
        }
        for(let i: number = 1; i < this.mSymmetry.getChiralOrder(); i++) {{
            const transform: Transformation = new SymmetryTransformation(this.mSymmetry, i, center);
            output.addAll(this.transform(params, transform, effects));
        };}
        return output;
    }
}
CommandSymmetry["__class"] = "com.vzome.core.commands.CommandSymmetry";
CommandSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];
