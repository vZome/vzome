import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ConvexHull } from "./ConvexHull.js";
import { GrahamScan2D } from "../math/convexhull/GrahamScan2D.js";

export class ConvexHull2d extends ConvexHull {
    public static NAME: string = "ConvexHull2d";

    public constructor(editorModel: EditorModel) {
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
        const hull2d: AlgebraicVector[] = GrahamScan2D.buildHull(this.getSelectedVertexSet(true));
        this.redo();
        const vertices: Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(hull2d.length);
        let p: number = 0;
        const pointMap: java.util.Map<AlgebraicVector, Point> = <any>(new java.util.HashMap<any, any>(hull2d.length));
        for(let index = 0; index < hull2d.length; index++) {
            let vertex = hull2d[index];
            {
                const point: Point = new FreePoint(vertex);
                pointMap.put(vertex, point);
                vertices[p++] = point;
                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(point));
            }
        }
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new PolygonFromVertices(vertices)));
        let start: Point = pointMap.get(hull2d[0]);
        for(let i: number = 1; i < hull2d.length; i++) {{
            const end: Point = pointMap.get(hull2d[i]);
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(start, end)));
            start = end;
        };}
        const end: Point = pointMap.get(hull2d[0]);
        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new SegmentJoiningPoints(start, end)));
        this.redo();
    }
}
ConvexHull2d["__class"] = "com.vzome.core.edits.ConvexHull2d";
