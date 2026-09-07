import { Element } from "../../../org/w3c/dom/Element.js";
import { Node } from "../../../org/w3c/dom/Node.js";
import { NodeList } from "../../../org/w3c/dom/NodeList.js";

export class DomUtils {
    public static addAttribute(elem: Element, name: string, value: string) {
        elem.setAttribute(name, value);
    }

    public static getFirstChildElement$org_w3c_dom_Element$java_lang_String(elem: Element, name: string): Element {
        const elems: NodeList = elem.getElementsByTagName(name);
        return <Element><any>elems.item(0);
    }

    public static getFirstChildElement(elem?: any, name?: any): Element {
        if (((elem != null && (elem.constructor != null && elem.constructor["__interfaces"] != null && elem.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || elem === null) && ((typeof name === 'string') || name === null)) {
            return <any>DomUtils.getFirstChildElement$org_w3c_dom_Element$java_lang_String(elem, name);
        } else if (((elem != null && (elem.constructor != null && elem.constructor["__interfaces"] != null && elem.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || elem === null) && name === undefined) {
            return <any>DomUtils.getFirstChildElement$org_w3c_dom_Element(elem);
        } else throw new Error('invalid overload');
    }

    public static preserveSpace(contentElem: Element) {
        contentElem.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
    }

    public static getFirstChildElement$org_w3c_dom_Element(parent: Element): Element {
        const children: NodeList = parent.getChildNodes();
        if (children.getLength() === 0)return null;
        for(let k: number = 0; k < children.getLength(); k++) {{
            const kid: Node = children.item(k);
            if (kid != null && (kid.constructor != null && kid.constructor["__interfaces"] != null && kid.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                return <Element><any>kid;
            }
        };}
        return null;
    }

    public static getChild(parent: Element, i: number): Element {
        const children: NodeList = parent.getChildNodes();
        if (children.getLength() === 0)return null;
        let count: number = 0;
        for(let k: number = 0; k < children.getLength(); k++) {{
            const kid: Node = children.item(k);
            if (kid != null && (kid.constructor != null && kid.constructor["__interfaces"] != null && kid.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                if (count === i)return <Element><any>kid; else ++count;
            }
        };}
        return null;
    }

    /**
     * This is required for JSweet, which ignores the radix in Integer.toString( i, 2 )
     * @param {number} i
     * @return
     * @return {string}
     */
    public static byteToBinary(i: number): string {
        let result: string = "";
        result += ((i / 8|0) === 1) ? "1" : "0";
        i = i % 8;
        result += ((i / 4|0) === 1) ? "1" : "0";
        i = i % 4;
        result += ((i / 2|0) === 1) ? "1" : "0";
        i = i % 2;
        result += (i === 1) ? "1" : "0";
        return result;
    }
}
DomUtils["__class"] = "com.vzome.xml.DomUtils";
