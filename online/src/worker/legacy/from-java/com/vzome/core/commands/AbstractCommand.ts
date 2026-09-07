import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { Axis } from "../math/symmetry/Axis.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export abstract class AbstractCommand implements Command {
    /**
     * This default behavior deserializes in the old way, before XmlSaveFormat .COMPACTED_COMMAND_EDITS
     * @param attributes
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @return
     * @return {AttributeMap}
     */
    public setXml(xml: Element, format: XmlSaveFormat): AttributeMap {
        const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
        this.setFixedAttributes(attrs, format);
        return attrs;
    }

    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        attributes.put(Command.FIELD_ATTR_NAME, format.getField());
    }

    /**
     * This default behavior serializes in the old way, before XmlSaveFormat .COMPACTED_COMMAND_EDITS
     * @param {AttributeMap} attributes
     * @return
     * @param {*} result
     */
    public getXml(result: Element, attributes: AttributeMap) {
        if (attributes == null)return;
        for(let index=attributes.keySet().iterator();index.hasNext();) {
            let key = index.next();
            {
                if (key === Command.FIELD_ATTR_NAME)continue;
                if (key === "symmetry.center")continue;
                if (key === "symmetry.axis.segment")continue;
                if (key === "org.vorthmann.zome.commands.CommandImportVEFData.field")continue;
                const value: any = attributes.get(key);
                if (value != null && value instanceof <any>IcosahedralSymmetry)continue;
                AbstractCommand.saveCommandAttribute(result, key, value);
            }
        }
    }

    public static saveCommandAttribute(command: Element, attrName: string, value: any) {
        const doc: Document = command.getOwnerDocument();
        let valElem: Element = null;
        if (value != null && value instanceof <any>Array && (value.length == 0 || value[0] == null ||typeof value[0] === 'number')){
            const v: number[] = <number[]>value;
            valElem = command.getOwnerDocument().createElement("RationalVector");
            let allOnes: boolean = true;
            let allZeros: boolean = true;
            for(let i: number = 0; i < (v.length / 2|0); i++) {{
                allZeros = allZeros && (v[2 * i] === 0);
                allOnes = allOnes && (v[2 * i + 1] === 1);
            };}
            if (!allZeros){
                const numerators: java.lang.StringBuffer = new java.lang.StringBuffer();
                for(let i: number = 0; i < (v.length / 2|0); i++) {{
                    if (i > 0)numerators.append(" ");
                    numerators.append(v[2 * i]);
                };}
                DomUtils.addAttribute(valElem, "nums", numerators.toString());
                if (!allOnes){
                    const denominators: java.lang.StringBuffer = new java.lang.StringBuffer();
                    for(let i: number = 0; i < (v.length / 2|0); i++) {{
                        if (i > 0)denominators.append(" ");
                        denominators.append(v[2 * i + 1]);
                    };}
                    DomUtils.addAttribute(valElem, "denoms", denominators.toString());
                }
            }
        } else if (value != null && value instanceof <any>Axis){
            valElem = doc.createElement("Axis");
            (<Axis>value).getXML(valElem);
        } else if (typeof value === 'boolean'){
            valElem = doc.createElement("Boolean");
            DomUtils.addAttribute(valElem, "value", (<boolean>value).toString());
        } else if (typeof value === 'number'){
            valElem = doc.createElement("Integer");
            DomUtils.addAttribute(valElem, "value", (<number>value).toString());
        } else if (value != null && value instanceof <any>Construction){
            valElem = (<Construction>value).getXml(command.getOwnerDocument());
        } else if (typeof value === 'string'){
            valElem = doc.createElement("String");
            const str: string = XmlSaveFormat.escapeNewlines(<string>value);
            valElem.appendChild(doc.createTextNode(str));
        } else if (value != null && value instanceof <any>QuaternionicSymmetry){
            valElem = doc.createElement("QuaternionicSymmetry");
            DomUtils.addAttribute(valElem, "name", (<QuaternionicSymmetry>value).getName());
        } else if (value != null && (value.constructor != null && value.constructor["__interfaces"] != null && value.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)){
            valElem = doc.createElement("Symmetry");
            DomUtils.addAttribute(valElem, "name", (<Symmetry><any>value).getName());
        } else if (value == null){
            valElem = doc.createElement("Null");
        } else {
            throw new java.lang.IllegalStateException("unable to save " + /* getName */(c => typeof c === 'string' ? c : c["__class"] ? c["__class"] : c["name"])((<any>value.constructor)));
        }
        DomUtils.addAttribute(valElem, "attrName", attrName);
        command.appendChild(valElem);
    }

    public attributeIs3D(attrName: string): boolean {
        return true;
    }

    public setQuaternion(offset: AlgebraicVector) {
    }

    public ordersSelection(): boolean {
        return false;
    }

    public abstract apply(parameters?: any, attributes?: any, effects?: any): any;
    public abstract getAttributeSignature(): any;
    public abstract getParameterSignature(): any;
    constructor() {
    }
}
AbstractCommand["__class"] = "com.vzome.core.commands.AbstractCommand";
AbstractCommand["__interfaces"] = ["com.vzome.core.commands.Command"];
