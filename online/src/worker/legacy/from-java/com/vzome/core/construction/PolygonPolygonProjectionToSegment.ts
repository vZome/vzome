import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { FreePoint } from "./FreePoint.js";
import { Line } from "./Line.js";
import { LineExtensionOfSegment } from "./LineExtensionOfSegment.js";
import { LineFromPointAndVector } from "./LineFromPointAndVector.js";
import { LineLineIntersectionPoint } from "./LineLineIntersectionPoint.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";
import { Segment } from "./Segment.js";
import { SegmentJoiningPoints } from "./SegmentJoiningPoints.js";

export class PolygonPolygonProjectionToSegment extends Segment {
    /*private*/ polygons: Polygon[];

    public constructor(polygon0: Polygon, polygon1: Polygon) {
        super(polygon0.getField());
        this.polygons = [null, null];
        this.polygons[0] = polygon0;
        this.polygons[1] = polygon1;
        this.mapParamsToState();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        if (this.polygons[0].isImpossible() || this.polygons[1].isImpossible()){
            return this.setStateVariables(null, null, true);
        }
        if (AlgebraicVectors.areParallel(this.polygons[0].getNormal(), this.polygons[1].getNormal())){
            return this.setStateVariables(null, null, true);
        }
        const intersections: java.util.Set<AlgebraicVector> = <any>(new java.util.HashSet<any>(2));
        for(let poly: number = 0; poly < 2; poly++) {{
            const edgePolygon: Polygon = this.polygons[poly];
            const planePolygon: Polygon = this.polygons[(poly + 1) % 2];
            const centroid: AlgebraicVector = planePolygon.getCentroid();
            const normal: AlgebraicVector = planePolygon.getNormal();
            const nVertices: number = edgePolygon.getVertexCount();
            for(let i: number = 0; i < nVertices; i++) {{
                const lineStart: AlgebraicVector = edgePolygon.getVertex(i);
                const lineDirection: AlgebraicVector = lineStart.minus(edgePolygon.getVertex((i + 1) % nVertices));
                if (!lineDirection.isOrigin()){
                    const intersection: AlgebraicVector = AlgebraicVectors.getLinePlaneIntersection(lineStart, lineDirection, centroid, normal);
                    if (intersection != null){
                        intersections.add(intersection);
                        if (intersections.size() === 2){
                            break;
                        }
                    }
                }
            };}
            if (intersections.size() === 2){
                break;
            }
        };}
        if (intersections.size() !== 2){
            for(let poly: number = 0; poly < 2; poly++) {{
                const edgePolygon: Polygon = this.polygons[poly];
                const planePolygon: Polygon = this.polygons[(poly + 1) % 2];
                const centroid: AlgebraicVector = planePolygon.getCentroid();
                const normal: AlgebraicVector = planePolygon.getNormal();
                const lineStart: AlgebraicVector = edgePolygon.getCentroid();
                for(let i: number = 0; i < edgePolygon.getVertexCount(); i++) {{
                    const lineDirection: AlgebraicVector = lineStart.minus(edgePolygon.getVertex(i));
                    if (!lineDirection.isOrigin()){
                        const intersection: AlgebraicVector = AlgebraicVectors.getLinePlaneIntersection(lineStart, lineDirection, centroid, normal);
                        if (intersection != null){
                            intersections.add(intersection);
                            if (intersections.size() === 2){
                                break;
                            }
                        }
                    }
                };}
                if (intersections.size() === 2){
                    break;
                }
            };}
        }
        if (intersections.size() !== 2){
            return this.setStateVariables(null, null, true);
        }
        let v0: AlgebraicVector = null;
        let v1: AlgebraicVector = null;
        for(let index=intersections.iterator();index.hasNext();) {
            let v = index.next();
            {
                if (v0 == null){
                    v0 = v;
                } else {
                    v1 = v;
                }
            }
        }
        const intersectionLine: Line = new LineExtensionOfSegment(new SegmentJoiningPoints(new FreePoint(v0), new FreePoint(v1)));
        const projections: java.util.Set<AlgebraicVector> = <any>(new java.util.TreeSet<any>());
        for(let poly: number = 0; poly < 2; poly++) {{
            const polygon: Polygon = this.polygons[poly];
            const v2: AlgebraicVector = v0.plus(polygon.getNormal());
            const vProjection: AlgebraicVector = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2);
            for(let i: number = 0; i < polygon.getVertexCount(); i++) {{
                const vertex: AlgebraicVector = polygon.getVertex(i);
                if (AlgebraicVectors.areCollinear$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, vertex)){
                    projections.add(vertex);
                } else {
                    const projectionLine: Line = new LineFromPointAndVector(vertex, vProjection);
                    const projection: Point = new LineLineIntersectionPoint(intersectionLine, projectionLine);
                    projections.add(projection.getLocation());
                }
            };}
        };}
        let start: AlgebraicVector = null;
        let offset: AlgebraicVector = null;
        let n: number = 0;
        for(let index=projections.iterator();index.hasNext();) {
            let v = index.next();
            {
                if (n === 0){
                    start = v;
                } else if (n === projections.size() - 1){
                    offset = v.minus(start);
                }
                n++;
            }
        }
        return this.setStateVariables(start, offset, (start == null || offset == null));
    }
}
PolygonPolygonProjectionToSegment["__class"] = "com.vzome.core.construction.PolygonPolygonProjectionToSegment";
