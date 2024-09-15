/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class StrutMove extends com.vzome.core.edits.StrutCreation {
        /*private*/ oldStrut: com.vzome.core.model.Strut;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.oldStrut === undefined) { this.oldStrut = null; }
        }

        /**
         * 
         * @param {*} params
         */
        public configure(params: java.util.Map<string, any>) {
            super.configure(params);
            this.oldStrut = <com.vzome.core.model.Strut><any>params.get("oldStrut");
        }

        /**
         * 
         */
        public perform() {
            this.deleteManifestation(this.oldStrut);
            this.manifestConstruction(this.mAnchor);
            super.redo();
            super.perform();
        }

        /**
         * 
         * @param {*} xml
         */
        getXmlAttributes(xml: org.w3c.dom.Element) {
            com.vzome.core.commands.XmlSaveFormat.serializeSegment(xml, "startSegment", "endSegment", <com.vzome.core.construction.Segment>this.oldStrut.getFirstConstruction());
            super.getXmlAttributes(xml);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const construction: com.vzome.core.construction.Construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
            if (construction != null)this.oldStrut = <com.vzome.core.model.Strut><any>this.getManifestation(construction);
            super.setXmlAttributes(xml, format);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "StrutMove";
        }
    }
    StrutMove["__class"] = "com.vzome.core.edits.StrutMove";

}

