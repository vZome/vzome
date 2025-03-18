/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ShowVertices extends com.vzome.core.editor.api.ChangeManifestations {
        public static NAME: string = "ShowVertices";

        /**
         * 
         */
        public perform() {
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const s: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>man.getFirstConstruction();
                        const start: com.vzome.core.construction.SegmentEndPoint = new com.vzome.core.construction.SegmentEndPoint(s, true);
                        this.manifestConstruction(start);
                        const end: com.vzome.core.construction.SegmentEndPoint = new com.vzome.core.construction.SegmentEndPoint(s, false);
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(end));
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const polygon: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>(<com.vzome.core.model.Panel><any>man).getFirstConstruction();
                        for(let i: number = 0; i < polygon.getVertexCount(); i++) {{
                            const v: com.vzome.core.construction.PolygonVertex = new com.vzome.core.construction.PolygonVertex(polygon, i);
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(v));
                        };}
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
            return ShowVertices.NAME;
        }
    }
    ShowVertices["__class"] = "com.vzome.core.edits.ShowVertices";

}

