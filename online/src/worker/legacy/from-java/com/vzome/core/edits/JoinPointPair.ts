import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class JoinPointPair extends ChangeManifestations {
    /*private*/ start: Point;

    /*private*/ end: Point;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.start === undefined) { this.start = null; }
        if (this.end === undefined) { this.end = null; }
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        this.start = <Point>props.get("start");
        this.end = <Point>props.get("end");
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        XmlSaveFormat.serializePoint(element, "start", this.start);
        XmlSaveFormat.serializePoint(element, "end", this.end);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.start = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "start");
        this.end = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "end");
    }

    /**
     * 
     */
    public perform() {
        if ((this.start !== this.end) && !(this.start.getLocation().equals(this.end.getLocation()))){
            const segment: Segment = new SegmentJoiningPoints(this.start, this.end);
            this.manifestConstruction(segment);
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "JoinPointPair";
    }
}
JoinPointPair["__class"] = "com.vzome.core.edits.JoinPointPair";
