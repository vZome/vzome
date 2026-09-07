import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { XmlSymmetryFormat } from "../commands/XmlSymmetryFormat.js";
import { AnchoredSegment } from "../construction/AnchoredSegment.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class StrutCreation extends ChangeManifestations {
    mAnchor: Point;

    /*private*/ mAxis: Axis;

    /*private*/ mLength: AlgebraicNumber;

    /**
     * 
     * @param {*} params
     */
    public configure(params: java.util.Map<string, any>) {
        this.mAnchor = <Point>params.get("anchor");
        this.mAxis = <Axis>params.get("zone");
        this.mLength = <AlgebraicNumber><any>params.get("length");
    }

    public constructor(anchor?: any, axis?: any, len?: any, editor?: any) {
        if (((anchor != null && anchor instanceof <any>Point) || anchor === null) && ((axis != null && axis instanceof <any>Axis) || axis === null) && ((len != null && (len.constructor != null && len.constructor["__interfaces"] != null && len.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || len === null) && ((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null)) {
            let __args = arguments;
            super(editor);
            if (this.mAnchor === undefined) { this.mAnchor = null; } 
            if (this.mAxis === undefined) { this.mAxis = null; } 
            if (this.mLength === undefined) { this.mLength = null; } 
            this.mAnchor = anchor;
            this.mAxis = axis;
            this.mLength = len;
        } else if (((anchor != null && (anchor.constructor != null && anchor.constructor["__interfaces"] != null && anchor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || anchor === null) && axis === undefined && len === undefined && editor === undefined) {
            let __args = arguments;
            let editor: any = __args[0];
            {
                let __args = arguments;
                let anchor: any = null;
                let axis: any = null;
                let len: any = null;
                super(editor);
                if (this.mAnchor === undefined) { this.mAnchor = null; } 
                if (this.mAxis === undefined) { this.mAxis = null; } 
                if (this.mLength === undefined) { this.mLength = null; } 
                this.mAnchor = anchor;
                this.mAxis = axis;
                this.mLength = len;
            }
        } else throw new Error('invalid overload');
    }

    /**
     * 
     */
    public perform() {
        const segment: Segment = new AnchoredSegment(this.mAxis, this.mLength, this.mAnchor);
        this.manifestConstruction(segment);
        const point: Point = new SegmentEndPoint(segment);
        this.manifestConstruction(point);
        this.redo();
    }

    /**
     * 
     * @param {*} xml
     */
    getXmlAttributes(xml: Element) {
        XmlSaveFormat.serializePoint(xml, "anchor", this.mAnchor);
        XmlSymmetryFormat.serializeAxis(xml, "symm", "dir", "index", "sense", this.mAxis);
        XmlSaveFormat.serializeNumber(xml, "len", this.mLength);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        if (format.rationalVectors()){
            this.mAnchor = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "anchor");
            this.mAxis = (<XmlSymmetryFormat>format).parseAxis(xml, "symm", "dir", "index", "sense");
            this.mLength = format.parseNumber(xml, "len");
        } else {
            const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element$boolean(xml, true);
            this.mAnchor = <Point>attrs.get("anchor");
            this.mAxis = <Axis>attrs.get("axis");
            this.mLength = <AlgebraicNumber><any>attrs.get("len");
        }
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "StrutCreation";
    }
}
StrutCreation["__class"] = "com.vzome.core.edits.StrutCreation";
