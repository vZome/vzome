/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export abstract class AbstractCommand implements com.vzome.core.commands.Command {
        /**
         * This default behavior deserializes in the old way, before XmlSaveFormat .COMPACTED_COMMAND_EDITS
         * @param attributes
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @return
         * @return {com.vzome.core.commands.AttributeMap}
         */
        public setXml(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat): com.vzome.core.commands.AttributeMap {
            const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
            this.setFixedAttributes(attrs, format);
            return attrs;
        }

        public setFixedAttributes(attributes: com.vzome.core.commands.AttributeMap, format: com.vzome.core.commands.XmlSaveFormat) {
            attributes.put(com.vzome.core.commands.Command.FIELD_ATTR_NAME, format.getField());
        }

        /**
         * This default behavior serializes in the old way, before XmlSaveFormat .COMPACTED_COMMAND_EDITS
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @return
         * @param {*} result
         */
        public getXml(result: org.w3c.dom.Element, attributes: com.vzome.core.commands.AttributeMap) {
            if (attributes == null)return;
            for(let index=attributes.keySet().iterator();index.hasNext();) {
                let key = index.next();
                {
                    if (key === com.vzome.core.commands.Command.FIELD_ATTR_NAME)continue;
                    if (key === com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME)continue;
                    if (key === com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME)continue;
                    if (key === com.vzome.core.commands.CommandImportVEFData.FIELD_ATTR_NAME)continue;
                    const value: any = attributes.get(key);
                    if (value != null && value instanceof <any>com.vzome.core.math.symmetry.IcosahedralSymmetry)continue;
                    AbstractCommand.saveCommandAttribute(result, key, value);
                }
            }
        }

        public static saveCommandAttribute(command: org.w3c.dom.Element, attrName: string, value: any) {
            const doc: org.w3c.dom.Document = command.getOwnerDocument();
            let valElem: org.w3c.dom.Element = null;
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
                    com.vzome.xml.DomUtils.addAttribute(valElem, "nums", numerators.toString());
                    if (!allOnes){
                        const denominators: java.lang.StringBuffer = new java.lang.StringBuffer();
                        for(let i: number = 0; i < (v.length / 2|0); i++) {{
                            if (i > 0)denominators.append(" ");
                            denominators.append(v[2 * i + 1]);
                        };}
                        com.vzome.xml.DomUtils.addAttribute(valElem, "denoms", denominators.toString());
                    }
                }
            } else if (value != null && value instanceof <any>com.vzome.core.math.symmetry.Axis){
                valElem = doc.createElement("Axis");
                (<com.vzome.core.math.symmetry.Axis>value).getXML(valElem);
            } else if (typeof value === 'boolean'){
                valElem = doc.createElement("Boolean");
                com.vzome.xml.DomUtils.addAttribute(valElem, "value", (<boolean>value).toString());
            } else if (typeof value === 'number'){
                valElem = doc.createElement("Integer");
                com.vzome.xml.DomUtils.addAttribute(valElem, "value", (<number>value).toString());
            } else if (value != null && value instanceof <any>com.vzome.core.construction.Construction){
                valElem = (<com.vzome.core.construction.Construction>value).getXml(command.getOwnerDocument());
            } else if (typeof value === 'string'){
                valElem = doc.createElement("String");
                const str: string = com.vzome.core.commands.XmlSaveFormat.escapeNewlines(<string>value);
                valElem.appendChild(doc.createTextNode(str));
            } else if (value != null && value instanceof <any>com.vzome.core.math.symmetry.QuaternionicSymmetry){
                valElem = doc.createElement("QuaternionicSymmetry");
                com.vzome.xml.DomUtils.addAttribute(valElem, "name", (<com.vzome.core.math.symmetry.QuaternionicSymmetry>value).getName());
            } else if (value != null && (value.constructor != null && value.constructor["__interfaces"] != null && value.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)){
                valElem = doc.createElement("Symmetry");
                com.vzome.xml.DomUtils.addAttribute(valElem, "name", (<com.vzome.core.math.symmetry.Symmetry><any>value).getName());
            } else if (value == null){
                valElem = doc.createElement("Null");
            } else {
                throw new java.lang.IllegalStateException("unable to save " + /* getName */(c => typeof c === 'string' ? c : c["__class"] ? c["__class"] : c["name"])((<any>value.constructor)));
            }
            com.vzome.xml.DomUtils.addAttribute(valElem, "attrName", attrName);
            command.appendChild(valElem);
        }

        public attributeIs3D(attrName: string): boolean {
            return true;
        }

        public setQuaternion(offset: com.vzome.core.algebra.AlgebraicVector) {
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


}

