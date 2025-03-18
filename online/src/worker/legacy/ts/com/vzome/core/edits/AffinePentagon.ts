/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class AffinePentagon extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            const errorMsg: string = "Affine pentagon command requires two selected struts with a common vertex.";
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
                        }
                    }
                }
            }
            this.redo();
            if (strut1 == null || strut2 == null){
                this.fail(errorMsg);
            }
            const field: com.vzome.core.algebra.AlgebraicField = strut1.getLocation().getField();
            const s1: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut1.getFirstConstruction();
            const s2: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut2.getFirstConstruction();
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s1, true));
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s1, false));
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s2, true));
            this.manifestConstruction(new com.vzome.core.construction.SegmentEndPoint(s2, false));
            this.redo();
            let offset1: com.vzome.core.algebra.AlgebraicVector = s1.getOffset();
            let offset2: com.vzome.core.algebra.AlgebraicVector = s2.getOffset();
            let v1: com.vzome.core.algebra.AlgebraicVector = null;
            let v2: com.vzome.core.algebra.AlgebraicVector = null;
            {
                const s1s: com.vzome.core.algebra.AlgebraicVector = s1.getStart();
                const s1e: com.vzome.core.algebra.AlgebraicVector = s1.getEnd();
                const s2s: com.vzome.core.algebra.AlgebraicVector = s2.getStart();
                const s2e: com.vzome.core.algebra.AlgebraicVector = s2.getEnd();
                if (s1s.equals(s2s)){
                    v1 = s1e;
                    v2 = s2e;
                } else if (s1e.equals(s2s)){
                    v1 = s1s;
                    v2 = s2e;
                    offset1 = offset1.negate();
                } else if (s1e.equals(s2e)){
                    v1 = s1s;
                    v2 = s2s;
                    offset2 = offset2.negate();
                    offset1 = offset1.negate();
                } else if (s1s.equals(s2e)){
                    v1 = s1e;
                    v2 = s2s;
                    offset2 = offset2.negate();
                } else {
                    this.fail(errorMsg);
                }
            };
            let p1: com.vzome.core.construction.Point = null;
            let p2: com.vzome.core.construction.Point = null;
            for(let index=this.mManifestations.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const loc: com.vzome.core.algebra.AlgebraicVector = m.getLocation();
                        if (loc.equals(v1))p1 = <com.vzome.core.construction.Point>m.getFirstConstruction(); else if (loc.equals(v2))p2 = <com.vzome.core.construction.Point>m.getFirstConstruction();
                    }
                }
            }
            const phi: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](1);
            let transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.Translation(offset1.scale(phi));
            const p3: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(transform, p2);
            this.manifestConstruction(p3);
            transform = new com.vzome.core.construction.Translation(offset2.scale(phi));
            const p4: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(transform, p1);
            this.manifestConstruction(p4);
            let segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(p1, p3);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
            segment = new com.vzome.core.construction.SegmentJoiningPoints(p2, p4);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
            segment = new com.vzome.core.construction.SegmentJoiningPoints(p3, p4);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
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
            return "AffinePentagon";
        }
    }
    AffinePentagon["__class"] = "com.vzome.core.edits.AffinePentagon";

}

