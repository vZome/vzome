/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class Label extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ target: com.vzome.core.model.Manifestation;

        /*private*/ label: string;

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
            if (this.target === undefined) { this.target = null; }
            if (this.label === undefined) { this.label = null; }
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            this.target = <com.vzome.core.model.Manifestation><any>props.get("picked");
            this.label = <string>props.get("text");
        }

        /**
         * 
         */
        public perform() {
            this.labelManifestation(this.target, this.label);
            super.perform();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "Label";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            const construction: com.vzome.core.construction.Construction = this.target.getFirstConstruction();
            if (construction != null && construction instanceof <any>com.vzome.core.construction.Point)com.vzome.core.commands.XmlSaveFormat.serializePoint(element, "point", <com.vzome.core.construction.Point>construction); else if (construction != null && construction instanceof <any>com.vzome.core.construction.Segment)com.vzome.core.commands.XmlSaveFormat.serializeSegment(element, "startSegment", "endSegment", <com.vzome.core.construction.Segment>construction); else if (construction != null && construction instanceof <any>com.vzome.core.construction.Polygon)com.vzome.core.commands.XmlSaveFormat.serializePolygon(element, "polygonVertex", <com.vzome.core.construction.Polygon>construction);
            element.setAttribute("text", this.label);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.label = xml.getAttribute("text");
            let construction: com.vzome.core.construction.Construction = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point");
            if (construction == null)construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
            if (construction == null)construction = format.parsePolygon$org_w3c_dom_Element$java_lang_String(xml, "polygonVertex");
            if (construction != null)this.target = this.getManifestation(construction);
        }
    }
    Label["__class"] = "com.vzome.core.edits.Label";

}

