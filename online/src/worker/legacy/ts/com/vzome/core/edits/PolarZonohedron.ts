/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class PolarZonohedron extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ symmetry: com.vzome.core.editor.api.OrbitSource;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.symmetry === undefined) { this.symmetry = null; }
            if (this.editor === undefined) { this.editor = null; }
            this.editor = editor;
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']();
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
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.symmetry != null){
                com.vzome.xml.DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
        }

        /**
         * 
         */
        public perform() {
            const errorMsg: java.lang.StringBuilder = new java.lang.StringBuilder();
            errorMsg.append("The Polar Zonohedron command requires either of the following selections:\n\n1) Two non-collinear struts with a common end point.\n   The first strut must have more than 2-fold rotational symmetry.\n   The second strut will be rotated around the first.\n\n2) Any three or more struts having a common end point.\n");
            const struts: java.util.List<com.vzome.core.model.Strut> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        struts.add(<com.vzome.core.model.Strut><any>man);
                    }
                    this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
            if (struts.size() < 2){
                errorMsg.append(struts.size() === 1 ? "\nonly one strut is selected." : "\nno struts are selected.");
                this.fail(errorMsg.toString());
            }
            const common: com.vzome.core.algebra.AlgebraicVector = struts.size() === 2 ? this.useRotationalSymmetry(struts, errorMsg) : this.useRadialSelection(struts);
            if (common == null){
                this.fail(errorMsg.append("\nselected struts do not have a common end point").toString());
            }
            const L1: number = 0;
            const L2: number = 1;
            const layers: number = struts.size();
            const offsets: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(layers));
            const vertices: com.vzome.core.algebra.AlgebraicVector[][] = [null, null];
            vertices[L1] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(layers);
            for(let i: number = 0; i < layers; i++) {{
                const strut: com.vzome.core.model.Strut = struts.get(i);
                const start: com.vzome.core.algebra.AlgebraicVector = strut.getLocation();
                const end: com.vzome.core.algebra.AlgebraicVector = strut.getEnd();
                const offset: com.vzome.core.algebra.AlgebraicVector = strut.getOffset();
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
                    const offset: com.vzome.core.algebra.AlgebraicVector = offsets.get(off);
                    const v1: com.vzome.core.algebra.AlgebraicVector = vertices[L1][i];
                    const v2: com.vzome.core.algebra.AlgebraicVector = v1.plus(offset);
                    const v3: com.vzome.core.algebra.AlgebraicVector = vertices[L1][(i + 1) % layers];
                    const v0: com.vzome.core.algebra.AlgebraicVector = v3.minus(offset);
                    vertices[L2][i] = v2;
                    const p0: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v0);
                    const p1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v1);
                    const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v2);
                    const p3: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v3);
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p0));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p1));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p2));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p3));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p1, p2)));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p2, p3)));
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p0, p1, p2, p3])));
                };}
                vertices[L1] = vertices[L2];
            };}
            this.redo();
        }

        public static getCommonEndpoint(strut1: com.vzome.core.model.Strut, strut2: com.vzome.core.model.Strut): com.vzome.core.algebra.AlgebraicVector {
            if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(strut1,strut2))){
                throw new java.lang.IllegalArgumentException("Identical struts have both end points in common.");
            }
            const start1: com.vzome.core.algebra.AlgebraicVector = strut1.getLocation();
            const end1: com.vzome.core.algebra.AlgebraicVector = strut1.getEnd();
            const start2: com.vzome.core.algebra.AlgebraicVector = strut2.getLocation();
            const end2: com.vzome.core.algebra.AlgebraicVector = strut2.getEnd();
            if (start1.equals(start2) || start1.equals(end2))return start1;
            if (end1.equals(start2) || end1.equals(end2))return end1;
            return null;
        }

        /*private*/ useRotationalSymmetry(struts: java.util.List<com.vzome.core.model.Strut>, errorMsg: java.lang.StringBuilder): com.vzome.core.algebra.AlgebraicVector {
            const axisStrut: com.vzome.core.model.Strut = struts.get(0);
            const axisSegment: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>axisStrut.getFirstConstruction();
            let v1: com.vzome.core.algebra.AlgebraicVector = axisSegment.getOffset();
            v1 = axisSegment.getField().projectTo3d(v1, true);
            const axis1: com.vzome.core.math.symmetry.Axis = this.symmetry.getAxis(v1);
            if (axis1 == null){
                this.fail(errorMsg.append("\nfirst selected strut is not an axis of rotational symmetry").toString());
            }
            const perm: com.vzome.core.math.symmetry.Permutation = axis1.getRotationPermutation();
            if (perm == null){
                this.fail(errorMsg.append("\nfirst selected strut is not an axis of rotation").toString());
            }
            let rotation: number = perm.mapIndex(0);
            const order: number = perm.getOrder();
            if (order <= 2){
                this.fail(errorMsg.append("\nfirst selected strut has " + order + "-fold symmetry").toString());
            }
            const spokeStrut: com.vzome.core.model.Strut = struts.get(1);
            const spokeSegment: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>spokeStrut.getFirstConstruction();
            let v2: com.vzome.core.algebra.AlgebraicVector = spokeSegment.getOffset();
            if (v1.equals(v2) || v1.equals(v2.negate())){
                this.fail(errorMsg.append("\nselected struts are collinear").toString());
            }
            const common: com.vzome.core.algebra.AlgebraicVector = PolarZonohedron.getCommonEndpoint(axisStrut, spokeStrut);
            if (common == null){
                this.fail(errorMsg.append("\nselected struts do not have a common end point").toString());
            }
            let s1: com.vzome.core.algebra.AlgebraicVector = axisSegment.getStart();
            let e1: com.vzome.core.algebra.AlgebraicVector = axisSegment.getEnd();
            const center: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentEndPoint(axisSegment, common.equals(e1));
            let s2: com.vzome.core.algebra.AlgebraicVector = spokeSegment.getStart();
            let e2: com.vzome.core.algebra.AlgebraicVector = spokeSegment.getEnd();
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
            const p0: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(s2);
            const p1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(e2);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p0));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p1));
            for(let i: number = 0; i < order - 1; i++) {{
                const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.SymmetryTransformation(this.symmetry.getSymmetry(), rotation, center);
                rotation = perm.mapIndex(rotation);
                const ball: com.vzome.core.model.Connector = <com.vzome.core.model.Connector><any>this.manifestConstruction(new com.vzome.core.construction.TransformedPoint(transform, p1));
                const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>this.manifestConstruction(new com.vzome.core.construction.TransformedSegment(transform, spokeSegment));
                struts.add(strut);
                this.select$com_vzome_core_model_Manifestation(ball);
                this.select$com_vzome_core_model_Manifestation(strut);
            };}
            this.redo();
            return common;
        }

        /*private*/ useRadialSelection(struts: java.util.List<com.vzome.core.model.Strut>): com.vzome.core.algebra.AlgebraicVector {
            const first: com.vzome.core.model.Strut = struts.get(0);
            const common: com.vzome.core.algebra.AlgebraicVector = PolarZonohedron.getCommonEndpoint(first, struts.get(1));
            if (common == null){
                return null;
            }
            for(let i: number = 1; i < struts.size(); i++) {{
                if (!common.equals(PolarZonohedron.getCommonEndpoint(first, struts.get(i)))){
                    return null;
                }
            };}
            this.redo();
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.FreePoint(common)));
            for(let index=struts.iterator();index.hasNext();) {
                let strut = index.next();
                {
                    this.select$com_vzome_core_model_Manifestation(strut);
                    const start: com.vzome.core.algebra.AlgebraicVector = strut.getLocation();
                    if (common.equals(start)){
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.FreePoint(strut.getEnd())));
                    } else {
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.FreePoint(start)));
                    }
                }
            }
            this.redo();
            return common;
        }
    }
    PolarZonohedron["__class"] = "com.vzome.core.edits.PolarZonohedron";

}

