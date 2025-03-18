/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class JoinPointPair extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ start: com.vzome.core.construction.Point;

        /*private*/ end: com.vzome.core.construction.Point;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.start === undefined) { this.start = null; }
            if (this.end === undefined) { this.end = null; }
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            this.start = <com.vzome.core.construction.Point>props.get("start");
            this.end = <com.vzome.core.construction.Point>props.get("end");
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            com.vzome.core.commands.XmlSaveFormat.serializePoint(element, "start", this.start);
            com.vzome.core.commands.XmlSaveFormat.serializePoint(element, "end", this.end);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.start = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "start");
            this.end = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "end");
        }

        /**
         * 
         */
        public perform() {
            if ((this.start !== this.end) && !(this.start.getLocation().equals(this.end.getLocation()))){
                const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(this.start, this.end);
                this.manifestConstruction(segment);
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "JoinPointPair";
        }
    }
    JoinPointPair["__class"] = "com.vzome.core.edits.JoinPointPair";

}

