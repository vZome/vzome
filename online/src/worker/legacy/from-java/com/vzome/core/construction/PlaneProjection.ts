import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Construction } from "./Construction.js";
import { FreePoint } from "./FreePoint.js";
import { Line } from "./Line.js";
import { LineExtensionOfSegment } from "./LineExtensionOfSegment.js";
import { LineFromPointAndVector } from "./LineFromPointAndVector.js";
import { LinePlaneIntersectionPoint } from "./LinePlaneIntersectionPoint.js";
import { Plane } from "./Plane.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";
import { Segment } from "./Segment.js";
import { SegmentJoiningPoints } from "./SegmentJoiningPoints.js";
import { Transformation } from "./Transformation.js";

/**
 * @param prototype
 * @param {Plane} projectionPlane
 * @param {Line} projectionLine
 * @class
 * @extends Transformation
 * @author Scott Vorthmann
 */
export class PlaneProjection extends Transformation {
    /*private*/ projectionPlane: Plane;

    /*private*/ projectionVector: AlgebraicVector;

    public constructor(projectionPlane: Plane, projectionLine: Line) {
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
        const loc: AlgebraicVector = this.projectionPlane.getBase();
        return this.setStateVariables(null, loc, false);
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        const line: Line = new LineFromPointAndVector(arg, this.projectionVector);
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
        if (c != null && c instanceof <any>Segment){
            if (AlgebraicVectors.areParallel(this.projectionVector, (<Segment>c).getOffset())){
                return new LinePlaneIntersectionPoint(this.projectionPlane, new LineExtensionOfSegment(<Segment>c));
            }
        } else if (c != null && c instanceof <any>Polygon){
            let p: Polygon = <Polygon>c;
            const points: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>(1 + p.getVertexCount()));
            points.add(p.getVertex(0).plus(this.projectionVector));
            for(let i: number = 0; i < p.getVertexCount(); i++) {{
                points.add(p.getVertex(i));
            };}
            if (AlgebraicVectors.areCoplanar(points)){
                p = <Polygon>super.transform$com_vzome_core_construction_Construction(p);
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
        }
        return super.transform$com_vzome_core_construction_Construction(c);
    }
}
PlaneProjection["__class"] = "com.vzome.core.construction.PlaneProjection";
