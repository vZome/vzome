/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class PolygonPolygonProjectionToSegment extends com.vzome.core.construction.Segment {
        /*private*/ polygons: com.vzome.core.construction.Polygon[];

        public constructor(polygon0: com.vzome.core.construction.Polygon, polygon1: com.vzome.core.construction.Polygon) {
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
            if (com.vzome.core.algebra.AlgebraicVectors.areParallel(this.polygons[0].getNormal(), this.polygons[1].getNormal())){
                return this.setStateVariables(null, null, true);
            }
            const intersections: java.util.Set<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.HashSet<any>(2));
            for(let poly: number = 0; poly < 2; poly++) {{
                const edgePolygon: com.vzome.core.construction.Polygon = this.polygons[poly];
                const planePolygon: com.vzome.core.construction.Polygon = this.polygons[(poly + 1) % 2];
                const centroid: com.vzome.core.algebra.AlgebraicVector = planePolygon.getCentroid();
                const normal: com.vzome.core.algebra.AlgebraicVector = planePolygon.getNormal();
                const nVertices: number = edgePolygon.getVertexCount();
                for(let i: number = 0; i < nVertices; i++) {{
                    const lineStart: com.vzome.core.algebra.AlgebraicVector = edgePolygon.getVertex(i);
                    const lineDirection: com.vzome.core.algebra.AlgebraicVector = lineStart.minus(edgePolygon.getVertex((i + 1) % nVertices));
                    if (!lineDirection.isOrigin()){
                        const intersection: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getLinePlaneIntersection(lineStart, lineDirection, centroid, normal);
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
                    const edgePolygon: com.vzome.core.construction.Polygon = this.polygons[poly];
                    const planePolygon: com.vzome.core.construction.Polygon = this.polygons[(poly + 1) % 2];
                    const centroid: com.vzome.core.algebra.AlgebraicVector = planePolygon.getCentroid();
                    const normal: com.vzome.core.algebra.AlgebraicVector = planePolygon.getNormal();
                    const lineStart: com.vzome.core.algebra.AlgebraicVector = edgePolygon.getCentroid();
                    for(let i: number = 0; i < edgePolygon.getVertexCount(); i++) {{
                        const lineDirection: com.vzome.core.algebra.AlgebraicVector = lineStart.minus(edgePolygon.getVertex(i));
                        if (!lineDirection.isOrigin()){
                            const intersection: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getLinePlaneIntersection(lineStart, lineDirection, centroid, normal);
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
            let v0: com.vzome.core.algebra.AlgebraicVector = null;
            let v1: com.vzome.core.algebra.AlgebraicVector = null;
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
            const intersectionLine: com.vzome.core.construction.Line = new com.vzome.core.construction.LineExtensionOfSegment(new com.vzome.core.construction.SegmentJoiningPoints(new com.vzome.core.construction.FreePoint(v0), new com.vzome.core.construction.FreePoint(v1)));
            const projections: java.util.Set<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.TreeSet<any>());
            for(let poly: number = 0; poly < 2; poly++) {{
                const polygon: com.vzome.core.construction.Polygon = this.polygons[poly];
                const v2: com.vzome.core.algebra.AlgebraicVector = v0.plus(polygon.getNormal());
                const vProjection: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2);
                for(let i: number = 0; i < polygon.getVertexCount(); i++) {{
                    const vertex: com.vzome.core.algebra.AlgebraicVector = polygon.getVertex(i);
                    if (com.vzome.core.algebra.AlgebraicVectors.areCollinear$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, vertex)){
                        projections.add(vertex);
                    } else {
                        const projectionLine: com.vzome.core.construction.Line = new com.vzome.core.construction.LineFromPointAndVector(vertex, vProjection);
                        const projection: com.vzome.core.construction.Point = new com.vzome.core.construction.LineLineIntersectionPoint(intersectionLine, projectionLine);
                        projections.add(projection.getLocation());
                    }
                };}
            };}
            let start: com.vzome.core.algebra.AlgebraicVector = null;
            let offset: com.vzome.core.algebra.AlgebraicVector = null;
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

}

