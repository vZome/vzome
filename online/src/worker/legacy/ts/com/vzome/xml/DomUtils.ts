/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.xml {
    export class DomUtils {
        public static addAttribute(elem: org.w3c.dom.Element, name: string, value: string) {
            elem.setAttribute(name, value);
        }

        public static getFirstChildElement$org_w3c_dom_Element$java_lang_String(elem: org.w3c.dom.Element, name: string): org.w3c.dom.Element {
            const elems: org.w3c.dom.NodeList = elem.getElementsByTagName(name);
            return <org.w3c.dom.Element><any>elems.item(0);
        }

        public static getFirstChildElement(elem?: any, name?: any): org.w3c.dom.Element {
            if (((elem != null && (elem.constructor != null && elem.constructor["__interfaces"] != null && elem.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || elem === null) && ((typeof name === 'string') || name === null)) {
                return <any>com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element$java_lang_String(elem, name);
            } else if (((elem != null && (elem.constructor != null && elem.constructor["__interfaces"] != null && elem.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || elem === null) && name === undefined) {
                return <any>com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element(elem);
            } else throw new Error('invalid overload');
        }

        public static preserveSpace(contentElem: org.w3c.dom.Element) {
            contentElem.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        }

        public static getFirstChildElement$org_w3c_dom_Element(parent: org.w3c.dom.Element): org.w3c.dom.Element {
            const children: org.w3c.dom.NodeList = parent.getChildNodes();
            if (children.getLength() === 0)return null;
            for(let k: number = 0; k < children.getLength(); k++) {{
                const kid: org.w3c.dom.Node = children.item(k);
                if (kid != null && (kid.constructor != null && kid.constructor["__interfaces"] != null && kid.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                    return <org.w3c.dom.Element><any>kid;
                }
            };}
            return null;
        }

        public static getChild(parent: org.w3c.dom.Element, i: number): org.w3c.dom.Element {
            const children: org.w3c.dom.NodeList = parent.getChildNodes();
            if (children.getLength() === 0)return null;
            let count: number = 0;
            for(let k: number = 0; k < children.getLength(); k++) {{
                const kid: org.w3c.dom.Node = children.item(k);
                if (kid != null && (kid.constructor != null && kid.constructor["__interfaces"] != null && kid.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                    if (count === i)return <org.w3c.dom.Element><any>kid; else ++count;
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

}

