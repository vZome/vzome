/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class Parallelepiped extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "Parallelepiped";
        }

        /**
         * 
         */
        public perform() {
            const errorMsg: string = "Parallelepiped command requires three selected struts with a common vertex.";
            let strut1: com.vzome.core.model.Strut = null;
            let strut2: com.vzome.core.model.Strut = null;
            let strut3: com.vzome.core.model.Strut = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (strut1 == null){
                            strut1 = <com.vzome.core.model.Strut><any>(man);
                        } else if (strut2 == null){
                            strut2 = <com.vzome.core.model.Strut><any>(man);
                        } else if (strut3 == null){
                            strut3 = <com.vzome.core.model.Strut><any>(man);
                        } else this.fail(errorMsg);
                    } else this.fail(errorMsg);
                }
            }
            if (strut1 == null || strut2 == null || strut3 == null){
                this.fail(errorMsg);
            }
            const s1: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>(strut1.getFirstConstruction());
            const s2: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>(strut2.getFirstConstruction());
            const s3: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>(strut3.getFirstConstruction());
            let offset1: com.vzome.core.algebra.AlgebraicVector = s1.getOffset();
            let offset2: com.vzome.core.algebra.AlgebraicVector = s2.getOffset();
            let offset3: com.vzome.core.algebra.AlgebraicVector = s3.getOffset();
            let v0: com.vzome.core.algebra.AlgebraicVector = null;
            let v1: com.vzome.core.algebra.AlgebraicVector = null;
            let v2: com.vzome.core.algebra.AlgebraicVector = null;
            let v3: com.vzome.core.algebra.AlgebraicVector = null;
            {
                const s1s: com.vzome.core.algebra.AlgebraicVector = s1.getStart();
                const s1e: com.vzome.core.algebra.AlgebraicVector = s1.getEnd();
                const s2s: com.vzome.core.algebra.AlgebraicVector = s2.getStart();
                const s2e: com.vzome.core.algebra.AlgebraicVector = s2.getEnd();
                if (s1s.equals(s2s)){
                    v1 = s1e;
                    v2 = s2e;
                    v0 = s2s;
                } else if (s1e.equals(s2s)){
                    v1 = s1s;
                    v2 = s2e;
                    v0 = s2s;
                    offset1 = offset1.negate();
                } else if (s1e.equals(s2e)){
                    v1 = s1s;
                    v2 = s2s;
                    v0 = s2e;
                    offset2 = offset2.negate();
                    offset1 = offset1.negate();
                } else if (s1s.equals(s2e)){
                    v1 = s1e;
                    v2 = s2s;
                    v0 = s2e;
                    offset2 = offset2.negate();
                } else {
                    this.fail(errorMsg);
                }
                const s3s: com.vzome.core.algebra.AlgebraicVector = s3.getStart();
                const s3e: com.vzome.core.algebra.AlgebraicVector = s3.getEnd();
                if (s3s.equals(v0)){
                    v3 = s3e;
                } else if (s3e.equals(v0)){
                    v3 = s3s;
                    offset3 = offset3.negate();
                } else {
                    this.fail(errorMsg);
                }
            };
            this.redo();
            const p0: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v0);
            const p1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v1);
            const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v2);
            const p3: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v3);
            this.manifestConstruction(p0);
            this.manifestConstruction(p1);
            this.manifestConstruction(p2);
            this.manifestConstruction(p3);
            this.redo();
            const v4: com.vzome.core.algebra.AlgebraicVector = v2.plus(offset3);
            const p4: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v4);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p4));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p2, p4)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p3, p4)));
            const v5: com.vzome.core.algebra.AlgebraicVector = v3.plus(offset1);
            const p5: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v5);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p5));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p1, p5)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p3, p5)));
            const v6: com.vzome.core.algebra.AlgebraicVector = v1.plus(offset2);
            const p6: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v6);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p6));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p1, p6)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p2, p6)));
            const v7: com.vzome.core.algebra.AlgebraicVector = v4.plus(offset1);
            const p7: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v7);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(p7));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p4, p7)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p5, p7)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(p6, p7)));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p0, p3, p4, p2])));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p0, p1, p5, p3])));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p0, p2, p6, p1])));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p7, p4, p3, p5])));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p7, p6, p2, p4])));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices([p7, p5, p1, p6])));
            this.redo();
        }
    }
    Parallelepiped["__class"] = "com.vzome.core.edits.Parallelepiped";

}

