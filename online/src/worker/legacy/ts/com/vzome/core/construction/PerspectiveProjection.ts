/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param prototype
     * @param {com.vzome.core.construction.Plane} projectionPlane
     * @param {com.vzome.core.construction.Point} perspectivePoint
     * @class
     * @extends com.vzome.core.construction.Transformation
     * @author Scott Vorthmann
     */
    export class PerspectiveProjection extends com.vzome.core.construction.Transformation {
        /*private*/ projectionPlane: com.vzome.core.construction.Plane;

        /*private*/ perspectivePoint: com.vzome.core.construction.Point;

        public constructor(projectionPlane: com.vzome.core.construction.Plane, perspectivePoint: com.vzome.core.construction.Point) {
            super(projectionPlane.field);
            if (this.projectionPlane === undefined) { this.projectionPlane = null; }
            if (this.perspectivePoint === undefined) { this.perspectivePoint = null; }
            this.projectionPlane = projectionPlane;
            this.perspectivePoint = perspectivePoint;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.projectionPlane.isImpossible())this.setStateVariables(null, null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.getField().origin(3);
            return this.setStateVariables(null, loc, false);
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(this.perspectivePoint, new com.vzome.core.construction.FreePoint(arg));
            if (segment.getOffset().isOrigin())return null;
            const line: com.vzome.core.construction.Line = new com.vzome.core.construction.LineExtensionOfSegment(segment);
            const point: com.vzome.core.construction.Point = new com.vzome.core.construction.LinePlaneIntersectionPoint(this.projectionPlane, line);
            return point.getLocation();
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} arg
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public transform(arg?: any): any {
            if (((arg != null && arg instanceof <any>com.vzome.core.algebra.AlgebraicVector) || arg === null)) {
                return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
            } else if (((arg != null && arg instanceof <any>com.vzome.core.construction.Construction) || arg === null)) {
                return <any>this.transform$com_vzome_core_construction_Construction(arg);
            } else throw new Error('invalid overload');
        }

        public transform$com_vzome_core_construction_Construction(c: com.vzome.core.construction.Construction): com.vzome.core.construction.Construction {
            if (c != null && c instanceof <any>com.vzome.core.construction.Point){
                const result: com.vzome.core.construction.Point = new com.vzome.core.construction.TransformedPoint(this, <com.vzome.core.construction.Point>c);
                if (result.isImpossible())return null;
                return result;
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Segment){
                const result: com.vzome.core.construction.Segment = new com.vzome.core.construction.TransformedSegment(this, <com.vzome.core.construction.Segment>c);
                if (result.isImpossible() || result.getOffset().isOrigin()){
                    return new com.vzome.core.construction.FreePoint((<com.vzome.core.construction.Segment>c).getStart());
                }
                return result;
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Polygon){
                const p: com.vzome.core.construction.Polygon = new com.vzome.core.construction.TransformedPolygon(this, <com.vzome.core.construction.Polygon>c);
                if (p.getNormal().isOrigin()){
                    let min: com.vzome.core.algebra.AlgebraicVector = p.getVertex(0);
                    let max: com.vzome.core.algebra.AlgebraicVector = min;
                    for(let i: number = 1; i < p.getVertexCount(); i++) {{
                        const v: com.vzome.core.algebra.AlgebraicVector = p.getVertex(i);
                        if (v.compareTo(min) === -1){
                            min = v;
                        }
                        if (v.compareTo(max) === 1){
                            max = v;
                        }
                    };}
                    const p1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(min);
                    const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(max);
                    return new com.vzome.core.construction.SegmentJoiningPoints(p1, p2);
                }
                return p;
            }
            return super.transform$com_vzome_core_construction_Construction(c);
        }
    }
    PerspectiveProjection["__class"] = "com.vzome.core.construction.PerspectiveProjection";

}

