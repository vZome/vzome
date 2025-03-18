/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * @author David Hall
     * @param {*} editorModel
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class AffineHeptagon extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            const errorMsg: string = "Affine heptagon command requires two selected struts with a common vertex.";
            let strut1: com.vzome.core.model.Strut = null;
            let strut2: com.vzome.core.model.Strut = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (strut1 == null){
                            strut1 = <com.vzome.core.model.Strut><any>man;
                        } else if (strut2 == null){
                            strut2 = <com.vzome.core.model.Strut><any>man;
                        } else {
                            this.fail(errorMsg);
                        }
                    }
                }
            }
            if (strut1 == null || strut2 == null){
                this.fail(errorMsg);
            }
            const field: com.vzome.core.algebra.AlgebraicField = strut1.getLocation().getField();
            this.redo();
            const s1: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut1.getFirstConstruction();
            const s2: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut2.getFirstConstruction();
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s1, true));
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s1, false));
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s2, true));
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s2, false));
            this.redo();
            let offset1: com.vzome.core.algebra.AlgebraicVector = s1.getOffset();
            let offset2: com.vzome.core.algebra.AlgebraicVector = s2.getOffset();
            let v0: com.vzome.core.algebra.AlgebraicVector = null;
            let v1: com.vzome.core.algebra.AlgebraicVector = null;
            let v2: com.vzome.core.algebra.AlgebraicVector = null;
            {
                const s1s: com.vzome.core.algebra.AlgebraicVector = s1.getStart();
                const s1e: com.vzome.core.algebra.AlgebraicVector = s1.getEnd();
                const s2s: com.vzome.core.algebra.AlgebraicVector = s2.getStart();
                const s2e: com.vzome.core.algebra.AlgebraicVector = s2.getEnd();
                if (s1s.equals(s2s)){
                    v0 = s1s;
                    v1 = s1e;
                    v2 = s2e;
                } else if (s1e.equals(s2s)){
                    v0 = s1e;
                    v1 = s1s;
                    v2 = s2e;
                    offset1 = offset1.negate();
                } else if (s1e.equals(s2e)){
                    v0 = s1e;
                    v1 = s1s;
                    v2 = s2s;
                    offset2 = offset2.negate();
                    offset1 = offset1.negate();
                } else if (s1s.equals(s2e)){
                    v0 = s1s;
                    v1 = s1e;
                    v2 = s2s;
                    offset2 = offset2.negate();
                } else {
                    this.fail(errorMsg);
                }
            };
            let c0: com.vzome.core.model.Connector = null;
            let c1: com.vzome.core.model.Connector = null;
            let c2: com.vzome.core.model.Connector = null;
            let p1: com.vzome.core.construction.Point = null;
            let p2: com.vzome.core.construction.Point = null;
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const loc: com.vzome.core.algebra.AlgebraicVector = man.getLocation();
                        if (loc.equals(v0)){
                            c0 = <com.vzome.core.model.Connector><any>man;
                        } else if (loc.equals(v1)){
                            c1 = <com.vzome.core.model.Connector><any>man;
                            p1 = <com.vzome.core.construction.Point>man.getFirstConstruction();
                        } else if (loc.equals(v2)){
                            c2 = <com.vzome.core.model.Connector><any>man;
                            p2 = <com.vzome.core.construction.Point>man.getFirstConstruction();
                        }
                    }
                }
            }
            const sigma: com.vzome.core.algebra.AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, 0, 1]);
            const rho: com.vzome.core.algebra.AlgebraicNumber = field['createAlgebraicNumber$int_A']([0, 1, 0]);
            const p3: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(new com.vzome.core.construction.Translation(offset1.scale(sigma)), p2);
            const p4: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(new com.vzome.core.construction.Translation(offset2.scale(sigma)), p1);
            const p5: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(new com.vzome.core.construction.Translation(offset1.scale(rho)), p4);
            const p6: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(new com.vzome.core.construction.Translation(offset2.scale(rho)), p3);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p1, p3)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p3, p5)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p5, p6)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p6, p4)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p4, p2)));
            this.select$com_vzome_core_model_Manifestation(c0);
            this.select$com_vzome_core_model_Manifestation(c1);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p3));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p5));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p6));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p4));
            this.select$com_vzome_core_model_Manifestation(c2);
            this.redo();
        }

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "AffineHeptagon";
        }
    }
    AffineHeptagon["__class"] = "com.vzome.core.edits.AffineHeptagon";

}

