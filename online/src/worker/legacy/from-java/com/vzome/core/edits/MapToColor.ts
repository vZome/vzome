import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Color } from "../construction/Color.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { SideEffect } from "../editor/api/SideEffect.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { ManifestationColorMappers } from "./ManifestationColorMappers.js";
import { Manifestation } from "../model/Manifestation.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author David Hall
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 */
export class MapToColor extends ChangeManifestations {
    /*private*/ colorMapper: ManifestationColorMappers.ManifestationColorMapper;

    /*private*/ editor: EditorModel;

    public constructor(editor: EditorModel) {
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
        const symmetry: OrbitSource = (<SymmetryAware><any>this.editor)['getSymmetrySystem$']();
        if (colorMapperName != null)this.colorMapper = ManifestationColorMappers.getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(colorMapperName, symmetry);
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
                const newColor: Color = this.colorMapper.apply$com_vzome_core_model_Manifestation(man);
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
    public getXmlAttributes(result: Element) {
        result.setAttribute(MapToColor.COLORMAPPER_ATTR_NAME, this.colorMapper.getName());
        this.colorMapper.getXmlAttributes(result);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        const symmetry: OrbitSource = (<SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
        const colorMapperName: string = xml.getAttribute(MapToColor.COLORMAPPER_ATTR_NAME);
        this.colorMapper = ManifestationColorMappers.getColorMapper$java_lang_String$com_vzome_core_editor_api_OrbitSource(colorMapperName, symmetry);
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

    export class ColorMapManifestation implements SideEffect {
        public __parent: any;
        mManifestation: Manifestation;

        oldColor: Color;

        newColor: Color;

        public constructor(__parent: any, manifestation: Manifestation, color: Color) {
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
        public getXml(doc: Document): Element {
            const result: Element = doc.createElement("color");
            DomUtils.addAttribute(result, "rgb", this.newColor.toString());
            const man: Element = this.mManifestation.getXml(doc);
            result.appendChild(man);
            return result;
        }
    }
    ColorMapManifestation["__class"] = "com.vzome.core.edits.MapToColor.ColorMapManifestation";
    ColorMapManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];


}
