import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Construction } from "./Construction.js";
import { FreePoint } from "./FreePoint.js";
import { Line } from "./Line.js";
import { LineExtensionOfSegment } from "./LineExtensionOfSegment.js";
import { LinePlaneIntersectionPoint } from "./LinePlaneIntersectionPoint.js";
import { Plane } from "./Plane.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";
import { Segment } from "./Segment.js";
import { SegmentJoiningPoints } from "./SegmentJoiningPoints.js";
import { Transformation } from "./Transformation.js";
import { TransformedPoint } from "./TransformedPoint.js";
import { TransformedPolygon } from "./TransformedPolygon.js";
import { TransformedSegment } from "./TransformedSegment.js";

/**
 * @param prototype
 * @param {Plane} projectionPlane
 * @param {Point} perspectivePoint
 * @class
 * @extends Transformation
 * @author Scott Vorthmann
 */
export class PerspectiveProjection extends Transformation {
    /*private*/ projectionPlane: Plane;

    /*private*/ perspectivePoint: Point;

    public constructor(projectionPlane: Plane, perspectivePoint: Point) {
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
        const loc: AlgebraicVector = this.getField().origin(3);
        return this.setStateVariables(null, loc, false);
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        const segment: Segment = new SegmentJoiningPoints(this.perspectivePoint, new FreePoint(arg));
        if (segment.getOffset().isOrigin())return null;
        const line: Line = new LineExtensionOfSegment(segment);
        const point: Point = new LinePlaneIntersectionPoint(this.projectionPlane, line);
        return point.getLocation();
    }

    /**
     * 
     * @param {AlgebraicVector} arg
     * @return {AlgebraicVector}
     */
    public transform(arg?: any): any {
        if (((arg != null && arg instanceof <any>AlgebraicVector) || arg === null)) {
            return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
        } else if (((arg != null && arg instanceof <any>Construction) || arg === null)) {
            return <any>this.transform$com_vzome_core_construction_Construction(arg);
        } else throw new Error('invalid overload');
    }

    public transform$com_vzome_core_construction_Construction(c: Construction): Construction {
        if (c != null && c instanceof <any>Point){
            const result: Point = new TransformedPoint(this, <Point>c);
            if (result.isImpossible())return null;
            return result;
        } else if (c != null && c instanceof <any>Segment){
            const result: Segment = new TransformedSegment(this, <Segment>c);
            if (result.isImpossible() || result.getOffset().isOrigin()){
                return new FreePoint((<Segment>c).getStart());
            }
            return result;
        } else if (c != null && c instanceof <any>Polygon){
            const p: Polygon = new TransformedPolygon(this, <Polygon>c);
            if (p.getNormal().isOrigin()){
                let min: AlgebraicVector = p.getVertex(0);
                let max: AlgebraicVector = min;
                for(let i: number = 1; i < p.getVertexCount(); i++) {{
                    const v: AlgebraicVector = p.getVertex(i);
                    if (v.compareTo(min) === -1){
                        min = v;
                    }
                    if (v.compareTo(max) === 1){
                        max = v;
                    }
                };}
                const p1: Point = new FreePoint(min);
                const p2: Point = new FreePoint(max);
                return new SegmentJoiningPoints(p1, p2);
            }
            return p;
        }
        return super.transform$com_vzome_core_construction_Construction(c);
    }
}
PerspectiveProjection["__class"] = "com.vzome.core.construction.PerspectiveProjection";
