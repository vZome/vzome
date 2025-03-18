/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ShowPoint extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ point: com.vzome.core.construction.Point;

        /*private*/ parameters: com.vzome.core.editor.api.ImplicitSymmetryParameters;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.point === undefined) { this.point = null; }
            if (this.parameters === undefined) { this.parameters = null; }
            this.parameters = <com.vzome.core.editor.api.ImplicitSymmetryParameters><any>editor;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            switch((<string>props.get("mode"))) {
            case "origin":
                const origin: com.vzome.core.algebra.AlgebraicVector = this.mManifestations.getField().origin(3);
                this.point = new com.vzome.core.construction.FreePoint(origin);
                break;
            case "symmCenter":
                this.point = this.parameters.getCenterPoint();
                break;
            }
        }

        /**
         * 
         */
        public perform() {
            this.manifestConstruction(this.point);
            this.redo();
        }

        /**
         * 
         * @param {*} xml
         */
        public getXmlAttributes(xml: org.w3c.dom.Element) {
            com.vzome.core.commands.XmlSaveFormat.serializePoint(xml, "point", this.point);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            if (format.commandEditsCompacted())this.point = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point"); else {
                const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                this.point = <com.vzome.core.construction.Point>attrs.get("point");
            }
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "ShowPoint";
        }
    }
    ShowPoint["__class"] = "com.vzome.core.edits.ShowPoint";

}

