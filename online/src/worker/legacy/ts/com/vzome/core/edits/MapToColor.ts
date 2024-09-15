/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * @author David Hall
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class MapToColor extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ colorMapper: com.vzome.core.edits.ManifestationColorMappers.ManifestationColorMapper;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.colorMapper === undefined) { this.colorMapper = null; }
            if (this.editor === undefined) { this.editor = null; }
            this.editor = editor;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const colorMapperName: string = <string>props.get("mode");
            const symmetry: com.vzome.core.editor.api.OrbitSource = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$']();
            if (colorMapperName != null)this.colorMapper = com.vzome.core.edits.ManifestationColorMappers.getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(colorMapperName, symmetry);
        }

        /**
         * Either configure() or setXmlAttributes() is always called before perform()
         */
        public perform() {
            if (this.colorMapper.requiresOrderedSelection()){
                this.setOrderedSelection(true);
            }
            this.colorMapper.initialize(this.getRenderedSelection());
            for(let index=this.getRenderedSelection().iterator();index.hasNext();) {
                let man = index.next();
                {
                    const newColor: com.vzome.core.construction.Color = this.colorMapper.apply$com_vzome_core_model_Manifestation(man);
                    this.plan(new MapToColor.ColorMapManifestation(this, man, newColor));
                    this.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                }
            }
            this.redo();
        }

        static COLORMAPPER_ATTR_NAME: string = "colorMapper";

        /**
         * 
         * @param {*} result
         */
        public getXmlAttributes(result: org.w3c.dom.Element) {
            result.setAttribute(MapToColor.COLORMAPPER_ATTR_NAME, this.colorMapper.getName());
            this.colorMapper.getXmlAttributes(result);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const symmetry: com.vzome.core.editor.api.OrbitSource = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
            const colorMapperName: string = xml.getAttribute(MapToColor.COLORMAPPER_ATTR_NAME);
            this.colorMapper = com.vzome.core.edits.ManifestationColorMappers.getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(colorMapperName, symmetry);
            this.colorMapper.setXmlAttributes(xml);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "MapToColor";
        }
    }
    MapToColor["__class"] = "com.vzome.core.edits.MapToColor";


    export namespace MapToColor {

        export class ColorMapManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mManifestation: com.vzome.core.model.Manifestation;

            oldColor: com.vzome.core.construction.Color;

            newColor: com.vzome.core.construction.Color;

            public constructor(__parent: any, manifestation: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
                this.__parent = __parent;
                if (this.mManifestation === undefined) { this.mManifestation = null; }
                if (this.oldColor === undefined) { this.oldColor = null; }
                if (this.newColor === undefined) { this.newColor = null; }
                this.mManifestation = manifestation;
                this.newColor = color;
                this.oldColor = manifestation.getColor();
            }

            /**
             * 
             */
            public redo() {
                this.__parent.mManifestations.setColor(this.mManifestation, this.newColor);
            }

            /**
             * 
             */
            public undo() {
                this.__parent.mManifestations.setColor(this.mManifestation, this.oldColor);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = doc.createElement("color");
                com.vzome.xml.DomUtils.addAttribute(result, "rgb", this.newColor.toString());
                const man: org.w3c.dom.Element = this.mManifestation.getXml(doc);
                result.appendChild(man);
                return result;
            }
        }
        ColorMapManifestation["__class"] = "com.vzome.core.edits.MapToColor.ColorMapManifestation";
        ColorMapManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];


    }

}

