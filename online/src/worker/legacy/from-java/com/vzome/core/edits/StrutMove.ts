import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { Segment } from "../construction/Segment.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { StrutCreation } from "./StrutCreation.js";
import { Strut } from "../model/Strut.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class StrutMove extends StrutCreation {
    /*private*/ oldStrut: Strut;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.oldStrut === undefined) { this.oldStrut = null; }
    }

    /**
     * 
     * @param {*} params
     */
    public configure(params: java.util.Map<string, any>) {
        super.configure(params);
        this.oldStrut = <Strut><any>params.get("oldStrut");
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
    getXmlAttributes(xml: Element) {
        XmlSaveFormat.serializeSegment(xml, "startSegment", "endSegment", <Segment>this.oldStrut.getFirstConstruction());
        super.getXmlAttributes(xml);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        const construction: Construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
        if (construction != null)this.oldStrut = <Strut><any>this.getManifestation(construction);
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
