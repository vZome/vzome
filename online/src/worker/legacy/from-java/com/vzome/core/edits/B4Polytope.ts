import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { Projection } from "../math/Projection.js";
import { QuaternionProjection } from "../math/QuaternionProjection.js";
import { B4Group } from "../math/symmetry/B4Group.js";
import { CoxeterGroup } from "../math/symmetry/CoxeterGroup.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * This is only used when opening legacy files.  The UI and controllers now use the generic Polytope4d.
 * @author Scott Vorthmann
 * @param {*} editorModel
 * @param {Segment} symmAxis
 * @param {number} index
 * @class
 * @extends ChangeManifestations
 */
export class B4Polytope extends ChangeManifestations {
    /*private*/ index: number;

    /*private*/ proj: Projection;

    /*private*/ symmAxis: Segment;

    /*private*/ field: AlgebraicField;

    public constructor(editorModel?: any, symmAxis?: any, index?: any) {
        if (((editorModel != null && (editorModel.constructor != null && editorModel.constructor["__interfaces"] != null && editorModel.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editorModel === null) && ((symmAxis != null && symmAxis instanceof <any>Segment) || symmAxis === null) && ((typeof index === 'number') || index === null)) {
            let __args = arguments;
            super(editorModel);
            if (this.index === undefined) { this.index = 0; } 
            if (this.proj === undefined) { this.proj = null; } 
            if (this.symmAxis === undefined) { this.symmAxis = null; } 
            if (this.field === undefined) { this.field = null; } 
            this.field = this.mManifestations.getField();
            this.index = index;
            this.symmAxis = symmAxis;
        } else if (((editorModel != null && (editorModel.constructor != null && editorModel.constructor["__interfaces"] != null && editorModel.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editorModel === null) && symmAxis === undefined && index === undefined) {
            let __args = arguments;
            {
                let __args = arguments;
                let symmAxis: any = null;
                let index: any = 0;
                super(editorModel);
                if (this.index === undefined) { this.index = 0; } 
                if (this.proj === undefined) { this.proj = null; } 
                if (this.symmAxis === undefined) { this.symmAxis = null; } 
                if (this.field === undefined) { this.field = null; } 
                this.field = this.mManifestations.getField();
                this.index = index;
                this.symmAxis = symmAxis;
            }
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "B4Polytope";
    }

    /**
     * 
     * @param {*} result
     */
    public getXmlAttributes(result: Element) {
        DomUtils.addAttribute(result, "dynkin", DomUtils.byteToBinary(this.index));
        if (this.symmAxis != null)XmlSaveFormat.serializeSegment(result, "start", "end", this.symmAxis);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        const binary: string = xml.getAttribute("dynkin");
        this.index = javaemul.internal.IntegerHelper.parseInt(binary, 2);
        if (format.commandEditsCompacted())this.symmAxis = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end"); else {
            const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
            this.symmAxis = <Segment>attrs.get("rotation");
        }
    }

    /**
     * 
     */
    public perform() {
        if (this.symmAxis == null)this.proj = new Projection.Default(this.field); else {
            const scale: AlgebraicNumber = this.field['createPower$int'](-5);
            this.proj = new QuaternionProjection(this.field, null, this.symmAxis.getOffset().scale(scale));
        }
        const edgeScales: AlgebraicNumber[] = [null, null, null, null];
        for(let i: number = 0; i < edgeScales.length; i++) {{
            edgeScales[i] = this.field.one();
        };}
        const group: CoxeterGroup = new B4Group(this.field);
        WythoffConstruction.constructPolytope(group, this.index, this.index, edgeScales, group, new B4Polytope.WythoffListener(this, this.field));
        this.redo();
    }
}
B4Polytope["__class"] = "com.vzome.core.edits.B4Polytope";


export namespace B4Polytope {

    export class WythoffListener implements WythoffConstruction.Listener {
        public __parent: any;
        numVertices: number;

        scale: AlgebraicNumber;

        public constructor(__parent: any, field: AlgebraicField) {
            this.__parent = __parent;
            this.numVertices = 0;
            if (this.scale === undefined) { this.scale = null; }
            this.scale = field['createPower$int'](5);
        }

        /**
         * 
         * @param {*} p1
         * @param {*} p2
         * @return {*}
         */
        public addEdge(p1: any, p2: any): any {
            const edge: Segment = new SegmentJoiningPoints(<Point>p1, <Point>p2);
            this.__parent.manifestConstruction(edge);
            return edge;
        }

        /**
         * 
         * @param {java.lang.Object[]} vertices
         * @return {*}
         */
        public addFace(vertices: any[]): any {
            return null;
        }

        /**
         * 
         * @param {AlgebraicVector} vertex
         * @return {*}
         */
        public addVertex(vertex: AlgebraicVector): any {
            let projected: AlgebraicVector = vertex;
            if (this.__parent.proj != null)projected = this.__parent.proj.projectImage(vertex, true);
            const p: Point = new FreePoint(projected.scale(this.scale));
            p.setIndex(this.numVertices++);
            this.__parent.manifestConstruction(p);
            return p;
        }
    }
    WythoffListener["__class"] = "com.vzome.core.edits.B4Polytope.WythoffListener";
    WythoffListener["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];


}
