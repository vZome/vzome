/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class StrutCreation extends com.vzome.core.editor.api.ChangeManifestations {
        mAnchor: com.vzome.core.construction.Point;

        /*private*/ mAxis: com.vzome.core.math.symmetry.Axis;

        /*private*/ mLength: com.vzome.core.algebra.AlgebraicNumber;

        /**
         * 
         * @param {*} params
         */
        public configure(params: java.util.Map<string, any>) {
            this.mAnchor = <com.vzome.core.construction.Point>params.get("anchor");
            this.mAxis = <com.vzome.core.math.symmetry.Axis>params.get("zone");
            this.mLength = <com.vzome.core.algebra.AlgebraicNumber><any>params.get("length");
        }

        public constructor(anchor?: any, axis?: any, len?: any, editor?: any) {
            if (((anchor != null && anchor instanceof <any>com.vzome.core.construction.Point) || anchor === null) && ((axis != null && axis instanceof <any>com.vzome.core.math.symmetry.Axis) || axis === null) && ((len != null && (len.constructor != null && len.constructor["__interfaces"] != null && len.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicNumber") >= 0)) || len === null) && ((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null)) {
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
            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.AnchoredSegment(this.mAxis, this.mLength, this.mAnchor);
            this.manifestConstruction(segment);
            const point: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentEndPoint(segment);
            this.manifestConstruction(point);
            this.redo();
        }

        /**
         * 
         * @param {*} xml
         */
        getXmlAttributes(xml: org.w3c.dom.Element) {
            com.vzome.core.commands.XmlSaveFormat.serializePoint(xml, "anchor", this.mAnchor);
            com.vzome.core.commands.XmlSymmetryFormat.serializeAxis(xml, "symm", "dir", "index", "sense", this.mAxis);
            com.vzome.core.commands.XmlSaveFormat.serializeNumber(xml, "len", this.mLength);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            if (format.rationalVectors()){
                this.mAnchor = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "anchor");
                this.mAxis = (<com.vzome.core.commands.XmlSymmetryFormat>format).parseAxis(xml, "symm", "dir", "index", "sense");
                this.mLength = format.parseNumber(xml, "len");
            } else {
                const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element$boolean(xml, true);
                this.mAnchor = <com.vzome.core.construction.Point>attrs.get("anchor");
                this.mAxis = <com.vzome.core.math.symmetry.Axis>attrs.get("axis");
                this.mLength = <com.vzome.core.algebra.AlgebraicNumber><any>attrs.get("len");
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

}

