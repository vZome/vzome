import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ImplicitSymmetryParameters } from "../editor/api/ImplicitSymmetryParameters.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class ShowPoint extends ChangeManifestations {
    /*private*/ point: Point;

    /*private*/ parameters: ImplicitSymmetryParameters;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.point === undefined) { this.point = null; }
        if (this.parameters === undefined) { this.parameters = null; }
        this.parameters = <ImplicitSymmetryParameters><any>editor;
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        switch((<string>props.get("mode"))) {
        case "origin":
            const origin: AlgebraicVector = this.mManifestations.getField().origin(3);
            this.point = new FreePoint(origin);
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
    public getXmlAttributes(xml: Element) {
        XmlSaveFormat.serializePoint(xml, "point", this.point);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        if (format.commandEditsCompacted())this.point = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point"); else {
            const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
            this.point = <Point>attrs.get("point");
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
