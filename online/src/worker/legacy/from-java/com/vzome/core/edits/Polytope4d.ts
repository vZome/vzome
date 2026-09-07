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
import { EditorModel } from "../editor/api/EditorModel.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Projection } from "../math/Projection.js";
import { QuaternionProjection } from "../math/QuaternionProjection.js";
import { Symmetries4D } from "../math/symmetry/Symmetries4D.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class Polytope4d extends ChangeManifestations {
    /*private*/ index: number;

    /*private*/ field: AlgebraicField;

    /*private*/ proj: Projection;

    /*private*/ quaternion: AlgebraicVector;

    /*private*/ groupName: string;

    /*private*/ edgesToRender: number;

    /*private*/ edgeScales: AlgebraicNumber[];

    /*private*/ renderGroupName: string;

    /*private*/ symmetries: Symmetries4D;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.index === undefined) { this.index = 0; }
        if (this.field === undefined) { this.field = null; }
        if (this.proj === undefined) { this.proj = null; }
        if (this.quaternion === undefined) { this.quaternion = null; }
        if (this.groupName === undefined) { this.groupName = null; }
        this.edgesToRender = 15;
        this.edgeScales = [null, null, null, null];
        if (this.renderGroupName === undefined) { this.renderGroupName = null; }
        if (this.symmetries === undefined) { this.symmetries = null; }
        this.symmetries = (<SymmetryAware><any>editor).get4dSymmetries();
        this.field = editor.getRealizedModel().getField();
        for(let i: number = 0; i < this.edgeScales.length; i++) {{
            this.edgeScales[i] = this.field.one();
        };}
    }

    /**
     * 
     * @param {*} params
     */
    public configure(params: java.util.Map<string, any>) {
        this.groupName = <string>params.get("groupName");
        this.renderGroupName = <string>params.get("renderGroupName");
        this.index = (<number>params.get("index")|0);
        this.edgesToRender = (<number>params.get("edgesToRender")|0);
        this.edgeScales = <AlgebraicNumber[]>params.get("edgeScales");
        this.quaternion = <AlgebraicVector>params.get("quaternion");
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "Polytope4d";
    }

    /**
     * 
     * @param {*} xml
     */
    public getXmlAttributes(xml: Element) {
        if (this.quaternion != null)DomUtils.addAttribute(xml, "quaternion", this.quaternion.toParsableString());
        DomUtils.addAttribute(xml, "group", this.groupName);
        DomUtils.addAttribute(xml, "wythoff", DomUtils.byteToBinary(this.index));
        if (this.edgesToRender !== 15)DomUtils.addAttribute(xml, "renderEdges", DomUtils.byteToBinary(this.edgesToRender));
        if (!(this.renderGroupName === this.groupName))DomUtils.addAttribute(xml, "renderGroup", this.renderGroupName);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        const binary: string = xml.getAttribute("wythoff");
        this.index = javaemul.internal.IntegerHelper.parseInt(binary, 2);
        const renderString: string = xml.getAttribute("renderEdges");
        this.edgesToRender = (renderString == null || /* isEmpty */(renderString.length === 0)) ? this.index : javaemul.internal.IntegerHelper.parseInt(renderString, 2);
        this.groupName = xml.getAttribute("group");
        const rgString: string = xml.getAttribute("renderGroup");
        this.renderGroupName = (rgString == null || /* isEmpty */(rgString.length === 0)) ? this.groupName : rgString;
        let quatString: string = xml.getAttribute("quaternion");
        if (quatString != null && !("" === quatString)){
            if (/* contains */(quatString.indexOf("+") != -1)){
                quatString = /* replace */quatString.split(',').join(' ');
                quatString = /* replace */quatString.split('(').join(' ');
                quatString = /* replace */quatString.split(')').join(' ');
                quatString = /* replace */quatString.split('+').join(' ');
                const irrat: string = this.field['getIrrational$int'](0).charAt(0);
                quatString = /* replace */quatString.split(irrat).join(' ');
                quatString = quatString + " 0 0 0";
            }
            this.quaternion = this.field.parseVector(quatString);
        } else {
            let segment: Segment = null;
            if (format.commandEditsCompacted())segment = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end"); else {
                const attrs: AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                segment = <Segment>attrs.get("rotation");
            }
            if (segment != null)this.quaternion = segment.getOffset().inflateTo4d$();
        }
    }

    /**
     * 
     */
    public perform() {
        if (this.quaternion == null)this.proj = new Projection.Default(this.field); else this.proj = new QuaternionProjection(this.field, null, this.quaternion.scale(this.field['createPower$int'](-5)));
        this.symmetries.constructPolytope(this.groupName, this.index, this.edgesToRender, this.edgeScales, new Polytope4d.WythoffListener(this));
        this.redo();
    }

    public static getSupportedGroups(): string[] {
        return ["A4", "B4/C4", "D4", "F4", "H4"];
    }
}
Polytope4d["__class"] = "com.vzome.core.edits.Polytope4d";


export namespace Polytope4d {

    export class WythoffListener implements WythoffConstruction.Listener {
        public __parent: any;
        numVertices: number;

        vertices: java.util.Map<string, Point>;

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
            let p: Point = this.vertices.get(vertex.toString());
            if (p == null){
                let projected: AlgebraicVector = vertex;
                if (this.__parent.proj != null)projected = this.__parent.proj.projectImage(vertex, true);
                projected = projected.scale(this.__parent.field['createPower$int'](5));
                p = new FreePoint(projected);
                p.setIndex(this.numVertices++);
                this.__parent.manifestConstruction(p);
                this.vertices.put(vertex.toString(), p);
            }
            return p;
        }

        constructor(__parent: any) {
            this.__parent = __parent;
            this.numVertices = 0;
            this.vertices = <any>(new java.util.HashMap<any, any>());
        }
    }
    WythoffListener["__class"] = "com.vzome.core.edits.Polytope4d.WythoffListener";
    WythoffListener["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];


}
