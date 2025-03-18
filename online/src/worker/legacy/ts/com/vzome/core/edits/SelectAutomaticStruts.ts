/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * @author David Hall
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class SelectAutomaticStruts extends com.vzome.core.editor.api.ChangeManifestations {
        symmetry: com.vzome.core.editor.api.OrbitSource;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.symmetry === undefined) { this.symmetry = null; }
            if (this.editor === undefined) { this.editor = null; }
            this.editor = editor;
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']();
        }

        /**
         * 
         */
        public perform() {
            this.unselectAll();
            for(let index=this.getVisibleStruts$java_util_function_Predicate((strut) => { return this.isAutomaticStrut(strut) }).iterator();index.hasNext();) {
                let strut = index.next();
                {
                    this.select$com_vzome_core_model_Manifestation(strut);
                }
            }
            super.perform();
        }

        /*private*/ isAutomaticStrut(strut: com.vzome.core.model.Strut): boolean {
            return this.symmetry.getAxis(strut.getOffset()).getOrbit().isAutomatic();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "SelectAutomaticStruts";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.symmetry != null){
                com.vzome.xml.DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
        }
    }
    SelectAutomaticStruts["__class"] = "com.vzome.core.edits.SelectAutomaticStruts";

}

