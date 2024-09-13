/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ConvexHull2d extends com.vzome.core.edits.ConvexHull {
        public static NAME: string = "ConvexHull2d";

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return ConvexHull2d.NAME;
        }

        /**
         * 
         */
        public perform() {
            const hull2d: com.vzome.core.algebra.AlgebraicVector[] = com.vzome.core.math.convexhull.GrahamScan2D.buildHull(this.getSelectedVertexSet(true));
            this.redo();
            const vertices: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(hull2d.length);
            let p: number = 0;
            const pointMap: java.util.Map<com.vzome.core.algebra.AlgebraicVector, com.vzome.core.construction.Point> = <any>(new java.util.HashMap<any, any>(hull2d.length));
            for(let index = 0; index < hull2d.length; index++) {
                let vertex = hull2d[index];
                {
                    const point: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(vertex);
                    pointMap.put(vertex, point);
                    vertices[p++] = point;
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(point));
                }
            }
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices(vertices)));
            let start: com.vzome.core.construction.Point = pointMap.get(hull2d[0]);
            for(let i: number = 1; i < hull2d.length; i++) {{
                const end: com.vzome.core.construction.Point = pointMap.get(hull2d[i]);
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(start, end)));
                start = end;
            };}
            const end: com.vzome.core.construction.Point = pointMap.get(hull2d[0]);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(start, end)));
            this.redo();
        }
    }
    ConvexHull2d["__class"] = "com.vzome.core.edits.ConvexHull2d";

}

