/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class HeptagonSubdivision extends com.vzome.core.editor.api.ChangeManifestations {
        /**
         * 
         */
        public perform() {
            let p1: com.vzome.core.construction.Point = null;
            this.setOrderedSelection(true);
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const nextPoint: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                        if (p1 == null)p1 = nextPoint; else {
                            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(p1, nextPoint);
                            const field: com.vzome.core.algebra.AlgebraicField = segment.getField();
                            const scaleFactor: com.vzome.core.algebra.AlgebraicNumber = field.getAffineScalar().reciprocal();
                            const offset: com.vzome.core.algebra.AlgebraicVector = segment.getOffset();
                            const off2: com.vzome.core.algebra.AlgebraicVector = offset.scale(scaleFactor);
                            const off1: com.vzome.core.algebra.AlgebraicVector = off2.scale(scaleFactor);
                            const v1: com.vzome.core.algebra.AlgebraicVector = p1.getLocation().plus(off1);
                            const firstPoint: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v1);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(firstPoint));
                            const v2: com.vzome.core.algebra.AlgebraicVector = v1.plus(off2);
                            const secondPoint: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(v2);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(secondPoint));
                            break;
                        }
                    }
                }
            }
            this.redo();
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "HeptagonSubdivision";
        }
    }
    HeptagonSubdivision["__class"] = "com.vzome.core.edits.HeptagonSubdivision";

}

