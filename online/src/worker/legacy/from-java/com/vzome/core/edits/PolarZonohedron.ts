import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { SymmetryTransformation } from "../construction/SymmetryTransformation.js";
import { Transformation } from "../construction/Transformation.js";
import { TransformedPoint } from "../construction/TransformedPoint.js";
import { TransformedSegment } from "../construction/TransformedSegment.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Connector } from "../model/Connector.js";
import { Strut } from "../model/Strut.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class PolarZonohedron extends ChangeManifestations {
    /*private*/ symmetry: OrbitSource;

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
     * @return {string}
     */
    getXmlElementName(): string {
        return "PolarZonohedron";
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

    /**
     * 
     */
    public perform() {
        const errorMsg: java.lang.StringBuilder = new java.lang.StringBuilder();
        errorMsg.append("The Polar Zonohedron command requires either of the following selections:\n\n1) Two non-collinear struts with a common end point.\n   The first strut must have more than 2-fold rotational symmetry.\n   The second strut will be rotated around the first.\n\n2) Any three or more struts having a common end point.\n");
        const struts: java.util.List<Strut> = <any>(new java.util.ArrayList<any>());
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    struts.add(<Strut><any>man);
                }
                this.unselect$com_vzome_core_model_Manifestation(man);
            }
        }
        if (struts.size() < 2){
            errorMsg.append(struts.size() === 1 ? "\nonly one strut is selected." : "\nno struts are selected.");
            this.fail(errorMsg.toString());
        }
        const common: AlgebraicVector = struts.size() === 2 ? this.useRotationalSymmetry(struts, errorMsg) : this.useRadialSelection(struts);
        if (common == null){
            this.fail(errorMsg.append("\nselected struts do not have a common end point").toString());
        }
        const L1: number = 0;
        const L2: number = 1;
        const layers: number = struts.size();
        const offsets: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>(layers));
        const vertices: AlgebraicVector[][] = [null, null];
        vertices[L1] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(layers);
        for(let i: number = 0; i < layers; i++) {{
            const strut: Strut = struts.get(i);
            const start: AlgebraicVector = strut.getLocation();
            const end: AlgebraicVector = strut.getEnd();
            const offset: AlgebraicVector = strut.getOffset();
            if (start.equals(common)){
                vertices[L1][i] = end;
                offsets.add(offset);
            } else {
                vertices[L1][i] = start;
                offsets.add(offset.negate());
            }
        };}
        for(let layer: number = 1; layer < layers; layer++) {{
            vertices[L2] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(layers);
            for(let i: number = 0; i < layers; i++) {{
                const off: number = (i + layer) % layers;
                const offset: AlgebraicVector = offsets.get(off);
                const v1: AlgebraicVector = vertices[L1][i];
                const v2: AlgebraicVector = v1.plus(offset);
                const v3: AlgebraicVector = vertices[L1][(i + 1) % layers];
                const v0: AlgebraicVector = v3.minus(offset);
                vertices[L2][i] = v2;
                const p0: Point = new FreePoint(v0);
                const p1: Point = new FreePoint(v1);
                const p2: Point = new FreePoint(v2);
                const p3: Point = new FreePoint(v3);
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p0));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p1));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p2));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p3));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p1, p2)));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(p2, p3)));
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices([p0, p1, p2, p3])));
            };}
            vertices[L1] = vertices[L2];
        };}
        this.redo();
    }

    public static getCommonEndpoint(strut1: Strut, strut2: Strut): AlgebraicVector {
        if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(strut1,strut2))){
            throw new java.lang.IllegalArgumentException("Identical struts have both end points in common.");
        }
        const start1: AlgebraicVector = strut1.getLocation();
        const end1: AlgebraicVector = strut1.getEnd();
        const start2: AlgebraicVector = strut2.getLocation();
        const end2: AlgebraicVector = strut2.getEnd();
        if (start1.equals(start2) || start1.equals(end2))return start1;
        if (end1.equals(start2) || end1.equals(end2))return end1;
        return null;
    }

    /*private*/ useRotationalSymmetry(struts: java.util.List<Strut>, errorMsg: java.lang.StringBuilder): AlgebraicVector {
        const axisStrut: Strut = struts.get(0);
        const axisSegment: Segment = <Segment>axisStrut.getFirstConstruction();
        let v1: AlgebraicVector = axisSegment.getOffset();
        v1 = axisSegment.getField().projectTo3d(v1, true);
        const axis1: Axis = this.symmetry.getAxis(v1);
        if (axis1 == null){
            this.fail(errorMsg.append("\nfirst selected strut is not an axis of rotational symmetry").toString());
        }
        const perm: Permutation = axis1.getRotationPermutation();
        if (perm == null){
            this.fail(errorMsg.append("\nfirst selected strut is not an axis of rotation").toString());
        }
        let rotation: number = perm.mapIndex(0);
        const order: number = perm.getOrder();
        if (order <= 2){
            this.fail(errorMsg.append("\nfirst selected strut has " + order + "-fold symmetry").toString());
        }
        const spokeStrut: Strut = struts.get(1);
        const spokeSegment: Segment = <Segment>spokeStrut.getFirstConstruction();
        let v2: AlgebraicVector = spokeSegment.getOffset();
        if (v1.equals(v2) || v1.equals(v2.negate())){
            this.fail(errorMsg.append("\nselected struts are collinear").toString());
        }
        const common: AlgebraicVector = PolarZonohedron.getCommonEndpoint(axisStrut, spokeStrut);
        if (common == null){
            this.fail(errorMsg.append("\nselected struts do not have a common end point").toString());
        }
        let s1: AlgebraicVector = axisSegment.getStart();
        let e1: AlgebraicVector = axisSegment.getEnd();
        const center: Point = new SegmentEndPoint(axisSegment, common.equals(e1));
        let s2: AlgebraicVector = spokeSegment.getStart();
        let e2: AlgebraicVector = spokeSegment.getEnd();
        if (common.equals(s1)){
            if (common.equals(e2)){
                v2 = v2.negate();
                e2 = s2;
                s2 = common;
            }
        } else {
            v1 = v1.negate();
            e1 = s1;
            s1 = common;
            if (common.equals(e2)){
                v2 = v2.negate();
                e2 = s2;
                s2 = common;
            }
        }
        this.redo();
        struts.remove(axisStrut);
        this.select$com_vzome_core_model_Manifestation(spokeStrut);
        const p0: Point = new FreePoint(s2);
        const p1: Point = new FreePoint(e2);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p0));
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p1));
        for(let i: number = 0; i < order - 1; i++) {{
            const transform: Transformation = new SymmetryTransformation(this.symmetry.getSymmetry(), rotation, center);
            rotation = perm.mapIndex(rotation);
            const ball: Connector = <Connector><any>this.manifestConstruction(new TransformedPoint(transform, p1));
            const strut: Strut = <Strut><any>this.manifestConstruction(new TransformedSegment(transform, spokeSegment));
            struts.add(strut);
            this.select$com_vzome_core_model_Manifestation(ball);
            this.select$com_vzome_core_model_Manifestation(strut);
        };}
        this.redo();
        return common;
    }

    /*private*/ useRadialSelection(struts: java.util.List<Strut>): AlgebraicVector {
        const first: Strut = struts.get(0);
        const common: AlgebraicVector = PolarZonohedron.getCommonEndpoint(first, struts.get(1));
        if (common == null){
            return null;
        }
        for(let i: number = 1; i < struts.size(); i++) {{
            if (!common.equals(PolarZonohedron.getCommonEndpoint(first, struts.get(i)))){
                return null;
            }
        };}
        this.redo();
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new FreePoint(common)));
        for(let index=struts.iterator();index.hasNext();) {
            let strut = index.next();
            {
                this.select$com_vzome_core_model_Manifestation(strut);
                const start: AlgebraicVector = strut.getLocation();
                if (common.equals(start)){
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new FreePoint(strut.getEnd())));
                } else {
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new FreePoint(start)));
                }
            }
        }
        this.redo();
        return common;
    }
}
PolarZonohedron["__class"] = "com.vzome.core.edits.PolarZonohedron";
