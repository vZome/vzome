/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class PanelPerimeters extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         */
        public perform() {
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (!(man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))){
                        this.unselect$com_vzome_core_model_Manifestation(man);
                    }
                }
            }
            this.redo();
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        this.unselect$com_vzome_core_model_Manifestation(man);
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        const polygon: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>panel.getFirstConstruction();
                        const vertices: com.vzome.core.algebra.AlgebraicVector[] = polygon.getVertices();
                        const first: com.vzome.core.construction.FreePoint = new com.vzome.core.construction.FreePoint(vertices[0]);
                        let start: com.vzome.core.construction.FreePoint = first;
                        for(let i: number = 1; i < vertices.length; i++) {{
                            const end: com.vzome.core.construction.FreePoint = new com.vzome.core.construction.FreePoint(vertices[i]);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(start));
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(start, end)));
                            start = end;
                        };}
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(start));
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(start, first)));
                    }
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "PanelPerimeters";
        }
    }
    PanelPerimeters["__class"] = "com.vzome.core.edits.PanelPerimeters";

}

