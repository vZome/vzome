import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Strut } from "../model/Strut.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author David Hall
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 */
export class SelectAutomaticStruts extends ChangeManifestations {
    symmetry: OrbitSource;

    /*private*/ editor: EditorModel;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.symmetry === undefined) { this.symmetry = null; }
        if (this.editor === undefined) { this.editor = null; }
        this.editor = editor;
        this.symmetry = (<SymmetryAware><any>editor)['getSymmetrySystem$']();
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

    /*private*/ isAutomaticStrut(strut: Strut): boolean {
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
    getXmlAttributes(element: Element) {
        if (this.symmetry != null){
            DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
        }
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.symmetry = (<SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
    }
}
SelectAutomaticStruts["__class"] = "com.vzome.core.edits.SelectAutomaticStruts";
