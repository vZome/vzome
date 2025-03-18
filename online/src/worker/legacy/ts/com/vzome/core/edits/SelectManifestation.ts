/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * Used by CommandEdit.
     * @param {*} editor
     * @param {*} m
     * @class
     * @extends com.vzome.core.editor.api.ChangeSelection
     */
    export class SelectManifestation extends com.vzome.core.editor.api.ChangeSelection {
        /*private*/ mManifestation: com.vzome.core.model.Manifestation;

        /*private*/ construction: com.vzome.core.construction.Construction;

        /*private*/ mRealized: com.vzome.core.model.RealizedModel;

        /*private*/ mReplace: boolean;

        /**
         * 
         * @return {boolean}
         */
        groupingAware(): boolean {
            return true;
        }

        public constructor(editor?: any, m?: any) {
            if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && ((m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || m === null)) {
                let __args = arguments;
                {
                    let __args = arguments;
                    super(editor.getSelection());
                    if (this.mManifestation === undefined) { this.mManifestation = null; } 
                    if (this.construction === undefined) { this.construction = null; } 
                    if (this.mRealized === undefined) { this.mRealized = null; } 
                    if (this.mReplace === undefined) { this.mReplace = false; } 
                    this.mRealized = editor.getRealizedModel();
                }
                (() => {
                    this.mManifestation = m;
                    if (this.mManifestation != null){
                        this.construction = this.mManifestation.toConstruction();
                    }
                })();
            } else if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && m === undefined) {
                let __args = arguments;
                super(editor.getSelection());
                if (this.mManifestation === undefined) { this.mManifestation = null; } 
                if (this.construction === undefined) { this.construction = null; } 
                if (this.mRealized === undefined) { this.mRealized = null; } 
                if (this.mReplace === undefined) { this.mReplace = false; } 
                this.mRealized = editor.getRealizedModel();
            } else throw new Error('invalid overload');
        }

        public configure(props: java.util.Map<string, any>) {
            const mode: string = <string>props.get("mode");
            this.mReplace = "replace" === mode;
            this.mManifestation = <com.vzome.core.model.Manifestation><any>props.get("picked");
            if (this.mManifestation != null){
                this.construction = this.mManifestation.toConstruction();
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.mReplace){
                for(let index=this.mSelection.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        this.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                    }
                }
                this.select$com_vzome_core_model_Manifestation(this.mManifestation);
            } else if (this.mSelection.manifestationSelected(this.mManifestation))this.unselect$com_vzome_core_model_Manifestation(this.mManifestation); else this.select$com_vzome_core_model_Manifestation(this.mManifestation);
            this.redo();
        }

        /**
         * 
         * @param {*} result
         */
        getXmlAttributes(result: org.w3c.dom.Element) {
            if (this.construction != null && this.construction instanceof <any>com.vzome.core.construction.Point)com.vzome.core.commands.XmlSaveFormat.serializePoint(result, "point", <com.vzome.core.construction.Point>this.construction); else if (this.construction != null && this.construction instanceof <any>com.vzome.core.construction.Segment)com.vzome.core.commands.XmlSaveFormat.serializeSegment(result, "startSegment", "endSegment", <com.vzome.core.construction.Segment>this.construction); else if (this.construction != null && this.construction instanceof <any>com.vzome.core.construction.Polygon)com.vzome.core.commands.XmlSaveFormat.serializePolygon(result, "polygonVertex", <com.vzome.core.construction.Polygon>this.construction);
            if (this.mReplace)com.vzome.xml.DomUtils.addAttribute(result, "replace", "true");
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            if (format.rationalVectors()){
                this.construction = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point");
                if (this.construction == null)this.construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
                if (this.construction == null){
                    const kid: org.w3c.dom.Element = com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element$java_lang_String(xml, "polygon");
                    if (kid != null)this.construction = format.parsePolygon$org_w3c_dom_Element$java_lang_String(kid, "vertex"); else this.construction = format.parsePolygon$org_w3c_dom_Element$java_lang_String(xml, "polygonVertex");
                }
            } else {
                const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                this.construction = <com.vzome.core.construction.Construction>attrs.get("manifestation");
                const replaceVal: boolean = <boolean>attrs.get("replace");
                if (replaceVal != null && replaceVal)this.mReplace = true;
            }
            this.mManifestation = this.mRealized.getManifestation(this.construction);
            if (this.mManifestation == null && format.rationalVectors() && (this.construction != null && this.construction instanceof <any>com.vzome.core.construction.Polygon)){
                this.construction = format.parsePolygonReversed(xml, "polygonVertex");
                this.mManifestation = this.mRealized.getManifestation(this.construction);
                if (this.mManifestation != null)com.vzome.core.editor.api.SideEffects.logBugAccommodation("reverse-oriented polygon");
            }
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "SelectManifestation";
        }
    }
    SelectManifestation["__class"] = "com.vzome.core.edits.SelectManifestation";

}

