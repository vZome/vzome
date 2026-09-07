import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { Segment } from "../construction/Segment.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Manifestation } from "../model/Manifestation.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class Label extends ChangeManifestations {
    /*private*/ target: Manifestation;

    /*private*/ label: string;

    public constructor(editorModel: EditorModel) {
        super(editorModel);
        if (this.target === undefined) { this.target = null; }
        if (this.label === undefined) { this.label = null; }
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        this.target = <Manifestation><any>props.get("picked");
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
    getXmlAttributes(element: Element) {
        const construction: Construction = this.target.getFirstConstruction();
        if (construction != null && construction instanceof <any>Point)XmlSaveFormat.serializePoint(element, "point", <Point>construction); else if (construction != null && construction instanceof <any>Segment)XmlSaveFormat.serializeSegment(element, "startSegment", "endSegment", <Segment>construction); else if (construction != null && construction instanceof <any>Polygon)XmlSaveFormat.serializePolygon(element, "polygonVertex", <Polygon>construction);
        element.setAttribute("text", this.label);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.label = xml.getAttribute("text");
        let construction: Construction = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point");
        if (construction == null)construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
        if (construction == null)construction = format.parsePolygon$org_w3c_dom_Element$java_lang_String(xml, "polygonVertex");
        if (construction != null)this.target = this.getManifestation(construction);
    }
}
Label["__class"] = "com.vzome.core.edits.Label";
