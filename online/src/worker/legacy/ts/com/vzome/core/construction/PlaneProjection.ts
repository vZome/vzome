/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @param prototype
     * @param {com.vzome.core.construction.Plane} projectionPlane
     * @param {com.vzome.core.construction.Line} projectionLine
     * @class
     * @extends com.vzome.core.construction.Transformation
     * @author Scott Vorthmann
     */
    export class PlaneProjection extends com.vzome.core.construction.Transformation {
        /*private*/ projectionPlane: com.vzome.core.construction.Plane;

        /*private*/ projectionVector: com.vzome.core.algebra.AlgebraicVector;

        public constructor(projectionPlane: com.vzome.core.construction.Plane, projectionLine: com.vzome.core.construction.Line) {
            super(projectionPlane.field);
            if (this.projectionPlane === undefined) { this.projectionPlane = null; }
            if (this.projectionVector === undefined) { this.projectionVector = null; }
            this.projectionPlane = projectionPlane;
            if (projectionLine == null)this.projectionVector = projectionPlane.getNormal(); else this.projectionVector = projectionLine.getDirection();
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.projectionPlane.isImpossible())this.setStateVariables(null, null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.projectionPlane.getBase();
            return this.setStateVariables(null, loc, false);
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            const line: com.vzome.core.construction.Line = new com.vzome.core.construction.LineFromPointAndVector(arg, this.projectionVector);
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
            if (c != null && c instanceof <any>com.vzome.core.construction.Segment){
                if (com.vzome.core.algebra.AlgebraicVectors.areParallel(this.projectionVector, (<com.vzome.core.construction.Segment>c).getOffset())){
                    return new com.vzome.core.construction.LinePlaneIntersectionPoint(this.projectionPlane, new com.vzome.core.construction.LineExtensionOfSegment(<com.vzome.core.construction.Segment>c));
                }
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Polygon){
                let p: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>c;
                const points: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(1 + p.getVertexCount()));
                points.add(p.getVertex(0).plus(this.projectionVector));
                for(let i: number = 0; i < p.getVertexCount(); i++) {{
                    points.add(p.getVertex(i));
                };}
                if (com.vzome.core.algebra.AlgebraicVectors.areCoplanar(points)){
                    p = <com.vzome.core.construction.Polygon>super.transform$com_vzome_core_construction_Construction(p);
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
            }
            return super.transform$com_vzome_core_construction_Construction(c);
        }
    }
    PlaneProjection["__class"] = "com.vzome.core.construction.PlaneProjection";

}

